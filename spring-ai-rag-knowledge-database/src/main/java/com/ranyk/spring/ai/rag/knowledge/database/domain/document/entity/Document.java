package com.ranyk.spring.ai.rag.knowledge.database.domain.document.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ranyk.spring.ai.rag.knowledge.database.base.domain.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;

/**
 * CLASS_NAME: Document.java
 
 * @author ranyk
 * @version V1.0
 * @description: 数据库表 t_kb_document 映射实体类
 * @date:   2026-06-27
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@TableName("t_kb_document")
@EqualsAndHashCode(callSuper=true)
public class Document extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -6269922818642100800L;
    /**
    * 分类ID
    */
    @TableField(value = "category_id")
    private Long categoryId;
    /**
    * 显示标题
    */
    @TableField(value = "title")
    private String title;
    /**
    * 原始文件名
    */
    @TableField(value = "file_name")
    private String fileName;
    /**
    * 磁盘相对路径（相对 uploads 根）
    */
    @TableField(value = "file_path")
    private String filePath;
    /**
    * 磁盘绝对路径
    */
    @TableField(value = "absolute_path")
    private String absolutePath;
    /**
    * 扩展名小写
    */
    @TableField(value = "file_type")
    private String fileType;
    /**
    * 字节大小
    */
    @TableField(value = "file_size")
    private Long fileSize;
    /**
    * PROCESSING/SUCCESS/FAIL
    */
    @TableField(value = "status")
    private String status;
    /**
    * 向量块数量
    */
    @TableField(value = "vector_count")
    private Integer vectorCount;
    /**
    * 上传用户ID
    */
    @TableField(value = "upload_user_id")
    private Long uploadUserId;
}