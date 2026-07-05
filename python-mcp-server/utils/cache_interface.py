"""统一缓存接口定义。

定义所有缓存管理器必须实现的接口规范，确保一致性。
"""
from abc import ABC, abstractmethod
from typing import Any, Optional, Dict


class CacheInterface(ABC):
    """缓存接口 - 定义统一的缓存操作规范。
    
    所有缓存实现类都必须继承此接口并实现以下方法。
    """
    
    @abstractmethod
    def get(self, key: str) -> Optional[Any]:
        """获取缓存值。
        
        Args:
            key: 缓存键
            
        Returns:
            缓存值，如果不存在或已过期则返回None
        """
        pass
    
    @abstractmethod
    def set(self, key: str, value: Any, ttl: Optional[int] = None) -> None:
        """设置缓存值。
        
        Args:
            key: 缓存键
            value: 缓存值
            ttl: 可选的TTL（秒），如果不提供则使用默认值
        """
        pass
    
    @abstractmethod
    def delete(self, key: str) -> None:
        """删除缓存值。
        
        Args:
            key: 缓存键
        """
        pass
    
    @abstractmethod
    def clear(self) -> None:
        """清空所有缓存。"""
        pass
    
    @abstractmethod
    def get_stats(self) -> Dict:
        """获取缓存统计信息。
        
        Returns:
            包含统计信息的字典
        """
        pass
