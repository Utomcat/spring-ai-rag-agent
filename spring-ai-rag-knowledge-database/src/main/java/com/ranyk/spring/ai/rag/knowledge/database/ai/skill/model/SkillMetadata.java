package com.ranyk.spring.ai.rag.knowledge.database.ai.skill.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * CLASS_NAME: SkillMetadata.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Skill 元数据信息
 * @date: 2026-07-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillMetadata {
    
    /**
     * Skill ID
     */
    private String skillId;
    
    /**
     * 分类
     */
    private String category;
    
    /**
     * 标签列表
     */
    private List<String> tags;
    
    /**
     * 版本号
     */
    private String version;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 最后更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 作者
     */
    private String author;
    
    /**
     * 描述
     */
    private String description;
}
