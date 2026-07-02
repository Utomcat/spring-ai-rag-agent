package com.ranyk.spring.ai.rag.knowledge.database.config;

import com.ranyk.spring.ai.rag.knowledge.database.ai.advisor.CustomSimpleLoggerAdvisor;
import com.ranyk.spring.ai.rag.knowledge.database.ai.tools.DocumentToolFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * CLASS_NAME: OpenAiConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: ChatClient 配置类
 * @date: 2026-06-22
 */
@Slf4j
@Configuration
public class ChatClientConfiguration {

    /**
     * 创建一个 ChatClient Bean 对象
     *
     * @param openAiChatModel           ChatModel 对象, 如果未手动进行配置, 则使用的是 Spring AI 自动配置创建的 {@link OpenAiChatModel} 对象, 根据配置文件中配置的 ChatModel
     * @param customSimpleLoggerAdvisor 自定义的简单日志记录顾问对象, {@link CustomSimpleLoggerAdvisor}
     * @param simpleLoggerAdvisor       Spring AI 自带的简单日志记录顾问对象, {@link SimpleLoggerAdvisor}
     * @param documentToolFunction      知识库文档工具函数对象, {@link DocumentToolFunction}
     * @param mcpToolCallbackProvider   Mcp 工具回调提供者对象, {@link SyncMcpToolCallbackProvider}
     * @return 返回一个创建好的 {@link ChatClient} 对象
     */
    @Bean
    public ChatClient chatClient(
            OpenAiChatModel openAiChatModel,
            CustomSimpleLoggerAdvisor customSimpleLoggerAdvisor,
            SimpleLoggerAdvisor simpleLoggerAdvisor,
            @Lazy DocumentToolFunction documentToolFunction,
            @Lazy SyncMcpToolCallbackProvider mcpToolCallbackProvider
    ) {
        log.info("正在创建 ChatClient , 当前 ChatClient 集成了 自定义 Advisor 、Spring AI 自带的 SimpleLoggerAdvisor 、 知识库文档工具、 MCP Server 工具.");
        log.info("注意, 在此处设置了之后， 全局使用的 ChatClient 均会带有设置的 Advisor 、Spring AI 自带的 SimpleLoggerAdvisor 、 知识库文档工具、 MCP Server 工具, 为避免过度配置, 请按需配置!");
        return ChatClient
                // 设置 ChatClient 对象的 ChatModel
                .builder(openAiChatModel)
                // 设置 ChatClient 对象的默认 Advisor 对象, 可设置多个
                .defaultAdvisors(
                        // 自定义简单日志记录顾问
                        customSimpleLoggerAdvisor,
                        // Spring AI 自带简单日志记录顾问
                        simpleLoggerAdvisor
                )
                // 设置 ChatClient 对象的工具, 可以设置多个, 自定义的 Spring AI Function Callback 工具 、 MCP Server 工具均在此处设置
                .defaultTools(
                        // Spring AI Function Callback 工具 - 知识库文档工具
                        documentToolFunction,
                        // MCP Server 工具
                        mcpToolCallbackProvider
                )
                // 构建 ChatClient 对象
                .build();
    }

}
