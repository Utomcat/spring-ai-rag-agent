package com.ranyk.spring.ai.rag.security.config.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * CLASS_NAME: JwtProperties.java
 *
 * @author ranyk
 * @version V1.0
 * @description: JWT 认证属性配置类
 * @date: 2026-06-27
 */
@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = JwtProperties.CONFIG_PREFIX)
public class JwtProperties {
    /**
     * 自定义 - 文件配置属性前缀
     */
    public static final String CONFIG_PREFIX = "jwt";

    /**
     * jwt 加密密钥
     */
    private String secret = "ranyk-rag-knowledge-database-secret-key-change-in-prod-min-32bytes!!";
    /**
     * token 过期时间（小时）
     */
    private int expireHours = 1;
}
