package com.ranyk.spring.ai.rag.knowledge.database.ai.agent.registry;

import com.ranyk.spring.ai.rag.knowledge.database.ai.agent.model.AgentDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CLASS_NAME: AgentRegistryTest.java
 *
 * @author ranyk
 * @version V1.0
 * @description: AgentRegistry 单元测试
 * @date: 2026-07-06
 */
@DisplayName("AgentRegistry 测试")
class AgentRegistryTest {

    private AgentRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new AgentRegistry();
    }

    @Test
    @DisplayName("测试注册 Agent - 正常情况")
    void testRegisterAgent_Success() {
        // Given
        AgentDefinition definition = createAgentDefinition("test-agent", "测试代理");

        // When
        registry.registerAgent(definition);

        // Then
        assertTrue(registry.hasAgent("test-agent"));
        assertEquals(1, registry.getAgentCount());
        assertNotNull(registry.getAgentDefinition("test-agent"));
    }

    @Test
    @DisplayName("测试注册 Agent - 定义为空")
    void testRegisterAgent_NullDefinition() {
        assertThrows(IllegalArgumentException.class, () -> {
            registry.registerAgent(null);
        });
    }

    @Test
    @DisplayName("测试重复注册 Agent - 应覆盖")
    void testRegisterAgent_DuplicateRegistration() {
        // Given
        AgentDefinition definition1 = createAgentDefinition("test-agent", "原始代理");
        AgentDefinition definition2 = createAgentDefinition("test-agent", "新代理");

        // When
        registry.registerAgent(definition1);
        registry.registerAgent(definition2);

        // Then
        assertEquals(1, registry.getAgentCount());
        assertEquals("新代理", registry.getAgentDefinition("test-agent").getDescription());
    }

    @Test
    @DisplayName("测试注销 Agent - 存在")
    void testUnregisterAgent_Exists() {
        // Given
        AgentDefinition definition = createAgentDefinition("test-agent", "测试代理");
        registry.registerAgent(definition);

        // When
        boolean result = registry.unregisterAgent("test-agent");

        // Then
        assertTrue(result);
        assertFalse(registry.hasAgent("test-agent"));
        assertEquals(0, registry.getAgentCount());
    }

    @Test
    @DisplayName("测试注销 Agent - 不存在")
    void testUnregisterAgent_NotExists() {
        // When
        boolean result = registry.unregisterAgent("non-existent");

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("测试获取 Agent 定义")
    void testGetAgentDefinition() {
        // Given
        AgentDefinition definition = createAgentDefinition("test-agent", "测试代理");
        registry.registerAgent(definition);

        // When
        AgentDefinition retrieved = registry.getAgentDefinition("test-agent");

        // Then
        assertNotNull(retrieved);
        assertEquals("test-agent", retrieved.getName());
        assertEquals("测试代理", retrieved.getDescription());
    }

    @Test
    @DisplayName("测试获取不存在的 Agent 定义")
    void testGetAgentDefinition_NotExists() {
        // When
        AgentDefinition retrieved = registry.getAgentDefinition("non-existent");

        // Then
        assertNull(retrieved);
    }

    @Test
    @DisplayName("测试获取所有 Agent 名称")
    void testGetAllAgentNames() {
        // Given
        registry.registerAgent(createAgentDefinition("agent-1", "代理1"));
        registry.registerAgent(createAgentDefinition("agent-2", "代理2"));
        registry.registerAgent(createAgentDefinition("agent-3", "代理3"));

        // When
        Set<String> names = registry.getAllAgentNames();

        // Then
        assertEquals(3, names.size());
        assertTrue(names.contains("agent-1"));
        assertTrue(names.contains("agent-2"));
        assertTrue(names.contains("agent-3"));
    }

    @Test
    @DisplayName("测试获取所有已启用的 Agents")
    void testGetAllEnabledAgents() {
        // Given
        AgentDefinition enabled1 = createAgentDefinition("enabled-1", "启用代理1");
        AgentDefinition enabled2 = createAgentDefinition("enabled-2", "启用代理2");
        AgentDefinition disabled = createAgentDefinition("disabled", "禁用代理");
        disabled.setEnabled(false);

        registry.registerAgent(enabled1);
        registry.registerAgent(enabled2);
        registry.registerAgent(disabled);

        // When
        List<AgentDefinition> enabledAgents = registry.getAllEnabledAgents();

        // Then
        assertEquals(2, enabledAgents.size());
        assertTrue(enabledAgents.stream().allMatch(AgentDefinition::getEnabled));
    }

    @Test
    @DisplayName("测试根据类型获取 Agents")
    void testGetAgentsByType() {
        // Given
        AgentDefinition chatAgent = createAgentDefinition("chat-agent", "聊天代理");
        chatAgent.setType("chat");
        
        AgentDefinition analysisAgent = createAgentDefinition("analysis-agent", "分析代理");
        analysisAgent.setType("analysis");
        
        AgentDefinition anotherChatAgent = createAgentDefinition("another-chat", "另一个聊天代理");
        anotherChatAgent.setType("chat");

        registry.registerAgent(chatAgent);
        registry.registerAgent(analysisAgent);
        registry.registerAgent(anotherChatAgent);

        // When
        List<AgentDefinition> chatAgents = registry.getAgentsByType("chat");

        // Then
        assertEquals(2, chatAgents.size());
        assertTrue(chatAgents.stream().allMatch(a -> "chat".equals(a.getType())));
    }

    @Test
    @DisplayName("测试根据工具获取 Agents")
    void testGetAgentsByTool() {
        // Given
        AgentDefinition agent1 = createAgentDefinition("agent-1", "代理1");
        agent1.setTools(Arrays.asList("tool-a", "tool-b"));
        
        AgentDefinition agent2 = createAgentDefinition("agent-2", "代理2");
        agent2.setTools(Arrays.asList("tool-b", "tool-c"));
        
        AgentDefinition agent3 = createAgentDefinition("agent-3", "代理3");
        agent3.setTools(Arrays.asList("tool-d"));

        registry.registerAgent(agent1);
        registry.registerAgent(agent2);
        registry.registerAgent(agent3);

        // When
        List<AgentDefinition> agentsWithToolB = registry.getAgentsByTool("tool-b");

        // Then
        assertEquals(2, agentsWithToolB.size());
        assertTrue(agentsWithToolB.stream().allMatch(a -> a.getTools().contains("tool-b")));
    }

    @Test
    @DisplayName("测试清空所有 Agents")
    void testClear() {
        // Given
        registry.registerAgent(createAgentDefinition("agent-1", "代理1"));
        registry.registerAgent(createAgentDefinition("agent-2", "代理2"));

        // When
        registry.clear();

        // Then
        assertEquals(0, registry.getAgentCount());
        assertTrue(registry.getAllAgentNames().isEmpty());
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
