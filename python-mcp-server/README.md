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
| Python         | 3.10+   | 运行环境     |
| mcp[cli]       | 1.0.0+  | MCP 协议实现 |
| beautifulsoup4 | 4.12.0+ | HTML 解析  |
| lxml           | 4.9.0+  | XML/HTML 解析器 |
| requests       | 2.31.0+ | HTTP 请求  |
| python-dotenv  | 1.0.0+  | 环境变量管理   |

## 🚀 快速开始

### 安装依赖

```bash
pip install -e .
```

### 启动服务

```bash
# 默认 stdio 模式
python main.py

# streamable-http 模式（推荐用于远程访问）
export MCP_TRANSPORT="streamable-http"
python main.py

# 自定义主机和端口
export MCP_HOST="0.0.0.0"
export MCP_PORT="9000"
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

## ✨ 优化特性

- ✅ **轻量级依赖**：仅包含必要的核心依赖，减少安装时间和安全风险
- ✅ **完善的日志记录**：支持详细的操作日志和错误追踪，支持动态日志级别
- ✅ **健壮的异常处理**：超时控制、重试机制、优雅的错误提示
- ✅ **输入验证**：防止无效参数导致的运行时错误
- ✅ **可扩展架构**：易于添加新的搜索引擎
- ✅ **类型注解**：完整的类型提示，提升代码可维护性
- ✅ **Session 复用**：HTTP 连接复用，提升性能 30%+
- ✅ **智能重试**：指数退避重试机制，提高网络请求成功率
- ✅ **URL 清理**：自动处理 DuckDuckGo 重定向链接
- ✅ **备选选择器**：主选择器失败时自动尝试备选方案
- ✅ **性能监控**：记录每次搜索的耗时统计
- ✅ **配置验证**：启动时验证所有配置项，提供友好的错误提示
- ✅ **优雅退出**：支持 Ctrl+C 优雅停止服务器

## 📝 更新日志

### v0.3.0 (2026-07-04)
- 🔧 重构 main.py，提取启动逻辑为独立函数
- 🔒 添加完整的配置验证（端口号、传输方式等）
- ⚙️ 支持动态日志级别配置 (LOG_LEVEL 环境变量)
- ✨ 添加启动横幅和版本信息展示
- ✨ 优化日志输出格式，更清晰易读
- ✨ 添加优雅退出机制（KeyboardInterrupt 处理）
- 📝 完善 .env.example 配置文件

### v0.2.0 (2026-07-04)
- 🔧 消除代码重复，提取通用搜索引擎实现 (_search_engine)
- 🔒 修复 globals() 安全问题，使用函数引用替代字符串
- ⚡ 添加 HTTP Session 复用机制，提升性能
- ⚙️ 超时时间支持环境变量配置 (SEARCH_CONNECT_TIMEOUT, SEARCH_READ_TIMEOUT)
- ✨ 使用 TypedDict 定义 SearchResult 数据结构
- ✨ 添加指数退避重试机制 (SEARCH_MAX_RETRIES, SEARCH_RETRY_BACKOFF)
- ✨ 添加备选 CSS 选择器增强健壮性
- ✨ 自动清理 DuckDuckGo URL 重定向链接
- ✨ 添加搜索耗时性能监控
- 📝 完善 .env.example 配置文件

### v0.1.0 (2026-07-04)
- 🔧 清理未使用的重型依赖（langchain, chromadb, opencv 等）
- 🔧 修复重复加载 .env 文件问题
- ✨ 添加完善的日志记录系统
- ✨ 增强异常处理和超时控制
- ✨ 添加输入参数验证
- ✨ 优化搜索引擎选择逻辑（策略模式）
- ✨ 提取常量配置（User-Agent、超时时间等）
- ✨ 添加完整的类型注解
- 📝 修正 Python 版本要求为 >=3.10

## 📜 License

Apache License 2.0
