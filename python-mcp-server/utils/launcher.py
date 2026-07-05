"""服务器启动模块。

提供配置加载和MCP服务器启动功能。
"""
import os
import logging

from dotenv import load_dotenv
from server.mcp_server import mcp
from server import get_mcp_config
from utils.validator import validate_port, validate_transport


def load_configuration(logger: logging.Logger) -> dict:
    """加载并验证配置。
    
    Args:
        logger: 日志记录器
    
    Returns:
        dict: 包含 transport 和 config 的配置字典
    
    Raises:
        ValueError: 配置无效时抛出
    """
    # 加载 .env 配置文件
    logger.info('正在加载环境变量...')
    load_dotenv()
    logger.info('环境变量加载完成')
    
    # 读取并验证传输方式
    transport_raw = os.getenv('MCP_TRANSPORT', 'stdio')
    try:
        transport = validate_transport(transport_raw)
    except ValueError as exception:
        logger.error(str(exception))
        raise
    
    # 读取并验证 MCP 配置
    try:
        config = get_mcp_config()
        # 额外验证端口号
        config['port'] = validate_port(str(config['port']))
    except (ValueError, TypeError) as exception:
        logger.error(f'配置验证失败: {exception}')
        raise
    
    return {
        'transport': transport,
        'config': config
    }


def start_server(transport: str, config: dict, logger: logging.Logger):
    """启动 MCP 服务器。
    
    Args:
        transport: 传输方式 (stdio, sse, streamable-http)
        config: MCP 服务器配置字典
        logger: 日志记录器
    """
    logger.info('=' * 60)
    logger.info('MCP 服务器启动配置:')
    logger.info(f'  - 传输方式: {transport}')
    logger.info(f'  - 主机地址: {config["host"]}')
    logger.info(f'  - 端口号: {config["port"]}')
    logger.info(f'  - 挂载路径: {config["mount_path"]}')
    logger.info('=' * 60)
    
    # 根据传输方式决定是否需要 host/port/mount_path
    if transport == 'stdio':
        logger.info('使用 stdio 传输方式，host/port/mount_path 参数将被忽略')
        logger.info('MCP 服务器已就绪，等待连接...')
        mcp.run(transport=transport)
    else:
        listen_addr = f'{config["host"]}:{config["port"]}{config["mount_path"]}'
        logger.info(f'使用 {transport} 传输方式，监听端点地址: {listen_addr}')
        logger.info(f'MCP 服务器已就绪，端点地址: {listen_addr}')
        # FastMCP 的 run() 方法只接受 transport 参数
        # HTTP 模式的配置需要通过环境变量设置
        os.environ['MCP_HOST'] = config['host']
        os.environ['MCP_PORT'] = str(config['port'])
        os.environ['MCP_MOUNT_PATH'] = config['mount_path']
        mcp.run(transport=transport)
