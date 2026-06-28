package com.ranyk.spring.ai.rag.knowledge.database.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;

/**
 * CLASS_NAME: FileException.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 文件异常类
 * @date: 2026-06-28
 */
@Data
@ToString
@NoArgsConstructor
@SuppressWarnings("unused")
@EqualsAndHashCode(callSuper = true)
public class FileException extends BaseException {
    @Serial
    private static final long serialVersionUID = -2549561565983624031L;

    /**
     * 构造函数 - 传入 异常信息 构造
     *
     * @param detailMessage 异常信息
     */
    public FileException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * 构造函数 - 传入 错误信息 、 错误信息 构造
     *
     * @param detailMessage 错误信息
     * @param e             错误信息
     */
    public FileException(String detailMessage, Throwable e) {
        super(detailMessage, e);
    }

    /**
     * 构造函数 - 传入 模块 、 异常信息 构造
     *
     * @param module        模块
     * @param detailMessage 错误信息
     */
    public FileException(String module, String detailMessage) {
        super(module, detailMessage);
    }

    /**
     * 构造函数 - 传入 异常代码 、 错误信息参数 构造
     *
     * @param code 错误代码
     * @param args 错误信息参数
     */
    public FileException(String code, Object[] args) {
        super(code, args);
    }

    /**
     * 构造函数 - 传入 模块 、 异常代码 、 异常信息参数 构造
     *
     * @param module 模块
     * @param code   异常代码
     * @param args   异常信息参数
     */
    public FileException(String module, String code, Object[] args) {
        super(module, code, args);
    }
}
