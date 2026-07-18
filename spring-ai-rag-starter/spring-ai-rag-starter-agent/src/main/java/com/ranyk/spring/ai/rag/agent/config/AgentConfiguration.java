package com.ranyk.spring.ai.rag.agent.config;

import com.ranyk.spring.ai.rag.agent.advisor.CustomSimpleLoggerAdvisor;
import com.ranyk.spring.ai.rag.agent.advisor.ReferenceExtractAdvisor;
import com.ranyk.spring.ai.rag.base.config.properties.SystemProperties;
import com.ranyk.spring.ai.rag.tool.ai.tools.KnowledgeRetrievalToolFunction;
import com.ranyk.spring.ai.rag.tool.ai.tools.WeatherForLocationToolFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Qualifier;
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
     * 创建 ChatClient 对象, 注册全局的 ChatClient 对象, 使用 Open AI 方式
     *
     * @param openAiChatModel                OpenAiChatModel 对象
     * @param systemProperties               系统属性对象
     * @param simpleLoggerAdvisor            Spring AI 自带简单日志记录顾问
     * @param referenceExtractAdvisor        引用提取顾问
     * @param inMemoryChatMemoryAdvisor      内存聊天记忆顾问
     * @param customSimpleLoggerAdvisor      自定义简单日志记录顾问
     * @param mcpToolCallbackProvider        MCP Server 回调提供者
     * @param knowledgeRetrievalToolFunction 知识库检索工具函数
     * @param weatherForLocationToolFunction 天气查询工具函数
     * @param skillTool                      SkillsTool 工具回调 - 用于加载 SKILL.md 技能
     * @return ChatClient 对象
     */
    @Bean
    public ChatClient chatClient(
            OpenAiChatModel openAiChatModel,
            @Lazy SystemProperties systemProperties,
            @Lazy SimpleLoggerAdvisor simpleLoggerAdvisor,
            @Lazy ReferenceExtractAdvisor referenceExtractAdvisor,
            MessageChatMemoryAdvisor inMemoryChatMemoryAdvisor,
            @Lazy CustomSimpleLoggerAdvisor customSimpleLoggerAdvisor,
            @Lazy SyncMcpToolCallbackProvider mcpToolCallbackProvider,
            @Lazy KnowledgeRetrievalToolFunction knowledgeRetrievalToolFunction,
            @Lazy WeatherForLocationToolFunction weatherForLocationToolFunction,
            @Lazy ToolCallback skillTool
    ) {
        log.debug("================================= 创建 ChatClient 对象 start =================================");
        log.debug("正在创建 ChatClient, 当前使用 OpenAI 方式创建 ChatClient 对象 ...");
        log.debug("当前 ChatClient 集成了 Agent 能力：知识库检索工具、引用提取 Advisor 、天气查询工具、SkillsTool 技能 ...");
        log.debug("知识库文档工具、MCP Server 工具 这两个工具在实际使用时再主动添加, 当前暂不配置为默认 Tools 和 Advisor ...");
        log.debug("注意, 在此处设置了之后, 全局使用的 ChatClient 均会带有设置的 Advisor、工具, 为避免过度配置, 请按需配置! ");
        log.debug("================================ 创建 ChatClient 对象 end   =================================");
        return ChatClient
                // 设置 ChatClient 对象的 ChatModel
                .builder(openAiChatModel)
                // 设置 ChatClient 对象的默认系统提示词
                .defaultSystem(systemProperties.getSystemPrompt())
                // 设置 ChatClient 对象的默认 Advisor 对象，可设置多个（顺序很重要）
                .defaultAdvisors(
                        // 引用提取 Advisor - 拦截工具调用结果提取 references
                        referenceExtractAdvisor,
                        // 自定义简单日志记录顾问
                        customSimpleLoggerAdvisor,
                        // Spring AI 自带简单日志记录顾问
                        simpleLoggerAdvisor,
                        // 内存聊天记忆顾问 - 用于短期记忆
                        inMemoryChatMemoryAdvisor
                )
                // 设置 ChatClient 对象的工具，可以设置多个，自定义的 Spring AI Function Callback 工具、MCP Server 工具均在此处设置
                .defaultTools(
                        // 知识库检索工具 - 供 Agent 自主调用进行向量检索
                        knowledgeRetrievalToolFunction,
                        // MCP Server 工具
                        mcpToolCallbackProvider,
                        // 天气查询工具 - 供 Agent 自主调用进行天气查询
                        weatherForLocationToolFunction,
                        // SkillsTool - 供 Agent 自主发现和加载 SKILL.md 技能
                        skillTool
                )
                // 构建 ChatClient 对象
                .build();
    }

}
