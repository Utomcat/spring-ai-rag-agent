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