package com.ranyk.spring.ai.rag.vector.store.config;

import com.ranyk.spring.ai.rag.vector.store.config.properties.VectorStoreProperties;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.observation.VectorStoreObservationConvention;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.ai.vectorstore.redis.autoconfigure.RedisVectorStoreProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import redis.clients.jedis.RedisClient;

/**
 * CLASS_NAME: RedisVectorStoreConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Redis 向量存储配置类
 * @date: 2026-07-10
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(value = {RedisVectorStoreProperties.class, VectorStoreProperties.class})
public class RedisVectorStoreConfiguration {

    /**
     * Redis 向量存储 配置属性 对象
     */
    private final RedisVectorStoreProperties redisVectorStoreProperties;
    /**
     * 自定义向量存储 配置属性 对象
     */
    private final VectorStoreProperties vectorStoreProperties;

    @Autowired
    public RedisVectorStoreConfiguration(RedisVectorStoreProperties redisVectorStoreProperties,
                                         VectorStoreProperties vectorStoreProperties) {
        this.redisVectorStoreProperties = redisVectorStoreProperties;
        this.vectorStoreProperties = vectorStoreProperties;
    }

    /**
     * 创建 RedisVectorStore 对象
     *
     * @param redisClient           Redis 客户端 {@link RedisClient} 对象
     * @param embeddingModel        嵌入模型 {@link EmbeddingModel} 对象
     * @param observationRegistry   观察注册表 {@link ObservationRegistry} 对象
     * @param observationConvention 观察惯例 {@link VectorStoreObservationConvention} 对象
     * @param batchingStrategy      批处理策略 {@link BatchingStrategy} 对象
     * @return {@link RedisVectorStore} 对象
     */
    @Bean
    @Primary
    public RedisVectorStore redisVectorStore(
            RedisClient redisClient,
            @Qualifier("ollamaEmbeddingModel") EmbeddingModel embeddingModel,
            ObjectProvider<ObservationRegistry> observationRegistry,
            ObjectProvider<VectorStoreObservationConvention> observationConvention,
            ObjectProvider<BatchingStrategy> batchingStrategy) {
        log.debug("================================= 创建 RedisVectorStore start   ============");
        log.debug("创建 RedisVectorStore Bean 中 ... ");
        RedisVectorStore.Builder builder = RedisVectorStore.builder(redisClient, embeddingModel)
                // 设置是否初始化
                .initializeSchema(redisVectorStoreProperties.isInitializeSchema())
                // 设置索引名称
                .indexName(redisVectorStoreProperties.getIndexName())
                // 设置前缀
                .prefix(redisVectorStoreProperties.getPrefix())
                // 设置元数据字段
                .metadataFields(vectorStoreProperties.getRedis().getMetadataField().getTags().stream().map(RedisVectorStore.MetadataField::tag).toList())
                // 设置观察注册表
                .observationRegistry(observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP))
                // 设置自定义观察惯例
                .customObservationConvention(observationConvention.getIfAvailable());
        // 设置批处理策略
        batchingStrategy.ifUnique(builder::batchingStrategy);
        log.debug("================================= 创建 RedisVectorStore end     ============");
        return builder.build();
    }


}
