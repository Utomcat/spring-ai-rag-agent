"""URL级别的请求频率限制器。

防止对同一URL的过度频繁访问,避免被目标网站封禁。
"""
import logging
import time
from collections import defaultdict
from typing import Dict, List, Tuple

from config.constants import WEBPAGE_RATE_LIMIT_PER_URL, WEBPAGE_RATE_LIMIT_WINDOW

# 配置日志
logger = logging.getLogger(__name__)


class UrlRateLimiter:
    """URL级别的请求频率限制器。
    
    使用滑动窗口算法,记录每个URL的请求时间戳,
    确保在指定时间窗口内不超过最大请求次数。
    """

    def __init__(
        self, 
        max_requests: int = WEBPAGE_RATE_LIMIT_PER_URL,
        window_seconds: int = WEBPAGE_RATE_LIMIT_WINDOW
    ):
        """
        初始化URL限流器。

        Args:
            max_requests: 时间窗口内允许的最大请求数
            window_seconds: 时间窗口大小(秒)
        """
        self.max_requests = max_requests
        self.window_seconds = window_seconds
        # 存储每个URL的请求时间戳列表
        self._request_log: Dict[str, List[float]] = defaultdict(list)

    def can_access(self, url: str) -> Tuple[bool, str]:
        """
        检查是否可以访问指定URL。

        Args:
            url: 要访问的URL

        Returns:
            (是否允许访问, 提示信息)
        """
        if not url:
            return False, "URL不能为空"

        # 清理过期的时间戳
        current_time = time.time()
        cutoff_time = current_time - self.window_seconds
        
        # 只保留时间窗口内的请求记录
        self._request_log[url] = [
            timestamp 
            for timestamp in self._request_log[url] 
            if timestamp > cutoff_time
        ]

        # 检查是否超过限制
        request_count = len(self._request_log[url])
        
        if request_count >= self.max_requests:
            # 计算需要等待的时间
            oldest_timestamp = min(self._request_log[url])
            wait_time = oldest_timestamp + self.window_seconds - current_time
            
            logger.warning(
                f'URL访问频率超限: {url}, '
                f'{request_count}次/{self.window_seconds}秒, '
                f'需等待{wait_time:.1f}秒'
            )
            
            return False, (
                f'访问频率受限: 该URL在{self.window_seconds}秒内已访问{request_count}次\n'
                f'请等待{wait_time:.0f}秒后重试,或访问其他URL'
            )

        # 记录本次请求
        self._request_log[url].append(current_time)
        
        logger.debug(
            f'URL访问允许: {url}, '
            f'当前窗口内请求数: {request_count + 1}/{self.max_requests}'
        )
        
        return True, "允许访问"

    def get_remaining_requests(self, url: str) -> int:
        """
        获取指定URL在当前时间窗口内剩余的请求次数。

        Args:
            url: URL地址

        Returns:
            剩余请求次数
        """
        if not url or url not in self._request_log:
            return self.max_requests

        current_time = time.time()
        cutoff_time = current_time - self.window_seconds
        
        # 清理过期记录
        active_requests = [
            timestamp 
            for timestamp in self._request_log[url] 
            if timestamp > cutoff_time
        ]
        
        remaining = max(0, self.max_requests - len(active_requests))
        return remaining

    def reset_url(self, url: str):
        """
        重置指定URL的请求记录。

        Args:
            url: URL地址
        """
        if url in self._request_log:
            del self._request_log[url]
            logger.info(f'已重置URL请求记录: {url}')

    def reset_all(self):
        """重置所有URL的请求记录。"""
        self._request_log.clear()
        logger.info('已重置所有URL请求记录')

    def get_stats(self) -> Dict:
        """
        获取限流器统计信息。

        Returns:
            包含统计信息的字典
        """
        current_time = time.time()
        cutoff_time = current_time - self.window_seconds
        
        stats = {
            'total_urls': len(self._request_log),
            'active_urls': 0,
            'urls_near_limit': [],
        }
        
        for url, timestamps in self._request_log.items():
            active_count = len([t for t in timestamps if t > cutoff_time])
            
            if active_count > 0:
                stats['active_urls'] += 1
                
                if active_count >= self.max_requests * 0.8:  # 达到80%阈值
                    stats['urls_near_limit'].append({
                        'url': url,
                        'requests': active_count,
                        'limit': self.max_requests,
                        'remaining': max(0, self.max_requests - active_count)
                    })
        
        return stats
