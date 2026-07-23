package com.ranyk.spring.ai.rag.common.constant;

import lombok.Getter;

import java.util.Objects;

/**
 * CLASS_NAME: AiFactoryOwnerTypeEnum.java
 *
 * @author ranyk
 * @version V1.0
 * @description: AI 提供厂商枚举类
 * @date: 2026-07-23
 */
@Getter
public enum AiFactoryOwnerTypeEnum {
    /**
     * 阿里云百炼
     */
    DASH_SCOPE("DashScope", "阿里云百炼"),
    /**
     * 小米
     */
    XIAO_MI("XiaoMi", "小米"),
    /**
     * 腾讯
     */
    TENCENT("Tencent", "腾讯"),
    /**
     * 百度
     */
    BAIDU("Baidu", "百度"),
    /**
     * 未知
     */
    UNKNOWN("Unknown", "未知");

    /**
     * AI 提供厂商编码
     */
    private final String code;
    /**
     * AI 提供厂商描述
     */
    private final String desc;

    /**
     * 枚举构造方法
     *
     * @param code AI 提供厂商编码
     * @param desc AI 提供厂商描述
     */
    AiFactoryOwnerTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据编码获取枚举类型
     *
     * @param code 枚举编码
     * @return 枚举类型, 如果不存在则返回 {@link AiFactoryOwnerTypeEnum#UNKNOWN}
     */
    public static AiFactoryOwnerTypeEnum getByCode(String code) {
        for (AiFactoryOwnerTypeEnum type : AiFactoryOwnerTypeEnum.values()) {
            if (Objects.equals(type.getCode(), code)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
