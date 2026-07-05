"""环境变量配置加载模块。

负责从环境变量读取MCP服务器配置，并提供默认值。
"""
import os
from typing import Dict


def get_mcp_config() -> Dict:
    """从环境变量读取 MCP 服务器配置。

    Returns:
        dict: 包含 host, port, mount_path, transport 的配置字典
    """
    return {
        'host': os.getenv('MCP_HOST', '127.0.0.1'),
        'port': int(os.getenv('MCP_PORT', '8084')),
        'mount_path': os.getenv('MCP_MOUNT_PATH', '/mcp'),
        'transport': os.getenv('MCP_TRANSPORT', 'streamable-http'),
    }


def validate_config(config: Dict) -> None:
    """验证配置参数的合法性。

    Args:
        config: 配置字典

    Raises:
        ValueError: 配置参数不合法时抛出
    """
    # 验证端口范围
    if not (1 <= config['port'] <= 65535):
        raise ValueError(f'端口号必须在1-65535之间，当前值: {config["port"]}')

    # 验证传输方式
    from config.constants import VALID_TRANSPORTS
    if config['transport'] not in VALID_TRANSPORTS:
        raise ValueError(
            f'不支持的传输方式: {config["transport"]}，支持的类型: {VALID_TRANSPORTS}'
        )

    # 验证挂载路径格式
    if config['transport'] in ('sse', 'streamable-http'):
        if not config['mount_path'].startswith('/'):
            raise ValueError(f'挂载路径必须以 / 开头，当前值: {config["mount_path"]}')
