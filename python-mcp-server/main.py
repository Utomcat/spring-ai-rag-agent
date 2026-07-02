from script.MCP.web_search_server import mcp
from dotenv import load_dotenv
import os

# 加载 .env 配置文件
load_dotenv()

if __name__ == '__main__':
    """
    启动 MCP 服务器, 参数说明:
    
    - host: 服务器主机地址
    - port: 服务器端口号
    - transport: 传输方式, 可选值如下:
        - stdio (标准输入输出)
            - 通信方式：通过进程的标准输入(stdin)和标准输出(stdout)进行通信
            - 适用场景：
                本地运行的 MCP 服务器
                作为子进程被其他应用启动
                Spring AI 等框架集成时最常用
            - 优点：
                - 简单直接，无需网络配置
                - 安全性高，只在本地进程间通信
                - 易于调试和管理生命周期
            - 缺点：
                - 只能本地使用，无法远程访问
                - 需要父进程管理子进程
        - sse (Server-Sent Events)
            - 通信方式：基于 HTTP 的单向服务器推送协议
            - 适用场景：
                - 需要浏览器或 HTTP 客户端连接
                - 服务器向客户端推送实时数据
            - 优点：
                - 基于 HTTP，防火墙友好
                - 支持跨域通信
                - 可以实现远程访问
            - 缺点：
                - 单向通信（服务器→客户端）
                - 需要额外的机制处理客户端到服务器的请求
                - 配置相对复杂
        - streamable-http
            - 通信方式：基于 HTTP 的双向流式通信
            - 适用场景：
                - 需要完整的 HTTP 协议支持
                - 远程 MCP 服务器部署
                - 需要负载均衡或代理的场景
            - 优点：
                - 双向通信
                - 支持远程访问
                - 可以利用现有的 HTTP 基础设施（负载均衡、认证等）
            - 缺点：
                - 配置最复杂
                - 需要处理 HTTP 相关的各种问题（超时、连接池等）
                - 性能开销相对较大
    - mount_path: MCP 服务器在使用 HTTP 相关传输方式（sse 或 streamable-http）时的 URL 路径配置
        - 作用: 定了 MCP 服务端点在 HTTP 服务器上的挂载路径（URL 路径前缀）
        - 使用场景:
            - 使用 stdio 时
                - mount_path 不生效，可以忽略
                - 因为 stdio 是通过标准输入输出通信，不涉及 HTTP URL
            - 使用 sse 时
                - 客户端通过 https://host:port/{mount_path} 访问
                - 默认值通常是 /sse 或 /message
            - 使用 streamable-http 时
                - 客户端通过 https://host:port/{mount_path} 访问
                - 默认值通常是 /mcp
        - 使用示例
        ```python
            # 示例 1: 默认路径
            mcp.run(transport="streamable-http")
            # 访问: https://localhost:8000/mcp
            
            # 示例 2: 自定义路径
            mcp.run(transport="streamable-http", mount_path="/custom/path")
            # 访问: https://localhost:8000/custom/path
            
            # 示例 3: 带端口和主机
            mcp.run(
                transport="streamable-http",
                host="0.0.0.0",
                port=9000,
                mount_path="/api/v1/mcp"
            )
            # 访问: https://0.0.0.0:9000/api/v1/mcp
        ```
    """
    # 从环境变量读取配置,提供默认值
    transport = os.getenv("MCP_TRANSPORT", "stdio")
        
    # 启动 MCP 服务器
    mcp.run(transport=transport)
