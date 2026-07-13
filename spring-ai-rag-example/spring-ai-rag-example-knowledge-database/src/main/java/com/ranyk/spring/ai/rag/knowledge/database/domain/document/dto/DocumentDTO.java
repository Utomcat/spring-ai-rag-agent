package com.ranyk.spring.ai.rag.knowledge.database.domain.document.dto;

import com.ranyk.spring.ai.rag.base.domain.dto.BaseDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.util.List;

/**
 * CLASS_NAME: DocumentDTO.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 知识库文档数据传输 DTO 类
 * @date: 2026-06-28
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DocumentDTO extends BaseDTO<DocumentDTO> {
    @Serial
    private static final long serialVersionUID = -7542337006814173884L;
    // 以下是数据库存储字段
    /**
     * 分类ID
     */
    private Long categoryId;
    /**
     * 显示标题
     */
    private String title;
    /**
     * 原始文件名
     */
    private String fileName;
    /**
     * 磁盘相对路径（相对 uploads 根）
     */
    private String filePath;
    /**
     * 磁盘绝对路径
     */
    private String absolutePath;
    /**
     * 扩展名小写
     */
    private String fileType;
    /**
     * 字节大小
     */
    private Long fileSize;
    /**
     * PROCESSING/SUCCESS/FAIL
     */
    private String status;
    /**
     * 向量块数量
     */
    private Integer vectorCount;
    /**
     * 上传用户ID
     */
    private Long uploadUserId;

    // 以下是其他额外的自定义字段

    /**
     * 查询条件关键字数据
     */
    private String keyword;
    /**
     * 查询条件分类ID List 集合
     */
    private List<Long> categoryIds;
    /**
     * 查询条件文件类型 List 集合
     */
    private List<String> fileTypes;
}
