package com.ranyk.spring.ai.rag.agent.factory;

import com.ranyk.spring.ai.rag.agent.advisor.CustomSimpleLoggerAdvisor;
import com.ranyk.spring.ai.rag.agent.advisor.ReferenceExtractAdvisor;
import com.ranyk.spring.ai.rag.base.config.properties.SystemProperties;
import com.ranyk.spring.ai.rag.llm.config.properties.MultiModelProperties;
import com.ranyk.spring.ai.rag.llm.router.ModelRouter;
import com.ranyk.spring.ai.rag.mcp.config.properties.McpProperties;
import com.ranyk.spring.ai.rag.skill.config.properties.SkillsProperties;
import com.ranyk.spring.ai.rag.tool.facade.BaseTool;
import com.ranyk.spring.ai.rag.tool.registry.ToolRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * CLASS_NAME: ChatClientFactory.java
 *
 * @author ranyk
 * @version V1.0
 * @description: ChatClient 动态工厂 - 根据模型和工具列表动态创建 ChatClient
 * @date: 2026-07-23
 */
@Slf4j
@Component
public class ChatClientFactory {
    /**
     * Spring AI 自带的 - 简单日志记录顾问
     */
    private final SimpleLoggerAdvisor simpleLoggerAdvisor;
    /**
     * 自定义的 知识库 - 引用提取顾问
     */
    private final ReferenceExtractAdvisor referenceExtractAdvisor;
    /**
     * Spring AI 自带的 - 消息聊天内存顾问
     */
    private final MessageChatMemoryAdvisor inMemoryChatMemoryAdvisor;
    /**
     * 自定义的 - 简单日志记录顾问
     */
    private final CustomSimpleLoggerAdvisor customSimpleLoggerAdvisor;
    /**
     * MCP 工具回调提供器 - 用于处理 MCP 工具调用结果
     */
    private final SyncMcpToolCallbackProvider mcpToolCallbackProvider;
    /**
     * 技能工具回调 - 用于处理技能工具调用结果
     */
    private final ToolCallback skillTool;
    /**
     * 模型路由器 - 用于根据会话上下文选择合适的模型
     */
    private final ModelRouter modelRouter;
    /**
     * 工具注册表 - 用于注册和管理工具
     */
    private final ToolRegistry toolRegistry;
    /**
     * MCP 配置属性 - 用于配置 MCP 相关的参数
     */
    private final McpProperties mcpProperties;
    /**
     * Skills 配置属性 - 用于配置 Skills 相关的参数
     */
    private final SkillsProperties skillsProperties;
    /**
     * 系统属性配置 - 用于配置系统相关参数
     */
    private final SystemProperties systemProperties;
    /**
     * 缓存 ChatClient - 用于缓存已创建的 ChatClient 实例, 避免重复创建
     */
    private static final Map<String, ChatClient> CACHE_CHAT_CLIENTS = new ConcurrentHashMap<>();

    /**
     * 构造方法 - 初始化 ChatClientFactory 实例
     * <p>
     * 该方法用于初始化 ChatClientFactory 实例，将系统属性配置、日志记录顾问、引用提取顾问、消息聊天内存顾问和自定义日志记录顾问注入到 ChatClientFactory 中。
     *
     * @param simpleLoggerAdvisor       Spring AI 自带的 - 简单日志记录顾问
     * @param referenceExtractAdvisor   自定义的 知识库 - 引用提取顾问
     * @param inMemoryChatMemoryAdvisor Spring AI 自带的 - 消息聊天内存顾问
     * @param customSimpleLoggerAdvisor 自定义的 - 简单日志记录顾问
     * @param mcpToolCallbackProvider   MCP 工具回调提供器 - 用于处理 MCP 工具调用结果
     * @param skillTool                 技能工具回调 - 用于处理技能工具调用结果
     * @param modelRouter               模型路由器 - 用于根据会话上下文选择合适的模型
     * @param toolRegistry              工具注册表 - 用于注册和管理工具
     * @param mcpProperties             MCP 配置属性 - 用于配置 MCP 相关的参数
     * @param skillsProperties          Skills 配置属性 - 用于配置 Skills 相关的参数
     * @param systemProperties          系统属性配置 - 用于配置系统相关参数
     */
    public ChatClientFactory(SimpleLoggerAdvisor simpleLoggerAdvisor,
                             ReferenceExtractAdvisor referenceExtractAdvisor,
                             MessageChatMemoryAdvisor inMemoryChatMemoryAdvisor,
                             CustomSimpleLoggerAdvisor customSimpleLoggerAdvisor,
                             SyncMcpToolCallbackProvider mcpToolCallbackProvider,
                             ToolCallback skillTool,
                             ModelRouter modelRouter,
                             ToolRegistry toolRegistry,
                             McpProperties mcpProperties,
                             SkillsProperties skillsProperties,
                             SystemProperties systemProperties) {
        this.simpleLoggerAdvisor = simpleLoggerAdvisor;
        this.referenceExtractAdvisor = referenceExtractAdvisor;
        this.inMemoryChatMemoryAdvisor = inMemoryChatMemoryAdvisor;
        this.customSimpleLoggerAdvisor = customSimpleLoggerAdvisor;
        this.mcpToolCallbackProvider = mcpToolCallbackProvider;
        this.skillTool = skillTool;
        this.modelRouter = modelRouter;
        this.toolRegistry = toolRegistry;
        this.mcpProperties = mcpProperties;
        this.skillsProperties = skillsProperties;
        this.systemProperties = systemProperties;
    }

    /**
     * 根据指定的 ChatModel 和工具列表创建 ChatClient
     *
     * @param selectedModelName 选中的模型名称
     * @return ChatClient 实例
     */
    public ChatClient create(String selectedModelName) {
        log.debug("缓存中暂无指定的 ChatClient, 动态创建 ChatClient, 模型: {} 并将其缓存进 CACHE_CHAT_CLIENTS 中", selectedModelName);
        // 1. 获取目标 ChatModel 实例
        ChatModel selectedModel = modelRouter.getSelectedModel(selectedModelName);
        StringBuilder appendSystemPrompt = new StringBuilder();
        // 2. 获取模型配置
        MultiModelProperties.ModelConfig config = modelRouter.getModelConfig(selectedModelName);
        // 3. 根据模型配置选择工具
        List<BaseTool> tools = new LinkedList<>();
        // 3.1 从 ToolRegistry 获取该模型配置的 Function Callback 工具
        if (Objects.nonNull(config) && Objects.nonNull(config.getTools()) && !config.getTools().isEmpty()) {
            List<BaseTool> registeredTools = toolRegistry.getTools(config.getTools());
            tools.addAll(registeredTools);
            appendSystemPrompt.append(registeredTools.stream().map(BaseTool::getDescription).collect(Collectors.joining("\n")));
            appendSystemPrompt.append("\n");
            log.debug("模型 {} 配置的 Function 工具: {}", selectedModelName, config.getTools());
        }
        // 3.2 条件添加 MCP 工具
        List<Object> mcpProviders = new LinkedList<>();
        if (Objects.nonNull(config) && Boolean.TRUE.equals(config.getMcpEnabled()) && Objects.nonNull(mcpToolCallbackProvider)) {
            mcpProviders.add(mcpToolCallbackProvider);
            log.debug("模型 {} 启用 MCP 工具", selectedModelName);
            appendSystemPrompt.append(mcpProperties.getDescriptions().stream().map(item -> "- " + item).collect(Collectors.joining("\n")));
            appendSystemPrompt.append("\n");
        }
        // 4. 条件添加 Skills 技能
        List<Object> skillsProviders = new LinkedList<>();
        if (Objects.nonNull(config) && Boolean.TRUE.equals(config.getSkillEnabled()) && Objects.nonNull(skillTool)) {
            skillsProviders.add(skillTool);
            log.debug("模型 {} 启用 Skills 技能", selectedModelName);
            appendSystemPrompt.append(skillsProperties.getDescriptions().stream().map(item -> "- " + item).collect(Collectors.joining("\n")));
            appendSystemPrompt.append("\n");
        }
        // 5. 构建 ChatClient 实例
        ChatClient.Builder builder = ChatClient.builder(selectedModel)
                .defaultAdvisors(
                        referenceExtractAdvisor,
                        customSimpleLoggerAdvisor,
                        simpleLoggerAdvisor,
                        inMemoryChatMemoryAdvisor
                );
        // 6. 设置 ChatClient.Builder 的默认系统提示词
        if ((Objects.isNull(config) && modelRouter.isDefaultModel(selectedModelName)) || config.getSystemPromptEnabled()) {
            String systemPrompt = String.format(systemProperties.getSystemPrompt(), appendSystemPrompt);
            log.info("模型 {} 配置的系统提示词内容为: {}", selectedModelName, systemPrompt);
            builder.defaultSystem(systemPrompt);
        }
        // 7. 创建 ChatClient 实例的默认工具列表
        List<Object> defaultTools = new LinkedList<>();
        // 7.1 添加 Function Callback 工具
        if (!tools.isEmpty()) {
            defaultTools.addAll(tools);
        }
        // 7.2 添加 MCP 工具
        if (!mcpProviders.isEmpty()) {
            defaultTools.addAll(mcpProviders);
        }
        // 7.3 添加 Skills 技能
        if (!skillsProviders.isEmpty()) {
            defaultTools.addAll(skillsProviders);
        }
        // 8. 设置 ChatClient.Builder 的默认工具列表
        builder.defaultTools(defaultTools.toArray());
        // 9. 构建 ChatClient 实例
        ChatClient chatClient = builder.build();
        // 10. 缓存 ChatClient 实例
        CACHE_CHAT_CLIENTS.put(selectedModelName, chatClient);
        // 11. 返回 ChatClient 实例
        return chatClient;
    }

    /**
     * 根据用户消息获取 ChatClient
     *
     * @param userMessage 用户消息
     * @return ChatClient 实例
     */
    public synchronized ChatClient getChatClient(String userMessage) {
        // 1. 通过模型路由对象选择模型
        String selectedModelName = modelRouter.route(userMessage);
        // 2. 从缓存中获取 ChatClient 实例
        ChatClient chatClient = CACHE_CHAT_CLIENTS.get(selectedModelName);
        // 3. 如果缓存中不存在该模型的 ChatClient 实例，则创建新的 ChatClient 实例并缓存进 CACHE_CHAT_CLIENTS 中
        if (Objects.isNull(chatClient)) {
            // 3.1 检查缓存中是否已存在该模型的 ChatClient 实例
            synchronized (this) {
                // 3.1.1 检查缓存中是否已存在该模型的 ChatClient 实例
                if (Objects.isNull(CACHE_CHAT_CLIENTS.get(selectedModelName))) {
                    log.info("缓存中暂无指定的 ChatClient, 动态创建 ChatClient, 模型: {} , 创建完成后缓存进 CACHE_CHAT_CLIENTS 中", selectedModelName);
                    // 3.1.2 创建新的 ChatClient 实例并缓存进 CACHE_CHAT_CLIENTS 中
                    return create(selectedModelName);
                }
            }
        }
        log.info("从缓存中获取 ChatClient 实例: {}", selectedModelName);
        return chatClient;
    }

}
