package com.ranyk.spring.ai.rag.knowledge.database;

import com.ranyk.spring.ai.rag.datasource.config.MyBatisPlusConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * CLASS_NAME: SpringAiRagExampleKnowledgeDatabaseApplication.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 系统启动类
 * @date: 2026-06-27
 */
@Import(MyBatisPlusConfiguration.class)
@MapperScans(value = {
        @MapperScan(value = {"com.ranyk.spring.ai.rag.knowledge.database.repository"})
})
@SpringBootApplication(scanBasePackages = {
        "com.ranyk.spring.ai.rag.knowledge.database.ai",
        "com.ranyk.spring.ai.rag.knowledge.database.api",
        "com.ranyk.spring.ai.rag.knowledge.database.domain",
        "com.ranyk.spring.ai.rag.knowledge.database.service",
        "com.ranyk.spring.ai.rag.knowledge.database.repository",
}, exclude = {DataSourceAutoConfiguration.class})
public class SpringAiRagExampleKnowledgeDatabaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiRagExampleKnowledgeDatabaseApplication.class, args);
    }

}
