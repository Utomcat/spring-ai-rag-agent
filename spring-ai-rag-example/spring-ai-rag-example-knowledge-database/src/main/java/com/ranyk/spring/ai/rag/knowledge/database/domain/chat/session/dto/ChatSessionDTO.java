package com.ranyk.spring.ai.rag.knowledge.database.domain.chat.session.dto;

import com.ranyk.spring.ai.rag.base.domain.dto.BaseDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;

/**
 * CLASS_NAME: ChatSessionDTO.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 聊天会话数据封装传输 DTO 类
 * @date: 2026-06-28
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ChatSessionDTO extends BaseDTO<ChatSessionDTO> {
    @Serial
    private static final long serialVersionUID = 5002469535938942938L;

    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 会话标题
     */
    private String title;
}
