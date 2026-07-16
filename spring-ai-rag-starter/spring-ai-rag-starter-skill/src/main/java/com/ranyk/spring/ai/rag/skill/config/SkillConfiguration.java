package com.ranyk.spring.ai.rag.skill.config;

import lombok.extern.slf4j.Slf4j;
import org.springaicommunity.agent.tools.SkillsTool;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * CLASS_NAME: SkillConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Spring AI Skills 自动配置类 - 使用 spring-ai-agent-utils 的 SkillsTool 集成 ChatClient
 * @date: 2026-07-16
 */
@Slf4j
@Configuration
public class SkillConfiguration {

    /**
     * 创建 SkillsTool ToolCallback Bean，用于在 ChatClient 中集成 SKILL.md 技能
     * <p>
     * SkillsTool 会扫描 classpath:skills 目录下的所有 SKILL.md 文件，
     * 将技能列表注入到工具描述中，供 LLM 自主发现和按需加载
     * <p>
     * 技能文件放置在本模块的 src/main/resources/skills/ 目录下，
     * 每个技能一个子目录，子目录中必须包含 SKILL.md 文件
     *
     * @return SkillsTool ToolCallback 对象
     */
    @Bean
    public ToolCallback skillTool() {
        log.debug("================================= 配置 SkillsTool start =================================");
        log.debug("加载 Skills 目录: classpath:skills");
        // 先执行扫描技能目录操作 - 确认存在那些技能
        scanSkillsDirectory();
        // 创建 SkillsTool ToolCallback Bean
        ToolCallback skillTool = SkillsTool.builder()
                .addSkillsResource(new ClassPathResource("skills"))
                .build();
        log.debug("SkillsTool 配置完成, 技能将从 classpath:skills 目录加载");
        log.debug("================================= 配置 SkillsTool end   =================================");
        return skillTool;
    }

    /**
     * 扫描 classpath:skills 目录，确认存在那些技能
     */
    private void scanSkillsDirectory() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:skills/**");
            log.info("===== classpath:skills 目录下的文件列表 =====");
            for (Resource resource : resources) {
                log.info("  -> {} (exists => {}, isFile => {})", resource.getURL(), resource.exists(), resource.isFile());
            }
            log.info("===== 共发现 {} 个资源 =====", resources.length);
        } catch (Exception e) {
            log.warn("扫描 classpath:skills 目录失败: {}", e.getMessage());
        }
    }
}
