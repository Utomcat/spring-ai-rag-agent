package com.ranyk.spring.ai.rag.knowledge.database.ai.skill.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * CLASS_NAME: SkillDefinition.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Skill 定义模型
 * @date: 2026-07-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillDefinition {
    
    /**
     * Skill 唯一标识
     */
    private String id;
    
    /**
     * Skill 名称
     */
    private String name;
    
    /**
     * Skill 描述
     */
    private String description;
    
    /**
     * 分类
     */
    private String category;
    
    /**
     * 标签列表
     */
    private List<String> tags;
    
    /**
     * 必需参数列表
     */
    private List<String> requiredParams;
    
    /**
     * 可选参数列表
     */
    private List<String> optionalParams;
    
    /**
     * 是否启用
     */
    @Builder.Default
    private Boolean enabled = Boolean.FALSE;
    
    /**
     * Handler 类名(完整类名)
     */
    private String handler;
    
    /**
     * 版本号
     */
    @Builder.Default
    private String version = "1.0.0";
}
