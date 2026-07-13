package com.ranyk.spring.ai.rag.knowledge.database.domain.chat.session.vo;

import java.time.LocalDateTime;

/**
 * CLASS_NAME: ChatSessionVO.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 聊天会话返回前端 VO 类, 其字段说明如下:
 * <ul>
 *     <li>id: 聊天会话的唯一标识符</li>
 *     <li>userId: 用户的唯一标识符</li>
 *     <li>title: 聊天会话的标题</li>
 *     <li>createTime: 聊天会话的创建时间</li>
 *     <li>updateTime: 聊天会话的更新时间</li>
 * </ul>
 * @date: 2026-06-28
 */
public record ChatSessionVO(Long id, Long userId, String title, LocalDateTime createTime, LocalDateTime updateTime) {
}
