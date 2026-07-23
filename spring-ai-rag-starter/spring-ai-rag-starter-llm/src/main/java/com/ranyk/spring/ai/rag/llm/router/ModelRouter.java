package com.ranyk.spring.ai.rag.llm.router;

import com.ranyk.spring.ai.rag.llm.config.properties.MultiModelProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.*;
import java.util.stream.Collectors;

/**
 * CLASS_NAME: ModelRouter.java
 *
 * @author ranyk
 * @version V1.0
 * @description: LLM 模型路由器 - 独立于 ChatModel 层, 提供 Service 层调用进行模型选择
 * @date: 2026-07-23
 */
@Slf4j
public class ModelRouter {
    /**
     * 默认模型名称
     */
    private static final String DEFAULT_MODEL_NAME = "openai_chat_model";
    /**
     * 模型路由系统提示
     */
    private static final String ROUTER_SYSTEM_PROMPT = """
            你是一个模型路由分类器。根据用户的请求内容，判断应该由哪个模型来处理。
            
            可用的模型及其描述：
            %s
            
            请只返回最合适的模型名称, 不要返回其他任何内容. 也不要有额外文字, 如果没有匹配到合适模型, 请返回默认模型: %s
            如果一个模型无法进行处理, 请再使用配置好的其他相同类型的模型进行处理, 最终无法处理时, 请返回默认模型: %s
            """;
    /**
     * 模型路由模型
     */
    private final ChatModel routerModel;
    /**
     * 工作模型 Map 集合
     */
    private final Map<String, ChatModel> workerModels;
    /**
     * 模型配置 Map 集合 - key: 模型名称, value: 模型配置
     */
    private final Map<String, MultiModelProperties.ModelConfig> modelConfigMap;
    /**
     * 模型描述 Map 集合 - key: 模型名称, value: 模型描述
     */
    private final Map<String, String> modelDescriptions;

    /**
     * 构造函数
     *
     * @param routerModel          模型路由模型
     * @param workerModels         工作模型 Map 集合
     * @param multiModelProperties 模型配置属性
     */
    public ModelRouter(ChatModel routerModel,
                       Map<String, ChatModel> workerModels,
                       MultiModelProperties multiModelProperties) {
        // 初始化路由模型, 由 Spring AI 自动配置生成
        this.routerModel = routerModel;
        // 初始化工作模型 Map 集合, 如果未传入工作模型, 则初始化工作模型 Map 集合, 传入工作模型 Map 集合, 则直接使用
        if (Objects.isNull(workerModels)) {
            this.workerModels = new LinkedHashMap<>();
        } else {
            this.workerModels = workerModels;
        }
        // 向工作模型中添加 openai_chat_model 模型, 后续用此模型作为默认模型, 该模型即为路由模型
        this.workerModels.put(DEFAULT_MODEL_NAME, routerModel);
        // 初始化模型配置 Map 集合, key: 模型名称, value: 模型配置
        this.modelConfigMap = new LinkedHashMap<>();
        // 如果传入了模型配置属性, 则遍历模型配置属性, 将模型名称和模型配置存入模型配置 Map 集合
        if (Objects.nonNull(multiModelProperties) && !multiModelProperties.getModels().isEmpty()) {
            multiModelProperties.getModels().forEach(model -> this.modelConfigMap.put(model.getName().toLowerCase(), model));
        }
        // 初始化模型描述 Map 集合, key: 模型名称, value: 模型描述
        this.modelDescriptions = modelConfigMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getDescription()));
        this.modelDescriptions.put(DEFAULT_MODEL_NAME, "默认模型");

    }

    /**
     * 根据用户消息路由选择模型
     *
     * @param userMessage 用户消息内容
     * @return 选中的模型名称（小写）
     */
    public String route(String userMessage) {
        try {
            // 构建模型描述字符串
            String modelDescriptionsStr = modelDescriptions.entrySet().stream()
                    .map(e -> "- " + e.getKey() + (e.getValue().isEmpty() ? "" : ": " + e.getValue()))
                    .collect(Collectors.joining("\n"));
            // 构建系统提示消息
            String systemPromptContent = String.format(ROUTER_SYSTEM_PROMPT, modelDescriptionsStr, DEFAULT_MODEL_NAME, DEFAULT_MODEL_NAME);
            log.info("路由模型的系统提示内容为: {}", systemPromptContent);
            SystemMessage systemMessage = new SystemMessage(systemPromptContent);
            // 构建用户消息
            UserMessage userMsg = new UserMessage(userMessage);
            // 构建路由提示
            Prompt routerPrompt = new Prompt(List.of(systemMessage, userMsg));
            // 调用路由器模型获取响应
            ChatResponse routerResponse = routerModel.call(routerPrompt);
            // 获取路由器模型的输出结果
            String selectedModel = Objects.requireNonNull(
                    Objects.requireNonNull(routerResponse.getResult()).getOutput().getText()
            ).trim().toLowerCase();
            log.info("路由器模型返回: {}", selectedModel);
            // 查找匹配的模型名称
            String matchedModel = workerModels.keySet().stream()
                    // 过滤出匹配的模型名称
                    .filter(modelName -> selectedModel.contains(modelName.toLowerCase()))
                    // 取第一个匹配的模型名称
                    .findFirst()
                    // 如果没有匹配到则返回默认模型名称
                    .orElse(DEFAULT_MODEL_NAME);
            log.info("路由模型调用后返回的模型名称为: {} , 使用的模型为: {}", selectedModel, matchedModel);
            return matchedModel;
        } catch (Exception e) {
            log.error("模型路由分类失败，使用默认模型: {}", DEFAULT_MODEL_NAME, e);
            return DEFAULT_MODEL_NAME;
        }
    }

    /**
     * 根据模型名称从工作模型 Map 集合中获取对应的 ChatModel 实例
     *
     * @param modelName 模型名称
     * @return 对应的 ChatModel 实例，如果不存在则返回默认模型实例
     */
    public ChatModel getSelectedModel(String modelName) {
        ChatModel model = workerModels.get(modelName);
        if (Objects.isNull(model)) {
            log.warn("未知模型: {}, 使用默认模型: {}", modelName, DEFAULT_MODEL_NAME);
            return workerModels.get(DEFAULT_MODEL_NAME);
        }
        return model;
    }

    /**
     * 根据模型名称获取模型配置
     *
     * @param modelName 模型名称
     * @return 模型配置对象 {@link MultiModelProperties.ModelConfig}
     */
    public MultiModelProperties.ModelConfig getModelConfig(String modelName) {
        MultiModelProperties.ModelConfig modelConfig = modelConfigMap.get(modelName);
        if (Objects.isNull(modelConfig) && isDefaultModel(modelName)) {
            Set<String> tools = modelConfigMap.values().stream()
                    .filter(Objects::nonNull)
                    .flatMap(item -> item.getTools().stream().filter(Objects::nonNull))
                    .collect(Collectors.toSet());
            modelConfig = new MultiModelProperties.ModelConfig();
            modelConfig.setName(DEFAULT_MODEL_NAME);
            modelConfig.setTimeout(120L);
            modelConfig.setTools(tools.stream().toList());
            modelConfig.setMcpEnabled(false);
            modelConfig.setSkillEnabled(false);
            modelConfig.setSystemPromptEnabled(true);
            modelConfigMap.put(DEFAULT_MODEL_NAME, modelConfig);
        }
        return modelConfig;
    }

    /**
     * 判断给定的模型名称是否为默认模型名称
     *
     * @param modelName 模型名称
     * @return 如果给定的模型名称与默认模型名称相同，则返回 true，否则返回 false
     */
    public boolean isDefaultModel(String modelName) {
        return Objects.equals(DEFAULT_MODEL_NAME, modelName.toLowerCase());
    }
}
