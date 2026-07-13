package com.ranyk.spring.ai.rag.knowledge.database.domain.chat.message.vo;

import java.time.LocalDateTime;

/**
 * CLASS_NAME: ChatMessagesVO.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 聊天消息返回前端 VO 类, 其字段说明如下:
 * <ul>
 *     <li>id: 消息 ID</li>
 *     <li>sessionId: 会话 ID</li>
 *     <li>role: 角色: USER / ASSISTANT </li>
 *     <li>content: 内容</li>
 *     <li>refs: 引用, JSON 字符串：引用列表</li>
 *     <li>createTime: 创建时间</li>
 * </ul>
 * @date: 2026-06-28
 */
public record ChatMessagesVO(Long id,
                             Long sessionId,
                             String role,
                             String content,
                             String refs,
                             LocalDateTime createTime) {
}
