package com.ranyk.spring.ai.rag.mcp.config;

import com.ranyk.spring.ai.rag.mcp.config.properties.McpProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * CLASS_NAME: McpConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: MCP 服务配置类
 * @date: 2026-07-23
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(McpProperties.class)
public class McpConfiguration {
}
