package com.ranyk.spring.ai.rag.knowledge.database.handle;

import com.ranyk.spring.ai.rag.knowledge.database.common.exception.AiException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * CLASS_NAME: ServiceExceptionHandler.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Service层异常处理器 - 提供标准化的异常捕获、转换和日志记录
 * @date: 2026-07-06
 */
@Slf4j
public class ServiceExceptionHandler {
    
    /**
     * 安全执行操作,捕获并转换异常
     *
     * @param operation   要执行的操作
     * @param errorPrefix 错误消息前缀
     * @return 执行结果,失败返回 null
     */
    public static <T> T safeExecute(Supplier<T> operation, String errorPrefix) {
        try {
            return operation.get();
        } catch (AiException e) {
            log.error("{}: {}", errorPrefix, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("{}: {}", errorPrefix, e.getMessage(), e);
            throw new AiException(errorPrefix + ": " + e.getMessage(), e);
        }
    }
    
    /**
     * 安全执行操作(无返回值),捕获并转换异常
     *
     * @param runnable    要执行的操作
     * @param errorPrefix 错误消息前缀
     */
    public static void safeExecute(Runnable runnable, String errorPrefix) {
        try {
            runnable.run();
        } catch (AiException e) {
            log.error("{}: {}", errorPrefix, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("{}: {}", errorPrefix, e.getMessage(), e);
            throw new AiException(errorPrefix + ": " + e.getMessage(), e);
        }
    }
    
    /**
     * 将普通异常转换为 AiException
     *
     * @param e       原始异常
     * @param message 自定义错误消息
     * @return AiException
     */
    public static AiException toAiException(Exception e, String message) {
        if (e instanceof AiException) {
            return (AiException) e;
        }
        return new AiException(message, e);
    }
    
    /**
     * 记录异常详情
     *
     * @param exception 异常对象
     * @param context   上下文信息
     */
    public static void logExceptionDetails(Exception exception, String context) {
        log.error("异常详情 - 上下文: {}, 类型: {}, 消息: {}", 
                context, 
                exception.getClass().getSimpleName(), 
                exception.getMessage(), 
                exception);
        
        // 如果有 cause,也记录
        if (exception.getCause() != null) {
            log.error("根本原因 - 类型: {}, 消息: {}", 
                    exception.getCause().getClass().getSimpleName(),
                    exception.getCause().getMessage());
        }
    }
    
    /**
     * 判断是否为可重试的异常
     *
     * @param exception 异常对象
     * @return true 如果可重试
     */
    public static boolean isRetryable(Exception exception) {
        if (exception instanceof IOException) {
            return true;
        }
        
        // 检查 cause
        Throwable cause = exception.getCause();
        while (cause != null) {
            if (cause instanceof IOException) {
                return true;
            }
            cause = cause.getCause();
        }
        
        return false;
    }
    
    /**
     * 获取用户友好的错误消息
     *
     * @param exception 异常对象
     * @return 用户友好的错误消息
     */
    public static String getUserFriendlyMessage(Exception exception) {
        if (exception instanceof AiException) {
            // 根据错误代码返回友好消息
            String message = exception.getMessage();
            if (message != null && !message.isEmpty()) {
                return message;
            }
        }
        
        return "系统异常,请联系管理员";
    }
}
