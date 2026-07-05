package com.ranyk.spring.ai.rag.knowledge.database.ai.agent.invoker;

import com.ranyk.spring.ai.rag.knowledge.database.ai.agent.model.AgentDefinition;
import com.ranyk.spring.ai.rag.knowledge.database.ai.agent.model.AgentExecutionResult;
import com.ranyk.spring.ai.rag.knowledge.database.ai.agent.registry.AgentRegistry;
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
    private final ExecutorService executorService;
    
    public AgentInvoker(AgentRegistry agentRegistry, AgentProperties agentProperties) {
        this.agentRegistry = agentRegistry;
        this.agentProperties = agentProperties;
        
        // 初始化线程池
        if (agentProperties.getAsyncEnabled()) {
            this.executorService = Executors.newFixedThreadPool(
                    agentProperties.getAsyncPoolSize(),
                    r -> {
                        Thread thread = new Thread(r);
                        thread.setName("agent-invoker-" + thread.getId());
                        thread.setDaemon(true);
                        return thread;
                    }
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
            
            // TODO: 实现真实的 Agent 调用逻辑
            // 这里需要集成 Spring AI 的 ChatClient 和 Function Calling
            
            log.info("开始调用 Agent [{}], 任务: {}", agentName, prompt);
            
            // 模拟执行
            String result = String.format("Agent [%s] 处理结果: %s", agentName, prompt);
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
     * 并行调用多个 Agents
     *
     * @param agentNames Agent 名称列表
     * @param prompt     提示词
     * @return 执行结果列表
     */
    public List<AgentExecutionResult> parallelInvoke(List<String> agentNames, String prompt) {
        List<CompletableFuture<AgentExecutionResult>> futures = agentNames.stream()
                .map(name -> invokeAsync(name, prompt))
                .collect(Collectors.toList());
        
        // 等待所有任务完成
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );
        
        try {
            allFutures.get(agentProperties.getDefaultTimeout(), TimeUnit.SECONDS);
            return futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("并行调用 Agents 失败", e);
            throw new RuntimeException("并行调用失败: " + e.getMessage(), e);
        }
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
     * 关闭调用器
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
            log.info("Agent 调用器已关闭");
        }
    }
}
