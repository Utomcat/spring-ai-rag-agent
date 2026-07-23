package com.ranyk.spring.ai.rag.common.constant;

import lombok.Getter;

/**
 * CLASS_NAME: AiTypeEnum.java
 *
 * @author ranyk
 * @version V1.0
 * @description: AI 接入方式枚举类
 * @date: 2026-07-21
 */
@Getter
public enum AiTypeEnum {
    /**
     * OpenAI 接入方式
     */
    OPENAI("OpenAI", "openai"),
    /**
     * Ollama 接入方式
     */
    OLLAMA("Ollama", "ollama"),
    /**
     * 其他接入方式
     */
    OTHER("Other", "other");

    /**
     * AI 接入方式描述
     */
    private final String desc;
    /**
     * AI 接入方式值
     */
    private final String value;

    /**
     * 构造函数
     *
     * @param desc AI 接入方式描述
     * @param value AI 接入方式值
     */
    AiTypeEnum(String desc, String value) {
        this.desc = desc;
        this.value = value;
    }

    /**
     * 根据描述获取枚举值
     *
     * @param desc 描述
     * @return 枚举值, 如果未找到则返回 {@link AiTypeEnum#OTHER}
     */
    public static AiTypeEnum valueOfDesc(String desc) {
        for (AiTypeEnum aiTypeEnum : AiTypeEnum.values()) {
            if (aiTypeEnum.desc.equals(desc)) {
                return aiTypeEnum;
            }
        }
        return OTHER;
    }
}
