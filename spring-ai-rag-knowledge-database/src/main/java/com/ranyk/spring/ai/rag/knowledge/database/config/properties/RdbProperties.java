package com.ranyk.spring.ai.rag.knowledge.database.config.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * CLASS_NAME: RdbProperties.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 关系型数据库配置属性
 * @date: 2026-06-30
 */
@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = RdbProperties.CONFIG_PREFIX)
public class RdbProperties {

    /**
     * 配置属性前缀
     */
    public static final String CONFIG_PREFIX = "rdb";

    /**
     * 自定义 - ORM配置属性
     */
    private Orm orm;

    /**
     * 自定义ORM配置属性
     */
    @Data
    @Component
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Orm {

        /**
         * 需要跳过自动填充的表名列表, 在此处配置不需要自动填充审计字段的表
         */
        private List<String> skipAutoFillTables;
    }
}
