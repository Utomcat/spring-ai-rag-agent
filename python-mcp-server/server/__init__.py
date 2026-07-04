"""MCP服务器包。

负责创建和配置FastMCP服务器实例,提供全局访问点。
"""
from server.mcp_server import mcp, get_mcp_config

__all__ = [
    'mcp',
    'get_mcp_config'
]
