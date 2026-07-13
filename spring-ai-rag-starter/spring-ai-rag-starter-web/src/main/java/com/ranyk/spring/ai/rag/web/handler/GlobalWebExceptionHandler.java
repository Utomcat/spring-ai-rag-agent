package com.ranyk.spring.ai.rag.web.handler;

import com.ranyk.spring.ai.rag.base.domain.vo.Result;
import com.ranyk.spring.ai.rag.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * CLASS_NAME: GlobalWebExceptionHandler.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 全局异常监听类
 * @date: 2026-06-22
 */
@Slf4j
@ControllerAdvice
public class GlobalWebExceptionHandler {

    /**
     * 全局异常处理器
     *
     * @param exception 异常对象
     * @return 封装的通用结果对象 {@link Result} 对象
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception.class)
    public Result<Object> exceptionHandler(Exception exception) {
        log.error("Current Exception occurred => {} ", exception.getMessage(), exception);
        return Result.<Object>builder()
                .success(Boolean.FALSE)
                .code(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .msg(exception.getMessage())
                .data(null)
                .build();
    }

    /**
     * 自定义业务异常处理器
     *
     * @param serviceException 自定义业务异常
     * @return 返回封装的通用结果对象 {@link Result} 对象
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ServiceException.class)
    public Result<String> serviceExceptionHandler(ServiceException serviceException) {
        log.error("Current ServiceException occurred => {} ", serviceException.getMessage(), serviceException);
        return Result.<String>builder()
                .success(Boolean.FALSE)
                .code(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .msg(serviceException.getMessage())
                .data(serviceException.getMessage())
                .build();
    }
}
