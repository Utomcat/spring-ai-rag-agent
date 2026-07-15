package com.ranyk.spring.ai.rag.tool.ai.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

/**
 * CLASS_NAME: KnowledgeRetrievalToolFunction.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 知识库检索 AI 工具类 - 供 Agent 自主调用进行向量检索 - Spring AI 原生方式
 * @date: 2026-07-04
 */
@Slf4j
@Component
@SuppressWarnings("unused")
public class KnowledgeRetrievalToolFunction {

    /**
     * RAG 算法中向量检索的 top_k 参数
     */
    private static final int RAG_TOP_K = 10;

    /**
     * 向量存储器
     */
    private final VectorStore vectorStore;

    /**
     * Jackson 对象映射器
     */
    private final ObjectMapper objectMapper;

    /**
     * 构造方法 - 由 Spring IOC 容器创建当前 Bean 实例对象时自动调用注入相关依赖
     *
     * @param vectorStore  向量存储器 {@link VectorStore}
     * @param objectMapper Jackson 对象映射器 {@link ObjectMapper}
     */
    @Autowired
    public KnowledgeRetrievalToolFunction(VectorStore vectorStore, @Qualifier("objectMapper") ObjectMapper objectMapper) {
        this.vectorStore = vectorStore;
        this.objectMapper = objectMapper;
    }

    /**
     * Spring AI Function Callback 工具类 - 从知识库中语义检索与用户问题相关的文档片段
     * <p>
     * 当用户询问知识库中的专业信息、技术文档、业务流程等问题时使用此工具
     *
     * @param question    用户的问题或检索关键词（必填）
     * @param categoryIds 可选的分类 ID 列表，用于限定检索范围。不传则全库检索
     * @return 结构化 JSON 字符串，包含 query、totalHits、documents 数组（每个文档包含 title、docId、categoryId、snippet）
     */
    @Tool(description = "从知识库中语义检索与用户问题相关的文档片段. 当需要查找知识库中的专业信息、技术文档、业务流程时使用此工具")
    public String retrieveKnowledge(
            @ToolParam(description = "用户的问题或检索关键词") String question,
            @ToolParam(description = "可选的分类ID列表, 用于限定检索范围. 不传则全库检索", required = false) List<Long> categoryIds
    ) {
        log.info("调用知识库检索工具 - retrieveKnowledge, 入参：question => {} , categoryIds => {}", question, categoryIds);

        try {
            // 执行向量检索（支持分类过滤）
            List<Document> documents = retrieveForCategories(question, categoryIds);

            log.info("知识库检索完成, 命中 {} 个文档片段", documents.size());

            // 构建结构化返回结果
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("query", question);
            result.put("totalHits", documents.size());

            List<Map<String, Object>> docsList = new ArrayList<>();
            for (Document doc : documents) {
                Map<String, Object> meta = doc.getMetadata();
                Map<String, Object> docInfo = new LinkedHashMap<>();
                docInfo.put("title", meta.get("title"));
                docInfo.put("docId", meta.get("docId"));
                docInfo.put("categoryId", meta.get("categoryId"));

                // 截取文档片段，避免过长
                String text = doc.getText();
                if (text != null && text.length() > 240) {
                    text = text.substring(0, 240) + "…";
                }
                docInfo.put("snippet", text);

                docsList.add(docInfo);
            }
            result.put("documents", docsList);

            String jsonResult = objectMapper.writeValueAsString(result);
            log.info("知识库检索工具返回结果长度：{} 字符", jsonResult.length());
            return jsonResult;

        } catch (Exception e) {
            log.error("知识库检索工具执行异常：{}", e.getMessage(), e);
            // 返回空结果而非抛出异常，让 LLM 能够继续处理
            return "{\"query\":\"" + question + "\",\"totalHits\":0,\"documents\":[],\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    /**
     * 根据问题和类别 ID 列表，检索相关的文档
     *
     * @param question    问题
     * @param categoryIds 类别 ID 列表
     * @return 相关的文档列表
     */
    private List<Document> retrieveForCategories(String question, List<Long> categoryIds) {
        // 判断是否传入类别 ID
        if (Objects.isNull(categoryIds) || categoryIds.isEmpty()) {
            // 不存在类别 ID，则直接使用全库的向量相似检索
            return vectorSimilaritySearch(question, null);
        }

        // 过滤类别 ID，去除空值和重复值
        Set<String> keys = categoryIds.stream()
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // 判断过滤后是否存在有效的类别 ID
        if (keys.isEmpty()) {
            // 不存在有效的类别 ID，则直接使用全库的向量相似检索
            return vectorSimilaritySearch(question, null);
        }

        try {
            // 构建类别 ID 过滤器表达式
            Filter.Expression expr = buildCategoryIdFilter(keys);
            // 执行向量相似度检索，获取过滤后的文档列表
            List<Document> filtered = vectorSimilaritySearch(question, expr);
            if (!filtered.isEmpty()) {
                // 过滤后的文档列表不为空，则返回过滤后的文档列表
                return filtered;
            }
            // 过滤后的文档列表为空，则进行全库无条件检索
            log.info("限定 categoryId {} 向量检索无命中，降级为全库无条件检索", keys);
            return vectorSimilaritySearch(question, null);
        } catch (Exception ex) {
            log.warn("限定 categoryId 过滤向量检索失败，降级为全库无条件检索，当前的异常信息为：{}", ex.getMessage(), ex);
            return vectorSimilaritySearch(question, null);
        }
    }

    /**
     * 向量相似度检索
     *
     * @param question 问题
     * @param filter   过滤器
     * @return 相关的文档列表
     */
    private List<Document> vectorSimilaritySearch(String question, Filter.Expression filter) {
        SearchRequest.Builder b = SearchRequest.builder()
                .query(question)
                .topK(RAG_TOP_K)
                .similarityThreshold(0.0);
        if (filter != null) {
            b.filterExpression(filter);
        }
        return vectorStore.similaritySearch(b.build());
    }

    /**
     * 构建类别 ID 过滤器
     *
     * @param categoryIdsAsString 类别 ID 字符串集合
     * @return 过滤器表达式
     */
    private static Filter.Expression buildCategoryIdFilter(Set<String> categoryIdsAsString) {
        // 创建过滤器表达式构建器，并设置类别 ID 列表过滤条件
        if (categoryIdsAsString.size() == 1) {
            // 只存在一个类别 ID，则直接使用等于条件进行过滤
            return new FilterExpressionBuilder().eq("categoryId", categoryIdsAsString.stream().findFirst().get()).build();
        }
        // 存在多个类别 ID，则使用 IN 条件进行过滤
        return new FilterExpressionBuilder().in("categoryId", categoryIdsAsString.stream().toList()).build();
    }

}
