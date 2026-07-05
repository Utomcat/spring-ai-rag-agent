package com.ranyk.spring.ai.rag.knowledge.database.ai.agent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * CLASS_NAME: AgentExecutionResult.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Agent 执行结果封装
 * @date: 2026-07-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentExecutionResult {
    
    /**
     * Agent 名称
     */
    private String agentName;
    
    /**
     * 是否成功
     */
    private Boolean success;
    
    /**
     * 执行结果内容
     */
    private String content;
    
    /**
     * 错误信息(如果失败)
     */
    private String errorMessage;
    
    /**
     * 错误代码(如果失败)
     */
    private String errorCode;
    
    /**
     * 执行耗时(毫秒)
     */
    private Long executionTimeMs;
    
    /**
     * 执行时间
     */
    private LocalDateTime executedAt;
    
    /**
     * 使用的工具列表
     */
    private java.util.List<String> usedTools;
    
    /**
     * 创建成功结果
     */
    public static AgentExecutionResult success(String agentName, String content, 
                                                long executionTimeMs, 
                                                java.util.List<String> usedTools) {
        return AgentExecutionResult.builder()
                .agentName(agentName)
                .success(true)
                .content(content)
                .executionTimeMs(executionTimeMs)
                .executedAt(LocalDateTime.now())
                .usedTools(usedTools)
                .build();
    }
    
    /**
     * 创建失败结果
     */
    public static AgentExecutionResult failure(String agentName, String errorMessage, String errorCode) {
        return AgentExecutionResult.builder()
                .agentName(agentName)
                .success(false)
                .errorMessage(errorMessage)
                .errorCode(errorCode)
                .executedAt(LocalDateTime.now())
                .build();
    }
}
