package com.ranyk.spring.ai.rag.tool.config;

import cn.hutool.core.util.StrUtil;
import com.ranyk.spring.ai.rag.tool.ai.tools.KnowledgeRetrievalToolFunction;
import com.ranyk.spring.ai.rag.tool.ai.tools.WeatherForLocationToolFunction;
import com.ranyk.spring.ai.rag.tool.config.properties.WeatherApiProperties;
import com.ranyk.spring.ai.rag.tool.domain.bean.WeatherApiDefinitionBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

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
@EnableConfigurationProperties(value = {
        WeatherApiProperties.class
})
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
     * @param objectMapper             对象转换器
     * @param weatherApiDefinitionBean 天气 API 接口调用基础信息 Bean
     * @return 天气查询工具函数
     */
    @Bean
    public WeatherForLocationToolFunction weatherForLocationToolFunction(@Qualifier("objectMapper") ObjectMapper objectMapper,
                                                                         WeatherApiDefinitionBean weatherApiDefinitionBean) {
        log.debug("================================= 创建天气查询工具函数 WeatherForLocationToolFunction start ============");
        log.debug("创建天气查询工具函数 WeatherForLocationToolFunction Bean 中 ... ");
        log.debug("================================= 创建天气查询工具函数 WeatherForLocationToolFunction end   ============");
        return new WeatherForLocationToolFunction(objectMapper, weatherApiDefinitionBean);
    }

    /**
     * 创建天气 API 接口调用基础信息 Bean
     *
     * @param weatherApiProperties 天气 API 属性对象
     * @return 天气 API 接口调用基础信息 Bean
     */
    @Bean
    public WeatherApiDefinitionBean weatherApiDefinitionBean(WeatherApiProperties weatherApiProperties) {
        WeatherApiDefinitionBean weatherApiDefinitionBean;
        log.debug("================================= 创建天气 API 接口调用基础信息 Bean start ============");
        log.debug("创建天气 API 接口调用基础信息 Bean 中 ... ");

        // 获取 weatherApiProperties 对象的 factoryOwners 属性
        List<WeatherApiProperties.FactoryOwner> factoryOwners = weatherApiProperties.getFactoryOwners();
        // 获取 weatherApiProperties 对象的 factoryOwners 属性中 enable 为 true 的对象 然后获取第一个值
        WeatherApiProperties.FactoryOwner factoryOwner = factoryOwners.stream().filter(WeatherApiProperties.FactoryOwner::getEnable).findFirst().orElse(new WeatherApiProperties.FactoryOwner());
        // 构建 WeatherApiDefinitionBean 对象
        weatherApiDefinitionBean = WeatherApiDefinitionBean.builder()
                .name(factoryOwner.getName())
                .enable(factoryOwner.getEnable())
                .baseUrl(factoryOwner.getBaseUrl())
                .apiKey(factoryOwner.getApiKey())
                .alternativeList(factoryOwners.stream()
                        // 过滤掉未启用的
                        .filter(WeatherApiProperties.FactoryOwner::getEnable)
                        // 过滤掉已经使用过的
                        .filter(item -> StrUtil.equals(item.getName(), factoryOwner.getName()))
                        // 遍历构建备用的 List 集合
                        .map(item -> WeatherApiDefinitionBean.builder()
                                .name(item.getName())
                                .enable(item.getEnable())
                                .baseUrl(item.getBaseUrl())
                                .apiKey(item.getApiKey())
                                .build())
                        .toList())
                .build();
        log.info("当前配置的天气查询 API 接口有: ");
        weatherApiProperties.getFactoryOwners().forEach(item -> log.info("配置天气 API 接口厂商: {} ,是否启用: {} , 配置的访问 URL => {} , API KEY => {} ", item.getName(), item.getEnable(), item.getBaseUrl(), item.getApiKey()));
        log.debug("================================= 创建天气 API 接口调用基础信息 Bean end   ============");
        return weatherApiDefinitionBean;
    }
}
