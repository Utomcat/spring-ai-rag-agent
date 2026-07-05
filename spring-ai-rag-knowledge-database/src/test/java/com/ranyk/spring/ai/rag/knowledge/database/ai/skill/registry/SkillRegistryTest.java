package com.ranyk.spring.ai.rag.knowledge.database.ai.skill.registry;

import com.ranyk.spring.ai.rag.knowledge.database.ai.skill.handler.SkillHandler;
import com.ranyk.spring.ai.rag.knowledge.database.ai.skill.model.SkillDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CLASS_NAME: SkillRegistryTest.java
 *
 * @author ranyk
 * @version V1.0
 * @description: SkillRegistry 单元测试
 * @date: 2026-07-06
 */
@DisplayName("SkillRegistry 测试")
class SkillRegistryTest {

    private SkillRegistry registry;
    private SkillHandler mockHandler;

    @BeforeEach
    void setUp() {
        registry = new SkillRegistry();
        // 创建一个简单的 Mock Handler
        mockHandler = params -> "Mock Result";
    }

    @Test
    @DisplayName("测试注册 Skill - 正常情况")
    void testRegisterSkill_Success() {
        // Given
        SkillDefinition definition = createTestSkill("test-skill", "测试技能");

        // When
        registry.registerSkill(definition, mockHandler);

        // Then
        assertTrue(registry.hasSkill("test-skill"));
        assertEquals(1, registry.getSkillCount());
        assertNotNull(registry.getSkillDefinition("test-skill"));
        assertNotNull(registry.getSkillHandler("test-skill"));
    }

    @Test
    @DisplayName("测试注册 Skill - 定义为空")
    void testRegisterSkill_NullDefinition() {
        assertThrows(IllegalArgumentException.class, () -> {
            registry.registerSkill(null, mockHandler);
        });
    }

    @Test
    @DisplayName("测试注册 Skill - Handler 为空")
    void testRegisterSkill_NullHandler() {
        SkillDefinition definition = createTestSkill("test-skill", "测试技能");
        
        assertThrows(IllegalArgumentException.class, () -> {
            registry.registerSkill(definition, null);
        });
    }

    @Test
    @DisplayName("测试重复注册 Skill - 应覆盖")
    void testRegisterSkill_DuplicateRegistration() {
        // Given
        SkillDefinition definition1 = createTestSkill("test-skill", "原始技能");
        SkillDefinition definition2 = createTestSkill("test-skill", "新技能");

        // When
        registry.registerSkill(definition1, mockHandler);
        registry.registerSkill(definition2, mockHandler);

        // Then
        assertEquals(1, registry.getSkillCount());
        assertEquals("新技能", registry.getSkillDefinition("test-skill").getName());
    }

    @Test
    @DisplayName("测试注销 Skill - 存在")
    void testUnregisterSkill_Exists() {
        // Given
        SkillDefinition definition = createTestSkill("test-skill", "测试技能");
        registry.registerSkill(definition, mockHandler);

        // When
        boolean result = registry.unregisterSkill("test-skill");

        // Then
        assertTrue(result);
        assertFalse(registry.hasSkill("test-skill"));
        assertEquals(0, registry.getSkillCount());
    }

    @Test
    @DisplayName("测试注销 Skill - 不存在")
    void testUnregisterSkill_NotExists() {
        // When
        boolean result = registry.unregisterSkill("non-existent");

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("测试获取 Skill 定义")
    void testGetSkillDefinition() {
        // Given
        SkillDefinition definition = createTestSkill("test-skill", "测试技能");
        registry.registerSkill(definition, mockHandler);

        // When
        SkillDefinition retrieved = registry.getSkillDefinition("test-skill");

        // Then
        assertNotNull(retrieved);
        assertEquals("test-skill", retrieved.getId());
        assertEquals("测试技能", retrieved.getName());
    }

    @Test
    @DisplayName("测试获取不存在的 Skill 定义")
    void testGetSkillDefinition_NotExists() {
        // When
        SkillDefinition retrieved = registry.getSkillDefinition("non-existent");

        // Then
        assertNull(retrieved);
    }

    @Test
    @DisplayName("测试获取所有 Skill IDs")
    void testGetAllSkillIds() {
        // Given
        registry.registerSkill(createTestSkill("skill-1", "技能1"), mockHandler);
        registry.registerSkill(createTestSkill("skill-2", "技能2"), mockHandler);
        registry.registerSkill(createTestSkill("skill-3", "技能3"), mockHandler);

        // When
        Set<String> ids = registry.getAllSkillIds();

        // Then
        assertEquals(3, ids.size());
        assertTrue(ids.contains("skill-1"));
        assertTrue(ids.contains("skill-2"));
        assertTrue(ids.contains("skill-3"));
    }

    @Test
    @DisplayName("测试获取所有已启用的 Skills")
    void testGetAllEnabledSkills() {
        // Given
        SkillDefinition enabled1 = createTestSkill("enabled-1", "启用技能1");
        SkillDefinition enabled2 = createTestSkill("enabled-2", "启用技能2");
        SkillDefinition disabled = createTestSkill("disabled", "禁用技能");
        disabled.setEnabled(false);

        registry.registerSkill(enabled1, mockHandler);
        registry.registerSkill(enabled2, mockHandler);
        registry.registerSkill(disabled, mockHandler);

        // When
        List<SkillDefinition> enabledSkills = registry.getAllEnabledSkills();

        // Then
        assertEquals(2, enabledSkills.size());
        assertTrue(enabledSkills.stream().allMatch(SkillDefinition::getEnabled));
    }

    @Test
    @DisplayName("测试根据分类获取 Skills")
    void testGetSkillsByCategory() {
        // Given
        SkillDefinition docSkill = createTestSkill("doc-skill", "文档处理");
        docSkill.setCategory("document");
        
        SkillDefinition dbSkill = createTestSkill("db-skill", "数据库查询");
        dbSkill.setCategory("database");
        
        SkillDefinition anotherDocSkill = createTestSkill("another-doc", "另一个文档技能");
        anotherDocSkill.setCategory("document");

        registry.registerSkill(docSkill, mockHandler);
        registry.registerSkill(dbSkill, mockHandler);
        registry.registerSkill(anotherDocSkill, mockHandler);

        // When
        List<SkillDefinition> docSkills = registry.getSkillsByCategory("document");

        // Then
        assertEquals(2, docSkills.size());
        assertTrue(docSkills.stream().allMatch(s -> "document".equals(s.getCategory())));
    }

    @Test
    @DisplayName("测试根据标签获取 Skills")
    void testGetSkillsByTag() {
        // Given
        SkillDefinition skill1 = createTestSkill("skill-1", "技能1");
        skill1.setTags(Arrays.asList("ai", "text"));
        
        SkillDefinition skill2 = createTestSkill("skill-2", "技能2");
        skill2.setTags(Arrays.asList("ai", "image"));
        
        SkillDefinition skill3 = createTestSkill("skill-3", "技能3");
        skill3.setTags(Arrays.asList("data"));

        registry.registerSkill(skill1, mockHandler);
        registry.registerSkill(skill2, mockHandler);
        registry.registerSkill(skill3, mockHandler);

        // When
        List<SkillDefinition> aiSkills = registry.getSkillsByTag("ai");

        // Then
        assertEquals(2, aiSkills.size());
        assertTrue(aiSkills.stream().allMatch(s -> s.getTags().contains("ai")));
    }

    @Test
    @DisplayName("测试清空所有 Skills")
    void testClear() {
        // Given
        registry.registerSkill(createTestSkill("skill-1", "技能1"), mockHandler);
        registry.registerSkill(createTestSkill("skill-2", "技能2"), mockHandler);

        // When
        registry.clear();

        // Then
        assertEquals(0, registry.getSkillCount());
        assertTrue(registry.getAllSkillIds().isEmpty());
    }

    /**
     * 创建测试用的 SkillDefinition
     */
    private SkillDefinition createTestSkill(String id, String name) {
        SkillDefinition definition = new SkillDefinition();
        definition.setId(id);
        definition.setName(name);
        definition.setDescription("测试描述");
        definition.setEnabled(true);
        return definition;
    }
}
