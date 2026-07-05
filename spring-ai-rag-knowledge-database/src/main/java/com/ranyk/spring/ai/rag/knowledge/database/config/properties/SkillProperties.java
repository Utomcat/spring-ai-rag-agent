package com.ranyk.spring.ai.rag.knowledge.database.config.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * CLASS_NAME: SkillProperties.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Skills 配置属性类
 * @date: 2026-07-06
 */
@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = SkillProperties.CONFIG_PREFIX)
public class SkillProperties {
    /**
     * 配置属性前缀
     */
    public static final String CONFIG_PREFIX = "ai.skills";

    /**
     * 是否启用 Skills 功能
     */
    private Boolean enabled = true;

    /**
     * Skills 配置文件路径(支持 classpath: 或 file: 前缀)
     */
    private String registryPath = "classpath:skills/";

    /**
     * 是否自动发现并加载 Skills
     */
    private Boolean autoDiscover = true;

    /**
     * 最大并发执行数
     */
    private Integer maxConcurrent = 10;

    /**
     * 单个 Skill 执行超时时间(秒)
     */
    private Integer timeoutSeconds = 60;

    /**
     * 默认重试次数
     */
    private Integer defaultRetries = 3;

    /**
     * 是否启用异步执行
     */
    private Boolean asyncEnabled = true;

    /**
     * 异步线程池大小
     */
    private Integer asyncPoolSize = 5;
}
