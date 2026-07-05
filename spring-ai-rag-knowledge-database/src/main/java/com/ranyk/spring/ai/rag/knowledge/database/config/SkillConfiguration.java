package com.ranyk.spring.ai.rag.knowledge.database.config;

import com.ranyk.spring.ai.rag.knowledge.database.ai.skill.executor.SkillsExecutor;
import com.ranyk.spring.ai.rag.knowledge.database.ai.skill.handler.CodeGenerationSkillHandler;
import com.ranyk.spring.ai.rag.knowledge.database.ai.skill.handler.DataAnalysisSkillHandler;
import com.ranyk.spring.ai.rag.knowledge.database.ai.skill.handler.DataVisualizationSkillHandler;
import com.ranyk.spring.ai.rag.knowledge.database.ai.skill.handler.DatabaseQuerySkillHandler;
import com.ranyk.spring.ai.rag.knowledge.database.ai.skill.handler.DocumentProcessingSkillHandler;
import com.ranyk.spring.ai.rag.knowledge.database.ai.skill.handler.WebSearchSkillHandler;
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
            registerSkill(skillRegistry, "data-analysis", "数据分析", 
                    "对数据进行统计分析、趋势分析和可视化",
                    "data_processing",
                    java.util.Arrays.asList("data", "analysis", "statistics"),
                    java.util.Arrays.asList("input_data"),
                    java.util.Arrays.asList("format", "chart_type"),
                    dataAnalysisHandler);
        }
        
        // 注册文档处理 Skill
        DocumentProcessingSkillHandler docHandler = getBeanOrNull(DocumentProcessingSkillHandler.class);
        if (docHandler != null) {
            registerSkill(skillRegistry, "document-processing", "文档处理",
                    "支持 PDF/Word/TXT 文本提取和格式转换",
                    "document",
                    java.util.Arrays.asList("document", "text", "conversion"),
                    java.util.Arrays.asList("file_path"),
                    java.util.Arrays.asList("output_format", "extract_metadata"),
                    docHandler);
        }
        
        // 注册数据库查询 Skill
        DatabaseQuerySkillHandler dbHandler = getBeanOrNull(DatabaseQuerySkillHandler.class);
        if (dbHandler != null) {
            registerSkill(skillRegistry, "database-query", "数据库查询",
                    "执行 SQL 查询并返回格式化结果",
                    "database",
                    java.util.Arrays.asList("sql", "query", "database"),
                    java.util.Arrays.asList("sql"),
                    java.util.Arrays.asList("jdbc_url", "username", "password", "max_rows"),
                    dbHandler);
        }
        
        // 注册代码生成 Skill
        CodeGenerationSkillHandler codeHandler = getBeanOrNull(CodeGenerationSkillHandler.class);
        if (codeHandler != null) {
            registerSkill(skillRegistry, "code-generation", "代码生成",
                    "根据描述生成 Java/Python/SQL 代码模板",
                    "development",
                    java.util.Arrays.asList("code", "generation", "template"),
                    java.util.Arrays.asList("description"),
                    java.util.Arrays.asList("language", "framework"),
                    codeHandler);
        }
        
        // 注册 Web 搜索 Skill
        WebSearchSkillHandler webSearchHandler = getBeanOrNull(WebSearchSkillHandler.class);
        if (webSearchHandler != null) {
            registerSkill(skillRegistry, "web-search", "Web 搜索",
                    "调用搜索引擎 API 获取搜索结果",
                    "search",
                    java.util.Arrays.asList("search", "web", "internet"),
                    java.util.Arrays.asList("query"),
                    java.util.Arrays.asList("num_results", "language"),
                    webSearchHandler);
        }
        
        // 注册数据可视化 Skill
        DataVisualizationSkillHandler dataVizHandler = getBeanOrNull(DataVisualizationSkillHandler.class);
        if (dataVizHandler != null) {
            registerSkill(skillRegistry, "data-visualization", "数据可视化",
                    "生成 ECharts/Matplotlib 图表配置",
                    "visualization",
                    java.util.Arrays.asList("chart", "visualization", "graph"),
                    java.util.Arrays.asList("data"),
                    java.util.Arrays.asList("chart_type", "title", "output_format"),
                    dataVizHandler);
        }
        
        log.info("默认 Skills 注册完成,共注册 {} 个 Skills", skillRegistry.getSkillCount());
    }
    
    /**
     * 注册单个 Skill
     */
    private void registerSkill(SkillRegistry registry, String id, String name, String description,
                              String category, java.util.List<String> tags,
                              java.util.List<String> requiredParams, java.util.List<String> optionalParams,
                              Object handler) {
        SkillDefinition definition = SkillDefinition.builder()
                .id(id)
                .name(name)
                .description(description)
                .category(category)
                .tags(tags)
                .requiredParams(requiredParams)
                .optionalParams(optionalParams)
                .enabled(true)
                .handler(handler.getClass().getName())
                .build();
        
        registry.registerSkill(definition, (com.ranyk.spring.ai.rag.knowledge.database.ai.skill.handler.SkillHandler) handler);
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
