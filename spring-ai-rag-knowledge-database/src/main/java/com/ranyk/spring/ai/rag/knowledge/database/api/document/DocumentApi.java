package com.ranyk.spring.ai.rag.knowledge.database.api.document;

import cn.hutool.core.util.StrUtil;
import com.ranyk.spring.ai.rag.knowledge.database.base.domain.po.PageQueryPO;
import com.ranyk.spring.ai.rag.knowledge.database.base.domain.vo.MultiResult;
import com.ranyk.spring.ai.rag.knowledge.database.base.domain.vo.Result;
import com.ranyk.spring.ai.rag.knowledge.database.domain.document.dto.DocumentDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.document.mapstruct.DocumentMapper;
import com.ranyk.spring.ai.rag.knowledge.database.domain.document.po.DocumentQueryPO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.document.vo.DocumentVO;
import com.ranyk.spring.ai.rag.knowledge.database.service.document.DocumentService;
import com.ranyk.spring.ai.rag.knowledge.database.utils.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * CLASS_NAME: DocumentApi.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 知识库文档 API 接口类
 * @date: 2026-06-28
 */
@RestController
@RequestMapping("/api/document")
public class DocumentApi {

    /**
     * 知识库文档业务逻辑处理类对象
     */
    private final DocumentService documentService;
    /**
     * 知识库文档数据转换 MapStruct 接口对象
     */
    private final DocumentMapper documentMapper;

    /**
     * 构造方法
     *
     * @param documentService 知识库文档业务逻辑处理类对象
     * @param documentMapper  知识库文档数据转换 MapStruct 接口对象
     */
    @Autowired
    public DocumentApi(DocumentService documentService, DocumentMapper documentMapper) {
        this.documentService = documentService;
        this.documentMapper = documentMapper;
    }

    /**
     * 知识库文档数据新增处理 - 上传文档文件、 文档文件解析、文档文件向量化、文档向量化保存、知识库文档数据保存
     *
     * @param files      文档文件
     * @param categoryId 文档分类ID
     * @param title      文档标题
     * @return 上传结果
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Boolean> upload(@RequestPart("files") MultipartFile[] files,
                                  @RequestParam Long categoryId,
                                  @RequestParam(required = false) String title) {
        // 获取当前登录用户信息
        var user = SecurityUtils.requireUser();
        // 过滤掉空文件: 文件名为空或文件大小为0的无效文件
        List<MultipartFile> validFiles = Arrays.stream(files)
                .filter(file -> file != null && StrUtil.isNotBlank(file.getOriginalFilename()) && file.getSize() > 0)
                .toList();
        return Result.success(documentService.upload(validFiles, DocumentDTO.builder().categoryId(categoryId).title(title).uploadUserId(user.getUserId()).build()));
    }

    /**
     * 知识库文档数据分页查询处理（管理员）
     *
     * @param documentQueryPO 知识库文档数据查询条件
     * @return 知识库文档数据分页查询结果
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public MultiResult<DocumentVO> list(@Valid PageQueryPO pageQueryPO, @Valid DocumentQueryPO documentQueryPO) {
        DocumentDTO query = documentMapper.documentQueryPOToDocumentDTO(documentQueryPO);
        query.setPage(pageQueryPO.page());
        query.setSize(pageQueryPO.size());
        DocumentDTO documentDTO = documentService.list(query);
        return MultiResult.successMulti(documentMapper.documentDTOListToDocumentVOList(documentDTO.getDataList()), documentDTO.getTotal(), documentDTO.getPage(), documentDTO.getSize());
    }

    /**
     * 删除文档及向量（管理员）
     *
     * @param id 文档ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        documentService.delete(id);
        return Result.success();
    }
}