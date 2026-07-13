package com.ranyk.spring.ai.rag.knowledge.database.domain.chat.session.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ranyk.spring.ai.rag.datasource.domain.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;

/**
 * CLASS_NAME: ChatSession.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 数据库表 t_chat_session 映射实体类
 * @date: 2026-06-27
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@TableName("t_chat_session")
@EqualsAndHashCode(callSuper = true)
public class ChatSession extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -7528677223112072223L;
    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private Long userId;
    /**
     * 会话标题
     */
    @TableField(value = "title")
    private String title;
}