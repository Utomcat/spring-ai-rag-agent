package com.ranyk.spring.ai.rag.web.config;

import com.ranyk.spring.ai.rag.web.config.properties.FileProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * CLASS_NAME: UploadBootstrapConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 文件上传的配置类
 * @date: 2026-06-27
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(value = {FileProperties.class})
public class UploadBootstrapConfiguration {

    /**
     * 文件上传的属性配置
     */
    private final FileProperties fileProperties;

    /**
     * 构造方法 - 在 Spring IOC 容器创建该 Bean 时依赖注入 FileProperties 对象
     *
     * @param fileProperties 文件上传的属性配置 {@link FileProperties} 对象
     */
    @Autowired
    public UploadBootstrapConfiguration(FileProperties fileProperties) {
        this.fileProperties = fileProperties;
    }

    /**
     * 启动时确保文件上传根目录存在
     *
     * @return {@link ApplicationRunner} 对象
     */
    @Bean
    public ApplicationRunner ensureUploadDir() {
        log.debug("===========================  配置 ApplicationRunner Bean start  ===============================");
        log.debug("配置 ApplicationRunner Bean 处理中 ...");
        log.debug("===========================  配置 ApplicationRunner Bean end   ================================");
        return args -> {
            Path p = Paths.get(fileProperties.getUpload().getRoot());
            if (!Files.isDirectory(p)) {
                Files.createDirectories(p);
                log.info("文件上传保存根目录 {} 创建成功, 或已存在无需创建", p.toAbsolutePath());
            }
        };
    }
}
