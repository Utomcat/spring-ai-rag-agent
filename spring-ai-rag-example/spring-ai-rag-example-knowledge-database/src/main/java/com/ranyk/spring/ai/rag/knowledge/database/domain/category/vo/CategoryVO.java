package com.ranyk.spring.ai.rag.knowledge.database.domain.category.vo;

import java.time.LocalDateTime;

/**
 * CLASS_NAME: CategoryVO.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 知识库分类信息返回前端响应 VO 类, 其字段说明如下:
 * <ul>
 *     <li>id: 分类 ID</li>
 *     <li>name: 分类名称</li>
 *     <li>description: 分类描述</li>
 *     <li>icon: 分类图标</li>
 *     <li>sortOrder: 分类排序</li>
 *     <li>createTime: 创建时间</li>
 * </ul>
 * @date: 2026-06-27
 */
public record CategoryVO(Long id, String name, String description, String icon, Integer sortOrder, LocalDateTime createTime) {
}
