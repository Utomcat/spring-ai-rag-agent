package com.ranyk.spring.ai.rag.knowledge.database.domain.chat.message.dto;


import com.ranyk.spring.ai.rag.base.domain.dto.BaseDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.util.List;
import java.util.Map;

/**
 * CLASS_NAME: ChatMessageDTO.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 聊天会话消息数据封装 DTO 类
 * @date: 2026-06-28
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ChatMessageDTO extends BaseDTO<ChatMessageDTO> {
    @Serial
    private static final long serialVersionUID = 1326776097886615121L;

    // 以下为会话消息数据库映射字段

    /**
     * 会话ID
     */
    private Long sessionId;
    /**
     * 会话用户角色, USER/ASSISTANT
     */
    private String role;
    /**
     * 会话用户内容
     */
    private String content;
    /**
     * 会话用户引用信息
     */
    private String refs;

    // 以下为额外扩展字段

    /**
     * 用户提出的问题信息
     */
    private String question;
    /**
     * 分类ID列表
     */
    private List<Long> categoryIds;
    /**
     * 调用 RAG 的回复内容
     */
    private String answer;
    /**
     * 调用 RAG 的回复内容的引用信息列表: title、docId、categoryId、snippet
     */
    private List<Map<String, Object>> references;
    /**
     * 会话用户ID
     */
    private Long userId;
}
