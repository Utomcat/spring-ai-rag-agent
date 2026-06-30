package com.ranyk.spring.ai.rag.knowledge.database.config;

import com.ranyk.spring.ai.rag.knowledge.database.config.properties.*;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * CLASS_NAME: ApplicationConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 应用配置类, 用于配置应用自定义的一些配置 Bean 或进行加载应用所需的配置属性
 * @date: 2026-06-25
 */
@Configuration
@AutoConfiguration
@EnableConfigurationProperties(value = {
        HikariDataSourceProperties.class,
        VectorStoreProperties.class,
        FileProperties.class,
        JwtProperties.class,
        CorsProperties.class,
        RdbProperties.class
})
public class ApplicationConfiguration {
}
