package com.ranyk.spring.ai.rag.base.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * CLASS_NAME: VirtualThreadConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Java21 虚拟线程配置类
 * @date: 2026-06-24
 */
@Slf4j
@Configuration
public class VirtualThreadConfiguration {

    /**
     * 创建一个虚拟线程调度对象
     *
     * @return 虚拟线程调度对象, {@link ScheduledExecutorService}
     */
    @Bean(name = "virtualThreadScheduler")
    public ScheduledExecutorService virtualThreadScheduler() {
        log.debug("=============================== 虚拟线程调度器 Bean 对象 start ====================================");
        log.debug("正在创建虚拟线程调度器 ScheduledExecutorService Bean, 其 name 为 virtualThreadScheduler ...");
        log.debug("=============================== 虚拟线程调度器 Bean 对象 end ======================================");
        return Executors.newSingleThreadScheduledExecutor(
            Thread.ofVirtual()
                .name("virtual-scheduler-", 0)
                .uncaughtExceptionHandler((t, e) -> log.error("虚拟线程调度器 [{}] 发生未捕获异常: {}", t.getName(), e.getMessage(), e))
                .factory()
        );
    }

    /**
     * 创建一个虚拟线程执行器
     *
     * @return 虚拟线程执行器对象, {@link ExecutorService}
     */
    @Bean(name = "virtualThreadExecutor")
    public ExecutorService virtualThreadExecutor() {
        log.debug("=============================== 虚拟线程执行器 Bean 对象 start ====================================");
        log.debug("正在创建虚拟线程执行器 ExecutorService Bean, 其 name 为 virtualThreadExecutor ...");
        log.debug("=============================== 虚拟线程执行器 Bean 对象 end ======================================");
        return Executors.newThreadPerTaskExecutor(
            Thread.ofVirtual()
                .name("virtual-worker-", 0)
                .uncaughtExceptionHandler((t, e) -> log.error("虚拟线程执行器 [{}] 发生未捕获异常: {}", t.getName(), e.getMessage(), e))
                .factory()
        );
    }
}
