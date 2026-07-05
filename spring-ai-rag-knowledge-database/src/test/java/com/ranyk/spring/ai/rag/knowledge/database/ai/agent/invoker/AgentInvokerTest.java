package com.ranyk.spring.ai.rag.knowledge.database.ai.agent.invoker;

import com.ranyk.spring.ai.rag.knowledge.database.ai.agent.model.AgentDefinition;
import com.ranyk.spring.ai.rag.knowledge.database.ai.agent.model.AgentExecutionResult;
import com.ranyk.spring.ai.rag.knowledge.database.ai.agent.registry.AgentRegistry;
import com.ranyk.spring.ai.rag.knowledge.database.config.properties.AgentProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * CLASS_NAME: AgentInvokerTest.java
 *
 * @author ranyk
 * @version V1.0
 * @description: AgentInvoker 单元测试
 * @date: 2026-07-06
 */
@DisplayName("AgentInvoker 测试")
class AgentInvokerTest {

    private AgentInvoker invoker;
    private AgentRegistry registry;
    
    @Mock
    private AgentProperties properties;
    
    @Mock
    private AgentChatClient chatClient;
    
    @Mock
    private ToolRegistry toolRegistry;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 配置默认属性
        when(properties.getAsyncEnabled()).thenReturn(false);
        when(properties.getDefaultTimeout()).thenReturn(30);
        
        registry = new AgentRegistry();
        invoker = new AgentInvoker(registry, properties, chatClient, toolRegistry);
    }

    @Test
    @DisplayName("测试调用 Agent - 成功情况(无工具)")
    void testInvoke_Success_NoTools() throws Exception {
        // Given
        AgentDefinition definition = createAgentDefinition("test-agent", "测试代理");
        registry.registerAgent(definition);
        
        when(chatClient.execute(anyString(), anyString())).thenReturn("LLM Response");

        // When
        AgentExecutionResult result = invoker.invoke("test-agent", "用户问题");

        // Then
        assertTrue(result.getSuccess());
        assertEquals("test-agent", result.getAgentName());
        assertEquals("LLM Response", result.getContent());
        assertNotNull(result.getExecutionTimeMs());
    }

    @Test
    @DisplayName("测试调用 Agent - 成功情况(带工具)")
    void testInvoke_Success_WithTools() throws Exception {
        // Given
        AgentDefinition definition = createAgentDefinition("test-agent", "测试代理");
        definition.setTools(Arrays.asList("tool-1", "tool-2"));
        registry.registerAgent(definition);
        
        when(chatClient.executeWithTools(anyString(), anyString(), anyList()))
                .thenReturn("LLM Response with Tools");

        // When
        AgentExecutionResult result = invoker.invoke("test-agent", "用户问题");

        // Then
        assertTrue(result.getSuccess());
        assertEquals("LLM Response with Tools", result.getContent());
        verify(chatClient).executeWithTools(anyString(), anyString(), eq(Arrays.asList("tool-1", "tool-2")));
    }

    @Test
    @DisplayName("测试调用 Agent - Agent 不存在")
    void testInvoke_AgentNotFound() {
        // When
        AgentExecutionResult result = invoker.invoke("non-existent", "用户问题");

        // Then
        assertFalse(result.getSuccess());
        assertEquals("AGENT_NOT_FOUND", result.getErrorCode());
        assertTrue(result.getErrorMessage().contains("不存在"));
    }

    @Test
    @DisplayName("测试调用 Agent - Agent 未启用")
    void testInvoke_AgentDisabled() {
        // Given
        AgentDefinition definition = createAgentDefinition("disabled-agent", "禁用代理");
        definition.setEnabled(false);
        registry.registerAgent(definition);

        // When
        AgentExecutionResult result = invoker.invoke("disabled-agent", "用户问题");

        // Then
        assertFalse(result.getSuccess());
        assertEquals("AGENT_DISABLED", result.getErrorCode());
    }

    @Test
    @DisplayName("测试异步调用 Agent")
    void testInvokeAsync() throws Exception {
        // Given
        AgentDefinition definition = createAgentDefinition("async-agent", "异步代理");
        registry.registerAgent(definition);
        
        when(properties.getAsyncEnabled()).thenReturn(true);
        when(properties.getAsyncPoolSize()).thenReturn(4);
        invoker = new AgentInvoker(registry, properties, chatClient, toolRegistry);
        
        when(chatClient.execute(anyString(), anyString())).thenReturn("Async Response");

        // When
        CompletableFuture<AgentExecutionResult> future = invoker.invokeAsync("async-agent", "用户问题");
        AgentExecutionResult result = future.get(5, TimeUnit.SECONDS);

        // Then
        assertTrue(result.getSuccess());
        assertEquals("Async Response", result.getContent());
    }

    @Test
    @DisplayName("测试并行调用多个 Agents")
    void testParallelInvoke() throws Exception {
        // Given
        AgentDefinition agent1 = createAgentDefinition("agent-1", "代理1");
        AgentDefinition agent2 = createAgentDefinition("agent-2", "代理2");
        registry.registerAgent(agent1);
        registry.registerAgent(agent2);
        
        when(properties.getAsyncEnabled()).thenReturn(true);
        when(properties.getAsyncPoolSize()).thenReturn(4);
        invoker = new AgentInvoker(registry, properties, chatClient, toolRegistry);
        
        when(chatClient.execute(anyString(), anyString()))
                .thenReturn("Response 1")
                .thenReturn("Response 2");

        List<String> agentNames = Arrays.asList("agent-1", "agent-2");

        // When
        List<AgentExecutionResult> results = invoker.parallelInvoke(agentNames, "用户问题");

        // Then
        assertEquals(2, results.size());
        assertTrue(results.get(0).getSuccess());
        assertTrue(results.get(1).getSuccess());
    }

    @Test
    @DisplayName("测试链式调用多个 Agents")
    void testChainInvoke() throws Exception {
        // Given
        AgentDefinition agent1 = createAgentDefinition("agent-1", "代理1");
        AgentDefinition agent2 = createAgentDefinition("agent-2", "代理2");
        registry.registerAgent(agent1);
        registry.registerAgent(agent2);
        
        when(chatClient.execute(anyString(), anyString()))
                .thenReturn("Intermediate Result")
                .thenReturn("Final Result");

        List<String> agentNames = Arrays.asList("agent-1", "agent-2");

        // When
        AgentExecutionResult result = invoker.chainInvoke(agentNames, "初始问题");

        // Then
        assertTrue(result.getSuccess());
        assertEquals("Final Result", result.getContent());
    }

    @Test
    @DisplayName("测试链式调用 - 中间失败")
    void testChainInvoke_FailureInMiddle() throws Exception {
        // Given
        AgentDefinition agent1 = createAgentDefinition("agent-1", "代理1");
        AgentDefinition agent2 = createAgentDefinition("agent-2", "代理2");
        registry.registerAgent(agent1);
        registry.registerAgent(agent2);
        
        when(chatClient.execute(anyString(), anyString()))
                .thenReturn("Success")
                .thenThrow(new RuntimeException("Simulated failure"));

        List<String> agentNames = Arrays.asList("agent-1", "agent-2");

        // When
        AgentExecutionResult result = invoker.chainInvoke(agentNames, "初始问题");

        // Then
        assertFalse(result.getSuccess());
        assertTrue(result.getErrorMessage().contains("Simulated failure"));
    }

    @Test
    @DisplayName("测试根据工具路由到 Agent")
    void testRouteByTool() {
        // Given
        AgentDefinition agent1 = createAgentDefinition("agent-1", "代理1");
        agent1.setTools(Arrays.asList("tool-a", "tool-b"));
        
        AgentDefinition agent2 = createAgentDefinition("agent-2", "代理2");
        agent2.setTools(Arrays.asList("tool-c", "tool-d"));
        
        registry.registerAgent(agent1);
        registry.registerAgent(agent2);

        // When
        String routedAgent = invoker.routeByTool("tool-b");

        // Then
        assertEquals("agent-1", routedAgent);
    }

    @Test
    @DisplayName("测试根据工具路由 - 未找到")
    void testRouteByTool_NotFound() {
        // Given
        AgentDefinition agent = createAgentDefinition("agent-1", "代理1");
        agent.setTools(Arrays.asList("tool-a"));
        registry.registerAgent(agent);

        // When
        String routedAgent = invoker.routeByTool("non-existent-tool");

        // Then
        assertNull(routedAgent);
    }

    @Test
    @DisplayName("测试关闭调用器")
    void testShutdown() {
        // Given
        when(properties.getAsyncEnabled()).thenReturn(true);
        when(properties.getAsyncPoolSize()).thenReturn(4);
        invoker = new AgentInvoker(registry, properties, chatClient, toolRegistry);

        // When
        invoker.shutdown();

        // Then - 不应该抛出异常
        assertDoesNotThrow(() -> invoker.shutdown());
    }

    @Test
    @DisplayName("测试异步调用器未启用时抛出异常")
    void testInvokeAsync_NotEnabled() {
        // Given
        when(properties.getAsyncEnabled()).thenReturn(false);
        invoker = new AgentInvoker(registry, properties, chatClient, toolRegistry);

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            invoker.invokeAsync("test-agent", "用户问题");
        });
    }

    /**
     * 创建测试用的 AgentDefinition
     */
    private AgentDefinition createAgentDefinition(String name, String description) {
        AgentDefinition definition = new AgentDefinition();
        definition.setName(name);
        definition.setDescription(description);
        definition.setEnabled(true);
        return definition;
    }
}
