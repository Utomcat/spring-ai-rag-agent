package com.ranyk.spring.ai.rag.knowledge.database.domain.chat.message.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ranyk.spring.ai.rag.knowledge.database.base.domain.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;

/**
 * CLASS_NAME: ChatMessage.java
 
 * @author ranyk
 * @version V1.0
 * @description: 数据库表 t_chat_message 映射实体类
 * @date:   2026-06-27
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@TableName("t_chat_message")
@EqualsAndHashCode(callSuper=true)
public class ChatMessage extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 2826523519851365877L;
    /**
    * 会话ID
    */
    private Long sessionId;

    /**
    * USER / ASSISTANT
    */
    private String role;

    /**
    * 消息内容
    */
    private String content;

    /**
    * 引用文档JSON
    */
    private String refs;
}