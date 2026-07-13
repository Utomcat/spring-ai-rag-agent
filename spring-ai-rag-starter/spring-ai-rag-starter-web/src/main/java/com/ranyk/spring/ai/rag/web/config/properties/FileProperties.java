package com.ranyk.spring.ai.rag.web.config.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * CLASS_NAME: FileProperties.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 文件配置属性类
 * @date: 2026-06-25
 */
@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = FileProperties.CONFIG_PREFIX)
public class FileProperties {
    /**
     * 自定义 - 文件配置属性前缀
     */
    public static final String CONFIG_PREFIX = "file";

    /**
     * 自定义 - 文件上传属性
     */
    private Upload upload;

    /**
     * 自定义 - 直接访问文件的请求 pathPatterns
     */
    private List<String> resourceHandlers = List.of("/files/**");

    /**
     * 自定义 - 文件上传属性类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Upload {

        /**
         * 自定义 - 文件上传根目录属性
         */
        private String root = "/upload";
    }
}
