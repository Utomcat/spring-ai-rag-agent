"""热点数据追踪模块。

追踪数据访问频率，识别热点数据并触发晋升机制。
"""
import logging
import time
import threading
from typing import Dict, List, Tuple

from config.constants import (
    HOT_DATA_THRESHOLD,
    HOT_DATA_WINDOW,
    HOT_DATA_PROMOTION_INTERVAL
)

logger = logging.getLogger(__name__)


class HotDataTracker:
    """热点数据追踪器。
    
    记录所有数据的访问频率，定期分析并识别热点数据。
    """
    
    def __init__(self):
        """初始化热点数据追踪器。"""
        self.access_log: Dict[str, List[float]] = {}
        self.lock = threading.Lock()
        self.running = False
    
    def record_access(self, key: str) -> None:
        """记录数据访问。
        
        Args:
            key: 被访问的缓存键
        """
        with self.lock:
            now = time.time()
            if key not in self.access_log:
                self.access_log[key] = []
            
            self.access_log[key].append(now)
            
            # 清理过期记录（保留窗口期内的记录）
            cutoff = now - HOT_DATA_WINDOW
            self.access_log[key] = [
                ts for ts in self.access_log[key] 
                if ts > cutoff
            ]
    
    def get_hot_keys(self, top_n: int = 10) -> List[Tuple[str, int]]:
        """获取 Top N 热点数据。
        
        Args:
            top_n: 返回的热点数据数量
            
        Returns:
            热点数据列表，每个元素为 (key, 访问次数) 的元组
        """
        with self.lock:
            now = time.time()
            cutoff = now - HOT_DATA_WINDOW
            
            hot_keys = []
            for key, timestamps in self.access_log.items():
                # 计算窗口期内的访问次数
                recent_accesses = [ts for ts in timestamps if ts > cutoff]
                count = len(recent_accesses)
                
                if count >= HOT_DATA_THRESHOLD:
                    hot_keys.append((key, count))
            
            # 按访问次数排序（降序）
            hot_keys.sort(key=lambda x: x[1], reverse=True)
            return hot_keys[:top_n]
    
    def start_tracking(self) -> None:
        """启动热点追踪。"""
        if self.running:
            logger.warning("热点追踪已在运行")
            return
        
        self.running = True
        
        def analyze_loop():
            """定期分析的后台线程函数。"""
            while self.running:
                time.sleep(HOT_DATA_PROMOTION_INTERVAL)
                self._analyze_and_promote()
        
        thread = threading.Thread(target=analyze_loop, daemon=True)
        thread.start()
        logger.info(f"热点数据追踪已启动，检查间隔: {HOT_DATA_PROMOTION_INTERVAL}秒")
    
    def stop_tracking(self) -> None:
        """停止热点追踪。"""
        self.running = False
        logger.info("热点数据追踪已停止")
    
    def _analyze_and_promote(self) -> None:
        """分析并触发晋升。"""
        hot_keys = self.get_hot_keys(top_n=20)
        
        if hot_keys:
            logger.info(f"检测到 {len(hot_keys)} 个热点数据")
            for key, count in hot_keys[:5]:  # 只打印前 5 个
                logger.debug(f"  - {key}: {count} 次访问")
        else:
            logger.debug("当前没有热点数据")
