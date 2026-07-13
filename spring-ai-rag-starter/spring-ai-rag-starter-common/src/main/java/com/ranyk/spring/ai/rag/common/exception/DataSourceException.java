package com.ranyk.spring.ai.rag.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;

/**
 * CLASS_NAME: DataSourceException.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 自定义数据源异常类
 * @date: 2026-06-26
 */
@Data
@ToString
@NoArgsConstructor
@SuppressWarnings("unused")
@EqualsAndHashCode(callSuper = true)
public class DataSourceException extends BaseException {
    @Serial
    private static final long serialVersionUID = -4226881916348732952L;

    /**
     * 构造函数 - 传入 异常信息 构造
     *
     * @param detailMessage 异常信息
     */
    public DataSourceException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * 构造函数 - 传入 错误信息 、 错误信息 构造
     *
     * @param detailMessage 错误信息
     * @param e             错误信息
     */
    public DataSourceException(String detailMessage, Throwable e) {
        super(detailMessage, e);
    }

    /**
     * 构造函数 - 传入 模块 、 异常信息 构造
     *
     * @param module        模块
     * @param detailMessage 错误信息
     */
    public DataSourceException(String module, String detailMessage) {
        super(module, detailMessage);
    }

    /**
     * 构造函数 - 传入 异常代码 、 错误信息参数 构造
     *
     * @param code 错误代码
     * @param args 错误信息参数
     */
    public DataSourceException(String code, Object[] args) {
        super(code, args);
    }

    /**
     * 构造函数 - 传入 模块 、 异常代码 、 异常信息参数 构造
     *
     * @param module 模块
     * @param code   异常代码
     * @param args   异常信息参数
     */
    public DataSourceException(String module, String code, Object[] args) {
        super(module, code, args);
    }
}
