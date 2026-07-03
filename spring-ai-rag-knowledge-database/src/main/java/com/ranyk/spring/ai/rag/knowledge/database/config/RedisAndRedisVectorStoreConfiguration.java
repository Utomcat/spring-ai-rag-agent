package com.ranyk.spring.ai.rag.knowledge.database.config;

import com.ranyk.spring.ai.rag.knowledge.database.config.properties.VectorStoreProperties;
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
import org.springframework.boot.data.redis.autoconfigure.DataRedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.RedisClient;

/**
 * CLASS_NAME: RedisAndRedisVectorStoreConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Redis 和 RedisVectorStore 配置类
 * @date: 2026-06-24
 */
@Slf4j
@Configuration
public class RedisAndRedisVectorStoreConfiguration {

    /**
     * Redis 向量存储 配置属性 对象
     */
    private final RedisVectorStoreProperties redisVectorStoreProperties;
    /**
     * 数据 Redis 配置属性 对象
     */
    private final DataRedisProperties dataRedisProperties;
    /**
     * 自定义向量存储 配置属性 对象
     */
    private final VectorStoreProperties vectorStoreProperties;

    /**
     * 构造方法 - 通过 Spring IOC 容器向 RedisAndRedisVectorStoreConfiguration 对象 Bean 中注入 RedisVectorStoreProperties 对象 、 DataRedisProperties 对象 和 VectorStoreProperties 对象
     *
     * @param redisVectorStoreProperties {@link RedisVectorStoreProperties} 对象
     * @param dataRedisProperties        {@link DataRedisProperties} 对象
     * @param vectorStoreProperties      {@link VectorStoreProperties} 对象
     */
    @Autowired
    public RedisAndRedisVectorStoreConfiguration(RedisVectorStoreProperties redisVectorStoreProperties,
                                                 VectorStoreProperties vectorStoreProperties,
                                                 DataRedisProperties dataRedisProperties) {
        this.redisVectorStoreProperties = redisVectorStoreProperties;
        this.dataRedisProperties = dataRedisProperties;
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
    public RedisVectorStore redisVectorStore(
            RedisClient redisClient,
            @Qualifier("ollamaEmbeddingModel") EmbeddingModel embeddingModel,
            ObjectProvider<ObservationRegistry> observationRegistry,
            ObjectProvider<VectorStoreObservationConvention> observationConvention,
            ObjectProvider<BatchingStrategy> batchingStrategy) {
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
        return builder.build();
    }

    /**
     * 创建 Jedis 的 RedisClient 的 Bean 实例对象
     *
     * @return {@link RedisClient} 对象
     */
    @Bean
    public RedisClient redisClient() {
        return RedisClient
                .builder()
                .hostAndPort(
                        // 设置 Redis 主机地址
                        dataRedisProperties.getHost(),
                        // 设置 Redis 端口号
                        dataRedisProperties.getPort()
                )
                .clientConfig(DefaultJedisClientConfig
                        .builder()
                        // 设置 Redis 用户名
                        .user(dataRedisProperties.getUsername())
                        // 设置 Redis 密码
                        .password(dataRedisProperties.getPassword())
                        .build()
                )
                .build();
    }
}
