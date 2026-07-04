"""常量配置模块。

集中管理所有项目常量,包括HTTP配置、重试策略、搜索结果限制等。
"""
import os

# 项目版本信息
VERSION = '0.2.0'

# 有效的传输方式
VALID_TRANSPORTS = {'stdio', 'sse', 'streamable-http'}

# HTTP请求相关常量
DEFAULT_USER_AGENT = 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36'

# 从环境变量读取超时配置（单位：秒）
CONNECT_TIMEOUT = int(os.getenv('SEARCH_CONNECT_TIMEOUT', '5'))
READ_TIMEOUT = int(os.getenv('SEARCH_READ_TIMEOUT', '10'))
# (连接超时, 读取超时)
REQUEST_TIMEOUT = (CONNECT_TIMEOUT, READ_TIMEOUT)

# 重试配置
MAX_RETRIES = int(os.getenv('SEARCH_MAX_RETRIES', '2'))
RETRY_BACKOFF_FACTOR = float(os.getenv('SEARCH_RETRY_BACKOFF', '1.5'))

# 最大搜索结果数量限制
MAX_RESULTS_LIMIT = 20
