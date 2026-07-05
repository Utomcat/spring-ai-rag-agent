"""双层缓存管理器 - 统一管理热点和完整数据缓存。

提供统一的缓存访问接口，自动管理数据在两个缓存层之间的流转。
"""
import logging
import time
from typing import Any, Optional, Dict

from config.constants import (
    HOT_DATA_THRESHOLD,
    HOT_DATA_WINDOW,
    CACHE_SYNC_ENABLED,
    CACHE_SYNC_CHANNEL
)
from utils.hot_data_cache_manager import HotDataCacheManager
from utils.full_data_cache_manager import FullDataCacheManager
from utils.cache_interface import CacheInterface

logger = logging.getLogger(__name__)


class DualLayerCacheManager(CacheInterface):
    """双层缓存管理器 - 自动管理热点和完整数据。
    
    工作流程:
    1. 读取时: 先查热点缓存，未命中再查完整缓存
    2. 写入时: 同时写入两个缓存
    3. 热点晋升: 访问频率高的数据自动提升到热点缓存
    """
    
    def __init__(
        self,
        hot_ttl: Optional[int] = None,
        hot_max_size: Optional[int] = None,
        full_ttl: Optional[int] = None,
        full_max_size: Optional[int] = None
    ):
        """初始化双层缓存管理器。
        
        Args:
            hot_ttl: 热点缓存TTL（秒）
            hot_max_size: 热点缓存最大条目数
            full_ttl: 完整缓存TTL（秒）
            full_max_size: 完整缓存最大条目数
        """
        # 初始化两个缓存层
        self.hot_cache = HotDataCacheManager(
            ttl=hot_ttl,
            max_size=hot_max_size
        )
        self.full_cache = FullDataCacheManager(
            ttl=full_ttl,
            max_size=full_max_size
        )
        
        # 访问计数器（用于热点检测）
        self.access_counter: Dict[str, int] = {}
        self.access_timestamps: Dict[str, float] = {}
        
        # 同步发布器
        if CACHE_SYNC_ENABLED:
            try:
                from utils.cache_sync import CacheSyncPublisher
                self.sync_publisher = CacheSyncPublisher()
            except ImportError:
                logger.warning("缓存同步模块未找到，禁用同步功能")
                self.sync_publisher = None
        else:
            self.sync_publisher = None
    
    def get(self, key: str) -> Optional[Any]:
        """获取缓存值 - 先查热点，再查完整数据。
        
        Args:
            key: 缓存键
            
        Returns:
            缓存值，如果不存在则返回None
        """
        # 记录访问
        self._record_access(key)
        
        # 1. 先查热点缓存
        value = self.hot_cache.get(key)
        if value is not None:
            logger.debug(f"热点缓存命中: {key}")
            return value
        
        # 2. 再查完整缓存
        value = self.full_cache.get(key)
        if value is not None:
            logger.debug(f"完整缓存命中: {key}")
            
            # 检查是否需要晋升到热点缓存
            if self._should_promote_to_hot(key):
                self.hot_cache.set(key, value)
                logger.info(f"数据晋升到热点缓存: {key}")
            
            return value
        
        logger.debug(f"缓存未命中: {key}")
        return None
    
    def set(self, key: str, value: Any, ttl: Optional[int] = None) -> None:
        """设置缓存值 - 同时写入两个缓存层。
        
        Args:
            key: 缓存键
            value: 缓存值
            ttl: 可选的TTL（秒）
        """
        # 写入完整缓存（总是保存）
        self.full_cache.set(key, value, ttl=ttl)
        
        # 如果是热点数据或新数据，也写入热点缓存
        if self._is_hot_key(key):
            self.hot_cache.set(key, value, ttl=ttl)
        
        # 发布同步事件
        if self.sync_publisher:
            self.sync_publisher.publish('SET', key)
    
    def delete(self, key: str) -> None:
        """删除缓存值 - 从两个缓存层都删除。
        
        Args:
            key: 缓存键
        """
        self.hot_cache.delete(key)
        self.full_cache.delete(key)
        
        # 清理访问计数
        if key in self.access_counter:
            del self.access_counter[key]
        if key in self.access_timestamps:
            del self.access_timestamps[key]
        
        # 发布同步事件
        if self.sync_publisher:
            self.sync_publisher.publish('DELETE', key)
    
    def clear(self) -> None:
        """清空所有缓存。"""
        self.hot_cache.clear()
        self.full_cache.clear()
        self.access_counter.clear()
        self.access_timestamps.clear()
        logger.info("双层缓存已清空")
    
    def get_stats(self) -> Dict:
        """获取双层缓存统计信息。
        
        Returns:
            包含两层缓存统计信息的字典
        """
        hot_stats = self.hot_cache.get_stats()
        full_stats = self.full_cache.get_stats()
        
        return {
            'hot_cache': hot_stats,
            'full_cache': full_stats,
            'tracked_keys': len(self.access_counter),
            'sync_enabled': CACHE_SYNC_ENABLED
        }
    
    def _record_access(self, key: str) -> None:
        """记录数据访问。
        
        Args:
            key: 被访问的缓存键
        """
        now = time.time()
        
        if key not in self.access_counter:
            self.access_counter[key] = 0
            self.access_timestamps[key] = now
        
        self.access_counter[key] += 1
        self.access_timestamps[key] = now
    
    def _should_promote_to_hot(self, key: str) -> bool:
        """判断是否应该晋升到热点缓存。
        
        Args:
            key: 缓存键
            
        Returns:
            如果应该晋升则返回True
        """
        if key not in self.access_counter:
            return False
        
        now = time.time()
        window_start = now - HOT_DATA_WINDOW
        
        # 检查是否在窗口期内达到阈值
        if self.access_counter[key] >= HOT_DATA_THRESHOLD:
            last_access = self.access_timestamps.get(key, 0)
            if last_access >= window_start:
                return True
        
        return False
    
    def _is_hot_key(self, key: str) -> bool:
        """判断是否是热点 key。
        
        Args:
            key: 缓存键
            
        Returns:
            如果是热点key则返回True
        """
        return key in self.access_counter and self.access_counter[key] >= HOT_DATA_THRESHOLD
