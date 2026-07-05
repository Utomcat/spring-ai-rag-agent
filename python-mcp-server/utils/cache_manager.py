"""结果缓存管理器。"""
import logging
import time
from typing import Any, Optional, Dict
from collections import OrderedDict
from config.constants import CACHE_TTL_SEARCH, CACHE_TTL_WEBPAGE, WEBPAGE_CACHE_MAX_SIZE

# 配置日志
logger = logging.getLogger(__name__)


class CacheManager:
    """结果缓存管理器 - 内存缓存 + TTL过期策略 + LRU淘汰。
    
    特性:
    - TTL过期: 自动清理过期的缓存项
    - LRU淘汰: 当缓存达到最大容量时,淘汰最近最少使用的项
    - 参数感知: 支持不同的TTL和最大容量配置
    """

    def __init__(self, ttl: int = CACHE_TTL_SEARCH, max_size: int = WEBPAGE_CACHE_MAX_SIZE):
        """
        初始化缓存管理器。

        Args:
            ttl: 缓存过期时间(秒)
            max_size: 最大缓存条目数(0表示无限制)
        """
        self.cache: OrderedDict[str, Any] = OrderedDict()
        self.timestamps: Dict[str, float] = {}
        self.ttl = ttl
        self.max_size = max_size

    def get(self, key: str) -> Optional[Any]:
        """
        从缓存获取结果。
    
        Args:
            key: 缓存键
    
        Returns:
            缓存值,如果不存在或已过期则返回None
        """
        if key not in self.cache:
            return None
    
        # 检查是否过期
        timestamp = self.timestamps.get(key, 0)
        if time.time() - timestamp > self.ttl:
            logger.debug(f'缓存已过期: {key}')
            self.delete(key)
            return None
    
        # 移动到末尾(标记为最近使用)
        self.cache.move_to_end(key)
            
        logger.debug(f'缓存命中: {key}')
        return self.cache[key]

    def set(self, key: str, value: Any) -> None:
        """
        存入缓存。

        Args:
            key: 缓存键
            value: 缓存值
        """
        # 如果key已存在,先删除再添加(确保在末尾)
        if key in self.cache:
            del self.cache[key]
            del self.timestamps[key]
        
        # 检查是否需要LRU淘汰
        if self.max_size > 0 and len(self.cache) >= self.max_size:
            self._evict_lru()
        
        # 添加到末尾(最近使用)
        self.cache[key] = value
        self.timestamps[key] = time.time()
        logger.debug(f'缓存设置: {key}, TTL={self.ttl}s')
    
    def _evict_lru(self) -> None:
        """
        淘汰最近最少使用的缓存项(LRU策略)。
        """
        if not self.cache:
            return
        
        # 获取第一个元素(最久未使用)
        lru_key, _ = next(iter(self.cache.items()))
        self.delete(lru_key)
        logger.debug(f'LRU淘汰: {lru_key[:16]}...')

    def delete(self, key: str) -> None:
        """
        删除缓存项。

        Args:
            key: 缓存键
        """
        if key in self.cache:
            del self.cache[key]
        if key in self.timestamps:
            del self.timestamps[key]

    def clear(self) -> None:
        """清空所有缓存。"""
        self.cache.clear()
        self.timestamps.clear()
        logger.info('缓存已清空')

    def cleanup_expired(self) -> int:
        """
        清理过期的缓存项。

        Returns:
            清理的缓存项数量
        """
        now = time.time()
        expired_keys = [
            key for key, ts in self.timestamps.items()
            if now - ts > self.ttl
        ]

        for key in expired_keys:
            self.delete(key)

        if expired_keys:
            logger.debug(f'清理了 {len(expired_keys)} 个过期缓存项')

        return len(expired_keys)

    def get_stats(self) -> Dict:
        """
        获取缓存统计信息。

        Returns:
            包含统计信息的字典
        """
        now = time.time()
        active_count = sum(
            1 for ts in self.timestamps.values()
            if now - ts <= self.ttl
        )
        
        return {
            'total_items': len(self.cache),
            'active_items': active_count,
            'expired_items': len(self.cache) - active_count,
            'ttl_seconds': self.ttl,
            'max_size': self.max_size,
            'usage_percent': (len(self.cache) / self.max_size * 100) if self.max_size > 0 else 0
        }
