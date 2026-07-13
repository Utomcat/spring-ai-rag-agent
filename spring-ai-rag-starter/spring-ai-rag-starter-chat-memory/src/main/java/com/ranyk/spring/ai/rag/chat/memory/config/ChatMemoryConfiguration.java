package com.ranyk.spring.ai.rag.chat.memory.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CLASS_NAME: ChatMemoryConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 会话聊天短期记忆配置类
 * @date: 2026-07-14
 */
@Slf4j
@Configuration
public class ChatMemoryConfiguration {

    /**
     * 创建 ChatMemory 对象, 用于在 ChatClient 和 ChatModel 之间进行会话记忆
     *
     * @return 返回创建好的 ChatMemory 对象 {@link ChatMemory} 对象
     */
    @Bean
    public ChatMemory inMemoryChatMemory() {
        return MessageWindowChatMemory.builder()
                // 设置最大消息数, 超过指定的消息数, 会自动删除最旧的消息
                .maxMessages(10)
                // 设置聊天内存仓库 - 当前配置为内存存储; 可选项有:
                // InMemoryChatMemoryRepository: 内存存储, 系统默认, 无需额外引入
                // RedisChatMemoryRepository: redis 存储, 需要额外引入依赖: spring-ai-starter-chat-memory-redis  和 spring-boot-starter-data-redis
                // JdbcChatMemoryRepository: jdbc 存储, 需要额外引入依赖: spring-ai-starter-model-chat-memory-repository-jdbc  和 对应的关系型数据库 Java 驱动
                // CassandraChatMemoryRepository: Cassandra 存储, 需要额外引入依赖: spring-ai-starter-model-chat-memory-repository-cassandra  和 Cassandra 的驱动
                // etc.
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .build();
    }

    /**
     * 创建 ChatMemory 顾问对象, 用于在 ChatClient 和 ChatModel 之间进行会话记忆
     *
     * @param inMemoryChatMemory 会话聊天短期记忆对象
     * @return 返回创建好的 ChatMemory 顾问对象 {@link MessageChatMemoryAdvisor} 对象
     */
    @Bean
    public MessageChatMemoryAdvisor inMemoryChatMemoryAdvisor(ChatMemory inMemoryChatMemory) {
        return MessageChatMemoryAdvisor.builder(inMemoryChatMemory).build();
    }
}
