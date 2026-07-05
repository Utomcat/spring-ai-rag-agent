"""热点数据缓存管理器 - 使用 Redis DB 1。

专门存储高频访问的热点数据，提供快速响应。
"""
import logging
import json
from typing import Any, Optional, Dict

import redis

from config.constants import (
    REDIS_HOST,
    REDIS_PORT,
    REDIS_PASSWORD,
    REDIS_TIMEOUT,
    REDIS_HOT_DATA_DB,
    REDIS_MAX_CONNECTIONS,
    HOT_DATA_CACHE_MAX_SIZE,
    HOT_DATA_CACHE_TTL,
    APP_ENVIRONMENT
)
from utils.cache_manager import CacheManager
from utils.cache_interface import CacheInterface

logger = logging.getLogger(__name__)


class HotDataCacheManager(CacheInterface):
    """热点数据缓存管理器 - 使用 Redis DB 1。
    
    特性:
    - 使用独立的 Redis DB (DB 1)
    - 较小的容量限制 (默认 50 条)
    - 较短的 TTL (默认 5 分钟)
    - 支持降级到内存缓存
    - LRU 淘汰策略
    """
    
    def __init__(
        self, 
        ttl: int = HOT_DATA_CACHE_TTL, 
        max_size: int = HOT_DATA_CACHE_MAX_SIZE
    ):
        """初始化热点数据缓存。
        
        Args:
            ttl: 缓存过期时间(秒)
            max_size: 最大缓存条目数
        """
        self.ttl = ttl
        self.max_size = max_size
        self.namespace = f"hot:{APP_ENVIRONMENT}"
        
        # 初始化 Redis 连接池 (DB 1)
        try:
            self.redis_pool = redis.ConnectionPool(
                host=REDIS_HOST,
                port=REDIS_PORT,
                db=REDIS_HOT_DATA_DB,
                password=REDIS_PASSWORD,
                socket_timeout=REDIS_TIMEOUT,
                max_connections=REDIS_MAX_CONNECTIONS,
                decode_responses=True
            )
            self.redis_client = redis.Redis(connection_pool=self.redis_pool)
            # 测试连接
            self.redis_client.ping()
            self.use_fallback = False
            logger.info(f"热点数据缓存已连接到 Redis DB {REDIS_HOT_DATA_DB}")
        except redis.exceptions.ConnectionError as e:
            logger.warning(f"Redis 连接失败，降级到内存缓存: {e}")
            self.use_fallback = True
            self.fallback_cache = CacheManager(ttl=ttl, max_size=max_size)
    
    def _get_namespaced_key(self, key: str) -> str:
        """生成带命名空间的key。
        
        Args:
            key: 原始缓存键
            
        Returns:
            带命名空间的完整键
        """
        return f"{self.namespace}:{key}"
    
    def get(self, key: str) -> Optional[Any]:
        """从热点缓存获取数据。
        
        Args:
            key: 缓存键
            
        Returns:
            缓存值，如果不存在或已过期则返回None
        """
        if self.use_fallback:
            return self.fallback_cache.get(key)
        
        try:
            namespaced_key = self._get_namespaced_key(key)
            value = self.redis_client.get(namespaced_key)
            
            if value is None:
                return None
            
            # 反序列化
            try:
                return json.loads(value)
            except json.JSONDecodeError:
                return value
                
        except redis.exceptions.ConnectionError:
            logger.warning("Redis 连接失败，降级到内存缓存")
            self.use_fallback = True
            self.fallback_cache = CacheManager(ttl=self.ttl, max_size=self.max_size)
            return self.fallback_cache.get(key)
    
    def set(self, key: str, value: Any, ttl: Optional[int] = None) -> None:
        """设置热点缓存数据。
        
        Args:
            key: 缓存键
            value: 缓存值
            ttl: 可选的TTL（秒）
        """
        if self.use_fallback:
            self.fallback_cache.set(key, value)
            return
        
        try:
            namespaced_key = self._get_namespaced_key(key)
            
            # 检查容量限制
            current_size = self.redis_client.dbsize()
            if current_size >= self.max_size:
                self._evict_lru()
            
            # 序列化
            if isinstance(value, (dict, list)):
                serialized_value = json.dumps(value, ensure_ascii=False)
            else:
                serialized_value = str(value)
            
            # 设置值和 TTL
            effective_ttl = ttl or self.ttl
            self.redis_client.setex(namespaced_key, effective_ttl, serialized_value)
            
        except redis.exceptions.ConnectionError:
            logger.warning("Redis 连接失败，降级到内存缓存")
            self.use_fallback = True
            self.fallback_cache = CacheManager(ttl=self.ttl, max_size=self.max_size)
            self.fallback_cache.set(key, value)
    
    def _evict_lru(self) -> None:
        """LRU 淘汰策略 - 删除最旧的 key。"""
        try:
            pattern = f"{self.namespace}:*"
            keys = list(self.redis_client.scan_iter(match=pattern, count=100))
            
            if not keys:
                return
            
            # 获取所有 key 的剩余 TTL
            key_info = []
            for key in keys:
                ttl = self.redis_client.ttl(key)
                if ttl > 0:
                    key_info.append((key, ttl))
            
            # 按 TTL 排序，删除剩余时间最短的（最老的）
            if key_info:
                key_info.sort(key=lambda x: x[1])
                oldest_key = key_info[0][0]
                self.redis_client.delete(oldest_key)
                logger.debug(f"LRU 淘汰热点缓存: {oldest_key}")
                
        except Exception as e:
            logger.error(f"LRU 淘汰失败: {e}")
    
    def delete(self, key: str) -> None:
        """删除热点缓存数据。
        
        Args:
            key: 缓存键
        """
        if self.use_fallback:
            self.fallback_cache.delete(key)
            return
        
        try:
            namespaced_key = self._get_namespaced_key(key)
            self.redis_client.delete(namespaced_key)
        except redis.exceptions.ConnectionError:
            logger.warning("Redis 连接失败，降级到内存缓存")
            self.use_fallback = True
            self.fallback_cache = CacheManager(ttl=self.ttl, max_size=self.max_size)
            self.fallback_cache.delete(key)
    
    def clear(self) -> None:
        """清空热点缓存。"""
        if self.use_fallback:
            self.fallback_cache.clear()
            return
        
        try:
            pattern = f"{self.namespace}:*"
            keys = list(self.redis_client.scan_iter(match=pattern, count=1000))
            if keys:
                self.redis_client.delete(*keys)
            logger.info(f"热点缓存已清空 ({len(keys)} 个键)")
        except redis.exceptions.ConnectionError:
            logger.warning("Redis 连接失败，降级到内存缓存")
            self.use_fallback = True
            self.fallback_cache = CacheManager(ttl=self.ttl, max_size=self.max_size)
            self.fallback_cache.clear()
    
    def get_stats(self) -> Dict:
        """获取热点缓存统计信息。
        
        Returns:
            包含统计信息的字典
        """
        if self.use_fallback:
            stats = self.fallback_cache.get_stats()
            stats['cache_type'] = 'hot_data_fallback'
            return stats
        
        try:
            pattern = f"{self.namespace}:*"
            keys = list(self.redis_client.scan_iter(match=pattern, count=1000))
            
            active_count = 0
            for key in keys:
                ttl = self.redis_client.ttl(key)
                if ttl > 0:
                    active_count += 1
            
            return {
                'cache_type': 'hot_data',
                'redis_db': REDIS_HOT_DATA_DB,
                'total_items': len(keys),
                'active_items': active_count,
                'expired_items': len(keys) - active_count,
                'ttl_seconds': self.ttl,
                'max_size': self.max_size,
                'usage_percent': (len(keys) / self.max_size * 100) if self.max_size > 0 else 0,
                'namespace': self.namespace
            }
        except redis.exceptions.ConnectionError:
            return {
                'cache_type': 'hot_data_error',
                'error': 'Redis connection failed'
            }
