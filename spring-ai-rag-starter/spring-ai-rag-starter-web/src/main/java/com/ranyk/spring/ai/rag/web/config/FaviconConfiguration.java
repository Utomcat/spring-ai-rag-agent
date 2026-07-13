package com.ranyk.spring.ai.rag.web.config;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CLASS_NAME: FaviconConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 图标资源配置类
 * @date: 2026-06-22
 */
@Slf4j
@Configuration
public class FaviconConfiguration implements WebMvcConfigurer {

    /**
     * 添加资源处理器
     *
     * @param registry 资源处理器
     */
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        log.debug("===========================  添加 Favicon ResourceHandlers start  =======================================");
        log.debug("添加 Favicon ResourceHandlers 中 ...");
        Resource resource = new ClassPathResource("static/favicon.ico");
        if (resource.exists()) {
            log.info("Favicon resource exists.");
            registry.addResourceHandler("/favicon.ico")
                    .addResourceLocations("classpath:/static/")
                    .setCachePeriod(3600);
        }
        log.debug("===========================  添加 Favicon ResourceHandlers end    =======================================");
    }
}
