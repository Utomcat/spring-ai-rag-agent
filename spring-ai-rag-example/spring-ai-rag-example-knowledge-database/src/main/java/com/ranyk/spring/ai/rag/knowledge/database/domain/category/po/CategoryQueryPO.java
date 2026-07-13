package com.ranyk.spring.ai.rag.knowledge.database.domain.category.po;

/**
 * CLASS_NAME: CategoryQueryPO.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 知识库分类数据查询条件参数数据 PO 类, 其字段说明如下:
 * <ul>
 *     <li>name: 分类名称</li>
 *     <li>description: 分类描述</li>
 *     <li>icon: 分类标识</li>
 *     <li>sortOrder: 分类排序</li>
 * </ul>
 * @date: 2026-06-28
 */
public record CategoryQueryPO(String name, String description, String icon, Integer sortOrder) {
}
