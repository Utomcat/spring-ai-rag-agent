package com.ranyk.spring.ai.rag.knowledge.database.ai.skill.executor;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * CLASS_NAME: SkillRetryStrategy.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Skill 重试策略 - 支持指数退避重试机制
 * @date: 2026-07-06
 */
@Slf4j
public class SkillRetryStrategy {
    
    private final int maxRetries;
    private final long initialBackoffMs;
    private final double backoffMultiplier;
    private final long maxBackoffMs;
    
    /**
     * 构造函数
     *
     * @param maxRetries       最大重试次数
     * @param initialBackoffMs 初始退避时间(毫秒)
     * @param backoffMultiplier 退避倍数
     * @param maxBackoffMs     最大退避时间(毫秒)
     */
    public SkillRetryStrategy(int maxRetries, long initialBackoffMs, double backoffMultiplier, long maxBackoffMs) {
        this.maxRetries = maxRetries;
        this.initialBackoffMs = initialBackoffMs;
        this.backoffMultiplier = backoffMultiplier;
        this.maxBackoffMs = maxBackoffMs;
    }
    
    /**
     * 执行带重试的操作
     *
     * @param operation 要执行的操作
     * @param <T>       返回值类型
     * @return 操作结果
     */
    public <T> T executeWithRetry(Supplier<T> operation) {
        return executeWithRetry(operation, null);
    }
    
    /**
     * 执行带重试的操作(可指定需要重试的异常类型)
     *
     * @param operation      要执行的操作
     * @param retryOnException 需要重试的异常类型,null 表示所有异常都重试
     * @param <T>            返回值类型
     * @return 操作结果
     */
    public <T> T executeWithRetry(Supplier<T> operation, Class<? extends Throwable> retryOnException) {
        int attempt = 0;
        long currentBackoff = initialBackoffMs;
        
        while (true) {
            try {
                attempt++;
                log.debug("执行操作,尝试次数: {}/{}", attempt, maxRetries + 1);
                
                return operation.get();
                
            } catch (Exception e) {
                // 检查是否需要重试
                if (!shouldRetry(attempt, e, retryOnException)) {
                    log.error("操作失败,不满足重试条件", e);
                    throw new RuntimeException("操作失败: " + e.getMessage(), e);
                }
                
                // 检查是否超过最大重试次数
                if (attempt > maxRetries) {
                    log.error("操作失败,已达到最大重试次数: {}", maxRetries, e);
                    throw new RuntimeException("操作失败,已达到最大重试次数: " + maxRetries, e);
                }
                
                // 执行退避等待
                log.warn("操作失败,将在 {}ms 后重试 ({}/{})", currentBackoff, attempt, maxRetries, e);
                
                try {
                    Thread.sleep(currentBackoff);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("重试被中断", ie);
                }
                
                // 计算下次退避时间(指数退避)
                currentBackoff = Math.min((long) (currentBackoff * backoffMultiplier), maxBackoffMs);
            }
        }
    }
    
    /**
     * 判断是否应该重试
     *
     * @param attempt          当前尝试次数
     * @param exception        捕获的异常
     * @param retryOnException 需要重试的异常类型
     * @return 是否应该重试
     */
    private boolean shouldRetry(int attempt, Exception exception, Class<? extends Throwable> retryOnException) {
        // 如果已超过最大重试次数,不重试
        if (attempt > maxRetries) {
            return false;
        }
        
        // 如果指定了需要重试的异常类型,只对该类型异常重试
        if (retryOnException != null) {
            return retryOnException.isInstance(exception);
        }
        
        // 否则对所有异常重试
        return true;
    }
    
    /**
     * 创建默认的重试策略
     *
     * @return 默认重试策略(最多重试3次,初始退避1秒,倍数2,最大退避30秒)
     */
    public static SkillRetryStrategy createDefault() {
        return new SkillRetryStrategy(3, 1000, 2.0, 30000);
    }
}
