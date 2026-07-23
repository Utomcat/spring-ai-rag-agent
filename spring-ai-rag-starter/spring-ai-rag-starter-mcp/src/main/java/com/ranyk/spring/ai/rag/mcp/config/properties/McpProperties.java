package com.ranyk.spring.ai.rag.mcp.config.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * CLASS_NAME: McpProperties.java
 *
 * @author ranyk
 * @version V1.0
 * @description: MCP 服务配置属性类
 * @date: 2026-07-23
 */
@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = McpProperties.CONFIG_PREFIX)
public class McpProperties {
    /**
     * 配置属性前缀
     */
    public static final String CONFIG_PREFIX = "mcp";
    /**
     * MCP 服务描述信息列表,用于后续的自动添加系统提示词
     */
    private List<String> descriptions = new ArrayList<>(10);
}
