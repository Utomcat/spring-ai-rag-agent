package com.ranyk.spring.ai.rag.llm.config;

import com.ranyk.spring.ai.rag.common.constant.AiTypeEnum;
import com.ranyk.spring.ai.rag.llm.config.properties.MultiModelProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * CLASS_NAME: OpenAiConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: OpenAI 模型配置类 - 当需要进行自定义属性配置时, 在此类中进行处理, 当前使用自动配置, 暂无其他额外配置
 * @date: 2026-07-11
 */
@Slf4j
@Configuration
public class OpenAiConfiguration {

    /**
     * 创建 OpenAI 多模型 Map 集合
     *
     * @param multiModelProperties 多模型配置属性
     * @return 模型实例 Map 集合
     */
    @Bean(name = "openAiWorkerChatModels")
    @ConditionalOnProperty(prefix = MultiModelProperties.CONFIG_PREFIX, name = "enabled", havingValue = "true")
    public Map<String, ChatModel> openAiWorkerChatModels(OpenAiChatModel openAiChatModel, MultiModelProperties multiModelProperties) {
        log.debug("================================= 创建 OpenAI Worker 模型实例 start =================================");
        // 初始化用来存放模型示例的 Map 集合
        Map<String, ChatModel> models = new LinkedHashMap<>();
        // 判断模型配置属性是否为空, 且不为空时, 才进行模型实例创建
        if (Objects.nonNull(multiModelProperties.getModels()) && !multiModelProperties.getModels().isEmpty()) {
            // 遍历模型配置属性, 创建模型实例并存入 Map 集合
            multiModelProperties.getModels().forEach(config -> {
                if (Objects.equals(AiTypeEnum.valueOfDesc(config.getType()), AiTypeEnum.OPENAI)) {
                    log.info("注册 Worker 模型 => name: {} , type: {} , model: {} , baseUrl: {}", config.getName(), config.getType(), config.getModel(), config.getBaseUrl());
                    var chatModel = OpenAiChatModel.builder()
                            .options(OpenAiChatOptions.builder()
                                    .apiKey(config.getApiKey())
                                    .model(config.getModel())
                                    .baseUrl(config.getBaseUrl())
                                    .timeout(Duration.ofSeconds(config.getTimeout()))
                                    .build())
                            .build();
                    models.put(config.getName().toLowerCase(), chatModel);
                } else {
                    log.warn("当前模型类型暂不支持使用 OpenAI 模型注册");
                }
            });
        }
        // 判断 Map 集合中是否已经存在 openAiChatModel, 不存在时才进行添加
        if (!models.containsKey("openai_chat_model")) {
            log.info("注册 OpenAI 模型 => name: openai_chat_model , type: openai , model: openai_chat_model , baseUrl: {}", openAiChatModel.getOptions().getBaseUrl());
            models.put("openai_chat_model", openAiChatModel);
        }
        log.debug("================================= 创建 OpenAI Worker 模型实例 end   =================================");
        return models;
    }
}
