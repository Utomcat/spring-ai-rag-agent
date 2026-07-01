package com.ranyk.spring.ai.rag.knowledge.database.config;

import com.ranyk.spring.ai.rag.knowledge.database.ai.tools.DocumentToolFunction;
import com.ranyk.spring.ai.rag.knowledge.database.service.document.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CLASS_NAME: SpringAiToolConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Spring AI Function Callback 工具类配置
 * @date: 2026-07-02
 */
@Slf4j
@Configuration
public class SpringAiToolConfiguration {

    /**
     * 配置 Spring AI Function Callback 文档处理工具 Bean
     *
     * @param documentService 知识库文档业务逻辑处理类实例对象 {@link DocumentService}
     * @return {@link DocumentToolFunction} 文档相关的 AI 工具类对象
     */
    @Bean
    public DocumentToolFunction documentToolFunction(DocumentService documentService) {
        return new DocumentToolFunction(documentService);
    }
}
