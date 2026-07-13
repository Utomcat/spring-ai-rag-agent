package com.ranyk.spring.ai.rag.knowledge.database.domain.category.po;

/**
 * CLASS_NAME: CategorySavePO.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 知识库分类数据保存 PO 类, 其字段说明如下:
 * <ul>
 *     <li>id: 分类ID</li>
 *     <li>name: 分类名称</li>
 *     <li>description: 分类描述</li>
 *     <li>icon: 分类图标</li>
 *     <li>sortOrder: 分类排序</li>
 * </ul>
 * @date: 2026-06-28
 */
public record CategorySavePO(Long id, String name, String description, String icon, Integer sortOrder) {
}
