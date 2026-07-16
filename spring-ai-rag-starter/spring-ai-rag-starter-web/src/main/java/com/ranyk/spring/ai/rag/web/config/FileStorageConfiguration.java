package com.ranyk.spring.ai.rag.web.config;

import com.ranyk.spring.ai.rag.web.config.properties.FileProperties;
import com.ranyk.spring.ai.rag.web.service.file.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CLASS_NAME: FileStorageConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 文件存储业务配置类
 * @date: 2026-07-17
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(value = {FileProperties.class})
public class FileStorageConfiguration {

    /**
     * 文件存储服务
     *
     * @param fileProperties 文件属性
     * @return 文件存储服务实例
     */
    @Bean
    public FileStorageService fileStorageService(FileProperties fileProperties) {
        return new FileStorageService(fileProperties);
    }
}
