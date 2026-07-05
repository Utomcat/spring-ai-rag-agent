package com.ranyk.spring.ai.rag.knowledge.database.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * CLASS_NAME: ConcurrentUtils.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 并发工具类 - 提供高效的并发执行和结果聚合功能
 * @date: 2026-07-06
 */
@Slf4j
public class ConcurrentUtils {
    
    /**
     * 并行处理列表中的每个元素
     *
     * @param items      输入列表
     * @param processor  处理器函数
     * @param executor   线程池
     * @param timeout    超时时间(秒)
     * @param taskName   任务名称
     * @return 处理结果列表(保持原始顺序)
     */
    public static <T, R> List<R> parallelProcess(
            List<T> items,
            Function<T, R> processor,
            ExecutorService executor,
            int timeout,
            String taskName) {
        
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }
        
        log.debug("开始并行处理 {} 个{}, 超时: {}秒", items.size(), taskName, timeout);
        long startTime = System.currentTimeMillis();
        
        // 提交所有任务
        List<Future<R>> futures = new ArrayList<>();
        for (T item : items) {
            futures.add(executor.submit(() -> processor.apply(item)));
        }
        
        // 收集结果
        List<R> results = new ArrayList<>(Collections.nCopies(items.size(), null));
        List<String> errors = new ArrayList<>();
        
        for (int i = 0; i < futures.size(); i++) {
            try {
                R result = futures.get(i).get(timeout, TimeUnit.SECONDS);
                results.set(i, result);
            } catch (TimeoutException e) {
                String errorMsg = String.format("%s[%d] 执行超时", taskName, i);
                log.error(errorMsg, e);
                errors.add(errorMsg);
            } catch (ExecutionException e) {
                String errorMsg = String.format("%s[%d] 执行失败: %s", taskName, i, e.getCause().getMessage());
                log.error(errorMsg, e);
                errors.add(errorMsg);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(taskName + " 被中断", e);
            }
        }
        
        long elapsed = System.currentTimeMillis() - startTime;
        log.info("并行处理完成: {}, 耗时: {}ms, 成功: {}, 失败: {}", 
                taskName, elapsed, 
                results.stream().filter(Objects::nonNull).count(),
                errors.size());
        
        if (!errors.isEmpty()) {
            log.warn("{} 有 {} 个任务失败: {}", taskName, errors.size(), errors);
        }
        
        // 过滤掉 null 值(失败的任务)
        return results.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    /**
     * 分批并行处理(适用于大数据量)
     *
     * @param items      输入列表
     * @param processor  处理器函数
     * @param executor   线程池
     * @param batchSize  每批大小
     * @param timeout    每批超时时间(秒)
     * @param taskName   任务名称
     * @return 处理结果列表
     */
    public static <T, R> List<R> batchParallelProcess(
            List<T> items,
            Function<T, R> processor,
            ExecutorService executor,
            int batchSize,
            int timeout,
            String taskName) {
        
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }
        
        log.info("开始分批并行处理 {} 个{}, 批次大小: {}", items.size(), taskName, batchSize);
        
        List<R> allResults = new ArrayList<>();
        int totalBatches = (int) Math.ceil((double) items.size() / batchSize);
        
        for (int i = 0; i < totalBatches; i++) {
            int fromIndex = i * batchSize;
            int toIndex = Math.min(fromIndex + batchSize, items.size());
            List<T> batch = items.subList(fromIndex, toIndex);
            
            log.debug("处理批次 {}/{}", i + 1, totalBatches);
            List<R> batchResults = parallelProcess(batch, processor, executor, timeout, 
                    taskName + "-batch-" + (i + 1));
            allResults.addAll(batchResults);
        }
        
        log.info("分批并行处理完成: {}, 总结果数: {}", taskName, allResults.size());
        return allResults;
    }
    
    /**
     * 快速失败并行执行(任何一个失败就取消其他)
     *
     * @param tasks      任务列表
     * @param executor   线程池
     * @param timeout    超时时间(秒)
     * @param taskName   任务名称
     * @return 结果列表
     * @throws RuntimeException 如果任何一个任务失败
     */
    public static <T> List<T> failFastParallelExecute(
            List<Callable<T>> tasks,
            ExecutorService executor,
            int timeout,
            String taskName) {
        
        if (tasks == null || tasks.isEmpty()) {
            return Collections.emptyList();
        }
        
        log.debug("开始快速失败并行执行 {} 个{}", tasks.size(), taskName);
        
        try {
            List<Future<T>> futures = executor.invokeAll(tasks, timeout, TimeUnit.SECONDS);
            List<T> results = new ArrayList<>();
            
            for (int i = 0; i < futures.size(); i++) {
                Future<T> future = futures.get(i);
                if (future.isCancelled()) {
                    throw new RuntimeException(taskName + "[" + i + "] 被取消");
                }
                results.add(future.get());
            }
            
            log.info("快速失败并行执行完成: {}, 结果数: {}", taskName, results.size());
            return results;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(taskName + " 被中断", e);
        } catch (ExecutionException e) {
            throw new RuntimeException(taskName + " 执行失败: " + e.getCause().getMessage(), e);
        }
    }
    
    /**
     * 创建带监控的线程池
     *
     * @param corePoolSize    核心线程数
     * @param maxPoolSize     最大线程数
     * @param queueCapacity   队列容量
     * @param namePrefix      线程名称前缀
     * @return ExecutorService
     */
    public static ExecutorService createMonitoredThreadPool(
            int corePoolSize,
            int maxPoolSize,
            int queueCapacity,
            String namePrefix) {
        
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(queueCapacity);
        
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60L, TimeUnit.SECONDS,
                workQueue,
                ExecutorUtils.createNamedThreadFactory(namePrefix),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        
        // 定期打印线程池状态
        ScheduledExecutorService monitor = Executors.newSingleThreadScheduledExecutor(
                ExecutorUtils.createNamedThreadFactory(namePrefix + "-monitor")
        );
        
        monitor.scheduleAtFixedRate(() -> {
            log.debug("线程池状态 [{}] - 活跃: {}/{}, 队列: {}/{}, 已完成: {}",
                    namePrefix,
                    executor.getActiveCount(),
                    executor.getPoolSize(),
                    workQueue.size(),
                    queueCapacity,
                    executor.getCompletedTaskCount());
        }, 30, 30, TimeUnit.SECONDS);
        
        // 添加关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            monitor.shutdown();
            ExecutorUtils.safeShutdown(executor, 10, namePrefix);
        }));
        
        log.info("已创建监控线程池: {}, 核心: {}, 最大: {}, 队列: {}", 
                namePrefix, corePoolSize, maxPoolSize, queueCapacity);
        
        return executor;
    }
    
    /**
     * 计算最佳线程池大小(CPU密集型)
     *
     * @return 推荐的线程池大小
     */
    public static int optimalCpuIntensivePoolSize() {
        int processors = Runtime.getRuntime().availableProcessors();
        return processors;
    }
    
    /**
     * 计算最佳线程池大小(IO密集型)
     *
     * @param blockingCoefficient IO阻塞系数(0-1之间,典型值0.9)
     * @return 推荐的线程池大小
     */
    public static int optimalIoIntensivePoolSize(double blockingCoefficient) {
        int processors = Runtime.getRuntime().availableProcessors();
        return (int) Math.ceil(processors / (1 - blockingCoefficient));
    }
}
