"""MCP Server 主启动入口。

负责加载环境变量、配置日志并启动 MCP 服务器。
"""
import os
import sys
import asyncio

from dotenv import load_dotenv
from utils import setup_logging, print_banner, load_configuration, start_server



# 先加载 .env，再配置日志（支持通过 LOG_LEVEL 环境变量动态调整日志级别）
load_dotenv()
log_level = os.getenv('LOG_LEVEL', 'INFO')
logger = setup_logging(log_level)


async def start_enhanced_features():
    """启动增强功能。"""
    from config.constants import (
        CACHE_WARMER_ENABLED,
        CACHE_MONITOR_ENABLED,
        PROMETHEUS_PORT,
        HOT_DATA_TRACKER_ENABLED
    )
    
    # 1. 缓存预热
    if CACHE_WARMER_ENABLED:
        try:
            from utils.cache_warmer import CacheWarmer
            warmer = CacheWarmer()
            await warmer.warmup()
        except Exception as e:
            logger.warning(f"缓存预热失败: {e}")
    
    # 2. 启动监控
    if CACHE_MONITOR_ENABLED:
        try:
            from utils.cache_monitor import start_monitor_server
            start_monitor_server(port=PROMETHEUS_PORT)
        except Exception as e:
            logger.warning(f"启动监控服务失败: {e}")
    
    # 3. 启动热点追踪
    if HOT_DATA_TRACKER_ENABLED:
        try:
            from utils.hot_data_tracker import HotDataTracker
            tracker = HotDataTracker()
            tracker.start_tracking()
        except Exception as e:
            logger.warning(f"启动热点追踪失败: {e}")


if __name__ == '__main__':
    try:
        # 打印启动横幅
        print_banner()
        
        # 加载并验证配置
        settings = load_configuration(logger)
        
        # 启动增强功能（异步）
        asyncio.run(start_enhanced_features())
        
        # 启动服务器
        start_server(settings['transport'], settings['config'], logger)
        
    except ValueError as e:
        logger.error(f'配置错误: {e}')
        logger.error('请检查 .env 文件或环境变量设置')
        sys.exit(1)
    except KeyboardInterrupt:
        logger.info('\n收到中断信号，正在优雅退出...')
        logger.info('MCP 服务器已停止')
        sys.exit(0)
    except Exception as e:
        logger.error(f'启动失败: {e}', exc_info=True)
        sys.exit(1)
