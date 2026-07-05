package com.ranyk.spring.ai.rag.knowledge.database.ai.skill.executor;

import com.ranyk.spring.ai.rag.knowledge.database.ai.skill.handler.SkillHandler;
import com.ranyk.spring.ai.rag.knowledge.database.ai.skill.model.SkillDefinition;
import com.ranyk.spring.ai.rag.knowledge.database.ai.skill.model.SkillExecutionResult;
import com.ranyk.spring.ai.rag.knowledge.database.ai.skill.registry.SkillRegistry;
import com.ranyk.spring.ai.rag.knowledge.database.config.properties.SkillProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

/**
 * CLASS_NAME: SkillsExecutorTest.java
 *
 * @author ranyk
 * @version V1.0
 * @description: SkillsExecutor 单元测试
 * @date: 2026-07-06
 */
@DisplayName("SkillsExecutor 测试")
class SkillsExecutorTest {

    private SkillsExecutor executor;
    private SkillRegistry registry;
    
    @Mock
    private SkillProperties properties;
    
    @Mock
    private SkillHandler mockHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 配置默认属性
        when(properties.getAsyncEnabled()).thenReturn(false);
        when(properties.getTimeoutSeconds()).thenReturn(30);
        
        registry = new SkillRegistry();
        executor = new SkillsExecutor(registry, properties);
    }

    @Test
    @DisplayName("测试执行 Skill - 成功情况")
    void testExecute_Success() throws Exception {
        // Given
        SkillDefinition definition = createSkillDefinition("test-skill", "测试技能");
        registry.registerSkill(definition, mockHandler);
        
        when(mockHandler.execute(anyMap())).thenReturn("Success Result");
        Map<String, Object> params = new HashMap<>();
        params.put("key", "value");

        // When
        SkillExecutionResult result = executor.execute("test-skill", params);

        // Then
        assertTrue(result.getSuccess());
        assertEquals("test-skill", result.getSkillId());
        assertEquals("Success Result", result.getData());
        assertNotNull(result.getExecutionTimeMs());
    }

    @Test
    @DisplayName("测试执行 Skill - Skill 不存在")
    void testExecute_SkillNotFound() {
        // When
        SkillExecutionResult result = executor.execute("non-existent", new HashMap<>());

        // Then
        assertFalse(result.getSuccess());
        assertEquals("SKILL_NOT_FOUND", result.getErrorCode());
        assertTrue(result.getErrorMessage().contains("不存在"));
    }

    @Test
    @DisplayName("测试执行 Skill - 缺少必需参数")
    void testExecute_MissingRequiredParam() {
        // Given
        SkillDefinition definition = createSkillDefinition("test-skill", "测试技能");
        definition.setRequiredParams(Arrays.asList("required_param"));
        registry.registerSkill(definition, mockHandler);

        Map<String, Object> params = new HashMap<>();
        // 不提供必需参数

        // When
        SkillExecutionResult result = executor.execute("test-skill", params);

        // Then
        assertFalse(result.getSuccess());
        assertEquals("MISSING_REQUIRED_PARAM", result.getErrorCode());
    }

    @Test
    @DisplayName("测试异步执行 Skill")
    void testExecuteAsync() throws Exception {
        // Given
        SkillDefinition definition = createSkillDefinition("async-skill", "异步技能");
        registry.registerSkill(definition, mockHandler);
        
        when(properties.getAsyncEnabled()).thenReturn(true);
        when(properties.getAsyncPoolSize()).thenReturn(4);
        executor = new SkillsExecutor(registry, properties);
        
        when(mockHandler.execute(anyMap())).thenReturn("Async Result");

        // When
        CompletableFuture<SkillExecutionResult> future = executor.executeAsync("async-skill", new HashMap<>());
        SkillExecutionResult result = future.get(5, TimeUnit.SECONDS);

        // Then
        assertTrue(result.getSuccess());
        assertEquals("Async Result", result.getData());
    }

    @Test
    @DisplayName("测试批量执行 Skills")
    void testBatchExecute() throws Exception {
        // Given
        SkillDefinition skill1 = createSkillDefinition("skill-1", "技能1");
        SkillDefinition skill2 = createSkillDefinition("skill-2", "技能2");
        registry.registerSkill(skill1, mockHandler);
        registry.registerSkill(skill2, mockHandler);
        
        when(mockHandler.execute(anyMap()))
                .thenReturn("Result 1")
                .thenReturn("Result 2");

        List<String> skillIds = Arrays.asList("skill-1", "skill-2");
        List<Map<String, Object>> paramsList = Arrays.asList(new HashMap<>(), new HashMap<>());

        // When
        List<SkillExecutionResult> results = executor.batchExecute(skillIds, paramsList);

        // Then
        assertEquals(2, results.size());
        assertTrue(results.get(0).getSuccess());
        assertTrue(results.get(1).getSuccess());
    }

    @Test
    @DisplayName("测试链式执行 Skills")
    void testChainExecute() throws Exception {
        // Given
        SkillDefinition skill1 = createSkillDefinition("skill-1", "技能1");
        SkillDefinition skill2 = createSkillDefinition("skill-2", "技能2");
        registry.registerSkill(skill1, mockHandler);
        registry.registerSkill(skill2, mockHandler);
        
        // 第一个 Skill 返回 Map,第二个 Skill 使用这个结果
        Map<String, Object> firstResult = new HashMap<>();
        firstResult.put("intermediate_data", "data_from_skill_1");
        
        when(mockHandler.execute(anyMap()))
                .thenReturn(firstResult)
                .thenReturn("Final Result");

        List<String> skillIds = Arrays.asList("skill-1", "skill-2");
        Map<String, Object> initialParams = new HashMap<>();
        initialParams.put("initial", "value");

        // When
        SkillExecutionResult result = executor.chainExecute(skillIds, initialParams);

        // Then
        assertTrue(result.getSuccess());
        assertEquals("Final Result", result.getData());
    }

    @Test
    @DisplayName("测试链式执行 - 中间失败")
    void testChainExecute_FailureInMiddle() throws Exception {
        // Given
        SkillDefinition skill1 = createSkillDefinition("skill-1", "技能1");
        SkillDefinition skill2 = createSkillDefinition("skill-2", "技能2");
        registry.registerSkill(skill1, mockHandler);
        registry.registerSkill(skill2, mockHandler);
        
        // 第一个成功,第二个失败
        when(mockHandler.execute(anyMap()))
                .thenReturn("Success")
                .thenThrow(new RuntimeException("Simulated failure"));

        List<String> skillIds = Arrays.asList("skill-1", "skill-2");

        // When
        SkillExecutionResult result = executor.chainExecute(skillIds, new HashMap<>());

        // Then
        assertFalse(result.getSuccess());
        // 错误消息被包装成 "Skill [skill-2] 执行失败: Simulated failure"
        assertTrue(result.getErrorMessage().contains("Simulated failure") || 
                   result.getErrorMessage().contains("skill-2"));
    }

    @Test
    @DisplayName("测试并行执行 Skills")
    void testParallelExecute() throws Exception {
        // Given
        SkillDefinition skill1 = createSkillDefinition("skill-1", "技能1");
        SkillDefinition skill2 = createSkillDefinition("skill-2", "技能2");
        registry.registerSkill(skill1, mockHandler);
        registry.registerSkill(skill2, mockHandler);
        
        when(properties.getAsyncEnabled()).thenReturn(true);
        when(properties.getAsyncPoolSize()).thenReturn(4);
        executor = new SkillsExecutor(registry, properties);
        
        when(mockHandler.execute(anyMap()))
                .thenReturn("Result 1")
                .thenReturn("Result 2");

        List<String> skillIds = Arrays.asList("skill-1", "skill-2");
        List<Map<String, Object>> paramsList = Arrays.asList(new HashMap<>(), new HashMap<>());

        // When
        List<CompletableFuture<SkillExecutionResult>> futures = executor.parallelExecute(skillIds, paramsList);
        
        // 等待所有完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // Then
        assertEquals(2, futures.size());
        assertTrue(futures.get(0).isDone());
        assertTrue(futures.get(1).isDone());
        assertTrue(futures.get(0).get().getSuccess());
        assertTrue(futures.get(1).get().getSuccess());
    }

    @Test
    @DisplayName("测试关闭执行器")
    void testShutdown() {
        // Given
        when(properties.getAsyncEnabled()).thenReturn(true);
        when(properties.getAsyncPoolSize()).thenReturn(4);
        executor = new SkillsExecutor(registry, properties);

        // When
        executor.shutdown();

        // Then - 不应该抛出异常
        assertDoesNotThrow(() -> executor.shutdown());
    }

    @Test
    @DisplayName("测试异步执行器未启用时抛出异常")
    void testExecuteAsync_NotEnabled() {
        // Given
        when(properties.getAsyncEnabled()).thenReturn(false);
        executor = new SkillsExecutor(registry, properties);

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            executor.executeAsync("test-skill", new HashMap<>());
        });
    }

    @Test
    @DisplayName("测试批量执行参数数量不匹配")
    void testBatchExecute_SizeMismatch() {
        // Given
        List<String> skillIds = Arrays.asList("skill-1", "skill-2");
        List<Map<String, Object>> paramsList = Collections.singletonList(new HashMap<>());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            executor.batchExecute(skillIds, paramsList);
        });
    }

    /**
     * 创建测试用的 SkillDefinition
     */
    private SkillDefinition createSkillDefinition(String id, String name) {
        SkillDefinition definition = new SkillDefinition();
        definition.setId(id);
        definition.setName(name);
        definition.setDescription("测试描述");
        definition.setEnabled(true);
        return definition;
    }
}
