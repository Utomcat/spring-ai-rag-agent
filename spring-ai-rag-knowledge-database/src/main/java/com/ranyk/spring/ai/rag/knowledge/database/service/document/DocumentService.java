package com.ranyk.spring.ai.rag.knowledge.database.service.document;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ranyk.spring.ai.rag.knowledge.database.base.domain.dto.BaseDTO;
import com.ranyk.spring.ai.rag.knowledge.database.base.domain.dto.StoredFile;
import com.ranyk.spring.ai.rag.knowledge.database.common.exception.ServiceException;
import com.ranyk.spring.ai.rag.knowledge.database.domain.document.dto.DocumentDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.document.entity.Document;
import com.ranyk.spring.ai.rag.knowledge.database.domain.document.mapstruct.DocumentMapper;
import com.ranyk.spring.ai.rag.knowledge.database.repository.document.DocumentRepository;
import com.ranyk.spring.ai.rag.knowledge.database.service.file.FileStorageService;
import com.ranyk.spring.ai.rag.knowledge.database.service.rag.RagIngestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * CLASS_NAME: DocumentService.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 知识库文档业务逻辑处理类
 * @date: 2026-06-27
 */
@Slf4j
@Service
public class DocumentService extends ServiceImpl<DocumentRepository, Document> {
    /**
     * 知识库文档数据转换 MapStruct 接口对象
     */
    private final DocumentMapper documentMapper;
    /**
     * 知识库文档文件存储业务逻辑处理类对象
     */
    private final FileStorageService fileStorageService;
    /**
     * 知识库文档 RAG 向量存储业务逻辑处理类对象
     */
    private final RagIngestService ragIngestService;
    /**
     * 知识库文档数据访问层接口对象
     */
    private final DocumentRepository documentRepository;

    /**
     * 构造方法 - 由 Spring IOC 容器在创建该 Bean 对象实例时自动注入 DocumentMapper 接口对象
     *
     * @param documentMapper     知识库文档数据转换 MapStruct 接口对象
     * @param fileStorageService 知识库文档文件存储业务逻辑处理类对象
     * @param ragIngestService   知识库文档 RAG 向量存储业务逻辑处理类对象
     * @param documentRepository 知识库文档数据访问层接口对象
     */
    @Autowired
    public DocumentService(DocumentMapper documentMapper,
                           FileStorageService fileStorageService,
                           RagIngestService ragIngestService,
                           DocumentRepository documentRepository) {
        this.documentMapper = documentMapper;
        this.fileStorageService = fileStorageService;
        this.ragIngestService = ragIngestService;
        this.documentRepository = documentRepository;
    }

    /**
     * 知识库文档上传处理
     *
     * @param validFiles  有效文件列表
     * @param documentDTO 知识库文档数据
     * @return 逻辑处理结果 {@link Boolean}
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean upload(List<MultipartFile> validFiles, DocumentDTO documentDTO) {
        // 进行文件参数校验
        if (validFiles.isEmpty()) {
            log.error("No file has been uploaded, so no processing is required.");
            throw new ServiceException("no.upload.file", new Object[]{""});
        }
        // TODO 此处可能需要增加文件过滤处理逻辑, 包括但不限于 支持的文档类型处理、单个文件的大小处理等

        // 文件上传处理
        List<StoredFile> storedFiles;
        try {
            storedFiles = fileStorageService.batchUpload(validFiles);
        } catch (Exception e) {
            log.error("在知识库文档上传至文档存储服务时, 发生异常, 异常信息为: {}", e.getMessage());
            throw new ServiceException("upload.knowledge.document.fail", new String[]{"知识库文档文件上传失败!"});
        }
        // 遍历文件存储结果 List 集合, 将其转换为知识库文档数据库映射对象 List 集合
        List<Document> storedDocumentList = storedFiles.stream().map(file -> {
            Document document = documentMapper.documentDTOToDocument(documentDTO);
            if (StrUtil.isBlank(document.getTitle())) {
                document.setTitle(file.fileName());
            }
            document.setFileName(file.fileName());
            document.setFilePath(file.relativePath());
            document.setAbsolutePath(file.absolutePath().toFile().getAbsolutePath());
            document.setFileType(file.fileType());
            document.setFileSize(file.fileSize());
            document.setStatus("PROCESSING");
            document.setVectorCount(0);
            document.setCreateBy(documentDTO.getUploadUserId());
            document.setUpdateBy(documentDTO.getUploadUserId());
            return document;
        }).toList();
        // 将知识库文档数据保存至关系型数据库中
        boolean saveBatchResult = saveOrUpdateBatch(storedDocumentList);
        // 判断数据保存至关系型数据库结果
        if (!saveBatchResult) {
            // 文件删除
            Boolean batchedDelete = fileStorageService.batchDelete(storedFiles.stream().map(item -> item.absolutePath().toAbsolutePath().toString()).toList());
            if (!batchedDelete) {
                log.error("上传知识库文档保存失败, 文件删除失败存在失败, 请联系管理员对需要删除的文件进行确认如下路径文件是否完全删除: {}", storedFiles.stream().map(item -> item.absolutePath().toAbsolutePath().toString()).toList());
            }
            log.error("上传知识库文档保存失败, 当前上传的文档数量为: {}", validFiles.size());
            throw new ServiceException("upload.knowledge.document.fail", new String[]{"保存知识库文档数据至关系型数据库失败!"});
        }
        // 文档文件向量化处理
        try {
            storedDocumentList.forEach(document -> {
                // 调用 RAG 向量存储业务逻辑处理类的 ingest 方法, 对文档文件进行向量化处理
                int n = ragIngestService.ingest(
                        Path.of(document.getAbsolutePath()),
                        document.getFileType(),
                        document.getId(),
                        documentDTO.getCategoryId(),
                        document.getTitle()
                );
                // 设置文档向量数量
                document.setVectorCount(n);
                // 设置文档状态为成功
                document.setStatus("SUCCESS");
                // 设置文档更新人
                document.setUpdateBy(documentDTO.getUploadUserId());
            });
        } catch (Exception e) {
            log.error("知识库文档向量化处理失败, 异常信息为: {}", e.getMessage());
            throw new ServiceException("upload.knowledge.document.fail", new String[]{"知识库文档向量化处理失败!"});
        }
        // 更新知识库文档数据至关系型数据库中
        boolean updateBatchResult = saveOrUpdateBatch(storedDocumentList);
        // 判断数据更新至关系型数据库结果
        if (!updateBatchResult) {
            log.error("上传知识库文档更新失败, 当前上传的文档数量为: {}", validFiles.size());
            throw new ServiceException("upload.knowledge.document.fail", new String[]{"更新知识库文档数据至关系型数据库失败!"});
        }
        // 返回成功
        return Boolean.TRUE;
    }

    /**
     * 知识库文档列表查询
     *
     * @param documentDTO 知识库文档数据查询条件
     * @return 知识库文档数据 {@link DocumentDTO}
     */
    public DocumentDTO list(DocumentDTO documentDTO) {
        Page<Document> page = BaseDTO.buildPage(documentDTO);
        LambdaQueryWrapper<Document> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Objects.nonNull(documentDTO.getCategoryId()) && !Objects.equals(0L, documentDTO.getCategoryId()), Document::getCategoryId, documentDTO.getCategoryId());
        queryWrapper.and(StrUtil.isNotBlank(documentDTO.getKeyword()), wrapper -> wrapper.like(Document::getTitle, documentDTO.getKeyword()).or().like(Document::getFileName, documentDTO.getKeyword()));
        Page<Document> documentPage = page(page, queryWrapper);
        return DocumentDTO.builder()
                .dataList(documentMapper.documentListToDocumentDTOList(documentPage.getRecords()))
                .total(documentPage.getTotal())
                .page(Long.valueOf(documentPage.getCurrent()).intValue())
                .size(Long.valueOf(documentPage.getSize()).intValue())
                .build();
    }

    /**
     * 知识库文档文件、数据库数据、向量库数据 删除
     *
     * @param id 知识库文档 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 根据 ID 获取知识库文档数据
        Document document = getById(id);
        // 判断是否存在知识库数据
        if (Objects.isNull(document)) {
            log.error("需要删除的知识库文档数据不存在, 无需进行数据删除!");
            throw new ServiceException("delete.knowledge.document.fail", new String[]{"需要删除的知识库文档数据不存在!"});
        }
        // 对知识库文档数据进行删除
        try {
            Files.deleteIfExists(Paths.get(document.getAbsolutePath()));
        } catch (Exception e) {
            log.error("在删除知识库文档文件时, 发生异常, 异常信息为: {}", e.getMessage());
            throw new ServiceException("delete.knowledge.document.fail", new String[]{"在删除知识库文档文件时, 发生异常!"});
        }
        // 对知识库向量数据库的数据进行删除
        ragIngestService.deleteVectorsByDocumentId(id);
        // 对知识库文档数据库数据进行删除
        boolean removeResult = removeById(id);
        // 判断数据删除结果
        if (!removeResult) {
            log.error("删除知识库文档数据失败, 当前删除的文档 ID 为: {}", id);
            throw new ServiceException("delete.knowledge.document.fail", new String[]{"删除知识库文档数据失败!"});
        }
    }

    /**
     * 获取所有知识库文档向量块数量
     *
     * @return 所有知识库文档向量块数量 {@link Integer}
     */
    public Integer countAllVectorChunks() {
        return getBaseMapper().selectObjs(
                        new LambdaQueryWrapper<Document>()
                                .select(Document::getVectorCount)
                ).stream()
                .mapToInt(obj -> obj != null ? (Integer) obj : 0)
                .sum();
    }

    /**
     * 获取所有知识库文档按类别分组数量
     *
     * @return 所有知识库文档按类别分组数量 {@link Map}&lt;String, Long&gt;
     */
    public List<Map<String, Long>> countGroupByCategory() {
        return documentRepository.countGroupByCategory();
    }
}
