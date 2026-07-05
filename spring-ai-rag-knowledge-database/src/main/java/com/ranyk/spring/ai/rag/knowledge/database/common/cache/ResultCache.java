package com.ranyk.spring.ai.rag.knowledge.database.common.cache;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * CLASS_NAME: ResultCache.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 通用结果缓存 - 提供带过期时间的内存缓存功能
 * @date: 2026-07-06
 */
@Slf4j
public class ResultCache<K, V> {
    
    /**
     * 缓存存储
     */
    private final ConcurrentHashMap<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    
    /**
     * 缓存容量限制
     */
    private final int maxSize;
    
    /**
     * 默认过期时间(秒)
     */
    private final long defaultTtlSeconds;
    
    /**
     * 缓存命中统计
     */
    private long hitCount = 0;
    
    /**
     * 缓存未命中统计
     */
    private long missCount = 0;

    /**
         * 缓存条目
         */
        private record CacheEntry<V>(V value, long expireTime) {
            private CacheEntry(V value, long expireTime) {
                this.value = value;
                this.expireTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(expireTime);
            }

        public boolean isExpired() {
                return System.currentTimeMillis() > expireTime;
            }

        }
    
    /**
     * 构造方法
     *
     * @param maxSize           最大缓存条目数
     * @param defaultTtlSeconds 默认过期时间(秒)
     */
    public ResultCache(int maxSize, long defaultTtlSeconds) {
        this.maxSize = maxSize;
        this.defaultTtlSeconds = defaultTtlSeconds;
        log.info("ResultCache 初始化完成,最大容量: {}, 默认TTL: {}秒", maxSize, defaultTtlSeconds);
    }
    
    /**
     * 获取缓存值,如果不存在则使用 loader 加载
     *
     * @param key    缓存键
     * @param loader 数据加载器
     * @return 缓存值
     */
    public V get(K key, Supplier<V> loader) {
        // 尝试从缓存获取
        CacheEntry<V> entry = cache.get(key);
        
        if (entry != null && !entry.isExpired()) {
            hitCount++;
            log.debug("缓存命中: {}", key);
            return entry.value();
        }
        
        // 缓存未命中或已过期
        missCount++;
        log.debug("缓存未命中: {}", key);
        
        // 加载新值
        V value = loader.get();
        
        // 放入缓存
        put(key, value);
        
        return value;
    }
    
    /**
     * 获取缓存值(不自动加载)
     *
     * @param key 缓存键
     * @return 缓存值,不存在或已过期返回 null
     */
    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        
        if (entry != null && !entry.isExpired()) {
            hitCount++;
            return entry.value();
        }
        
        // 如果已过期,删除
        if (entry != null) {
            cache.remove(key);
        }
        
        missCount++;
        return null;
    }
    
    /**
     * 放入缓存(使用默认 TTL)
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public void put(K key, V value) {
        put(key, value, defaultTtlSeconds);
    }
    
    /**
     * 放入缓存(自定义 TTL)
     *
     * @param key         缓存键
     * @param value       缓存值
     * @param ttlSeconds  过期时间(秒)
     */
    public void put(K key, V value, long ttlSeconds) {
        // 检查容量
        if (cache.size() >= maxSize && !cache.containsKey(key)) {
            evictOldest();
        }
        
        cache.put(key, new CacheEntry<>(value, ttlSeconds));
        log.debug("缓存已更新: {}, TTL: {}秒", key, ttlSeconds);
    }
    
    /**
     * 删除缓存
     *
     * @param key 缓存键
     */
    public void remove(K key) {
        cache.remove(key);
        log.debug("缓存已删除: {}", key);
    }
    
    /**
     * 清空缓存
     */
    public void clear() {
        cache.clear();
        hitCount = 0;
        missCount = 0;
        log.info("缓存已清空");
    }
    
    /**
     * 驱逐最旧的条目(简单策略:随机删除一个)
     */
    private void evictOldest() {
        if (!cache.isEmpty()) {
            K oldestKey = cache.keys().nextElement();
            cache.remove(oldestKey);
            log.warn("缓存已满,已驱逐条目: {}", oldestKey);
        }
    }
    
    /**
     * 清理过期的缓存条目
     *
     * @return 清理的数量
     */
    public int cleanup() {
        int cleaned = 0;
        for (K key : cache.keySet()) {
            CacheEntry<V> entry = cache.get(key);
            if (entry != null && entry.isExpired()) {
                cache.remove(key);
                cleaned++;
            }
        }
        
        if (cleaned > 0) {
            log.info("清理了 {} 个过期缓存条目", cleaned);
        }
        
        return cleaned;
    }
    
    /**
     * 获取缓存命中率
     *
     * @return 命中率(0-1之间)
     */
    public double getHitRate() {
        long total = hitCount + missCount;
        if (total == 0) {
            return 0.0;
        }
        return (double) hitCount / total;
    }
    
    /**
     * 获取缓存统计信息
     *
     * @return 统计信息字符串
     */
    public String getStats() {
        return String.format("缓存统计 - 大小: %d/%d, 命中: %d, 未命中: %d, 命中率: %.2f%%",
                cache.size(), maxSize, hitCount, missCount, getHitRate() * 100);
    }
    
    /**
     * 获取当前缓存大小
     *
     * @return 缓存条目数
     */
    public int size() {
        return cache.size();
    }
}


