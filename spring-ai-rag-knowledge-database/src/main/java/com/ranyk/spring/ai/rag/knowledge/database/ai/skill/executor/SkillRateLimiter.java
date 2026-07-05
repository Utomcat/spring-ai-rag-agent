package com.ranyk.spring.ai.rag.knowledge.database.ai.skill.executor;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * CLASS_NAME: SkillRateLimiter.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Skill 限流器 - 使用令牌桶算法实现限流
 * @date: 2026-07-06
 */
@Slf4j
public class SkillRateLimiter {
    
    /**
     * 每个 Skill 的限流器映射表
     */
    private final Map<String, TokenBucket> rateLimiters = new ConcurrentHashMap<>();
    
    /**
     * 默认每秒请求数
     */
    private final double defaultPermitsPerSecond;
    
    /**
     * 构造函数
     *
     * @param defaultPermitsPerSecond 默认每秒允许的请求数
     */
    public SkillRateLimiter(double defaultPermitsPerSecond) {
        this.defaultPermitsPerSecond = defaultPermitsPerSecond;
        log.info("Skill 限流器已初始化,默认速率: {} req/s", defaultPermitsPerSecond);
    }
    
    /**
     * 尝试获取许可(阻塞等待)
     *
     * @param skillId Skill ID
     * @return 是否成功获取许可
     */
    public boolean acquire(String skillId) {
        return acquire(skillId, 1);
    }
    
    /**
     * 尝试获取许可(阻塞等待)
     *
     * @param skillId   Skill ID
     * @param permits   需要的许可数
     * @return 是否成功获取许可
     */
    public boolean acquire(String skillId, int permits) {
        TokenBucket bucket = getOrCreateBucket(skillId);
        return bucket.acquire(permits);
    }
    
    /**
     * 尝试获取许可(带超时)
     *
     * @param skillId   Skill ID
     * @param permits   需要的许可数
     * @param timeout   超时时间
     * @param unit      时间单位
     * @return 是否成功获取许可
     */
    public boolean tryAcquire(String skillId, int permits, long timeout, TimeUnit unit) {
        TokenBucket bucket = getOrCreateBucket(skillId);
        return bucket.tryAcquire(permits, timeout, unit);
    }
    
    /**
     * 配置 Skill 的限流速率
     *
     * @param skillId          Skill ID
     * @param permitsPerSecond 每秒允许的请求数
     */
    public void configureRate(String skillId, double permitsPerSecond) {
        TokenBucket bucket = new TokenBucket(permitsPerSecond);
        rateLimiters.put(skillId, bucket);
        log.info("Skill [{}] 限流速率已配置为: {} req/s", skillId, permitsPerSecond);
    }
    
    /**
     * 获取或创建令牌桶
     *
     * @param skillId Skill ID
     * @return 令牌桶
     */
    private TokenBucket getOrCreateBucket(String skillId) {
        return rateLimiters.computeIfAbsent(skillId, 
                k -> new TokenBucket(defaultPermitsPerSecond));
    }
    
    /**
     * 移除 Skill 限流器
     *
     * @param skillId Skill ID
     */
    public void removeRateLimiter(String skillId) {
        rateLimiters.remove(skillId);
        log.info("Skill [{}] 限流器已移除", skillId);
    }
    
    /**
     * 清除所有限流器
     */
    public void clearAll() {
        rateLimiters.clear();
        log.info("所有限流器已清除");
    }
    
    /**
     * 令牌桶算法实现
     */
    private static class TokenBucket {
        private final double permitsPerSecond;
        private double availablePermits;
        private long lastRefillTime;
        private final Semaphore semaphore;
        
        public TokenBucket(double permitsPerSecond) {
            this.permitsPerSecond = permitsPerSecond;
            this.availablePermits = permitsPerSecond;
            this.lastRefillTime = System.nanoTime();
            this.semaphore = new Semaphore((int) permitsPerSecond);
        }
        
        public synchronized boolean acquire(int permits) {
            refill();
            
            if (availablePermits >= permits) {
                availablePermits -= permits;
                return true;
            }
            
            // 等待直到有足够的令牌
            long waitTime = (long) ((permits - availablePermits) / permitsPerSecond * 1000);
            try {
                Thread.sleep(waitTime);
                refill();
                availablePermits -= permits;
                return true;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        
        public synchronized boolean tryAcquire(int permits, long timeout, TimeUnit unit) {
            refill();
            
            if (availablePermits >= permits) {
                availablePermits -= permits;
                return true;
            }
            
            return false;
        }
        
        private synchronized void refill() {
            long now = System.nanoTime();
            long elapsed = now - lastRefillTime;
            double permitsToAdd = elapsed * permitsPerSecond / 1_000_000_000.0;
            
            if (permitsToAdd > 0) {
                availablePermits = Math.min(permitsPerSecond, availablePermits + permitsToAdd);
                lastRefillTime = now;
            }
        }
    }
}
