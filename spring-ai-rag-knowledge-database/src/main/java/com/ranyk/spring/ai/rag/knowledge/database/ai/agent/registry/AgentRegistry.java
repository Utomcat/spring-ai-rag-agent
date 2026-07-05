package com.ranyk.spring.ai.rag.knowledge.database.ai.agent.registry;

import com.ranyk.spring.ai.rag.knowledge.database.ai.agent.model.AgentDefinition;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * CLASS_NAME: AgentRegistry.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Agent 注册中心 - 管理所有已注册的 Agents
 * @date: 2026-07-06
 */
@Slf4j
public class AgentRegistry {
    
    /**
     * Agent 定义映射表 (agentName -> AgentDefinition)
     */
    private final Map<String, AgentDefinition> agentDefinitions = new ConcurrentHashMap<>();
    
    /**
     * 注册 Agent
     *
     * @param definition Agent 定义
     */
    public void registerAgent(AgentDefinition definition) {
        if (definition == null || definition.getName() == null) {
            throw new IllegalArgumentException("Agent 定义和名称不能为空");
        }
        
        String agentName = definition.getName();
        
        // 检查是否已存在
        if (agentDefinitions.containsKey(agentName)) {
            log.warn("Agent [{}] 已存在,将被覆盖", agentName);
        }
        
        agentDefinitions.put(agentName, definition);
        log.info("Agent [{}] 注册成功: {}", agentName, definition.getDescription());
    }
    
    /**
     * 注销 Agent
     *
     * @param agentName Agent 名称
     * @return 是否成功注销
     */
    public boolean unregisterAgent(String agentName) {
        AgentDefinition removed = agentDefinitions.remove(agentName);
        
        if (removed != null) {
            log.info("Agent [{}] 已注销", agentName);
            return true;
        }
        
        log.warn("Agent [{}] 不存在,无法注销", agentName);
        return false;
    }
    
    /**
     * 获取 Agent 定义
     *
     * @param agentName Agent 名称
     * @return Agent 定义,不存在返回 null
     */
    public AgentDefinition getAgentDefinition(String agentName) {
        return agentDefinitions.get(agentName);
    }
    
    /**
     * 检查 Agent 是否存在
     *
     * @param agentName Agent 名称
     * @return 是否存在
     */
    public boolean hasAgent(String agentName) {
        return agentDefinitions.containsKey(agentName);
    }
    
    /**
     * 获取所有已注册的 Agent 名称
     *
     * @return Agent 名称集合
     */
    public Set<String> getAllAgentNames() {
        return Collections.unmodifiableSet(agentDefinitions.keySet());
    }
    
    /**
     * 获取所有已启用的 Agent 定义
     *
     * @return Agent 定义列表
     */
    public List<AgentDefinition> getAllEnabledAgents() {
        return agentDefinitions.values().stream()
                .filter(AgentDefinition::getEnabled)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据类型获取 Agents
     *
     * @param type Agent 类型
     * @return Agent 定义列表
     */
    public List<AgentDefinition> getAgentsByType(String type) {
        return agentDefinitions.values().stream()
                .filter(a -> type.equals(a.getType()))
                .filter(AgentDefinition::getEnabled)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取包含指定工具的 Agents
     *
     * @param tool 工具名称
     * @return Agent 定义列表
     */
    public List<AgentDefinition> getAgentsByTool(String tool) {
        return agentDefinitions.values().stream()
                .filter(a -> a.getTools() != null && a.getTools().contains(tool))
                .filter(AgentDefinition::getEnabled)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取已注册的 Agent 数量
     *
     * @return Agent 数量
     */
    public int getAgentCount() {
        return agentDefinitions.size();
    }
    
    /**
     * 清空所有注册的 Agents
     */
    public void clear() {
        int count = agentDefinitions.size();
        agentDefinitions.clear();
        log.info("已清空所有 {} 个 Agents", count);
    }
}
