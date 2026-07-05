package com.ranyk.spring.ai.rag.knowledge.database.handle;

import com.ranyk.spring.ai.rag.knowledge.database.base.domain.vo.Result;
import com.ranyk.spring.ai.rag.knowledge.database.common.exception.AiException;
import com.ranyk.spring.ai.rag.knowledge.database.common.exception.DataSourceException;
import com.ranyk.spring.ai.rag.knowledge.database.common.exception.FileException;
import com.ranyk.spring.ai.rag.knowledge.database.common.exception.ServiceException;
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
 * @version V2.0
 * @description: 全局异常处理器 - 提供分层异常处理和统一响应格式
 * @date: 2026-06-22
 */
@Slf4j
@ControllerAdvice
public class GlobalWebExceptionHandler {

    /**
     * AI 模块异常处理器
     * <p>
     * 处理 AI 相关的业务异常,包括配置错误、参数验证错误、执行错误等
     *
     * @param aiException AI 异常对象
     * @return 返回封装的通用结果对象 {@link Result} 对象
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(AiException.class)
    public Result<String> aiExceptionHandler(AiException aiException) {
        log.error("AI 模块异常 => {} ", aiException.getMessage(), aiException);
        // 使用父类 BaseException 的 getMessage() 方法获取国际化消息
        String friendlyMessage = aiException.getMessage();
        return Result.<String>builder()
                .success(Boolean.FALSE)
                .code(aiException.getCode() != null ? aiException.getCode() : "AI_ERROR")
                .msg(friendlyMessage)
                .data(null)
                .build();
    }

    /**
     * 数据源异常处理器
     * <p>
     * 处理数据源相关的异常,如数据库连接失败、查询超时等
     *
     * @param dataSourceException 数据源异常对象
     * @return 返回封装的通用结果对象 {@link Result} 对象
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(DataSourceException.class)
    public Result<String> dataSourceExceptionHandler(DataSourceException dataSourceException) {
        log.error("数据源异常 => {} ", dataSourceException.getMessage(), dataSourceException);
        return Result.<String>builder()
                .success(Boolean.FALSE)
                .code(dataSourceException.getCode() != null ? dataSourceException.getCode() : "DATA_SOURCE_ERROR")
                .msg(dataSourceException.getMessage())
                .data(null)
                .build();
    }

    /**
     * 文件操作异常处理器
     * <p>
     * 处理文件上传、下载、解析等相关异常
     *
     * @param fileException 文件异常对象
     * @return 返回封装的通用结果对象 {@link Result} 对象
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(FileException.class)
    public Result<String> fileExceptionHandler(FileException fileException) {
        log.error("文件操作异常 => {} ", fileException.getMessage(), fileException);
        return Result.<String>builder()
                .success(Boolean.FALSE)
                .code(fileException.getCode() != null ? fileException.getCode() : "FILE_ERROR")
                .msg(fileException.getMessage())
                .data(null)
                .build();
    }

    /**
     * 自定义业务异常处理器
     * <p>
     * 处理业务逻辑层的自定义异常
     *
     * @param serviceException 自定义业务异常
     * @return 返回封装的通用结果对象 {@link Result} 对象
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ServiceException.class)
    public Result<String> serviceExceptionHandler(ServiceException serviceException) {
        log.error("业务异常 => {} ", serviceException.getMessage(), serviceException);
        return Result.<String>builder()
                .success(Boolean.FALSE)
                .code(serviceException.getCode() != null ? serviceException.getCode() : "SERVICE_ERROR")
                .msg(serviceException.getMessage())
                .data(null)
                .build();
    }

    /**
     * 全局兜底异常处理器
     * <p>
     * 捕获所有未被专门处理的异常,作为最后的防线
     *
     * @param exception 异常对象
     * @return 封装的通用结果对象 {@link Result} 对象
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception.class)
    public Result<Object> globalExceptionHandler(Exception exception) {
        log.error("未预期的系统异常 => {} ", exception.getMessage(), exception);
        return Result.<Object>builder()
                .success(Boolean.FALSE)
                .code(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .msg("系统异常,请联系管理员")
                .data(null)
                .build();
    }
}
