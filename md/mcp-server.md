# Python MCP Server 子项目

> **← 返回主文档**：[README.md](../README.md)

基于 MCP（Model Context Protocol）协议的 Python 服务器，为 Spring AI RAG 知识库系统提供网络搜索等扩展工具能力。

## ⭐ 功能特性

- **网络搜索**：支持 DuckDuckGo 和 Bing 搜索引擎
- **MCP 协议**：支持 stdio、sse、streamable-http 三种传输方式
- **工具扩展**：易于添加新的工具方法
- **环境变量配置**：支持 `.env` 文件配置

## 🛠️ 子项目技术栈

| 依赖             | 版本      | 说明       |
|----------------|---------|----------|
| Python         | 3.14+   | 运行环境     |
| mcp[cli]       | 1.28.1+ | MCP 协议实现 |
| langchain      | 1.3.11  | LLM 应用框架 |
| beautifulsoup4 | 4.15.0+ | HTML 解析  |
| requests       | 2.34.2+ | HTTP 请求  |
| python-dotenv  | 1.2.2+  | 环境变量管理  |
| lxml           | 6.1.1+  | XML/HTML 解析 |

## 子项目文件结构

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

## 📖 功能说明

### 网络搜索工具

**工具名称**：`web_search`

**功能**：通过网络搜索引擎搜索最新的信息

**触发场景**：

- "最新的新闻是什么？"
- "搜索一下 Spring AI 的最新动态"
- "今天的天气怎么样？"
- "查询股票行情"
- "查找技术文档"
- 其他需要获取最新信息的问题

**工具参数**：

| 参数名       | 类型    | 必填 | 默认值 | 说明                    |
|------------|-------|-----|-------|-----------------------|
| query      | String | 是   | -     | 搜索关键词或问题             |
| max_results | Integer | 否   | 5     | 搜索结果数量               |
| engine     | String | 否   | duckduckgo | 搜索引擎（bing 或 duckduckgo） |

**使用示例**：

```bash
# 使用 DuckDuckGo 搜索（默认）
web_search("Spring AI 最新动态")

# 使用 Bing 搜索
web_search("Spring AI 最新动态", engine="bing")

# 指定结果数量
web_search("Spring AI 最新动态", max_results=10)
```

**返回格式**：

```text
搜索结果（共 5 条）：
1. **标题1**
   - 摘要：摘要内容
   - 链接：https://example.com

2. **标题2**
   - 摘要：摘要内容
   - 链接：https://example.com
```

## 🚀 快速开始

### 安装依赖

```bash
cd python-mcp-server
pip install -e .
```

### 启动服务

**方式一：标准输入输出（stdio）**

```bash
python main.py
```

**方式二：HTTP 流式通信（streamable-http）**

```bash
export MCP_TRANSPORT="streamable-http"
python main.py
```

**方式三：Server-Sent Events（sse）**

```bash
export MCP_TRANSPORT="sse"
python main.py
```

> **Windows 用户注意**：设置环境变量请使用 `set MCP_TRANSPORT=streamable-http`（CMD）或 `$env:MCP_TRANSPORT="streamable-http"`（PowerShell）。

## 配置说明

### .env 文件

```env
# MCP Server Configuration

# Server host address
MCP_HOST=0.0.0.0

# Server port number
MCP_PORT=8084

# Transport mode: stdio, sse, or streamable-http
MCP_TRANSPORT=stdio

# URL mount path (only used for sse or streamable-http)
MCP_MOUNT_PATH=
```

### 配置项说明

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| MCP_HOST | 0.0.0.0 | 服务器主机地址 |
| MCP_PORT | 8084 | 服务器端口号 |
| MCP_TRANSPORT | stdio | 传输方式（stdio/sse/streamable-http） |
| MCP_MOUNT_PATH | - | URL 挂载路径（仅用于 sse/streamable-http） |

### pyproject.toml

```toml
[project]
name = "python-mcp-server"
version = "0.1.0"
requires-python = ">=3.14"
dependencies = [
    "beautifulsoup4>=4.15.0",
    "chromadb>=1.5.9",
    "chromadb-client>=1.5.9",
    "deepagents==0.6.12",
    "langchain==1.3.11",
    "langchain-anthropic>=1.4.4",
    "langchain-core>=1.4.0",
    "langchain-deepseek>=1.1.0",
    "langchain-openai>=1.2.2",
    "langgraph==1.2.7",
    "lxml>=6.1.1",
    "mcp[cli]>=1.28.1",
    "notebook>=7.5.6",
    "openai==2.44.0",
    "opencv-python>=4.13.0.92",
    "pydantic>=2.13.4",
    "pyinstaller>=6.20.0",
    "python-dotenv>=1.2.2",
    "requests>=2.34.2",
    "tinycss2==1.5.1",
]
```

## 📡 传输方式

| 传输方式              | 说明                 | 适用场景              |
|-------------------|--------------------|-------------------|
| `stdio`           | 标准输入输出通信           | 本地运行、作为子进程启动      |
| `sse`             | Server-Sent Events | 需要浏览器连接、单向推送      |
| `streamable-http` | HTTP 双向流式通信        | 远程部署、需要 HTTP 基础设施 |

### stdio 模式

**通信方式**：通过进程的标准输入(stdin)和标准输出(stdout)进行通信

**适用场景**：
- 本地运行的 MCP 服务器
- 作为子进程被其他应用启动
- Spring AI 等框架集成时最常用

**优点**：
- 简单直接，无需网络配置
- 安全性高，只在本地进程间通信
- 易于调试和管理生命周期

**缺点**：
- 只能本地使用，无法远程访问
- 需要父进程管理子进程

### sse 模式

**通信方式**：基于 HTTP 的单向服务器推送协议

**适用场景**：
- 需要浏览器或 HTTP 客户端连接
- 服务器向客户端推送实时数据

**优点**：
- 基于 HTTP，防火墙友好
- 支持跨域通信
- 可以实现远程访问

**缺点**：
- 单向通信（服务器→客户端）
- 需要额外的机制处理客户端到服务器的请求
- 配置相对复杂

### streamable-http 模式

**通信方式**：基于 HTTP 的双向流式通信

**适用场景**：
- 需要完整的 HTTP 协议支持
- 远程 MCP 服务器部署
- 需要负载均衡或代理的场景

**优点**：
- 双向通信
- 支持远程访问
- 可以利用现有的 HTTP 基础设施（负载均衡、认证等）

**缺点**：
- 配置最复杂
- 需要处理 HTTP 相关的各种问题（超时、连接池等）
- 性能开销相对较大

## 与 Java 应用集成

### 配置方式

在 Java 应用的 `mcp.yml` 中配置（详细说明请参考 [配置文件说明 - mcp.yml](configuration.md#14-mcpyml)）：

```yaml
mcp:
  enabled: true
  servers:
    python-mcp-web-serach-server:
      url: http://127.0.0.1:8084/mcp
      transport: streamable-http
```

### 集成流程

1. 启动 Python MCP Server（使用 `streamable-http` 传输方式）
2. 启动 Java 应用
3. Java 应用的 MCP Client 会自动连接到配置的 MCP Server
4. LLM 在对话中可以自动发现并调用 MCP Server 提供的工具

### 注意事项

- 确保 Python MCP Server 在 Java 应用启动前或同时启动
- 使用 `streamable-http` 传输方式时，确保端口一致（默认 8084）
- 如果使用 `stdio` 传输方式，需要通过进程管理方式启动 MCP Server

## ➕ 扩展新工具

### 步骤

1. 在 `script/MCP/` 目录下创建新的工具文件
2. 使用 `@mcp.tool()` 装饰器定义工具方法
3. 在方法中实现工具逻辑
4. 在 `main.py` 中导入并注册新工具

### 示例

```python
from mcp.server.fastmcp import FastMCP

mcp = FastMCP(
    name="My Custom Server",
    host="127.0.0.1",
    port=8084,
    mount_path="/mcp"
)

@mcp.tool()
def my_tool(param1: str, param2: int = 10) -> str:
    """
    我的自定义工具描述。

    Args:
        param1: 参数1描述
        param2: 参数2描述，默认值为10

    Returns:
        str: 工具执行结果
    """
    # 实现逻辑
    return f"执行成功：param1={param1}, param2={param2}"
```

### 工具定义规范

1. 使用 `@mcp.tool()` 装饰器
2. 方法参数需要类型注解
3. 方法需要文档字符串（docstring），描述工具的用途和参数
4. 返回类型通常为 `str`，便于 LLM 理解

## 📦 部署

### 本地部署

```bash
cd python-mcp-server
pip install -e .
export MCP_TRANSPORT="streamable-http"
python main.py
```

### Docker 部署（推荐）

创建 `Dockerfile`：

```dockerfile
FROM python:3.14-slim

WORKDIR /app

COPY pyproject.toml .
COPY main.py .
COPY script/ ./script/

RUN pip install --no-cache-dir -e .

ENV MCP_TRANSPORT=streamable-http
ENV MCP_HOST=0.0.0.0
ENV MCP_PORT=8084

EXPOSE 8084

CMD ["python", "main.py"]
```

构建并运行：

```bash
docker build -t python-mcp-server .
docker run -p 8084:8084 python-mcp-server
```

### 系统服务部署

创建 systemd 服务文件 `/etc/systemd/system/mcp-server.service`：

```ini
[Unit]
Description=Python MCP Server
After=network.target

[Service]
Type=simple
User=appuser
WorkingDirectory=/opt/python-mcp-server
Environment="MCP_TRANSPORT=streamable-http"
Environment="MCP_HOST=0.0.0.0"
Environment="MCP_PORT=8084"
ExecStart=/usr/bin/python main.py
Restart=always

[Install]
WantedBy=multi-user.target
```

启动服务：

```bash
systemctl daemon-reload
systemctl enable mcp-server
systemctl start mcp-server
```

## 调试

### 查看日志

MCP Server 启动后会输出日志到控制台：

```log
INFO - MCP Server starting on http://127.0.0.1:8084/mcp
INFO - Registered tools: ['web_search']
INFO - Server is ready
```

### 测试工具

可以使用 MCP 客户端工具测试：

```bash
# 安装 MCP CLI
pip install mcp[cli]

# 连接到 MCP Server 并测试
mcp call http://127.0.0.1:8084/mcp web_search --query "test"
```

## ⚡ 性能优化

### 1. 缓存搜索结果

对于相同的搜索关键词，可以缓存结果，避免重复请求。

### 2. 限制结果数量

通过 `max_results` 参数控制返回的结果数量，减少网络传输。

### 3. 使用异步请求

对于多个搜索引擎，可以使用异步请求并行获取结果。

### 4. 设置超时时间

在 `requests.get` 中设置合理的超时时间，避免长时间等待。

## 安全注意事项

1. **输入验证**：对用户输入进行验证，防止恶意请求
2. **请求限制**：限制每秒的请求次数，防止滥用
3. **HTTPS**：生产环境建议使用 HTTPS
4. **认证**：可以添加 API Key 认证，限制访问
5. **日志记录**：记录所有请求，便于审计和排查问题

## ❓ 常见问题

### Q: MCP Server 启动失败？

A: 请检查：
- Python 版本是否 >= 3.14
- 依赖是否已安装（`pip install -e .`）
- 端口是否被占用

### Q: Java 应用无法连接到 MCP Server？

A: 请检查：
- MCP Server 是否已启动
- 传输方式是否一致（Java 配置中使用的 transport 和 MCP Server 的 transport）
- 网络是否可达
- 防火墙是否允许连接

### Q: 搜索结果为空？

A: 请检查：
- 网络连接是否正常
- 搜索关键词是否合理
- 搜索引擎是否正常工作

### Q: 如何添加新的搜索引擎？

A: 在 `web_search_server.py` 中添加新的搜索函数，然后在 `web_search` 方法中调用该函数。

---

<div style="display: flex; justify-content: space-between; align-items: center;">
  <span style="color: #888; font-size: 0.9em;">📅 更新日期：2026-07-03</span>
  <a href="#python-mcp-server-子项目">⬆️ 返回顶部</a>
</div>
