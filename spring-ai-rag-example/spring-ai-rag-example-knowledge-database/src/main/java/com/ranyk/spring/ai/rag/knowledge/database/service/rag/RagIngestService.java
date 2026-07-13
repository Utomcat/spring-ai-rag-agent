package com.ranyk.spring.ai.rag.knowledge.database.service.rag;

import cn.hutool.core.util.StrUtil;
import com.ranyk.spring.ai.rag.common.constant.VectorMetaKeyEnum;
import com.ranyk.spring.ai.rag.vector.store.config.properties.VectorStoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.ai.vectorstore.redis.autoconfigure.RedisVectorStoreProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redis.clients.jedis.RedisClient;
import redis.clients.jedis.json.Path2;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;
import redis.clients.jedis.search.Query;
import redis.clients.jedis.search.SearchResult;

import java.nio.file.Path;
import java.util.*;

/**
 * CLASS_NAME: RagIngestService.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 对文件进行 Tika 读取 + 切块 /  Markdown 读取 + Tika 切块 + Redis 存储 处理业务逻辑类
 * @date: 2026-06-28
 */
@Slf4j
@Service
@SuppressWarnings("unused")
public class RagIngestService {

    /**
     * vectorStoreProperties: 向量存储属性对象
     */
    private final VectorStoreProperties vectorStoreProperties;
    /**
     * redisVectorStoreProperties: Redis 向量存储属性对象
     */
    private final RedisVectorStoreProperties redisVectorStoreProperties;
    /**
     * redisVectorStore: Redis 向量存储对象
     */
    private final RedisVectorStore redisVectorStore;
    /**
     * tokenTextSplitter: Token 文本切分器对象 ，用于将文本切分成 Token
     */
    private final TokenTextSplitter tokenTextSplitter;

    /**
     * 构造方法 - 由 Spring IOC 容器在创建当前 Bean 实例时自动注入 {@link VectorStoreProperties} 对象
     *
     * @param vectorStoreProperties      向量存储属性对象
     * @param redisVectorStoreProperties Redis 向量存储属性对象
     * @param redisVectorStore           Redis 向量存储对象
     */
    @Autowired
    public RagIngestService(VectorStoreProperties vectorStoreProperties,
                            RedisVectorStoreProperties redisVectorStoreProperties,
                            RedisVectorStore redisVectorStore,
                            TokenTextSplitter tokenTextSplitter) {
        this.vectorStoreProperties = vectorStoreProperties;
        this.redisVectorStoreProperties = redisVectorStoreProperties;
        this.redisVectorStore = redisVectorStore;
        this.tokenTextSplitter = tokenTextSplitter;
    }

    /**
     * 根据传入的文件绝对路径, 解析对应的文档为 {@link List}&lt;{@link Document}&gt; 对象, 并将其存储到向量数据库中
     *
     * @param absolutePath 文件绝对路径
     * @param ext          文件扩展名
     * @param documentId   文档 ID
     * @param categoryId   文档类别 ID
     * @param title        文档标题
     * @return 向量数据库中存储的文档块数
     */
    public int ingest(Path absolutePath, String ext, Long documentId, Long categoryId, String title) {
        // 将指定路径下的文档文件解析为 List<Document>
        List<Document> loaded = loadDocuments(absolutePath, ext);
        // 将解析后的文档拆分为文档块
        List<Document> chunks = tokenTextSplitter.apply(loaded);
        List<Document> toAdd = new ArrayList<>();
        // 遍历当前的文档块, 为每个块添加元数据, 并将文本内容转换为新的 Document 对象
        for (Document ch : chunks) {
            String text = ch.getText();
            if (StrUtil.isBlank(text)) {
                continue;
            }
            // 构建当前文档块的元数据 Map<String, Object> 对象
            Map<String, Object> meta = new HashMap<>(ch.getMetadata());
            // 添加文档 ID 元数据
            meta.put(VectorMetaKeyEnum.DOC_ID.getValue(), String.valueOf(documentId));
            // 添加文档类别 ID 元数据
            meta.put(VectorMetaKeyEnum.CATEGORY_ID.getValue(), String.valueOf(categoryId));
            // 添加文档标题元数据
            meta.put(VectorMetaKeyEnum.TITLE.getValue(), StrUtil.isNotBlank(title) ? title : "");
            // 构建新的 Document 对象, 并添加到 List<Document> 中
            toAdd.add(new Document(text, meta));
        }
        // 将处理后的文档块添加到 Redis 向量存储中
        if (!toAdd.isEmpty()) {
            // 批量添加文档块到 Redis 向量存储中
            redisVectorStore.add(toAdd);
        }
        log.info("文档 {} 已入向量数据库, 入库的 docId => {} , categoryId => {} , title => {} , 入库的数据块数为: {}", absolutePath.toAbsolutePath(), documentId, categoryId, title, toAdd.size());
        // 返回向量数据库中存储的文档块数
        return toAdd.size();
    }

    /**
     * 依次：Spring AI 官方 {@code delete(Filter)}、Redis Search 批量删除、多前缀 SCAN + JSON/DEL 物理删除, 解决索引/key 前缀不一致、历史 embedding: 前缀、仅用 JSON.DEL 删不掉等情况
     *
     * @param documentId 文档 ID
     */
    public void deleteVectorsByDocumentId(Long documentId) {
        String docIdStr = String.valueOf(documentId);
        String tag = escapeRedisTag(docIdStr);
        RedisClient jedisClient = redisVectorStore.getJedisClient();
        List<String> scanPrefixes = resolveScanPrefixes();

        deleteByFrameworkFilter(docIdStr);
        int bySearch = deleteByRedisSearchAllVariants(jedisClient, scanPrefixes, docIdStr, tag);
        int byScan = deleteByScanJsonDocId(jedisClient, scanPrefixes, docIdStr);

        int total = bySearch + byScan;
        boolean stillIndexed = anyChunkRemainingInIndex(jedisClient, tag);
        if (stillIndexed) {
            log.error(
                    "文档 {} 删除后 Redis Search 仍能查到 docId 向量，物理删除 FT={}, SCAN={}; 请核对索引 [{}]、前缀 {}",
                    documentId,
                    bySearch,
                    byScan,
                    redisVectorStoreProperties.getIndexName(),
                    scanPrefixes);
        } else if (total == 0) {
            log.info(
                    "文档 {} 向量已从 Redis 清理（未在本轮 FT/SCAN 计数到 key，可能由框架 delete 完成或无向量）",
                    documentId);
        } else {
            log.info("已从 Redis 清理文档 {} 向量：FT={}, SCAN={}", documentId, bySearch, byScan);
        }
    }

    /**
     * 与 Spring AI RedisVectorStore 一致的过滤删除
     *
     * @param docIdStr 文档 ID 字符串
     */
    private void deleteByFrameworkFilter(String docIdStr) {
        try {
            Filter.Expression expr = new FilterExpressionBuilder().eq("docId", docIdStr).build();
            redisVectorStore.delete(expr);
        } catch (Exception ex) {
            log.debug("RedisVectorStore.delete(Filter) docId={} : {}", docIdStr, ex.toString());
        }
    }

    /**
     * 删除后校验索引中是否仍存在该 docId（LIMIT 1）
     *
     * @param jedisClient Jedis 客户端对象 {@link RedisClient}
     * @param escapedTag  转义的标签
     * @return 是否存在剩余的块
     */
    private boolean anyChunkRemainingInIndex(RedisClient jedisClient, String escapedTag) {
        try {
            Query q = new Query("* @docId:{" + escapedTag + "}").limit(0, 1).dialect(2);
            SearchResult r = jedisClient.ftSearch(redisVectorStoreProperties.getIndexName(), q);
            List<redis.clients.jedis.search.Document> docs = r.getDocuments();
            return docs != null && !docs.isEmpty();
        } catch (Exception ex) {
            log.debug("删除后校验 FT 失败: {}", ex.toString());
            return false;
        }
    }

    /**
     * 解析扫描前缀列表
     *
     * @return 扫描前缀列表
     */
    private List<String> resolveScanPrefixes() {
        Set<String> set = new LinkedHashSet<>();
        if (StrUtil.isNotBlank(vectorStoreProperties.getDeleteScanPrefixes())) {
            Arrays.stream(vectorStoreProperties.getDeleteScanPrefixes().split(","))
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .forEach(p -> set.add(ensureColonSuffix(p)));
        }
        set.add(ensureColonSuffix(redisVectorStoreProperties.getPrefix()));
        set.add("embedding:");
        return new ArrayList<>(set);
    }

    /**
     * SCAN/MATCH 需要明确前缀形态，如 java1234_rag:
     *
     * @param p 前缀字符串
     * @return 确保后缀为冒号的前缀字符串
     */
    private static String ensureColonSuffix(String p) {
        if (p == null || p.isEmpty()) {
            return p;
        }
        return p.endsWith(":") ? p : p + ":";
    }

    /**
     * 使用 Redis Search 删除匹配的文档
     *
     * @param jedisClient  Jedis 客户端对象 {@link RedisClient}
     * @param scanPrefixes 扫描前缀列表
     * @param docIdStr     文档 ID 字符串
     * @param escapedTag   转义的标签
     * @return 删除的文档数量
     */
    private int deleteByRedisSearchAllVariants(RedisClient jedisClient, List<String> scanPrefixes, String docIdStr, String escapedTag) {
        String[] queryStrings = {
                "* @docId:{" + escapedTag + "}",
                "@docId:{" + escapedTag + "}",
        };
        int total = 0;
        for (String qs : queryStrings) {
            total += deleteByRedisSearchQuery(jedisClient, scanPrefixes, qs);
            if (total > 0) {
                log.debug("Redis Search 使用查询 [{}] 删除 docId={}", qs, docIdStr);
                break;
            }
        }
        return total;
    }

    /**
     * 使用 Redis Search 删除匹配的文档
     *
     * @param jedisClient  Jedis 客户端对象 {@link RedisClient}
     * @param scanPrefixes 扫描前缀列表
     * @param queryString  查询字符串
     * @return 删除的文档数量
     */
    private int deleteByRedisSearchQuery(RedisClient jedisClient, List<String> scanPrefixes, String queryString) {
        Query query = new Query(queryString).limit(0, vectorStoreProperties.getDelete().getBatchQuantity()).dialect(2);
        int totalRemoved = 0;
        while (true) {
            SearchResult result;
            try {
                result = jedisClient.ftSearch(redisVectorStoreProperties.getIndexName(), query);
            } catch (Exception ex) {
                log.debug("FT.SEARCH 失败 query=[{}]: {}", queryString, ex.toString());
                return totalRemoved;
            }
            List<redis.clients.jedis.search.Document> docs = result.getDocuments();
            if (docs == null || docs.isEmpty()) {
                break;
            }
            int batchOk = 0;
            for (redis.clients.jedis.search.Document doc : docs) {
                batchOk += unlinkVectorKeyCandidates(jedisClient, scanPrefixes, doc.getId());
            }
            if (batchOk < docs.size()) {
                log.warn("本批 FT 命中 {} 条，物理删除成功 {} 条", docs.size(), batchOk);
            }
            if (batchOk == 0) {
                log.warn("FT 命中 {} 条但无任何 key 删除成功，结束以免死循环", docs.size());
                return totalRemoved;
            }
            totalRemoved += batchOk;
            if (docs.size() < vectorStoreProperties.getDelete().getBatchQuantity()) {
                break;
            }
        }
        return totalRemoved;
    }

    /**
     * 尝试多种可能 Redis key：完整 key、仅 suffix、补当前/历史前缀（避免双前缀或漏前缀）
     *
     * @param jedisClient  Jedis 客户端对象 {@link RedisClient}
     * @param scanPrefixes 扫描前缀列表
     * @param idFromSearch 从搜索中获取的 ID
     * @return 删除的文档数量
     */
    private static int unlinkVectorKeyCandidates(RedisClient jedisClient, List<String> scanPrefixes, String idFromSearch) {
        if (idFromSearch == null || idFromSearch.isEmpty()) {
            return 0;
        }
        LinkedHashSet<String> tried = new LinkedHashSet<>();
        tried.add(idFromSearch);
        String suffix = stripAnyKnownPrefix(idFromSearch, scanPrefixes);
        for (String prefix : scanPrefixes) {
            tried.add(prefix + suffix);
        }
        int removed = 0;
        for (String key : tried) {
            if (unlinkJsonOrDel(jedisClient, key)) {
                removed = 1;
                break;
            }
        }
        return removed;
    }

    /**
     * 去除已知前缀
     *
     * @param fullKey  完整的 key
     * @param prefixes 前缀列表
     * @return 去除已知前缀后的 key
     */
    private static String stripAnyKnownPrefix(String fullKey, List<String> prefixes) {
        for (String p : prefixes) {
            if (fullKey.startsWith(p)) {
                return fullKey.substring(p.length());
            }
        }
        return fullKey;
    }

    /**
     * RedisJSON 文档优先 JSON.DEL; 失败或非 JSON 再 DEL , 兼容不同模块/版本
     *
     * @param jedisClient Jedis 客户端对象 {@link RedisClient}
     * @param key         Redis key
     * @return 是否删除成功
     */
    private static boolean unlinkJsonOrDel(RedisClient jedisClient, String key) {
        try {
            long n = jedisClient.jsonDel(key);
            if (n >= 1L) {
                return true;
            }
        } catch (Exception ignored) {
            // 非 JSON key 或路径错误
        }
        try {
            long n = jedisClient.del(key);
            return n >= 1L;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * 扫描并删除匹配的 JSON 文档
     *
     * @param jedisClient  Jedis 客户端对象 {@link RedisClient}
     * @param scanPrefixes 扫描前缀列表
     * @param docIdStr     文档 ID 字符串
     * @return 删除的文档数量
     */
    private int deleteByScanJsonDocId(RedisClient jedisClient, List<String> scanPrefixes, String docIdStr) {
        int removed = 0;
        for (String prefix : scanPrefixes) {
            ScanParams params = new ScanParams().match(prefix + "*").count(500);
            String cursor = "0";
            do {
                ScanResult<String> scan = jedisClient.scan(cursor, params);
                for (String key : scan.getResult()) {
                    if (!key.startsWith(prefix)) {
                        continue;
                    }
                    String stored = readJsonDocId(jedisClient, key);
                    if (!docIdMatches(docIdStr, stored)) {
                        continue;
                    }
                    if (unlinkJsonOrDel(jedisClient, key)) {
                        removed++;
                    }
                }
                cursor = scan.getCursor();
            } while (!"0".equals(cursor));
        }
        return removed;
    }

    /**
     * 从 Redis JSON 文档中读取 docId
     *
     * @param jedisClient Jedis 客户端对象 {@link RedisClient}
     * @param key         Redis JSON 文档的 key
     * @return docId 字符串
     */
    private static String readJsonDocId(RedisClient jedisClient, String key) {
        try {
            Object raw = jedisClient.jsonGet(key, new Path2("$.docId"));
            String s = extractDocIdString(raw);
            if (s != null) {
                return s;
            }
            raw = jedisClient.jsonGet(key, new Path2("$.metadata.docId"));
            return extractDocIdString(raw);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 从 JSON 值中提取 docId 字符串
     *
     * @param raw JSON 值
     * @return docId 字符串
     */
    private static String extractDocIdString(Object raw) {
        switch (raw) {
            case null -> {
                return null;
            }
            case Number num -> {
                return String.valueOf(num.longValue());
            }
            case String s -> {
                return normalizeJsonScalarOrArrayString(s);
            }
            case List<?> list when !list.isEmpty() -> {
                Object first = list.getFirst();
                if (first instanceof Number num) {
                    return String.valueOf(num.longValue());
                }
                return normalizeJsonScalarOrArrayString(String.valueOf(first));
            }
            default -> {
            }
        }
        return normalizeJsonScalarOrArrayString(String.valueOf(raw));
    }

    /**
     * Jedis 可能返回带引号的标量或 JSON 数组字符串，如 \"5\" 或 [\"5\"]
     *
     * @param s JSON 标量或数组字符串
     * @return 正则表达式匹配的字符串
     */
    private static String normalizeJsonScalarOrArrayString(String s) {
        String t = s.trim();
        if (t.startsWith("[") && t.endsWith("]")) {
            String inner = t.substring(1, t.length() - 1).trim();
            if (inner.startsWith("\"") && inner.endsWith("\"") && inner.length() >= 2) {
                inner = inner.substring(1, inner.length() - 1);
            }
            return inner.trim();
        }
        return stripJsonQuotes(t).trim();
    }

    /**
     * 去除 JSON 引号
     *
     * @param s JSON 字符串
     * @return 去除引号后的字符串
     */
    private static String stripJsonQuotes(String s) {
        if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    /**
     * 检查存储的 docId 是否与预期的 docId 匹配
     *
     * @param expected 预期的 docId
     * @param stored   存储的 docId
     * @return 是否匹配
     */
    private static boolean docIdMatches(String expected, String stored) {
        if (stored == null) {
            return false;
        }
        if (Objects.equals(expected, stored)) {
            return true;
        }
        try {
            return Long.parseLong(expected) == Long.parseLong(stored.trim());
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 加载文档
     *
     * @param absolutePath 绝对路径
     * @param ext          扩展名
     * @return 文档列表
     */
    private List<Document> loadDocuments(Path absolutePath, String ext) {
        String e = Objects.isNull(ext) ? "" : ext.toLowerCase(Locale.ROOT);
        FileSystemResource resource = new FileSystemResource(absolutePath.toFile());
        if ("md".equals(e)) {
            return new MarkdownDocumentReader(resource, MarkdownDocumentReaderConfig.defaultConfig()).get();
        }
        return new TikaDocumentReader(resource).get();
    }

    /**
     * 转义 Redis 标签
     *
     * @param value 要转义的值
     * @return 转义后的值
     */
    private static String escapeRedisTag(String value) {
        StringBuilder sb = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '\\', '$', '|', '{', '}', '(', ')', '[', ']', '-', '\'' -> sb.append('\\').append(c);
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }
}
