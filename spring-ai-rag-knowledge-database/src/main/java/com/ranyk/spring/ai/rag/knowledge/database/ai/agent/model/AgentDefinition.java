package com.ranyk.spring.ai.rag.knowledge.database.ai.agent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * CLASS_NAME: AgentDefinition.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Agent 定义模型
 * @date: 2026-07-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentDefinition {
    
    /**
     * Agent 名称(唯一标识)
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
    @Builder.Default
    private Boolean enabled = Boolean.FALSE;
    
    /**
     * 最大重试次数
     */
    @Builder.Default
    private Integer maxRetries = 3;
    
    /**
     * 超时时间(秒)
     */
    @Builder.Default
    private Integer timeoutSeconds = 60;
}
