package com.ranyk.spring.ai.rag.task.config;

import com.ranyk.spring.ai.rag.task.service.DelayedTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

/**
 * CLASS_NAME: TaskConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 任务配置类
 * @date: 2026-07-14
 */
@Slf4j
@Configuration
public class TaskConfiguration {

    /**
     * 创建延迟任务服务
     *
     * @param virtualThreadScheduler 虚拟线程调度器
     * @param virtualThreadExecutor  虚拟线程执行器
     * @return DelayedTaskService 延迟任务服务
     */
    @Bean
    public DelayedTaskService delayedTaskService(ScheduledExecutorService virtualThreadScheduler,
                                                 ExecutorService virtualThreadExecutor) {
        log.debug("========================== 创建 DelayedTaskService Bean 对象 start ====================================");
        log.debug("创建 DelayedTaskService Bean 中 ... ");
        log.debug("========================== 创建 DelayedTaskService Bean 对象 end   =================================");
        return new DelayedTaskService(virtualThreadScheduler, virtualThreadExecutor);
    }

}
