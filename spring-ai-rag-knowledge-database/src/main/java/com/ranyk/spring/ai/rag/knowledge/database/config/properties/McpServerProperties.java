package com.ranyk.spring.ai.rag.knowledge.database.config.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * CLASS_NAME: McpServerProperties.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 自定义 Mcp Server 服务端配置属性类
 * @date: 2026-07-02
 */
@Data
@Component
@Deprecated
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = McpServerProperties.CONFIG_PREFIX)
public class McpServerProperties {
    /**
     * 自定义 - 文件配置属性前缀
     */
    public static final String CONFIG_PREFIX = "mcp";

    /**
     * 自定义 MCP Server -服务端配置属性
     */
    private Server server;

    /**
     * 服务端配置属性类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Server{

        /**
         * 服务端 URL 配置
         */
        private List<String> urls;
    }
}
