package com.ranyk.spring.ai.rag.knowledge.database.service.task;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * CLASS_NAME: DelayedTaskService.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 延时任务 Service 业务逻辑类
 * @date: 2026-07-02
 */
@Slf4j
@Service
@SuppressWarnings("unused")
public class DelayedTaskService {

    /**
     * 虚拟线程定时任务调度器对象
     */
    private final ScheduledExecutorService scheduler;
    /**
     * 虚拟线程执行器对象
     */
    private final ExecutorService virtualThreadExecutor;

    /**
     * 构造方法 - 向 Spring IOC 容器中注册 DelayedTaskService 类型的 Bean, 同时通过 DI 进行依赖注入相关对象
     *
     * @param scheduler             虚拟线程定时任务调度器对象
     * @param virtualThreadExecutor 虚拟线程执行器对象
     */
    @Autowired
    public DelayedTaskService(@Qualifier("virtualThreadScheduler") ScheduledExecutorService scheduler,
                              @Qualifier("virtualThreadExecutor") ExecutorService virtualThreadExecutor) {
        this.scheduler = scheduler;
        this.virtualThreadExecutor = virtualThreadExecutor;
    }

    /**
     * 延迟指定时间后执行任务（无返回值）
     *
     * @param task  要执行的任务
     * @param delay 延迟时间
     * @param unit  时间单位
     * @return ScheduledFuture 可用于取消任务
     */
    public ScheduledFuture<?> executeAfterDelay(Runnable task, long delay, TimeUnit unit) {
        log.info("调度延迟任务: 延迟 {} {}", delay, unit);

        return scheduler.schedule(() -> virtualThreadExecutor.execute(() -> {
            try {
                log.info("[{}] 开始执行延迟任务", LocalDateTime.now());
                task.run();
                log.info("[{}] 延迟任务执行完成", LocalDateTime.now());
            } catch (Exception e) {
                log.error("延迟任务执行失败: {}", e.getMessage(), e);
            }
        }), delay, unit);
    }

    /**
     * 延迟指定时间后执行任务（带返回值）
     *
     * @param task  要执行的任务
     * @param delay 延迟时间
     * @param unit  时间单位
     * @return CompletableFuture 异步返回结果
     */
    public <T> CompletableFuture<T> executeAfterDelayWithResult(Supplier<T> task, long delay, TimeUnit unit) {
        log.info("调度延迟任务(带返回值): 延迟 {} {}", delay, unit);

        CompletableFuture<T> future = new CompletableFuture<>();

        scheduler.schedule(() -> virtualThreadExecutor.execute(() -> {
            try {
                log.info("[{}] 开始执行延迟任务(带返回值)", LocalDateTime.now());
                T result = task.get();
                log.info("[{}] 延迟任务执行完成，结果: {}", LocalDateTime.now(), result);
                future.complete(result);
            } catch (Exception e) {
                log.error("延迟任务执行失败: {}", e.getMessage(), e);
                future.completeExceptionally(e);
            }
        }), delay, unit);

        return future;
    }

    /**
     * 在指定时间点执行任务
     *
     * @param task          要执行的任务
     * @param executionTime 执行时间（毫秒时间戳）
     * @return ScheduledFuture 可用于取消任务
     */
    public ScheduledFuture<?> executeAtTime(Runnable task, long executionTime) {
        long delay = executionTime - System.currentTimeMillis();

        if (delay <= 0) {
            log.info("指定时间已过，立即执行任务");
            virtualThreadExecutor.execute(() -> {
                try {
                    task.run();
                } catch (Exception e) {
                    log.error("任务执行失败: {}", e.getMessage(), e);
                }
            });
            return null;
        }

        log.info("调度定时任务: 在 {} 执行 (延迟 {} 毫秒)", executionTime, delay);
        return executeAfterDelay(task, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 周期性执行任务（固定延迟）
     *
     * @param task   要执行的任务
     * @param initialDelay 初始延迟
     * @param period       周期时间
     * @param unit         时间单位
     * @return ScheduledFuture 可用于取消任务
     */
    public ScheduledFuture<?> executePeriodically(Runnable task, long initialDelay, long period, TimeUnit unit) {
        log.info("调度周期性任务: 初始延迟 {} {}, 周期 {} {}", initialDelay, unit, period, unit);

        return scheduler.scheduleAtFixedRate(() -> virtualThreadExecutor.execute(() -> {
            try {
                log.info("[{}] 执行周期性任务", LocalDateTime.now());
                task.run();
            } catch (Exception e) {
                log.error("周期性任务执行失败: {}", e.getMessage(), e);
            }
        }), initialDelay, period, unit);
    }

    /**
     * 关闭服务
     */
    @PreDestroy
    public void shutdown() {
        log.info("开始关闭虚拟线程相关 Bean , 进行资源释放...");
        scheduler.shutdown();
        virtualThreadExecutor.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                log.warn("虚拟线程定时任务调度器正常关闭失败, 执行强制关闭...");
                scheduler.shutdownNow();
            }
            if (!virtualThreadExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                log.warn("虚拟线程执行器正常关闭失败, 执行强制关闭...");
                virtualThreadExecutor.shutdownNow();
            }
            log.info("虚拟线程相关 Bean 关闭完成, 资源已成功释放.");
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            virtualThreadExecutor.shutdownNow();
            Thread.currentThread().interrupt();
            log.error("关闭虚拟线程相关 Bean 时被中断: ");
            log.error("", e);

        }
    }
}
