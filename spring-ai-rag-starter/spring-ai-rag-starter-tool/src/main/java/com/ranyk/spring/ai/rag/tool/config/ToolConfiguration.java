package com.ranyk.spring.ai.rag.tool.config;

import com.ranyk.spring.ai.rag.tool.ai.tools.KnowledgeRetrievalToolFunction;
import com.ranyk.spring.ai.rag.tool.ai.tools.WeatherForLocationToolFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

/**
 * CLASS_NAME: ToolConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Spring AI 工具配置类
 * @date: 2026-07-11
 */
@Slf4j
@Configuration
public class ToolConfiguration {

    /**
     * 创建知识检索工具函数
     *
     * @param redisVectorStore redis向量存储对象
     * @param objectMapper     对象转换器
     * @return 知识检索工具函数
     */
    @Bean
    public KnowledgeRetrievalToolFunction knowledgeRetrievalToolFunction(RedisVectorStore redisVectorStore,
                                                                         @Qualifier("objectMapper") ObjectMapper objectMapper) {
        log.debug("================================= 创建知识检索工具函数 KnowledgeRetrievalToolFunction start ============");
        log.debug("创建知识检索工具函数 KnowledgeRetrievalToolFunction Bean 中 ... ");
        log.debug("================================= 创建知识检索工具函数 KnowledgeRetrievalToolFunction end   ============");
        return new KnowledgeRetrievalToolFunction(redisVectorStore, objectMapper);
    }

    /**
     * 创建天气查询工具函数
     *
     * @param objectMapper 对象转换器
     * @return 天气查询工具函数
     */
    @Bean
    public WeatherForLocationToolFunction weatherForLocationToolFunction(@Qualifier("objectMapper") ObjectMapper objectMapper) {
        log.debug("================================= 创建天气查询工具函数 WeatherForLocationToolFunction start ============");
        log.debug("创建天气查询工具函数 WeatherForLocationToolFunction Bean 中 ... ");
        log.debug("================================= 创建天气查询工具函数 WeatherForLocationToolFunction end   ============");
        return new WeatherForLocationToolFunction(objectMapper);
    }
}
