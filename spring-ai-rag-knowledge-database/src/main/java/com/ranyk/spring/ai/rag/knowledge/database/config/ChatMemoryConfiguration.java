package com.ranyk.spring.ai.rag.knowledge.database.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * CLASS_NAME: ChatMemoryConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: ChatClient 的会话记忆存储对象配置类 - 用于 Agent 多轮对话上下文管理
 *              当前使用 Spring AI 自动配置的 InMemoryChatMemoryRepository，无需手动创建 Bean
 * @date: 2026-06-25
 */
@Slf4j
@Configuration
public class ChatMemoryConfiguration {
    // Spring AI 会自动配置 InMemoryChatMemoryRepository 和 ChatMemory
    // 如需自定义，可在此处添加 Bean 定义
}
