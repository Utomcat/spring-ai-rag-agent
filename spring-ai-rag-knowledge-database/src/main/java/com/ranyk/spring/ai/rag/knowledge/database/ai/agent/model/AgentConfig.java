package com.ranyk.spring.ai.rag.knowledge.database.ai.agent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CLASS_NAME: AgentConfig.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Agent 执行配置
 * @date: 2026-07-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentConfig {

    /**
     * 超时时间(秒)
     */
    @Builder.Default
    private Integer timeoutSeconds = 60;

    /**
     * 最大重试次数
     */
    @Builder.Default
    private Integer maxRetries = 3;

    /**
     * 是否启用流式输出
     */
    @Builder.Default
    private Boolean streaming = Boolean.FALSE;

    /**
     * 温度参数
     */
    @Builder.Default
    private Double temperature = 0.7;
}
