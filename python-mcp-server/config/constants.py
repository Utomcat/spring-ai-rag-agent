"""常量配置模块。

集中管理所有项目常量,包括HTTP配置、重试策略、搜索结果限制等。
"""
import os
from dotenv import load_dotenv

# 确保 .env 文件已加载(防止在其他模块导入时环境变量未就绪)
load_dotenv()

# ==================== 项目版本信息 ====================
VERSION = '1.0.0'

# ==================== MCP服务器配置 ====================
# 有效的传输方式
VALID_TRANSPORTS = {'stdio', 'sse', 'streamable-http'}

# ==================== HTTP请求相关常量 ====================
# 默认User-Agent
DEFAULT_USER_AGENT = 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36'

# 从环境变量读取超时配置（单位：秒）
CONNECT_TIMEOUT = int(os.getenv('SEARCH_CONNECT_TIMEOUT', '5'))
READ_TIMEOUT = int(os.getenv('SEARCH_READ_TIMEOUT', '10'))
# (连接超时, 读取超时)
REQUEST_TIMEOUT = (CONNECT_TIMEOUT, READ_TIMEOUT)

# 重试配置
MAX_RETRIES = int(os.getenv('SEARCH_MAX_RETRIES', '3'))
RETRY_BACKOFF_FACTOR = float(os.getenv('SEARCH_RETRY_BACKOFF', '2'))

# ==================== 搜索功能限制 ====================
# 最大搜索结果数量限制
MAX_RESULTS_LIMIT = 20
MIN_RESULTS = 1

# ==================== 缓存配置 ====================
# 搜索结果缓存TTL（秒）
CACHE_TTL_SEARCH = 300  # 5分钟
# 分析报告缓存TTL（秒）
CACHE_TTL_ANALYSIS = 1800  # 30分钟
# 网页内容缓存TTL（秒）
CACHE_TTL_WEBPAGE = int(os.getenv('WEBPAGE_CACHE_TTL', '600'))  # 默认10分钟
# 网页缓存最大条目数
WEBPAGE_CACHE_MAX_SIZE = int(os.getenv('WEBPAGE_CACHE_MAX_SIZE', '100'))

# ==================== 限流配置 ====================
# 时间窗口内最大请求数(全局)
RATE_LIMIT_MAX_REQUESTS = 10
# 时间窗口（秒）
RATE_LIMIT_TIME_WINDOW = 60
# 单个URL的请求频率限制(次/分钟)
WEBPAGE_RATE_LIMIT_PER_URL = int(os.getenv('WEBPAGE_RATE_LIMIT_PER_URL', '5'))
# URL限流时间窗口(秒)
WEBPAGE_RATE_LIMIT_WINDOW = int(os.getenv('WEBPAGE_RATE_LIMIT_WINDOW', '60'))

# ==================== Redis缓存配置 ====================
REDIS_HOST = os.getenv('REDIS_HOST', 'localhost')
REDIS_PORT = int(os.getenv('REDIS_PORT', '6379'))
REDIS_PASSWORD = os.getenv('REDIS_PASSWORD', '') or None
REDIS_TIMEOUT = int(os.getenv('REDIS_TIMEOUT', '5'))
REDIS_CACHE_ENABLED = os.getenv('REDIS_CACHE_ENABLED', 'true').lower() == 'true'
REDIS_MAX_CONNECTIONS = int(os.getenv('REDIS_MAX_CONNECTIONS', '20'))

# 双 DB 架构配置
REDIS_HOT_DATA_DB = int(os.getenv('REDIS_HOT_DATA_DB', '1'))
REDIS_FULL_DATA_DB = int(os.getenv('REDIS_FULL_DATA_DB', '2'))

# 热点数据配置
HOT_DATA_CACHE_MAX_SIZE = int(os.getenv('HOT_DATA_CACHE_MAX_SIZE', '50'))
HOT_DATA_CACHE_TTL = int(os.getenv('HOT_DATA_CACHE_TTL', '300'))

# 完整数据配置
FULL_DATA_CACHE_MAX_SIZE = int(os.getenv('FULL_DATA_CACHE_MAX_SIZE', '500'))
FULL_DATA_CACHE_TTL = int(os.getenv('FULL_DATA_CACHE_TTL', '1800'))

# 缓存预热配置
CACHE_WARMER_ENABLED = os.getenv('CACHE_WARMER_ENABLED', 'false').lower() == 'true'
CACHE_WARMER_KEYS = [k.strip() for k in os.getenv('CACHE_WARMER_KEYS', '').split(',') if k.strip()]

# 监控配置
CACHE_MONITOR_ENABLED = os.getenv('CACHE_MONITOR_ENABLED', 'false').lower() == 'true'
PROMETHEUS_PORT = int(os.getenv('PROMETHEUS_PORT', '9090'))

# 多实例同步配置
CACHE_SYNC_ENABLED = os.getenv('CACHE_SYNC_ENABLED', 'false').lower() == 'true'
CACHE_SYNC_CHANNEL = os.getenv('CACHE_SYNC_CHANNEL', 'mcp-cache-sync')

# 热点数据追踪配置
HOT_DATA_TRACKER_ENABLED = os.getenv('HOT_DATA_TRACKER_ENABLED', 'false').lower() == 'true'
HOT_DATA_THRESHOLD = int(os.getenv('HOT_DATA_THRESHOLD', '10'))
HOT_DATA_WINDOW = int(os.getenv('HOT_DATA_WINDOW', '3600'))
HOT_DATA_PROMOTION_INTERVAL = int(os.getenv('HOT_DATA_PROMOTION_INTERVAL', '300'))

# 环境标识
APP_ENVIRONMENT = os.getenv('APP_ENVIRONMENT', 'dev')
