package com.ranyk.spring.ai.rag.knowledge.database.config.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * CLASS_NAME: VectorStoreProperties.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 自定义向量存储配置属性类
 * @date: 2026-06-27
 */
@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = VectorStoreProperties.CONFIG_PREFIX)
public class VectorStoreProperties {
    /**
     * 自定义 - 文件配置属性前缀
     */
    public static final String CONFIG_PREFIX = "vector-store";

    /**
     * redis 向量存储配置属性
     */
    private Redis redis;
    /**
     * 自定义 - 向量数据删除属性
     */
    private Delete delete;
    /**
     * 自定义 - 删除文档时 SCAN 的 key 前缀（逗号分隔，末尾可加可不加冒号）；留空则自动包含上面 prefix + 历史 embedding:
     */
    private String deleteScanPrefixes = "";

    /**
     * redis 配置属性类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Redis{

        /**
         * 元数据字段配置属性
         */
        private MetadataField metadataField;

        /**
         * 元数据字段配置属性类
         */
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MetadataField{

            /**
             * 标签列表
             */
            private List<String> tags;
        }

    }

    /**
     * 自定义 - 向量数据删除属性类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Delete {
        /**
         * 自定义 - 向量数据删除每批次数量
         */
        private Integer batchQuantity;
    }
}
