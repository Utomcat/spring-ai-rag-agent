package com.ranyk.spring.ai.rag.llm.config;

import com.ranyk.spring.ai.rag.llm.config.properties.MultiModelProperties;
import com.ranyk.spring.ai.rag.llm.router.ModelRouter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * CLASS_NAME: LlmModelConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: LLM 模型配置类
 * @date: 2026-07-21
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(value = {MultiModelProperties.class})
@Import(value = {OllamaConfiguration.class, OpenAiConfiguration.class})
public class LlmModelConfiguration {

    /**
     * 创建独立模型路由器 - 供 Service 层调用, 实现模型选择与工具选择联动
     *
     * @param openAiChatModel      路由模型（用于分析请求意图）
     * @param workerModels         Worker 模型 Map 集合
     * @param multiModelProperties 多模型配置属性
     * @return ModelRouter 实例
     */
    @Bean(name = "modelRouter")
    public ModelRouter modelRouter(
            @Qualifier("openAiChatModel") OpenAiChatModel openAiChatModel,
            @Qualifier("openAiWorkerChatModels") Map<String, ChatModel> workerModels,
            MultiModelProperties multiModelProperties) {
        log.debug("================================= 创建 ModelRouter start =================================");
        log.info("ModelRouter 初始化完成, Worker 模型: {}", workerModels.keySet());
        if (Objects.nonNull(multiModelProperties) && !multiModelProperties.getModels().isEmpty()) {
            multiModelProperties.getModels().forEach(modelConfig -> {
                if (Objects.isNull(modelConfig.getSystemPrompt())) {
                    modelConfig.setSystemPrompt(modelConfig.getDescription());
                }
            });
        }
        log.debug("================================= 创建 ModelRouter end   =================================");
        return new ModelRouter(openAiChatModel, workerModels, multiModelProperties);
    }
}
