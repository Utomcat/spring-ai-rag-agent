package com.ranyk.spring.ai.rag.knowledge.database.common.constant;

import lombok.Getter;

/**
 * CLASS_NAME: SymbolEnum.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 标点符号常量枚举类
 * @date: 2026-07-04
 */
@Getter
public enum SymbolEnum {
    /**
     * 符号 - 逗号 英文
     */
    COMMA_EN(",", "英文逗号"),
    /**
     * 符号 - 逗号 中文
     */
    COMMA_CN("，", "中文逗号"),
    /**
     * 符号 - 分号 英文
     */
    SEMICOLON_EN(";", "英文分号"),
    /**
     * 符号 - 分号 中文
     */
    SEMICOLON_CN("；", "中文分号"),
    /**
     * 符号 - 破折号 英文
     */
    DASH_EN("-", "英文破折号"),
    /**
     * 符号 - 破折号 中文
     */
    DASH_CN("—", "中文破折号"),
    /**
     * 符号 - 空的JSON数组
     */
    EMPTY_JSON_ARRAY("[]", "空的JSON数组"),
    /**
     * 符号 - 水平省略号
     */
    ELLIPSIS("…", "水平省略号");

    /**
     * 符号编码
     */
    private final String code;
    /**
     * 符号描述
     */
    private final String desc;

    /**
     * 构造方法
     *
     * @param code 符号编码
     * @param desc 符号描述
     */
    SymbolEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
