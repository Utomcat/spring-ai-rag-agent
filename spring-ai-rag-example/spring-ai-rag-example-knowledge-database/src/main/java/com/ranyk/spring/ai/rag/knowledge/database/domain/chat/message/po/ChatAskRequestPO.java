package com.ranyk.spring.ai.rag.knowledge.database.domain.chat.message.po;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * CLASS_NAME: ChatAskRequestPO.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 聊天消息请求数据封装 PO 类, 其子段说明如下:
 * <ul>
 *     <li>question: 用户录入的问题</li>
 *     <li>sessionId: 会话 ID</li>
 *     <li>categoryIds: 分类 IDs</li>
 * </ul>
 * @date: 2026-06-28
 */
public record ChatAskRequestPO(@NotBlank(message = "用户录入的问题不能为空") String question, Long sessionId, List<Long> categoryIds) {
}
