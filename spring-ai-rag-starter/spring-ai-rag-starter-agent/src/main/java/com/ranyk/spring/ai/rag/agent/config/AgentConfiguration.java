package com.ranyk.spring.ai.rag.agent.config;

import com.ranyk.spring.ai.rag.agent.advisor.CustomSimpleLoggerAdvisor;
import com.ranyk.spring.ai.rag.agent.advisor.ReferenceExtractAdvisor;
import com.ranyk.spring.ai.rag.agent.factory.ChatClientFactory;
import com.ranyk.spring.ai.rag.base.config.properties.SystemProperties;
import com.ranyk.spring.ai.rag.llm.router.ModelRouter;
import com.ranyk.spring.ai.rag.mcp.config.properties.McpProperties;
import com.ranyk.spring.ai.rag.skill.config.properties.SkillsProperties;
import com.ranyk.spring.ai.rag.tool.ai.tools.KnowledgeRetrievalToolFunction;
import com.ranyk.spring.ai.rag.tool.ai.tools.WeatherForLocationToolFunction;
import com.ranyk.spring.ai.rag.tool.registry.ToolRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import tools.jackson.databind.ObjectMapper;

/**
 * CLASS_NAME: AgentConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Agent 配置类
 * @date: 2026-07-11
 */
@Slf4j
@Configuration
public class AgentConfiguration {

    /**
     * 创建 ChatClientFactory 对象
     *
     * @param simpleLoggerAdvisor       Spring AI 自带简单日志记录顾问
     * @param referenceExtractAdvisor   引用提取顾问
     * @param inMemoryChatMemoryAdvisor 内存聊天记忆顾问
     * @param customSimpleLoggerAdvisor 自定义简单日志记录顾问
     * @param mcpToolCallbackProvider   MCP 工具回调提供器 - 用于处理 MCP 工具调用结果
     * @param skillTool                 技能工具回调 - 用于处理技能工具调用结果
     * @param modelRouter               模型路由器 - 用于根据会话上下文选择合适的模型
     * @param toolRegistry              工具注册表 - 用于注册和管理工具
     * @param mcpProperties             MCP 配置属性 - 用于配置 MCP 相关的参数
     * @param skillsProperties          Skills 配置属性 - 用于配置 Skills 相关的参数
     * @param systemProperties          系统属性配置 - 用于配置系统相关参数
     * @return ChatClientFactory 对象
     */
    @Bean
    public ChatClientFactory chatClientFactory(SimpleLoggerAdvisor simpleLoggerAdvisor,
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
        return new ChatClientFactory(simpleLoggerAdvisor,
                referenceExtractAdvisor,
                inMemoryChatMemoryAdvisor,
                customSimpleLoggerAdvisor,
                mcpToolCallbackProvider,
                skillTool,
                modelRouter,
                toolRegistry,
                mcpProperties,
                skillsProperties,
                systemProperties);
    }

}
