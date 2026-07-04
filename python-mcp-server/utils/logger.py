"""日志配置模块。

提供统一的日志系统配置功能。
"""
import logging


def setup_logging(level: str = 'INFO') -> logging.Logger:
    """配置日志系统。
    
    Args:
        level: 日志级别 (DEBUG, INFO, WARNING, ERROR, CRITICAL)
    
    Returns:
        logging.Logger: 配置好的 logger 对象
    """
    # 转换日志级别字符串为常量
    numeric_level = getattr(logging, level.upper(), logging.INFO)
    
    # 配置根 logger
    logging.basicConfig(
        level=numeric_level,
        format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
        datefmt='%Y-%m-%d %H:%M:%S'
    )
    
    return logging.getLogger(__name__)
