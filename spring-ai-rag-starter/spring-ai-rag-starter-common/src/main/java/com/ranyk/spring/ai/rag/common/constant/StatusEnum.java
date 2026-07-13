package com.ranyk.spring.ai.rag.common.constant;

import lombok.Getter;

/**
 * CLASS_NAME: StatusEnum.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 数据状态枚举类
 * @date: 2026-06-27
 */
@Getter
public enum StatusEnum {

    /**
     * 账号状态 - 正常
     */
    ACCOUNT_NORMAL("account", 1, "正常", "Normal"),
    /**
     * 账号状态 - 冻结/禁用
     */
    ACCOUNT_FROZEN("account", 0, "冻结/禁用", "Frozen/Disabled"),
    /**
     * 账号状态 - 删除
     */
    ACCOUNT_DELETED("account", -1, "删除", "Deleted"),
    /**
     * 账号状态 - 未知
     */
    ACCOUNT_UNKNOWN("account", -999, "未知", "Unknown"),
    /**
     * 数据状态 - 未知
     */
    UNKNOW("unknown", -999, "未知", "Unknown");

    /**
     * 数据状态所属类型
     */
    private final String type;
    /**
     * 数据状态值
     */
    private final Integer value;
    /**
     * 数据状态描述 - 中文
     */
    private final String descCn;
    /**
     * 数据状态描述 - 英文
     */
    private final String descEN;

    /**
     * 枚举构造方法
     *
     * @param type   数据状态所属类型
     * @param value  数据状态值
     * @param descCn 数据状态描述 - 中文
     * @param descEN 数据状态描述 - 英文
     */
    StatusEnum(String type, Integer value, String descCn, String descEN) {
        this.type = type;
        this.value = value;
        this.descCn = descCn;
        this.descEN = descEN;
    }

    /**
     * 根据类型和值获取枚举值
     *
     * @param type  数据状态所属类型
     * @param value 数据状态值
     * @return 枚举值
     */
    public static StatusEnum getStatusEnumByTypeAndValue(String type, Integer value) {
        for (StatusEnum statusEnum : StatusEnum.values()) {
            if (statusEnum.getType().equals(type) && statusEnum.getValue().equals(value)) {
                return statusEnum;
            }
        }
        return UNKNOW;
    }

}
