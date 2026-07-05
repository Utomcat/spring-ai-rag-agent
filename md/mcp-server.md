# Python MCP Server 概览

> **← 返回主文档**：[README.md](../README.md)

本文档提供 Python MCP Server 子项目的快速概览。详细的技术文档、使用指南和版本说明请参考以下资源：

## 📚 相关文档

| 文档                                           | 说明                                     |
|----------------------------------------------|----------------------------------------|
| [完整技术文档](python-mcp-server.md)               | Python MCP Server 完整技术文档，包含所有工具列表和使用示例 |
| [fetch_webpage 使用指南](fetch-webpage-usage.md) | 网页内容抓取工具的详细说明                          |
| [优化说明 v1.1.0](optimization-summary.md)       | 缓存、限流和动态页面扩展功能介绍                       |
| [增强说明 v1.2.0](enhancement-summary.md)        | 管理工具、Playwright适配器和LRU淘汰策略             |

---

## ⭐ 功能特性

Python MCP Server 为 Spring AI RAG 知识库系统提供网络搜索、数据获取和分析能力：

### Phase 1: 网络搜索能力
- ✅ **多引擎支持**: Bing、DuckDuckGo搜索引擎
- ✅ **智能降级**: 主引擎失败自动切换到备用引擎
- ✅ **结果缓存**: 内存缓存提升重复查询性能
- ✅ **URL验证**: 安全的URL清洗和验证机制

### Phase 2: 数据源适配与基础分析
- ✅ **网页数据提取**: 自动识别表格和结构化内容
- ✅ **API数据获取**: REST API调用，支持JSON解析
- ✅ **文件读取**: CSV/Excel/TSV文件格式支持
- ✅ **统计分析**: 描述性统计、分布分析、分组对比

### Phase 3: 高级分析与可视化
- ✅ **趋势检测**: 线性回归、增长率计算、R²拟合优度
- ✅ **移动平均**: 平滑短期波动，识别长期趋势
- ✅ **季节性分析**: 周期性模式检测
- ✅ **简单预测**: 基于趋势外推的未来值预测
- ✅ **图表生成**: 折线图/柱状图/饼图/散点图（ECharts/AntV兼容）
- ✅ **报告生成**: Markdown格式结构化分析报告

### Phase 4: 优化增强 (v1.2.0)
- ✅ **智能缓存**: TTL过期策略，参数感知的缓存键
- ✅ **URL限流**: URL级别速率限制，防止被封禁
- ✅ **管理工具**: 缓存和限流的查询、清除、重置等管理功能
- ✅ **Playwright适配器**: 完整实现，支持JavaScript渲染页面抓取
- ✅ **LRU淘汰**: 缓存达到最大容量时自动淘汰最近最少使用的项

---

## 🛠️ 技术栈

| 依赖                    | 版本        | 说明          |
|-----------------------|-----------|-------------|
| Python                | 3.10+     | 运行环境        |
| mcp[cli]              | >= 1.0.0  | MCP 协议实现    |
| requests              | >= 2.31.0 | HTTP 请求     |
| beautifulsoup4 + lxml | -         | HTML/XML 解析 |
| Pandas                | >= 2.0.0  | 数据处理        |
| NumPy                 | >= 1.24.0 | 数值计算        |
| Pydantic              | >= 2.0.0  | 数据验证        |
| Uvicorn               | >= 0.24.0 | Web服务器      |

---

## 🚀 快速开始

### 安装依赖

```bash
cd python-mcp-server
uv sync
```

或使用 pip：

```bash
pip install -e .
```

### 启动服务

```bash
# 默认方式（stdio）
python main.py

# HTTP 流式通信
export MCP_TRANSPORT="streamable-http"
python main.py
```

服务将在 `http://127.0.0.1:8084/mcp` 启动。

详细配置说明请参考 [完整技术文档](python-mcp-server.md#配置说明)。

---

## 🔧 核心工具

Python MCP Server 提供以下主要工具：

| 工具名称                         | 功能                    |
|------------------------------|-----------------------|
| web_search                   | 网络搜索（Bing/DuckDuckGo） |
| fetch_data                   | 从网页/API/文件获取数据        |
| fetch_webpage                | 网页内容抓取（智能摘要/全文/结构化）   |
| analyze_data                 | 数据统计分析                |
| trend_analysis               | 时间序列趋势分析              |
| generate_chart_data          | 图表数据生成                |
| generate_report              | 结构化报告生成               |
| get_webpage_cache_stats      | 获取缓存统计                |
| clear_webpage_cache          | 清除缓存                  |
| reset_webpage_rate_limit     | 重置限流记录                |
| get_webpage_rate_limit_stats | 获取限流统计                |

详细工具使用说明请参考 [完整技术文档](python-mcp-server.md#工具列表)。

---

## 📖 使用场景

### 场景1: 实时信息获取

```python
# Agent 搜索最新新闻
web_search(query="AI发展趋势 2024", max_results=10)
```

### 场景2: 网页数据提取

```python
# 抓取文章内容
fetch_webpage(url="https://example.com/article", mode="summary")

# 提取表格数据
fetch_webpage(url="https://example.com/data", mode="structured", extract_tables=True)
```

### 场景3: 数据分析与可视化

```python
# 基础统计分析
analyze_data(data_description="销售数据", analysis_type="basic")

# 趋势分析
trend_analysis(
    data_description="股票数据",
    date_column="date",
    value_column="price",
    analysis_type="detect"
)

# 生成图表数据
generate_chart_data(
    data_description="销售趋势",
    chart_type="line",
    x_column="month",
    y_columns=["sales"]
)
```

更多使用示例请参考 [完整技术文档](python-mcp-server.md#使用示例)。

---

## 🏗️ 架构设计

### 项目结构

```
python-mcp-server/
├── config/                    # 配置模块
├── server/                    # MCP服务器核心
├── search/                    # 搜索引擎模块
├── parser/                    # HTML解析模块
├── datasource/                # 数据源适配器
│   ├── base_adapter.py       # 适配器基类
│   ├── webpage_adapter.py    # 网页适配器
│   ├── api_adapter.py        # API适配器
│   ├── file_adapter.py       # 文件适配器
│   ├── dynamic_page_adapter.py  # 动态页面适配器(Playwright)
│   └── factory.py            # 数据源工厂
├── analyzer/                  # 分析模块
├── visualization/             # 可视化模块
├── tools/                     # MCP工具
├── models/                    # 数据模型
├── utils/                     # 工具模块
│   ├── cache_manager.py      # 缓存管理器(LRU)
│   ├── url_rate_limiter.py   # URL速率限制器
│   └── ...
└── tests/                     # 测试
```

详细架构说明请参考 [完整技术文档](python-mcp-server.md#架构设计)。

---

## 📡 传输方式

| 传输方式              | 说明                 | 适用场景              |
|-------------------|--------------------|-------------------|
| `stdio`           | 标准输入输出通信           | 本地运行、作为子进程启动      |
| `sse`             | Server-Sent Events | 需要浏览器连接、单向推送      |
| `streamable-http` | HTTP 双向流式通信        | 远程部署、需要 HTTP 基础设施 |

推荐使用 `streamable-http` 方式与 Java 应用集成。

---

## 🔌 与 Java 应用集成

在 Java 应用的 `mcp.yml` 中配置：

```
mcp:
  enabled: true
  servers:
    python-mcp-web-search-server:
      url: http://127.0.0.1:8084/mcp
      transport: streamable-http
```

集成流程：
1. 启动 Python MCP Server（使用 `streamable-http` 传输方式）
2. 启动 Java 应用
3. Java 应用的 MCP Client 会自动连接到配置的 MCP Server
4. LLM 在对话中可以自动发现并调用 MCP Server 提供的工具

详细配置说明请参考 [配置文件说明 - mcp.yml](configuration.md#14-mcpyml)。

---

## ➕ 扩展新工具

### 步骤

1. 在 `tools/` 目录创建新工具文件
2. 使用 `@mcp.tool()` 装饰器定义工具方法
3. 在 `server/mcp_server.py` 中导入新工具

### 示例

```
from server.mcp_server import mcp

@mcp.tool()
def my_custom_tool(param1: str, param2: int = 10) -> str:
    """我的自定义工具描述。"""
    return f"Result: {param1}, {param2}"
```

详细开发指南请参考 [完整技术文档](python-mcp-server.md#开发指南)。

---

## 📦 部署指南

### 开发环境

```bash
uv sync
python main.py
```

### 生产环境

```
# 使用 gunicorn
pip install gunicorn
gunicorn -w 4 -k uvicorn.workers.UvicornWorker main:app

# 或使用 Docker
docker build -t python-mcp-server .
docker run -p 8084:8084 python-mcp-server
```

详细部署说明请参考 [完整技术文档](python-mcp-server.md#部署指南)。

---

## ❓ 常见问题

### Q: MCP Server 启动失败？

A: 请检查：
- Python 版本是否 >= 3.10
- 依赖是否已安装（`pip install -e .`）
- 端口是否被占用

### Q: Java 应用无法连接到 MCP Server？

A: 请检查：
- MCP Server 是否已启动
- 传输方式是否一致
- 网络是否可达
- 防火墙是否允许连接

更多问题和解决方案请参考 [完整技术文档](python-mcp-server.md#常见问题)。

---

<div style="display: flex; justify-content: space-between; align-items: center;">
  <span style="color: #888; font-size: 0.9em;">📅 最后更新：2026-07-06</span>
  <a href="#python-mcp-server-概览">⬆️ 返回顶部</a>
</div>
