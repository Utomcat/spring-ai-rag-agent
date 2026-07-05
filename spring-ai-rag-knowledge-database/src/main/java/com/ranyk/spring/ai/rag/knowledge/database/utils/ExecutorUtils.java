package com.ranyk.spring.ai.rag.knowledge.database.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * CLASS_NAME: ExecutorUtils.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 执行器工具类 - 提供通用的线程池管理和异步执行功能
 * @date: 2026-07-06
 */
@Slf4j
public class ExecutorUtils {
    
    /**
     * 创建命名线程工厂
     *
     * @param namePrefix 线程名称前缀
     * @return ThreadFactory
     */
    public static ThreadFactory createNamedThreadFactory(String namePrefix) {
        return r -> {
            Thread thread = new Thread(r);
            thread.setName(namePrefix + "-" + thread.threadId());
            thread.setDaemon(true);
            return thread;
        };
    }
    
    /**
     * 创建固定大小线程池
     *
     * @param poolSize   线程池大小
     * @param namePrefix 线程名称前缀
     * @return ExecutorService
     */
    public static ExecutorService createFixedThreadPool(int poolSize, String namePrefix) {
        return Executors.newFixedThreadPool(poolSize, createNamedThreadFactory(namePrefix));
    }
    
    /**
     * 安全关闭线程池
     *
     * @param executorService 要关闭的执行器
     * @param timeoutSeconds  等待终止的超时时间(秒)
     * @param executorName    执行器名称(用于日志)
     */
    public static void safeShutdown(ExecutorService executorService, int timeoutSeconds, String executorName) {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) {
                    log.warn("{} 未能在 {} 秒内正常关闭,强制关闭", executorName, timeoutSeconds);
                    executorService.shutdownNow();
                } else {
                    log.info("{} 已安全关闭", executorName);
                }
            } catch (InterruptedException e) {
                log.warn("{} 关闭时被中断,强制关闭", executorName);
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * 带超时控制的异步执行
     *
     * @param operation      要执行的操作
     * @param executor       执行器(可为null,使用默认线程池)
     * @param timeoutSeconds 超时时间(秒)
     * @param operationName  操作名称(用于错误提示)
     * @return 执行结果
     * @throws RuntimeException 如果执行失败或超时
     */
    public static Object executeWithTimeout(
            java.util.function.Supplier<Object> operation,
            ExecutorService executor,
            int timeoutSeconds,
            String operationName) {
        
        ExecutorService actualExecutor = executor != null ? executor : Executors.newCachedThreadPool();
        
        try {
            CompletableFuture<Object> future = CompletableFuture.supplyAsync(operation, actualExecutor);
            return future.get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            throw new RuntimeException(
                String.format("%s 执行超时(%d秒)", operationName, timeoutSeconds), e);
        } catch (ExecutionException e) {
            throw new RuntimeException(
                String.format("%s 执行失败: %s", operationName, e.getCause().getMessage()), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(
                String.format("%s 执行被中断", operationName), e);
        }
    }
    
    /**
     * 并行执行多个任务并等待所有完成
     *
     * @param tasks          任务列表
     * @param executor       执行器
     * @param timeoutSeconds 总超时时间(秒)
     * @param taskName       任务名称(用于日志)
     * @return 结果列表
     */
    public static <T> java.util.List<T> parallelExecuteAndWait(
            java.util.List<java.util.concurrent.Callable<T>> tasks,
            ExecutorService executor,
            int timeoutSeconds,
            String taskName) {
        
        try {
            java.util.List<Future<T>> futures = executor.invokeAll(tasks, timeoutSeconds, TimeUnit.SECONDS);
            java.util.List<T> results = new java.util.ArrayList<>();
            
            for (Future<T> future : futures) {
                results.add(future.get());
            }
            
            return results;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(taskName + " 并行执行被中断", e);
        } catch (ExecutionException e) {
            throw new RuntimeException(taskName + " 并行执行失败", e);
        }
    }
    
    /**
     * 验证参数列表大小匹配
     *
     * @param list1 第一个列表
     * @param list2 第二个列表
     * @param name1 第一个列表名称
     * @param name2 第二个列表名称
     * @throws IllegalArgumentException 如果大小不匹配
     */
    public static void validateListSizesMatch(
            java.util.List<?> list1,
            java.util.List<?> list2,
            String name1,
            String name2) {
        
        if (list1.size() != list2.size()) {
            throw new IllegalArgumentException(
                String.format("%s 和 %s 数量不匹配: %d vs %d", 
                    name1, name2, list1.size(), list2.size()));
        }
    }
}
