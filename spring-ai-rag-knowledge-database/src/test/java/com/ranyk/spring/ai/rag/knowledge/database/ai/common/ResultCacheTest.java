package com.ranyk.spring.ai.rag.knowledge.database.ai.common;

import org.junit.jupiter.api.*;

import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CLASS_NAME: ResultCacheTest.java
 *
 * @author ranyk
 * @version V1.0
 * @description: ResultCache 单元测试
 * @date: 2026-07-06
 */
@DisplayName("ResultCache 缓存测试")
class ResultCacheTest {
    
    private ResultCache<String, String> cache;
    
    @BeforeEach
    void setUp() {
        cache = new ResultCache<>(100, 60); // 最大100条,默认60秒过期
    }
    
    @Test
    @DisplayName("测试基本缓存操作")
    void testBasicCacheOperations() {
        // 放入缓存
        cache.put("key1", "value1");
        
        // 获取缓存
        assertEquals("value1", cache.get("key1"));
        
        // 删除缓存
        cache.remove("key1");
        assertNull(cache.get("key1"));
    }
    
    @Test
    @DisplayName("测试带加载器的缓存获取")
    void testGetWithLoader() {
        String result = cache.get("test-key", () -> "loaded-value");
        
        assertEquals("loaded-value", result);
        
        // 第二次应该从缓存获取
        String cachedResult = cache.get("test-key", () -> "should-not-be-called");
        assertEquals("loaded-value", cachedResult);
    }
    
    @Test
    @DisplayName("测试缓存过期")
    void testCacheExpiration() throws InterruptedException {
        ResultCache<String, String> shortTtlCache = new ResultCache<>(100, 1); // 1秒过期
        
        shortTtlCache.put("key", "value");
        assertNotNull(shortTtlCache.get("key"));
        
        // 等待过期
        Thread.sleep(1500);
        
        assertNull(shortTtlCache.get("key"));
    }
    
    @Test
    @DisplayName("测试缓存容量限制")
    void testCacheCapacityLimit() {
        ResultCache<String, String> smallCache = new ResultCache<>(3, 60);
        
        smallCache.put("key1", "value1");
        smallCache.put("key2", "value2");
        smallCache.put("key3", "value3");
        smallCache.put("key4", "value4"); // 应该触发驱逐
        
        assertEquals(3, smallCache.size());
    }
    
    @Test
    @DisplayName("测试缓存统计")
    void testCacheStats() {
        cache.get("non-existent", () -> "value"); // miss
        cache.get("non-existent", () -> "value"); // miss again
        cache.get("non-existent"); // hit
        
        assertTrue(cache.getHitRate() > 0);
        assertTrue(cache.getStats().contains("命中率"));
    }
    
    @Test
    @DisplayName("测试清理过期条目")
    void testCleanup() throws InterruptedException {
        ResultCache<String, String> shortTtlCache = new ResultCache<>(100, 1);
        
        shortTtlCache.put("key1", "value1");
        shortTtlCache.put("key2", "value2");
        
        Thread.sleep(1500);
        
        int cleaned = shortTtlCache.cleanup();
        assertEquals(2, cleaned);
        assertEquals(0, shortTtlCache.size());
    }
    
    @Test
    @DisplayName("测试清空缓存")
    void testClear() {
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        
        cache.clear();
        
        assertEquals(0, cache.size());
    }
}
