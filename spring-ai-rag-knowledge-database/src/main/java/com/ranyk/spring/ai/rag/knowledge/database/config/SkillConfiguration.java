package com.ranyk.spring.ai.rag.knowledge.database.config;

import com.ranyk.spring.ai.rag.knowledge.database.ai.skill.executor.SkillsExecutor;
import com.ranyk.spring.ai.rag.knowledge.database.ai.skill.handler.DataAnalysisSkillHandler;
import com.ranyk.spring.ai.rag.knowledge.database.ai.skill.model.SkillDefinition;
import com.ranyk.spring.ai.rag.knowledge.database.ai.skill.registry.SkillRegistry;
import com.ranyk.spring.ai.rag.knowledge.database.config.properties.SkillProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CLASS_NAME: SkillConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Skills 配置类 - 负责注册 Skills 相关的 Bean
 * @date: 2026-07-06
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "ai.skills", name = "enabled", havingValue = "true")
public class SkillConfiguration {
    
    private final SkillProperties skillProperties;
    private final ApplicationContext applicationContext;
    
    /**
     * 注册 SkillRegistry
     */
    @Bean
    public SkillRegistry skillRegistry() {
        log.info("初始化 SkillRegistry");
        return new SkillRegistry();
    }
    
    /**
     * 注册 SkillsExecutor
     */
    @Bean
    public SkillsExecutor skillsExecutor(SkillRegistry skillRegistry) {
        log.info("初始化 SkillsExecutor");
        SkillsExecutor executor = new SkillsExecutor(skillRegistry, skillProperties);
        
        // 自动发现 Skills
        if (skillProperties.getAutoDiscover()) {
            executor.autoDiscoverSkills();
        }
        
        // 注册默认的 Skill Handlers
        registerDefaultSkills(skillRegistry);
        
        return executor;
    }
    
    /**
     * 注册默认的 Skills
     */
    private void registerDefaultSkills(SkillRegistry skillRegistry) {
        log.info("注册默认 Skills");
        
        // 注册数据分析 Skill
        DataAnalysisSkillHandler dataAnalysisHandler = getBeanOrNull(DataAnalysisSkillHandler.class);
        if (dataAnalysisHandler != null) {
            SkillDefinition dataAnalysisDef = SkillDefinition.builder()
                    .id("data-analysis")
                    .name("数据分析")
                    .description("对数据进行统计分析、趋势分析和可视化")
                    .category("data_processing")
                    .tags(java.util.Arrays.asList("data", "analysis", "statistics"))
                    .requiredParams(java.util.Arrays.asList("input_data"))
                    .optionalParams(java.util.Arrays.asList("format", "chart_type"))
                    .enabled(true)
                    .handler("com.ranyk.spring.ai.rag.knowledge.database.ai.skill.handler.DataAnalysisSkillHandler")
                    .build();
            
            skillRegistry.registerSkill(dataAnalysisDef, dataAnalysisHandler);
        }
        
        log.info("默认 Skills 注册完成,共注册 {} 个 Skills", skillRegistry.getSkillCount());
    }
    
    /**
     * 安全获取 Bean
     */
    private <T> T getBeanOrNull(Class<T> beanClass) {
        try {
            return applicationContext.getBean(beanClass);
        } catch (Exception e) {
            log.warn("未找到 Bean: {}", beanClass.getSimpleName());
            return null;
        }
    }
}
