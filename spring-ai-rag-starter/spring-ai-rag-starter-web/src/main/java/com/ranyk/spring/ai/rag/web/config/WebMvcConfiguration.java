package com.ranyk.spring.ai.rag.web.config;

import com.ranyk.spring.ai.rag.web.config.properties.CorsProperties;
import com.ranyk.spring.ai.rag.web.config.properties.FileProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CLASS_NAME: WebMvcConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Web MVC 跨域与本地文件目录映射(供前端访问已上传的文件)
 * @date: 2026-06-27
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(value = {CorsProperties.class})
public class WebMvcConfiguration implements WebMvcConfigurer {

    /**
     * 文件配置属性对象 {@link FileProperties}
     */
    private final FileProperties fileProperties;
    /**
     * 跨域配置属性对象 {@link CorsProperties}
     */
    private final CorsProperties corsProperties;

    /**
     * 构造函数 - 通过 Spring IOC 容器向当前 Bean 注入 文件配置属性对象 {@link FileProperties}
     *
     * @param fileProperties 文件配置属性对象 {@link FileProperties}
     * @param corsProperties 跨域配置属性对象 {@link CorsProperties}
     */
    @Autowired
    public WebMvcConfiguration(FileProperties fileProperties, CorsProperties corsProperties) {
        this.fileProperties = fileProperties;
        this.corsProperties = corsProperties;
    }

    /**
     * 跨域允许前端开发服务器访问
     *
     * @param registry 跨域注册表
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.debug("===========================  添加跨域映射 CorsMappings  start  ===============================");
        log.debug("添加跨域映射 CorsMappings 处理中 ...");
        registry.addMapping(corsProperties.getMapping())
                .allowedOriginPatterns(corsProperties.getAllowedOriginPatterns().toArray(new String[]{}))
                .allowedMethods(corsProperties.getAllowedMethods().toArray(new String[]{}))
                .allowedHeaders(corsProperties.getAllowedHeaders().toArray(new String[]{}))
                .allowCredentials(corsProperties.getAllowCredentials())
                .maxAge(corsProperties.getMaxAge());
        log.debug("===========================  添加跨域映射 CorsMappings end   ================================");
    }

    /**
     * 配置静态资源映射, 将 /uploads/** 映射到磁盘 uploads3 目录
     *
     * @param registry 资源注册表
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.debug("===========================  配置静态资源映射  start  ===============================");
        log.debug("配置静态资源映射 处理中 ...");
        String loc = fileProperties.getUpload().getRoot().endsWith("/") ? fileProperties.getUpload().getRoot() : fileProperties.getUpload().getRoot() + "/";
        log.info("文件上传根目录映射: /files/** -> {}", loc);
        registry.addResourceHandler(fileProperties.getResourceHandlers().toArray(new String[]{})).addResourceLocations("file:" + loc);
        log.debug("===========================  配置静态资源映射  end   ================================");
    }
}
