package com.ranyk.spring.ai.rag.common.constant;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

/**
 * CLASS_NAME: FactoryOwnerTypeEnum.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 第三方厂商支持类型枚举
 * @date: 2026-07-16
 */
@Getter
public enum FactoryOwnerTypeEnum {
    /**
     * 第三方厂商 - 天气功能 - 聚合数据
     */
    FACTORY_OWNER_WEATHER_JU_HE(TypeEnum.WEATHER, "JUHE"),
    /**
     * 第三方厂商 - 天气功能 - 中国气象网
     */
    FACTORY_OWNER_CHINA_METEOROLOGICAL_NETWORK(TypeEnum.WEATHER, "CHINA_METEOROLOGICAL_NETWORK"),
    /**
     * 第三方厂商 - 其他
     */
    FACTORY_OWNER_OTHER(TypeEnum.UNKNOWN, "OTHER");

    /**
     * 第三方厂商工具功能类型, 如 天气、新闻等, 具体参见 {@link TypeEnum}
     */
    private final TypeEnum type;
    /**
     * 厂商名称, 如 百度: BAIDU、阿里: ALI、腾讯: TENCENT 等
     */
    private final String name;

    /**
     * 枚举构造方法
     *
     * @param type 第三方厂商工具功能类型
     * @param name 厂商名称
     */
    FactoryOwnerTypeEnum(TypeEnum type, String name) {
        this.type = type;
        this.name = name;
    }

    /**
     * 根据厂商名称获取厂商类型
     *
     * @param name 厂商名称
     * @return 厂商类型 {@link FactoryOwnerTypeEnum} , 默认返回 {@link FactoryOwnerTypeEnum#FACTORY_OWNER_OTHER}
     */
    public static FactoryOwnerTypeEnum getFactoryOwnerTypeEnumByName(String name) {
        for (FactoryOwnerTypeEnum factoryOwnerTypeEnum : FactoryOwnerTypeEnum.values()) {
            if (StrUtil.equalsIgnoreCase(factoryOwnerTypeEnum.name, name)) {
                return factoryOwnerTypeEnum;
            }
        }
        return FACTORY_OWNER_OTHER;
    }

    /**
     * 第三方厂商工具功能类型枚举
     */
    @Getter
    public enum TypeEnum {
        /**
         * 天气
         */
        WEATHER,
        /**
         * 新闻
         */
        NEWS,
        /**
         * 股票
         */
        STOCK,
        /**
         * 预测
         */
        FORECAST,
        /**
         * 未知
         */
        UNKNOWN;
    }
}
