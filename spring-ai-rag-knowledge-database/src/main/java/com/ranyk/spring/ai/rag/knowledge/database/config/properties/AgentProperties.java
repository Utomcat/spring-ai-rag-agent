package com.ranyk.spring.ai.rag.knowledge.database.config.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * CLASS_NAME: AgentProperties.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Agents 配置属性类
 * @date: 2026-07-06
 */
@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = AgentProperties.CONFIG_PREFIX)
public class AgentProperties {
    /**
     * 配置属性前缀
     */
    public static final String CONFIG_PREFIX = "ai.agent";

    /**
     * 是否启用 Agent 功能
     */
    private Boolean enabled = true;

    /**
     * 默认超时时间(秒)
     */
    private Integer defaultTimeout = 60;

    /**
     * 默认最大重试次数
     */
    private Integer defaultMaxRetries = 3;

    /**
     * 是否启用异步执行
     */
    private Boolean asyncEnabled = true;

    /**
     * 异步线程池大小
     */
    private Integer asyncPoolSize = 5;

    /**
     * 子 Agent 列表
     */
    private List<SubAgent> subAgents;

    /**
     * 子 Agent 配置
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubAgent {
        /**
         * Agent 名称
         */
        private String name;

        /**
         * Agent 类型
         */
        private String type;

        /**
         * Agent 描述
         */
        private String description;

        /**
         * 可用的工具列表
         */
        private List<String> tools;

        /**
         * 是否启用
         */
        private Boolean enabled = true;

        /**
         * 最大重试次数
         */
        private Integer maxRetries = 3;

        /**
         * 超时时间(秒)
         */
        private Integer timeoutSeconds = 60;
    }
}
