"""MCP Server 主启动入口。

负责加载环境变量并启动 MCP 服务器。
"""
import os
import sys
import logging

from dotenv import load_dotenv
from script.MCP.web_search_server import mcp, get_mcp_config

# 项目版本信息
VERSION = '0.2.0'

# 有效的传输方式
VALID_TRANSPORTS = {'stdio', 'sse', 'streamable-http'}


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


def validate_port(port_str: str) -> int:
    """验证端口号是否有效。
    
    Args:
        port_str: 端口号字符串
    
    Returns:
        int: 有效的端口号
    
    Raises:
        ValueError: 端口号无效时抛出
    """
    try:
        port = int(port_str)
        if not (1 <= port <= 65535):
            raise ValueError(f'端口号必须在 1-65535 范围内，当前值: {port}')
        return port
    except (ValueError, TypeError) as exception:
        raise ValueError(f'无效的端口号: {port_str}') from exception


def validate_transport(transport: str) -> str:
    """验证传输方式是否有效。
    
    Args:
        transport: 传输方式字符串
    
    Returns:
        str: 有效的传输方式（小写）
    
    Raises:
        ValueError: 传输方式无效时抛出
    """
    transport_lower = transport.lower().strip()
    if transport_lower not in VALID_TRANSPORTS:
        raise ValueError(
            f'无效的传输方式: {transport}，支持的方式: {", ".join(sorted(VALID_TRANSPORTS))}'
        )
    return transport_lower


def load_configuration() -> dict:
    """加载并验证配置。
    
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


def print_banner():
    """打印启动横幅。"""
    banner = f'''
╔══════════════════════════════════════════════════════════╗
║           Python MCP Server v{VERSION:<39}               ║
║           Web Search Tool for Spring AI RAG              ║
╚══════════════════════════════════════════════════════════╝
'''
    print(banner)


def start_server(transport: str, config: dict):
    """启动 MCP 服务器。
    
    Args:
        transport: 传输方式 (stdio, sse, streamable-http)
        config: MCP 服务器配置字典
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
        logger.info(f'使用 {transport} 传输方式，监听 {listen_addr}')
        logger.info(f'MCP 服务器已就绪，端点地址: {listen_addr}')
        mcp.run(
            transport=transport,
            host=config['host'],
            port=config['port'],
            mount_path=config['mount_path']
        )


# 先加载 .env，再配置日志（支持通过 LOG_LEVEL 环境变量动态调整日志级别）
load_dotenv()
log_level = os.getenv('LOG_LEVEL', 'INFO')
logger = setup_logging(log_level)

if __name__ == '__main__':
    """启动 MCP 服务器。
    
    支持的传输方式：
    - stdio: 标准输入输出，适用于本地进程间通信
    - sse: Server-Sent Events，基于 HTTP 的单向推送
    - streamable-http: 基于 HTTP 的双向流式通信
    
    配置参数通过环境变量设置：
    - MCP_TRANSPORT: 传输方式 (默认: stdio)
    - MCP_HOST: 服务器主机地址 (默认: 127.0.0.1)
    - MCP_PORT: 服务器端口号 (默认: 8084)
    - MCP_MOUNT_PATH: URL 挂载路径 (默认: /mcp)
    - LOG_LEVEL: 日志级别 (默认: INFO)
    """
    try:
        # 打印启动横幅
        print_banner()
        
        # 加载并验证配置
        settings = load_configuration()
        
        # 启动服务器
        start_server(settings['transport'], settings['config'])
        
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
