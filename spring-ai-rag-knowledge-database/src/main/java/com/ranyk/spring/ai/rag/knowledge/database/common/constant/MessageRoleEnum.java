package com.ranyk.spring.ai.rag.knowledge.database.common.constant;

import lombok.Getter;

/**
 * CLASS_NAME: MessageRoleEnum.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 消息角色类型枚举类
 * @date: 2026-07-04
 */
@Getter
public enum MessageRoleEnum {
    /**
     * 用户角色
     */
    ROLE_USER("USER"),
    /**
     * 机器人角色
     */
    ROLE_ASSISTANT("ASSISTANT"),
    /**
     * 系统角色
     */
    ROLE_SYSTEM("SYSTEM");

    /**
     * 角色
     */
    private final String role;

    /**
     * 枚举构造方法
     *
     * @param role 角色
     */
    MessageRoleEnum(String role) {
        this.role = role;
    }
}
