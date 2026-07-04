"""启动横幅模块。

提供程序启动时的欢迎信息展示功能。
"""
from config.constants import VERSION


def print_banner():
    """打印启动横幅。"""
    # 计算实际显示宽度：
    # ║(1) + 空格(11) + 'Python MCP Server v'(18) + VERSION(5) + 空格(24) + ║(1) = 60
    # 其中 VERSION 使用 <24 确保总宽度为 60
    banner = f'''
╔══════════════════════════════════════════════════════════╗
║           Python MCP Server v{VERSION:<28}║
║           Web Search Tool for Spring AI RAG              ║
╚══════════════════════════════════════════════════════════╝
'''
    print(banner)
