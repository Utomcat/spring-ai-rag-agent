"""结果缓存管理器。"""
import logging
import time
from typing import Any, Optional, Dict
from config.constants import CACHE_TTL_SEARCH

# 配置日志
logger = logging.getLogger(__name__)


class CacheManager:
    """结果缓存管理器 - 内存缓存 + TTL过期策略。"""

    def __init__(self, ttl: int = CACHE_TTL_SEARCH):
        """
        初始化缓存管理器。

        Args:
            ttl: 缓存过期时间（秒）
        """
        self.cache: Dict[str, Any] = {}
        self.timestamps: Dict[str, float] = {}
        self.ttl = ttl

    def get(self, key: str) -> Optional[Any]:
        """
        从缓存获取结果。

        Args:
            key: 缓存键

        Returns:
            缓存值，如果不存在或已过期则返回None
        """
        if key not in self.cache:
            return None

        # 检查是否过期
        timestamp = self.timestamps.get(key, 0)
        if time.time() - timestamp > self.ttl:
            logger.debug(f'缓存已过期: {key}')
            self.delete(key)
            return None

        logger.debug(f'缓存命中: {key}')
        return self.cache[key]

    def set(self, key: str, value: Any) -> None:
        """
        存入缓存。

        Args:
            key: 缓存键
            value: 缓存值
        """
        self.cache[key] = value
        self.timestamps[key] = time.time()
        logger.debug(f'缓存设置: {key}, TTL={self.ttl}s')

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
