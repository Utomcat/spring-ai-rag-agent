package com.ranyk.spring.ai.rag.knowledge.database.common.constant;

import lombok.Getter;

/**
 * CLASS_NAME: FileCategoryEnum.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 文件类型分类枚举类
 * @date: 2026-07-02
 */
@Getter
public enum FileCategoryEnum {
    /**
     * 文件类型分类 - 办公文档
     */
    DOCUMENT("办公文档"),
    /**
     * 文件类型分类 - Web文件
     */
    WEB("Web文件"),
    /**
     * 文件类型分类 - 数据格式
     */
    DATA("数据格式"),
    /**
     * 文件类型分类 - 源代码
     */
    CODE("源代码"),
    /**
     * 文件类型分类 - 脚本文件
     */
    SCRIPT("脚本文件"),
    /**
     * 文件类型分类 - 系统文件
     */
    SYSTEM("系统文件"),
    /**
     * 文件类型分类 - 多媒体
     */
    MEDIA("多媒体"),
    /**
     * 文件类型分类 - 压缩归档
     */
    ARCHIVE("压缩归档"),
    /**
     * 文件类型分类 - 其他
     */
    OTHER("其他");

    /**
     * 文件类型分类描述
     */
    private final String description;

    /**
     * 枚举构造方法
     *
     * @param description 文件类型分类描述
     */
    FileCategoryEnum(String description) {
        this.description = description;
    }
}
