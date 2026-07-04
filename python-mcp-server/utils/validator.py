"""参数验证模块。

提供端口号、传输方式等参数的验证功能。
"""
from config.constants import VALID_TRANSPORTS


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
