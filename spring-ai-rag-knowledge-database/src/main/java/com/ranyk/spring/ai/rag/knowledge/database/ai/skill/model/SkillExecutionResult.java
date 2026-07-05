package com.ranyk.spring.ai.rag.knowledge.database.ai.skill.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * CLASS_NAME: SkillExecutionResult.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Skill 执行结果封装
 * @date: 2026-07-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillExecutionResult {
    
    /**
     * Skill ID
     */
    private String skillId;
    
    /**
     * 是否成功
     */
    private Boolean success;
    
    /**
     * 执行结果数据
     */
    private Object data;
    
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
     * 创建成功结果
     */
    public static SkillExecutionResult success(String skillId, Object data, long executionTimeMs) {
        return SkillExecutionResult.builder()
                .skillId(skillId)
                .success(true)
                .data(data)
                .executionTimeMs(executionTimeMs)
                .executedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 创建失败结果
     */
    public static SkillExecutionResult failure(String skillId, String errorMessage, String errorCode) {
        return SkillExecutionResult.builder()
                .skillId(skillId)
                .success(false)
                .errorMessage(errorMessage)
                .errorCode(errorCode)
                .executedAt(LocalDateTime.now())
                .build();
    }
}
