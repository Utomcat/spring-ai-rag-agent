package com.ranyk.spring.ai.rag.common.constant;

import lombok.Getter;

/**
 * CLASS_NAME: VectorMetaKeyEnum.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 向量存储元数据的key枚举类
 * @date: 2026-07-01
 */
@Getter
public enum VectorMetaKeyEnum {
    /**
     * 文档id
     */
    DOC_ID("文档id", "docId"),
    /**
     * 标题
     */
    TITLE("标题", "title"),
    /**
     * 类别id
     */
    CATEGORY_ID("类别id", "categoryId"),
    ;

    /**
     * 描述
     */
    private final String desc;
    /**
     * 值
     */
    private final String value;

    /**
     * 枚举构造方法
     *
     * @param desc  描述
     * @param value 值
     */
    VectorMetaKeyEnum(String desc, String value) {
        this.desc = desc;
        this.value = value;
    }
}
