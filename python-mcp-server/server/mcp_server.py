import logging
import os

from mcp.server.fastmcp import FastMCP

# 配置日志
logger = logging.getLogger(__name__)


def get_mcp_config() -> dict:
    """从环境变量读取 MCP 服务器配置。

    Returns:
        dict: 包含 host, port, mount_path 的配置字典
    """
    return {
        'host': os.getenv('MCP_HOST', '127.0.0.1'),
        'port': int(os.getenv('MCP_PORT', '8084')),
        'mount_path': os.getenv('MCP_MOUNT_PATH', '/mcp'),
    }


# 创建MCP实例(使用配置中的host和port)
_mcp_config = get_mcp_config()

mcp = FastMCP(
    name='Web Search Server',
    host=_mcp_config['host'],
    port=_mcp_config['port']
)

logger.info(f'MCP Server 已初始化 (监听地址: {_mcp_config["host"]}:{_mcp_config["port"]})')

# 导入工具模块（确保工具被注册）
# 注意：必须在mcp实例创建后导入
try:
    from tools import web_search_tool  # noqa: F401
    logger.info('web_search 工具已加载')
except ImportError as e:
    logger.warning(f'web_search工具加载失败: {e}')

try:
    from tools import fetch_data_tool  # noqa: F401
    logger.info('fetch_data 工具已加载')
except ImportError as e:
    logger.warning(f'fetch_data工具加载失败: {e}')

try:
    from tools import analyze_data_tool  # noqa: F401
    logger.info('analyze_data 工具已加载')
except ImportError as e:
    logger.warning(f'analyze_data工具加载失败: {e}')

try:
    from tools import trend_analysis_tool  # noqa: F401
    logger.info('trend_analysis 工具已加载')
except ImportError as e:
    logger.warning(f'trend_analysis工具加载失败: {e}')

try:
    from tools import generate_chart_data_tool  # noqa: F401
    logger.info('generate_chart_data 工具已加载')
except ImportError as e:
    logger.warning(f'generate_chart_data工具加载失败: {e}')

try:
    from tools import generate_report_tool  # noqa: F401
    logger.info('generate_report 工具已加载')
except ImportError as e:
    logger.warning(f'generate_report工具加载失败: {e}')