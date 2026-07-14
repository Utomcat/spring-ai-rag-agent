package com.ranyk.spring.ai.rag.agent.config;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.ranyk.spring.ai.rag.agent.advisor.CustomSimpleLoggerAdvisor;
import com.ranyk.spring.ai.rag.agent.advisor.ReferenceExtractAdvisor;
import com.ranyk.spring.ai.rag.base.config.properties.SystemProperties;
import com.ranyk.spring.ai.rag.tool.ai.tools.KnowledgeRetrievalToolFunction;
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
     * 创建 ReactAgent 对象, 注册全局的 Agent 对象, 使用 Spring AI Alibaba
     *
     * @param systemProperties 系统属性对象
     * @param openAiChatModel  OpenAiChatModel 对象
     * @return ReactAgent 对象
     */
    @Bean
    public ReactAgent reactAgent(SystemProperties systemProperties,
                                 OpenAiChatModel openAiChatModel,
                                 MemorySaver memorySaver,
                                 ToolCallback weatherForLocationToolCallBack) {
        log.info("创建 ReactAgent 对象, 注册全局的 Agent 对象, 使用 Spring AI Alibaba");
        return ReactAgent.builder()
                // 设置 ReactAgent 对象的名称
                .name(systemProperties.getAgentName())
                // 设置 ReactAgent 对象的模型
                .model(openAiChatModel)
                // 设置 ReactAgent 对象的系统提示词
                .systemPrompt(systemProperties.getSystemPrompt())
                // 设置 ReactAgent 对象的工具
                .tools(weatherForLocationToolCallBack)
                // 设置 ReactAgent 对象的输出类型
                // .outputType(String.class)
                // 设置 ReactAgent 对象的检查点保存器
                .saver(memorySaver)
                .build();
    }

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
            @Lazy KnowledgeRetrievalToolFunction knowledgeRetrievalToolFunction
    ) {
        log.debug("================================= 创建 ChatClient 对象 start =================================");
        log.debug("正在创建 ChatClient, 当前使用 OpenAI 方式创建 ChatClient 对象 ...");
        log.debug("当前 ChatClient 集成了 Agent 能力：知识库检索工具、引用提取 Advisor ...");
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
                        mcpToolCallbackProvider
                )
                // 构建 ChatClient 对象
                .build();
    }

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
