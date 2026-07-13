package com.ranyk.spring.ai.rag.knowledge.database.domain.category.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ranyk.spring.ai.rag.datasource.domain.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;

/**
 * CLASS_NAME: Category.java
 
 * @author ranyk
 * @version V1.0
 * @description: 数据库表 t_kb_category 映射实体类
 * @date:   2026-06-27
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@TableName("t_kb_category")
@EqualsAndHashCode(callSuper=true)
public class Category extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 6879742667103503642L;
    /**
    * 分类名称
    */
    @TableField(value = "name")
    private String name;
    /**
    * 描述
    */
    @TableField(value = "description")
    private String description;
    /**
    * 图标标识
    */
    @TableField(value = "icon")
    private String icon;
    /**
    * 排序
    */
    @TableField(value = "sort_order")
    private Integer sortOrder;
}