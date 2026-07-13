package com.ranyk.spring.ai.rag.web.config.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * CLASS_NAME: CorsConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Cors 跨域访问相关配置属性
 * @date: 2026-06-27
 */
@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = CorsProperties.CONFIG_PREFIX)
public class CorsProperties {
    /**
     * 自定义 - 跨域配置属性前缀
     */
    public static final String CONFIG_PREFIX = "cors";

    /**
     * 跨域访问的映射路径
     */
    private String mapping = "/**";
    /**
     * 允许的源列表
     */
    private List<String>  allowedOriginPatterns = List.of("*");
    /**
     * 允许的 HTTP 方法列表
     */
    private List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");
    /**
     * 允许的 HTTP 头列表
     */
    private List<String> allowedHeaders = List.of("*") ;
    /**
     * 是否允许发送Cookie
     */
    private Boolean allowCredentials = true;
    /**
     * 预检请求的缓存时间
     */
    private Long maxAge = 3600L;
}
