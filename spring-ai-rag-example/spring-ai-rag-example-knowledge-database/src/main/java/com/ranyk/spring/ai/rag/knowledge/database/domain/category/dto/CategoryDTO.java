package com.ranyk.spring.ai.rag.knowledge.database.domain.category.dto;


import com.ranyk.spring.ai.rag.base.domain.dto.BaseDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.util.List;

/**
 * CLASS_NAME: CategoryDTO.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 知识库分类数据传输 DTO 类
 * @date: 2026-06-27
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CategoryDTO extends BaseDTO<CategoryDTO> {
    @Serial
    private static final long serialVersionUID = -7397013808925382277L;

    // 以下是数据库表字段

    /**
     * 分类名称
     */
    private String name;
    /**
     * 描述
     */
    private String description;
    /**
     * 图标标识
     */
    private String icon;
    /**
     * 排序
     */
    private Integer sortOrder;

    // 以下是额外字段

    /**
     * 知识库分类数据 List 集合 - 主要用于需要返回结果为 List 的场景
     */
    private List<CategoryDTO> dataList;


}
