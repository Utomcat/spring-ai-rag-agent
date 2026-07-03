# Python MCP Server

> **📖 项目主文档**：[spring-ai-rag-agent README](../README.md)
>
> **详细文档**：[MCP Server 详细文档](../md/mcp-server.md)（配置说明、传输方式、部署、调试、性能优化、安全等）

基于 MCP（Model Context Protocol）协议的 Python 服务器，为 Spring AI RAG 知识库系统提供网络搜索等扩展工具能力。

## ⭐ 功能特性

- **网络搜索**：支持 DuckDuckGo 和 Bing 搜索引擎
- **MCP 协议**：支持 stdio、sse、streamable-http 三种传输方式
- **工具扩展**：易于添加新的工具方法
- **环境变量配置**：支持 `.env` 文件配置

## 🛠️ 技术栈

| 依赖             | 版本      | 说明       |
|----------------|---------|----------|
| Python         | 3.14+   | 运行环境     |
| mcp[cli]       | 1.28.1+ | MCP 协议实现 |
| langchain      | 1.3.11  | LLM 应用框架 |
| beautifulsoup4 | 4.15.0+ | HTML 解析  |
| requests       | 2.34.2+ | HTTP 请求  |
| python-dotenv  | 1.2.2+  | 环境变量管理   |

## 🚀 快速开始

### 安装依赖

```bash
pip install -e .
```

### 启动服务

```bash
# 默认 stdio 模式
python main.py

# streamable-http 模式
export MCP_TRANSPORT="streamable-http"
python main.py
```

> **Windows 用户**：使用 `set MCP_TRANSPORT=streamable-http`（CMD）或 `$env:MCP_TRANSPORT="streamable-http"`（PowerShell）。

服务默认运行在 `http://127.0.0.1:8084/mcp`。

## 文件结构

```
python-mcp-server/
├── main.py                              # MCP Server 启动入口
├── pyproject.toml                       # Python 项目依赖配置
├── .env                                 # 环境变量配置
├── .env.example                         # 环境变量示例
└── script/
    ├── __init__.py
    └── MCP/
        ├── __init__.py
        └── web_search_server.py         # 网络搜索工具实现
```

## 更多内容

完整的配置说明、传输方式对比、部署方式、调试方法、性能优化和安全注意事项，请参考 [MCP Server 详细文档](../md/mcp-server.md)。

## 📜 License

Apache License 2.0
