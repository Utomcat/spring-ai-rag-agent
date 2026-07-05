"""缓存预热模块。

在系统启动时预加载热点数据到缓存中，提升初始响应速度。
"""
import logging
from typing import List

from config.constants import CACHE_WARMER_KEYS

logger = logging.getLogger(__name__)


class CacheWarmer:
    """缓存预热器 - 启动时加载热点数据。
    
    从配置中读取需要预热的缓存键，并在系统启动时预先加载这些数据。
    """
    
    def __init__(self):
        """初始化缓存预热器。"""
        self.warmup_keys = CACHE_WARMER_KEYS
    
    async def warmup(self) -> None:
        """执行缓存预热。
        
        遍历配置的预热键列表，逐个加载数据到缓存中。
        """
        if not self.warmup_keys:
            logger.info("未配置预热缓存键，跳过预热")
            return
        
        logger.info(f"开始缓存预热，共 {len(self.warmup_keys)} 个键")
        
        success_count = 0
        fail_count = 0
        
        for key_config in self.warmup_keys:
            try:
                # 解析配置格式: "type:url|query"
                parts = key_config.split(':')
                if len(parts) != 2:
                    logger.warning(f"无效的预热配置格式: {key_config}")
                    fail_count += 1
                    continue
                
                cache_type, target = parts[0], parts[1]
                
                if cache_type == 'webpage':
                    # 预热网页缓存
                    from tools.fetch_webpage_tool import fetch_webpage
                    result = fetch_webpage(url=target, mode='summary')
                    if result and '错误' not in result:
                        success_count += 1
                        logger.info(f"✓ 预热网页缓存: {target}")
                    else:
                        fail_count += 1
                        logger.warning(f"✗ 预热网页缓存失败: {target}")
                        
                elif cache_type == 'search':
                    # 预热搜索缓存
                    from tools.web_search_tool import web_search
                    result = web_search(query=target, max_results=5)
                    if result and '错误' not in result:
                        success_count += 1
                        logger.info(f"✓ 预热搜索缓存: {target}")
                    else:
                        fail_count += 1
                        logger.warning(f"✗ 预热搜索缓存失败: {target}")
                else:
                    logger.warning(f"未知的缓存类型: {cache_type}")
                    fail_count += 1
                        
            except Exception as e:
                fail_count += 1
                logger.error(f"✗ 预热失败: {key_config} - {e}", exc_info=True)
        
        logger.info(
            f"缓存预热完成: 成功 {success_count}, 失败 {fail_count}"
        )
