package com.ranyk.spring.ai.rag.knowledge.database.domain.chat.session.po;

/**
 * CLASS_NAME: ChatSessionQueryPO.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 聊天会话查询条件数据封装 PO 类, 其字段说明如下:
 * <ul>
 *     <li>userId: 用户 ID</li>
 *     <li>title: 会话标题</li>
 * </ul>
 * @date: 2026-06-28
 */
public record ChatSessionQueryPO(Long userId, String title) {
}
