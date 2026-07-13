package com.ranyk.spring.ai.rag.knowledge.database.domain.document.po;

import java.util.List;

/**
 * CLASS_NAME: DocumentQueryPO.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 知识库文档数据查询参数封装 PO 类, 其字段说明如下:
 * <ul>
 *     <li>keyword: 查询关键词</li>
 *     <li>categoryId: 分类ID</li>
 *     <li>categoryIds: 分类ID列表</li>
 *     <li>fileTypes: 文件类型列表</li>
 * </ul>
 * @date: 2026-06-28
 */
public record DocumentQueryPO(String keyword, Long categoryId, List<Long> categoryIds, List<String> fileTypes) {
}
