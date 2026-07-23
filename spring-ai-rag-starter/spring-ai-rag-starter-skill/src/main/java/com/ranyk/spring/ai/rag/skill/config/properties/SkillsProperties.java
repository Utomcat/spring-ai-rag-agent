package com.ranyk.spring.ai.rag.skill.config.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * CLASS_NAME: SkillsProperties.java
 *
 * @author ranyk
 * @version V1.0
 * @description: SKILLS 配置属性类
 * @date: 2026-07-23
 */
@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = SkillsProperties.CONFIG_PREFIX)
public class SkillsProperties {
    /**
     * 配置属性前缀
     */
    public static final String CONFIG_PREFIX = "skills";
    /**
     * SKILLS 描述信息列表,用于后续的自动添加系统提示词
     */
    private List<String> descriptions = new ArrayList<>(10);
}
