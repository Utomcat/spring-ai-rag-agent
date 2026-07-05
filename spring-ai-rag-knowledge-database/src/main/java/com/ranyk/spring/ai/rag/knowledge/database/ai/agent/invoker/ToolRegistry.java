package com.ranyk.spring.ai.rag.knowledge.database.ai.agent.invoker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * CLASS_NAME: ToolRegistry.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 工具注册中心 - 管理所有可用的 Function Calling 工具
 * @date: 2026-07-06
 */
@Slf4j
@Component
public class ToolRegistry {
    
    /**
     * 工具定义映射表 (toolName -> ToolDefinition)
     */
    private final Map<String, ToolDefinition> toolDefinitions = new ConcurrentHashMap<>();
    
    /**
     * 工具处理器映射表 (toolName -> handler function)
     */
    private final Map<String, Function<Map<String, Object>, Object>> toolHandlers = new ConcurrentHashMap<>();
    
    /**
     * 注册工具
     *
     * @param definition 工具定义
     * @param handler    工具处理器函数
     */
    public void registerTool(ToolDefinition definition, Function<Map<String, Object>, Object> handler) {
        if (definition == null || definition.getName() == null) {
            throw new IllegalArgumentException("工具定义和名称不能为空");
        }
        
        if (handler == null) {
            throw new IllegalArgumentException("工具处理器不能为空");
        }
        
        toolDefinitions.put(definition.getName(), definition);
        toolHandlers.put(definition.getName(), handler);
        
        log.info("工具已注册: {} ({})", definition.getName(), definition.getDescription());
    }
    
    /**
     * 注销工具
     *
     * @param toolName 工具名称
     */
    public void unregisterTool(String toolName) {
        toolDefinitions.remove(toolName);
        toolHandlers.remove(toolName);
        log.info("工具已注销: {}", toolName);
    }
    
    /**
     * 获取工具定义
     *
     * @param toolName 工具名称
     * @return 工具定义,不存在返回 null
     */
    public ToolDefinition getToolDefinition(String toolName) {
        return toolDefinitions.get(toolName);
    }
    
    /**
     * 获取工具处理器
     *
     * @param toolName 工具名称
     * @return 工具处理器,不存在返回 null
     */
    public Function<Map<String, Object>, Object> getToolHandler(String toolName) {
        return toolHandlers.get(toolName);
    }
    
    /**
     * 检查工具是否存在
     *
     * @param toolName 工具名称
     * @return 是否存在
     */
    public boolean hasTool(String toolName) {
        return toolDefinitions.containsKey(toolName);
    }
    
    /**
     * 获取所有已注册的工具名称
     *
     * @return 工具名称列表
     */
    public Set<String> getAllToolNames() {
        return Collections.unmodifiableSet(toolDefinitions.keySet());
    }
    
    /**
     * 根据分类获取工具列表
     *
     * @param category 分类
     * @return 工具定义列表
     */
    public List<ToolDefinition> getToolsByCategory(String category) {
        return toolDefinitions.values().stream()
                .filter(def -> category.equals(def.getCategory()))
                .collect(Collectors.toList());
    }
    
    /**
     * 批量获取工具定义
     *
     * @param toolNames 工具名称列表
     * @return 工具定义列表
     */
    public List<ToolDefinition> getToolDefinitions(List<String> toolNames) {
        return toolNames.stream()
                .map(this::getToolDefinition)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    /**
     * 执行工具
     *
     * @param toolName 工具名称
     * @param params   参数
     * @return 执行结果
     */
    public Object executeTool(String toolName, Map<String, Object> params) {
        Function<Map<String, Object>, Object> handler = toolHandlers.get(toolName);
        
        if (handler == null) {
            throw new IllegalArgumentException("工具不存在: " + toolName);
        }
        
        try {
            log.debug("执行工具: {}, 参数: {}", toolName, params);
            Object result = handler.apply(params);
            log.debug("工具执行成功: {}", toolName);
            return result;
        } catch (Exception e) {
            log.error("工具执行失败: {}", toolName, e);
            throw new RuntimeException("工具执行失败: " + toolName, e);
        }
    }
    
    /**
     * 清除所有工具
     */
    public void clearAllTools() {
        toolDefinitions.clear();
        toolHandlers.clear();
        log.info("已清除所有工具");
    }
    
    /**
     * 获取已注册工具数量
     *
     * @return 工具数量
     */
    public int getToolCount() {
        return toolDefinitions.size();
    }
    
    /**
     * 工具定义模型
     */
    public static class ToolDefinition {
        private String name;
        private String description;
        private String category;
        private Map<String, Object> parameters;
        
        public ToolDefinition() {
        }
        
        public ToolDefinition(String name, String description, String category, Map<String, Object> parameters) {
            this.name = name;
            this.description = description;
            this.category = category;
            this.parameters = parameters;
        }
        
        // Getters and Setters
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public String getCategory() {
            return category;
        }
        
        public void setCategory(String category) {
            this.category = category;
        }
        
        public Map<String, Object> getParameters() {
            return parameters;
        }
        
        public void setParameters(Map<String, Object> parameters) {
            this.parameters = parameters;
        }
    }
}
