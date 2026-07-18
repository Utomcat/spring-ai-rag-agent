package com.ranyk.spring.ai.rag.agent.config;

import com.ranyk.spring.ai.rag.agent.advisor.CustomSimpleLoggerAdvisor;
import com.ranyk.spring.ai.rag.agent.advisor.ReferenceExtractAdvisor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

/**
 * CLASS_NAME: AdvisorConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 自定义 Advisor 配置类
 * @date: 2026-07-17
 */
@Slf4j
@Configuration
public class AdvisorConfiguration {

    /**
     * 创建自定义简单日志记录顾问
     *
     * @return CustomSimpleLoggerAdvisor 对象
     */
    @Bean
    public CustomSimpleLoggerAdvisor customSimpleLoggerAdvisor() {
        log.debug("================================= 创建自定义简单日志记录顾问 start =================================");
        log.debug("正在创建自定义简单日志记录 CustomSimpleLoggerAdvisor Bean ...");
        log.debug("================================= 创建自定义简单日志记录顾问 end   =================================");
        return new CustomSimpleLoggerAdvisor();
    }

    /**
     * 创建 Spring AI 自带简单日志记录顾问
     *
     * @return SimpleLoggerAdvisor 对象
     */
    @Bean
    public SimpleLoggerAdvisor simpleLoggerAdvisor() {
        log.debug("================================= 创建 Spring AI 自带简单日志记录顾问 start =================================");
        log.debug("正在创建 Spring AI 自带简单日志记录 SimpleLoggerAdvisor Bean ...");
        log.debug("================================= 创建 Spring AI 自带简单日志记录顾问 end   =================================");
        return new SimpleLoggerAdvisor();
    }

    /**
     * 创建引用提取顾问
     *
     * @param objectMapper ObjectMapper 对象
     * @return ReferenceExtractAdvisor 对象
     */
    @Bean
    public ReferenceExtractAdvisor referenceExtractAdvisor(@Qualifier("objectMapper") ObjectMapper objectMapper) {
        log.debug("================================= 创建引用提取顾问 start =================================");
        log.debug("正在创建引用提取 ReferenceExtractAdvisor Bean ...");
        log.debug("================================= 创建引用提取顾问 end   =================================");
        return new ReferenceExtractAdvisor(objectMapper);
    }

}
