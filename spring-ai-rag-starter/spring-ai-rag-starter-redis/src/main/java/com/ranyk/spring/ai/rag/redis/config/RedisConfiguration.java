package com.ranyk.spring.ai.rag.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.data.redis.autoconfigure.DataRedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.RedisClient;

/**
 * CLASS_NAME: RedisConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Redis 配置类
 * @date: 2026-07-10
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(DataRedisProperties.class)
public class RedisConfiguration {

    /**
     * 数据 Redis 配置属性 对象
     */
    private final DataRedisProperties dataRedisProperties;

    @Autowired
    public RedisConfiguration(DataRedisProperties dataRedisProperties) {
        this.dataRedisProperties = dataRedisProperties;
    }

    /**
     * 创建 Jedis 的 RedisClient 的 Bean 实例对象
     *
     * @return {@link RedisClient} 对象
     */
    @Bean
    @Primary
    public RedisClient redisClient() {
        log.debug("================================ 创建 RedisClient start  ======================================");
        log.debug("创建 RedisClient Bean 中 ... ");
        log.debug("================================ 创建 RedisClient end    ======================================");
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
