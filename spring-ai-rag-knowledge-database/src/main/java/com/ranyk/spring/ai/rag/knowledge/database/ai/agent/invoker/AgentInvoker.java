package com.ranyk.spring.ai.rag.knowledge.database.ai.agent.invoker;

import com.ranyk.spring.ai.rag.knowledge.database.ai.agent.model.AgentDefinition;
import com.ranyk.spring.ai.rag.knowledge.database.ai.agent.model.AgentExecutionResult;
import com.ranyk.spring.ai.rag.knowledge.database.ai.agent.registry.AgentRegistry;
import com.ranyk.spring.ai.rag.knowledge.database.utils.ConcurrentUtils;
import com.ranyk.spring.ai.rag.knowledge.database.utils.ExecutorUtils;
import com.ranyk.spring.ai.rag.knowledge.database.config.properties.AgentProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * CLASS_NAME: AgentInvoker.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Agent 调用器 - 负责调用和管理 Agents
 * @date: 2026-07-06
 */
@Slf4j
public class AgentInvoker {
    
    private final AgentRegistry agentRegistry;
    private final AgentProperties agentProperties;
    private final AgentChatClient agentChatClient;
    private final ToolRegistry toolRegistry;
    private final ExecutorService executorService;
    
    public AgentInvoker(AgentRegistry agentRegistry, AgentProperties agentProperties, 
                       AgentChatClient agentChatClient, ToolRegistry toolRegistry) {
        this.agentRegistry = agentRegistry;
        this.agentProperties = agentProperties;
        this.agentChatClient = agentChatClient;
        this.toolRegistry = toolRegistry;
        
        // 初始化线程池
        if (agentProperties.getAsyncEnabled()) {
            this.executorService = ExecutorUtils.createFixedThreadPool(
                    agentProperties.getAsyncPoolSize(),
                    "agent-invoker"
            );
            log.info("Agent 异步调用器已初始化,线程池大小: {}", agentProperties.getAsyncPoolSize());
        } else {
            this.executorService = null;
            log.info("Agent 异步调用未启用");
        }
    }
    
    /**
     * 同步调用单个 Agent
     *
     * @param agentName Agent 名称
     * @param prompt    提示词/任务描述
     * @return 执行结果
     */
    public AgentExecutionResult invoke(String agentName, String prompt) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 验证 Agent 是否存在
            if (!agentRegistry.hasAgent(agentName)) {
                String errorMsg = String.format("Agent [%s] 不存在", agentName);
                log.error(errorMsg);
                return AgentExecutionResult.failure(agentName, errorMsg, "AGENT_NOT_FOUND");
            }
            
            AgentDefinition definition = agentRegistry.getAgentDefinition(agentName);
            
            if (!definition.getEnabled()) {
                String errorMsg = String.format("Agent [%s] 未启用", agentName);
                log.error(errorMsg);
                return AgentExecutionResult.failure(agentName, errorMsg, "AGENT_DISABLED");
            }
            
            // 构建系统提示词
            String systemPrompt = buildSystemPrompt(definition);
            
            log.info("开始调用 Agent [{}], 任务: {}", agentName, prompt);
            
            // 执行真实的 LLM 调用
            String result;
            if (definition.getTools() != null && !definition.getTools().isEmpty()) {
                // 带工具的调用
                result = agentChatClient.executeWithTools(systemPrompt, prompt, definition.getTools());
            } else {
                // 普通调用
                result = agentChatClient.execute(systemPrompt, prompt);
            }
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            log.info("Agent [{}] 调用成功,耗时: {}ms", agentName, executionTime);
            return AgentExecutionResult.success(agentName, result, executionTime, definition.getTools());
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            String errorMsg = String.format("Agent [%s] 调用失败: %s", agentName, e.getMessage());
            log.error(errorMsg, e);
            return AgentExecutionResult.failure(agentName, errorMsg, "EXECUTION_ERROR");
        }
    }
    
    /**
     * 异步调用 Agent
     *
     * @param agentName Agent 名称
     * @param prompt    提示词
     * @return Future
     */
    public CompletableFuture<AgentExecutionResult> invokeAsync(String agentName, String prompt) {
        if (executorService == null || executorService.isShutdown()) {
            throw new IllegalStateException("异步调用器未启用或已关闭");
        }
        
        return CompletableFuture.supplyAsync(() -> invoke(agentName, prompt), executorService);
    }
    
    /**
     * 并行调用多个 Agents(使用 ConcurrentUtils 优化)
     *
     * @param agentNames Agent 名称列表
     * @param prompt     提示词
     * @return 执行结果列表
     */
    public List<AgentExecutionResult> parallelInvoke(List<String> agentNames, String prompt) {
        if (agentNames == null || agentNames.isEmpty()) {
            return Collections.emptyList();
        }
        
        log.info("开始并行调用 {} 个 Agents", agentNames.size());
        long startTime = System.currentTimeMillis();
        
        // 使用 ConcurrentUtils 进行并行处理
        List<AgentExecutionResult> results = ConcurrentUtils.parallelProcess(
            agentNames,
            name -> invoke(name, prompt),
            executorService,
            agentProperties.getDefaultTimeout(),
            "Agent 并行调用"
        );
        
        long elapsed = System.currentTimeMillis() - startTime;
        log.info("并行调用完成,耗时: {}ms, 成功: {}/{}", 
                elapsed, results.size(), agentNames.size());
        
        return results;
    }
    
    /**
     * 链式调用多个 Agents(前一个的结果作为后一个的输入)
     *
     * @param agentNames Agent 名称列表
     * @param prompt     初始提示词
     * @return 最终执行结果
     */
    public AgentExecutionResult chainInvoke(List<String> agentNames, String prompt) {
        if (agentNames == null || agentNames.isEmpty()) {
            throw new IllegalArgumentException("agentNames 不能为空");
        }
        
        String currentPrompt = prompt;
        AgentExecutionResult lastResult = null;
        
        for (String agentName : agentNames) {
            lastResult = invoke(agentName, currentPrompt);
            
            if (!lastResult.getSuccess()) {
                log.error("链式调用在 Agent [{}] 处失败", agentName);
                return lastResult;
            }
            
            // 将结果作为下一个 Agent 的输入
            currentPrompt = lastResult.getContent();
        }
        
        return lastResult;
    }
    
    /**
     * 根据工具名称路由到合适的 Agent
     *
     * @param toolName 工具名称
     * @return Agent 名称,如果没有找到返回 null
     */
    public String routeByTool(String toolName) {
        List<AgentDefinition> agents = agentRegistry.getAgentsByTool(toolName);
        if (agents.isEmpty()) {
            return null;
        }
        
        // 返回第一个匹配的 Agent
        return agents.get(0).getName();
    }
    
    /**
     * 构建系统提示词
     *
     * @param definition Agent 定义
     * @return 系统提示词
     */
    private String buildSystemPrompt(AgentDefinition definition) {
        StringBuilder sb = new StringBuilder();
        
        if (definition.getDescription() != null) {
            sb.append(definition.getDescription()).append("\n\n");
        }
        
        if (definition.getTools() != null && !definition.getTools().isEmpty()) {
            sb.append("你可以使用以下工具:\n");
            for (String toolName : definition.getTools()) {
                ToolRegistry.ToolDefinition toolDef = toolRegistry.getToolDefinition(toolName);
                if (toolDef != null) {
                    sb.append("- ").append(toolName).append(": ")
                      .append(toolDef.getDescription()).append("\n");
                }
            }
            sb.append("\n");
        }
        
        sb.append("请根据用户的问题提供帮助。");
        
        return sb.toString();
    }
    
    /**
     * 关闭调用器
     */
    public void shutdown() {
        ExecutorUtils.safeShutdown(executorService, 5, "Agent 调用器");
    }
}
