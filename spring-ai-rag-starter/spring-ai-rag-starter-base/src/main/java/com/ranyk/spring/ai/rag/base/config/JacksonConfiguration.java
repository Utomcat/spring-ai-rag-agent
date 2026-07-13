package com.ranyk.spring.ai.rag.base.config;


import com.ranyk.spring.ai.rag.base.config.properties.SystemProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

/**
 * CLASS_NAME: JacksonConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 提供全局 ObjectMapper , 供业务类注入
 * @date: 2026-07-10
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(value = {SystemProperties.class})
public class JacksonConfiguration {

    /**
     * 提供全局 {@link ObjectMapper}，供业务类（如 ChatMessageService）注入, 与 Spring 生态兼容的 JSON 处理器（含 Java 8 时间等模块自动发现）
     *
     * @return 全局 {@link ObjectMapper} 实例
     */
    @Bean
    public ObjectMapper objectMapper() {
        log.debug("=============================== 自定义 ObjectMapper Bean 对象 start ======================================");
        log.debug("正在创建自定义 ObjectMapper Bean ...");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registeredModules();
        log.debug("=============================== 自定义 ObjectMapper Bean 对象 end ======================================");
        return mapper;
    }
}
