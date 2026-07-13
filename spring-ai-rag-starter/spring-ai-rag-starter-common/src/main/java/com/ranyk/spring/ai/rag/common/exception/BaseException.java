package com.ranyk.spring.ai.rag.common.exception;

import cn.hutool.core.util.StrUtil;
import com.ranyk.spring.ai.rag.international.utils.MessageUtils;
import lombok.*;

import java.io.Serial;

/**
 * CLASS_NAME: BaseException.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 自定义通用基础异常类
 * @date: 2026-06-25
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -6529062418042844846L;

    /**
     * 所属模块
     */
    private String module;
    /**
     * 异常代码
     */
    private String code;
    /**
     * 错误信息参数
     */
    private Object[] args;

    /**
     * 构造函数 - 传入 异常信息 构造
     *
     * @param detailMessage 异常信息
     */
    public BaseException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * 构造函数 - 传入 模块 、 异常信息 构造
     *
     * @param module        模块
     * @param detailMessage 异常信息
     */
    public BaseException(String module, String detailMessage) {
        super(detailMessage, null);
        this.module = module;

    }

    /**
     *
     * 构造函数 - 传入 异常信息 、 异常对象 构造
     *
     * @param detailMessage 异常信息
     * @param e             异常对象
     */
    public BaseException(String detailMessage, Throwable e) {
        super(detailMessage, e);
    }

    /**
     *
     * 构造函数 - 传入 模块 、 异常信息 、 异常对象 构造
     *
     * @param module        模块
     * @param detailMessage 异常信息
     * @param e             异常对象
     */
    public BaseException(String module, String detailMessage, Throwable e) {
        super(detailMessage, e);
        this.module = module;
    }

    /**
     * 构造函数 - 传入 异常代码 、 异常信息参数 构造
     *
     * @param code 异常代码
     * @param args 异常信息参数
     */
    public BaseException(String code, Object[] args) {
        super();
        this.code = code;
        this.args = args;
    }

    /**
     * 获取异常信息
     *
     * @return 返回异常信息
     */
    @Override
    public String getMessage() {
        // 存在异常代码
        if (StrUtil.isNotBlank(this.code)) {
            // 获取国际化异常信息
            return MessageUtils.message(this.code, this.args);
        }
        // 不存在异常代码则从 detailMessage 属性中获取
        if (StrUtil.isNotBlank(super.getMessage()) && super.getMessage().contains(".")) {
            this.code = super.getMessage();
            return MessageUtils.message(this.code, this.args);
        }
        // 否则直接返回异常信息
        return super.getMessage();
    }
}
