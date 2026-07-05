package com.ranyk.spring.ai.rag.knowledge.database.ai.skill.registry;

import com.ranyk.spring.ai.rag.knowledge.database.common.cache.ResultCache;
import com.ranyk.spring.ai.rag.knowledge.database.ai.skill.handler.SkillHandler;
import com.ranyk.spring.ai.rag.knowledge.database.ai.skill.model.SkillDefinition;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * CLASS_NAME: SkillRegistry.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Skill 注册中心 - 管理所有已注册的 Skills
 * @date: 2026-07-06
 */
@Slf4j
public class SkillRegistry {
    
    /**
     * Skill 定义映射表 (skillId -> SkillDefinition)
     */
    private final Map<String, SkillDefinition> skillDefinitions = new ConcurrentHashMap<>();
    
    /**
     * Skill Handler 映射表 (skillId -> SkillHandler)
     */
    private final Map<String, SkillHandler> skillHandlers = new ConcurrentHashMap<>();
    
    /**
     * Skill 查询结果缓存 (skillId -> SkillDefinition)
     * 最大1000条,默认5分钟过期
     */
    private final ResultCache<String, SkillDefinition> skillCache = new ResultCache<>(1000, 300);
    
    /**
     * 注册 Skill
     *
     * @param definition Skill 定义
     * @param handler    Skill 处理器
     */
    public void registerSkill(SkillDefinition definition, SkillHandler handler) {
        if (definition == null || definition.getId() == null) {
            throw new IllegalArgumentException("Skill 定义和 ID 不能为空");
        }
        
        if (handler == null) {
            throw new IllegalArgumentException("Skill Handler 不能为空");
        }
        
        String skillId = definition.getId();
        
        // 检查是否已存在
        if (skillDefinitions.containsKey(skillId)) {
            log.warn("Skill [{}] 已存在,将被覆盖", skillId);
            // 清除旧缓存
            skillCache.remove(skillId);
        }
        
        skillDefinitions.put(skillId, definition);
        skillHandlers.put(skillId, handler);
        
        // 更新缓存
        skillCache.put(skillId, definition);
        
        log.info("Skill [{}] 注册成功: {}", skillId, definition.getName());
    }
    
    /**
     * 注销 Skill
     *
     * @param skillId Skill ID
     * @return 是否成功注销
     */
    public boolean unregisterSkill(String skillId) {
        SkillDefinition removed = skillDefinitions.remove(skillId);
        skillHandlers.remove(skillId);
        
        // 清除缓存
        skillCache.remove(skillId);
        
        if (removed != null) {
            log.info("Skill [{}] 已注销", skillId);
            return true;
        }
        
        log.warn("Skill [{}] 不存在,无法注销", skillId);
        return false;
    }
    
    /**
     * 获取 Skill 定义(带缓存优化)
     *
     * @param skillId Skill ID
     * @return Skill 定义,不存在返回 null
     */
    public SkillDefinition getSkillDefinition(String skillId) {
        // 使用缓存获取,未命中时从内存加载
        return skillCache.get(skillId, () -> skillDefinitions.get(skillId));
    }
    
    /**
     * 获取 Skill Handler(带缓存优化)
     *
     * @param skillId Skill ID
     * @return Skill Handler,不存在返回 null
     */
    public SkillHandler getSkillHandler(String skillId) {
        return skillHandlers.get(skillId);
    }
    
    /**
     * 检查 Skill 是否存在
     *
     * @param skillId Skill ID
     * @return 是否存在
     */
    public boolean hasSkill(String skillId) {
        return skillDefinitions.containsKey(skillId);
    }
    
    /**
     * 获取所有已注册的 Skill IDs
     *
     * @return Skill ID 集合
     */
    public Set<String> getAllSkillIds() {
        return Collections.unmodifiableSet(skillDefinitions.keySet());
    }
    
    /**
     * 获取所有已启用的 Skill 定义
     *
     * @return Skill 定义列表
     */
    public List<SkillDefinition> getAllEnabledSkills() {
        return skillDefinitions.values().stream()
                .filter(SkillDefinition::getEnabled)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据分类获取 Skills
     *
     * @param category 分类
     * @return Skill 定义列表
     */
    public List<SkillDefinition> getSkillsByCategory(String category) {
        return skillDefinitions.values().stream()
                .filter(s -> category.equals(s.getCategory()))
                .filter(SkillDefinition::getEnabled)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据标签获取 Skills
     *
     * @param tag 标签
     * @return Skill 定义列表
     */
    public List<SkillDefinition> getSkillsByTag(String tag) {
        return skillDefinitions.values().stream()
                .filter(s -> s.getTags() != null && s.getTags().contains(tag))
                .filter(SkillDefinition::getEnabled)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取已注册的 Skill 数量
     *
     * @return Skill 数量
     */
    public int getSkillCount() {
        return skillDefinitions.size();
    }
    
    /**
     * 清空所有注册的 Skills
     */
    public void clear() {
        int count = skillDefinitions.size();
        skillDefinitions.clear();
        skillHandlers.clear();
        
        // 清空缓存
        skillCache.clear();
        
        log.info("已清空所有 {} 个 Skills", count);
    }
    
    /**
     * 获取缓存统计信息
     *
     * @return 缓存统计字符串
     */
    public String getCacheStats() {
        return skillCache.getStats();
    }
}
