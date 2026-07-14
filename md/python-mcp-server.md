# Python MCP Server 完整技术文档

> **← 返回主文档**：[README.md](../README.md)

基于 MCP (Model Context Protocol) 协议的 Python 智能数据服务引擎,为 Spring AI Agent 提供**网络搜索**、**多源数据获取**、**统计分析**和**可视化**能力。

## 📋 目录

- [核心功能](#核心功能)
- [技术栈](#技术栈)
- [快速开始](#快速开始)
- [工具列表](#工具列表)
- [fetch_webpage 使用指南](#fetch_webpage-使用指南)
- [使用示例](#使用示例)
- [架构设计](#架构设计)
- [配置说明](#配置说明)
- [与 Java 应用集成](#与-java-应用集成)
- [部署指南](#部署指南)
- [开发指南](#开发指南)
- [版本历史](#版本历史)

---

## ✨ 核心功能

### Phase 1: 网络搜索能力
- ✅ **多引擎支持**: Bing、DuckDuckGo搜索引擎
- ✅ **智能降级**: 主引擎失败自动切换到备用引擎
- ✅ **结果缓存**: 内存缓存提升重复查询性能
- ✅ **URL验证**: 安全的URL清洗和验证机制

### Phase 2: 数据源适配与基础分析
- ✅ **网页数据提取**: 自动识别表格和结构化内容
- ✅ **API数据获取**: REST API调用,支持JSON解析
- ✅ **文件读取**: CSV/Excel/TSV文件格式支持
- ✅ **统计分析**: 描述性统计、分布分析、分组对比

### Phase 3: 高级分析与可视化
- ✅ **趋势检测**: 线性回归、增长率计算、R²拟合优度
- ✅ **移动平均**: 平滑短期波动,识别长期趋势
- ✅ **季节性分析**: 周期性模式检测
- ✅ **简单预测**: 基于趋势外推的未来值预测
- ✅ **图表生成**: 折线图/柱状图/饼图/散点图(ECharts/AntV兼容)
- ✅ **报告生成**: Markdown格式结构化分析报告

### Phase 4: 优化增强 (v1.2.0)
- ✅ **智能缓存**: TTL过期策略,参数感知的缓存键
- ✅ **URL限流**: URL级别速率限制,防止被封禁
- ✅ **管理工具**: 缓存和限流的查询、清除、重置等管理功能
- ✅ **Playwright适配器**: 完整实现,支持JavaScript渲染页面抓取
- ✅ **LRU淘汰**: 缓存达到最大容量时自动淘汰最近最少使用的项

---

## 🛠️ 技术栈

| 依赖                    | 版本        | 说明            |
|-----------------------|-----------|---------------|
| Python                | 3.10+     | 运行环境          |
| mcp[cli]              | >= 1.0.0  | MCP 协议实现      |
| requests              | >= 2.31.0 | HTTP 请求       |
| beautifulsoup4 + lxml | -         | HTML/XML 解析   |
| Pandas                | >= 2.0.0  | 数据处理          |
| NumPy                 | >= 1.24.0 | 数值计算          |
| Pydantic              | >= 2.0.0  | 数据验证          |
| Uvicorn               | >= 0.24.0 | Web服务器        |
| Playwright            | 可选        | 动态页面抓取(需单独安装) |

---

## 🚀 快速开始

### 📝 前置要求

- Python 3.10+
- UV包管理器(或pip)

### 📥 安装步骤

```bash
# 进入项目目录
cd python-mcp-server

# 使用UV安装依赖(推荐)
uv sync

# 或使用pip
pip install -e .
```

### 🚀 启动服务

```bash
# 方式1: 直接运行main.py
python main.py

# 方式2: 使用uv run
uv run python main.py

# 方式3: HTTP流式通信
export MCP_TRANSPORT="streamable-http"
python main.py
```

服务将在 `http://127.0.0.1:8084/mcp` 启动。

---

## 🔧 工具列表

### 1. web_search - 网络搜索

通过网络搜索引擎搜索最新的信息。

**参数:**
- `query` (str): 搜索关键词或问题
- `max_results` (int): 搜索结果数量(1-20),默认5
- `engine` (str): 搜索引擎('bing' 或 'duckduckgo'),默认'duckduckgo'

**示例:**
```python
web_search(query="AI发展趋势 2024", max_results=10, engine="bing")
```

### 2. fetch_data - 数据获取

从指定数据源获取结构化数据(网页/API/文件)。

**参数:**
- `source_url` (str): 数据源URL或路径
- `source_type` (str): 数据源类型('webpage', 'api', 'file', 'auto'),默认'auto'
- `**kwargs`: 额外参数(table_index, method, headers, encoding等)

**示例:**
```python
# 获取网页表格
fetch_data(source_url="https://example.com/data", source_type="webpage")

# 调用API
fetch_data(source_url="https://api.example.com/data", source_type="api")

# 读取CSV文件
fetch_data(source_url="/path/to/data.csv", source_type="file")
```

### 3. analyze_data - 数据分析

对已获取的数据进行统计分析。

**参数:**
- `data_description` (str): 数据描述或数据源标识
- `analysis_type` (str): 分析类型('basic', 'distribution', 'patterns', 'time_series', 'comparison')
- `columns` (List[str], optional): 指定要分析的列名列表

**示例:**
```python
analyze_data(data_description="销售数据", analysis_type="basic")
```

### 4. trend_analysis - 趋势分析

对时间序列数据进行趋势分析。

**参数:**
- `data_description` (str): 数据描述或数据源标识
- `date_column` (str): 日期列名
- `value_column` (str): 数值列名
- `analysis_type` (str): 分析类型('detect', 'moving_average', 'seasonality', 'forecast')

**示例:**
```python
trend_analysis(
    data_description="股票数据",
    date_column="date",
    value_column="price",
    analysis_type="detect"
)
```

### 5. generate_chart_data - 图表数据生成

为前端可视化生成图表数据(JSON格式)。

**参数:**
- `data_description` (str): 数据描述或数据源标识
- `chart_type` (str): 图表类型('line', 'bar', 'pie', 'scatter', 'table')
- `x_column` (str, optional): X轴/分类列名
- `y_columns` (List[str], optional): Y轴/数值列名列表

**示例:**
```python
generate_chart_data(
    data_description="销售数据",
    chart_type="line",
    x_column="date",
    y_columns=["sales", "profit"]
)
```

### 6. generate_report - 报告生成

根据已获取的数据和分析结果生成结构化报告。

**参数:**
- `data_description` (str): 数据描述或数据源标识
- `report_type` (str): 报告类型('full', 'summary', 'trend', 'statistical')
- `title` (str, optional): 自定义报告标题

**示例:**
```python
generate_report(
    data_description="季度销售数据",
    report_type="full",
    title="2024年Q1销售分析报告"
)
```

### 7. fetch_webpage - 网页内容抓取

访问指定URL并提取网页的详细内容。

**特性:**
- ✅ **智能摘要模式**: 自动识别文章主要内容区域,移除导航、广告等噪音
- ✅ **全文模式**: 提取网页所有文本内容
- ✅ **结构化模式**: 重点提取表格和列表数据
- ✅ **元数据提取**: 自动提取标题、描述、作者、发布时间等
- ✅ **智能缓存**: 自动缓存抓取结果,避免重复请求(默认10分钟TTL)
- ✅ **速率限制**: URL级别的访问频率控制,防止被目标网站封禁(默认5次/分钟)
- ✅ **动态页面扩展**: 完整实现Playwright适配器,支持JavaScript渲染页面
- ✅ **LRU淘汰**: 缓存达到最大容量时自动淘汰最近最少使用的项
- ✅ **管理工具**: 提供缓存和限流的查询、清除、重置等管理功能

**参数:**

| 参数               | 类型   | 默认值       | 说明                                                   |
|------------------|------|-----------|------------------------------------------------------|
| `url`            | str  | 必填        | 要抓取的网页URL地址                                          |
| `mode`           | str  | 'summary' | 提取模式: 'summary'(智能摘要), 'full'(全文), 'structured'(结构化) |
| `max_length`     | int  | 10000     | 最大文本长度限制(字符数)                                        |
| `extract_tables` | bool | True      | 是否提取表格数据(structured模式有效)                             |
| `extract_links`  | bool | False     | 是否提取页面链接                                             |
| `extract_images` | bool | False     | 是否提取图片信息                                             |

**示例:**
```python
# 智能摘要模式(推荐用于文章阅读)
fetch_webpage(url="https://example.com/article")

# 全文模式(需要完整内容)
fetch_webpage(url="https://example.com/page", mode="full", max_length=5000)

# 结构化模式(提取表格数据)
fetch_webpage(url="https://example.com/data", mode="structured", extract_tables=True)

# 提取链接和图片
fetch_webpage(url="https://example.com/news", mode="summary", extract_links=True, extract_images=True)
```

### 8. get_webpage_cache_stats - 获取缓存统计

查看网页缓存的使用情况统计信息。

**示例:**
```python
get_webpage_cache_stats()
```

### 9. clear_webpage_cache - 清除缓存

清除网页缓存,可以清除所有缓存或指定URL的缓存。

**参数:**
- `url` (str, optional): 可选,指定要清除的URL。不提供则清除所有缓存

**示例:**
```python
clear_webpage_cache()
clear_webpage_cache(url="https://example.com/article")
```

### 10. reset_webpage_rate_limit - 重置限流记录

重置网页访问的限流计数。

**参数:**
- `url` (str, optional): 可选,指定要重置的URL。不提供则重置所有URL

**示例:**
```python
reset_webpage_rate_limit()
reset_webpage_rate_limit(url="https://example.com")
```

### 11. get_webpage_rate_limit_stats - 获取限流统计

查看当前所有URL的访问频率统计信息。

**示例:**
```python
get_webpage_rate_limit_stats()
```

---

## 📖 fetch_webpage 使用指南

### 📖 基本用法

#### 1. 🧠 智能摘要模式(推荐)

适用于阅读文章、新闻等内容型网页:

```python
fetch_webpage(url="https://zh.wikipedia.org/wiki/人工智能")
```

**输出示例:**
```
网页内容摘要

URL: https://zh.wikipedia.org/wiki/人工智能

## 人工智能

元数据:
  - 描述: 人工智能(AI)是...
  - 作者: Wikipedia Contributors

主要内容:
人工智能(Artificial Intelligence, AI)...
[文章内容]

统计: 约 1523 个单词
```

#### 2. 📄 全文模式

需要获取完整网页内容时使用:

```python
fetch_webpage(url="https://example.com/page", mode="full", max_length=5000)
```

#### 3. 📊 结构化模式

适用于包含表格数据的页面:

```python
fetch_webpage(
    url="https://en.wikipedia.org/wiki/List_of_countries_by_GDP_(nominal)",
    mode="structured",
    extract_tables=True
)
```

**输出示例:**
```
网页结构化数据

URL: https://...

## GDP排名

发现 2 个表格:

### 表格 1
国家      | GDP(十亿美元) | 年份
美国      | 25462.7       | 2022
中国      | 17963.2       | 2022
...
```

#### 4. 🔗 提取链接和图片

```python
fetch_webpage(url="https://example.com/news", mode="summary", extract_links=True, extract_images=True)
```

### 🎯 使用场景

#### 场景1: 🔍 RAG知识增强

```python
# Agent先搜索相关信息
search_results = web_search(query="最新AI发展趋势")

# 从搜索结果中选择有价值的链接深入阅读
article_url = "https://example.com/ai-trends-2024"
content = fetch_webpage(url=article_url, mode="summary")

# 基于提取的内容进行分析
analysis = analyze_data(data_description=content, analysis_type="basic")
```

#### 场景2: 📋 数据表格提取

```python
data = fetch_webpage(url="https://stats.example.com/economic-data", mode="structured", extract_tables=True)
trend = trend_analysis(data_description=data, date_column="年份", value_column="GDP增长率", analysis_type="detect")
```

#### 场景3: 🏢 竞品分析

```python
competitor_info = fetch_webpage(url="https://competitor.com/products", mode="summary", extract_links=True)
report = generate_report(data_description=competitor_info, report_type="summary", title="竞品分析报告")
```

### ⚠️ 错误处理

```python
# 无效URL
result = fetch_webpage(url="not-a-valid-url")
# 返回: 错误: 无效的URL格式 "not-a-valid-url"

# 网络超时
result = fetch_webpage(url="https://slow-site.com")
# 返回: 错误: 无法访问URL "https://slow-site.com"

# HTTP错误
result = fetch_webpage(url="https://example.com/404-page")
# 返回: 错误: HTTP 404

# 限流触发
for i in range(7):
    result = fetch_webpage(url="https://example.com")
# 第6次请求会被限流: "错误: 访问频率受限: 该URL在60秒内已访问5次"
```

### 💡 最佳实践

1. **优先使用summary模式**: 智能摘要模式能自动识别主要内容,去除噪音,适合大多数场景
2. **合理设置max_length**: 根据实际需求调整,避免返回过多无用信息
3. **按需启用links/images**: 只在确实需要时才开启,减少返回数据量
4. **结合web_search使用**: 先搜索再深入阅读
5. **注意网站robots.txt**: 尊重网站的爬虫规则
6. **监控缓存使用情况**: 定期使用 `get_webpage_cache_stats()` 查看缓存状态
7. **及时清理缓存**: 对于频繁更新的网站,可使用 `clear_webpage_cache(url="...")` 清除特定缓存

### 🔧 技术实现

- **HTML解析**: 使用BeautifulSoup + lxml
- **智能提取**: 基于常见内容容器选择器(article, main等)
- **内容清洗**: 自动移除脚本、样式、导航、广告等元素
- **HTTP客户端**: 复用项目现有的HttpClient,支持超时和重试
- **缓存管理**: CacheManager with LRU淘汰策略
- **速率限制**: URLRateLimiter滑动窗口算法
- **动态页面**: Playwright浏览器自动化(需单独安装)

### ⚠️ 注意事项

- 某些网站可能有反爬虫机制,导致抓取失败
- JavaScript动态渲染的内容需要安装Playwright才能正确提取
- 需要登录才能访问的页面无法抓取
- 缓存命中时会返回 `[注: 此结果来自缓存]` 标记

---

## 💡 完整工作流程示例

```python
# 1. 网络搜索获取最新信息
search_results = web_search(query="2024年人工智能发展趋势", max_results=10)

# 2. 从网页获取详细数据
data = fetch_data(source_url="https://example.com/ai-report-2024", source_type="webpage")

# 3. 基础统计分析
stats = analyze_data(data_description="AI行业报告数据", analysis_type="basic")

# 4. 趋势分析
trend = trend_analysis(data_description="AI投资数据", date_column="year", value_column="investment", analysis_type="detect")

# 5. 生成可视化数据
chart = generate_chart_data(data_description="AI投资趋势", chart_type="line", x_column="year", y_columns=["investment"])

# 6. 生成完整报告
report = generate_report(data_description="AI行业分析", report_type="full", title="2024年AI行业发展报告")
```

---

## 🏗️ 架构设计

### 项目结构

```
python-mcp-server/
├── config/                    # 配置模块
│   ├── constants.py          # 常量定义
│   └── settings.py           # 环境变量配置
├── server/                    # MCP服务器核心
│   └── mcp_server.py         # MCP实例创建
├── search/                    # 搜索引擎模块
│   └── engine.py             # 搜索引擎实现
├── parser/                    # HTML解析模块
│   └── html_parser.py        # HTML解析器
├── datasource/                # 数据源适配器
│   ├── base_adapter.py       # 适配器基类
│   ├── webpage_adapter.py    # 网页适配器
│   ├── api_adapter.py        # API适配器
│   ├── file_adapter.py       # 文件适配器
│   ├── dynamic_page_adapter.py  # 动态页面适配器(Playwright完整实现)
│   └── factory.py            # 数据源工厂
├── analyzer/                  # 分析模块
│   ├── data_extractor.py     # 数据提取器
│   ├── statistic_calculator.py  # 统计计算器
│   ├── trend_analyzer.py     # 趋势分析器
│   └── report_generator.py   # 报告生成器
├── visualization/             # 可视化模块
│   └── chart_generator.py    # 图表数据生成器
├── tools/                     # MCP工具
│   ├── web_search_tool.py
│   ├── fetch_data_tool.py
│   ├── fetch_webpage_tool.py  # 网页内容抓取
│   ├── analyze_data_tool.py
│   ├── trend_analysis_tool.py
│   ├── generate_chart_data_tool.py
│   └── generate_report_tool.py
├── models/                    # 数据模型
│   ├── search_result.py
│   └── data_source.py
├── utils/                     # 工具模块
│   ├── http_client.py        # HTTP客户端
│   ├── cache_manager.py      # 缓存管理器(支持LRU淘汰)
│   ├── url_validator.py      # URL验证器
│   ├── url_rate_limiter.py   # URL速率限制器
│   ├── content_cleaner.py    # 内容清洗工具
│   └── exceptions.py         # 异常管理
└── tests/                     # 测试
    ├── test_phase1.py
    ├── test_phase2.py
    ├── test_phase3.py
    ├── test_fetch_webpage.py
    ├── test_optimized_fetch.py
    ├── test_new_features.py
    └── test_dual_layer_cache.py
```

---

## ⚙️ 配置说明

### 环境变量

| 变量名                        | 说明            | 默认值             |
|----------------------------|---------------|-----------------|
| MCP_HOST                   | 监听地址          | 127.0.0.1       |
| MCP_PORT                   | 监听端口          | 8084            |
| MCP_MOUNT_PATH             | 挂载路径          | /mcp            |
| MCP_TRANSPORT              | 传输方式          | streamable-http |
| SEARCH_CONNECT_TIMEOUT     | 连接超时(秒)       | 5               |
| SEARCH_READ_TIMEOUT        | 读取超时(秒)       | 10              |
| SEARCH_MAX_RETRIES         | 最大重试次数        | 3               |
| WEBPAGE_CACHE_TTL          | 网页缓存TTL(秒)    | 600 (10分钟)      |
| WEBPAGE_CACHE_MAX_SIZE     | 网页缓存最大条目数     | 100             |
| WEBPAGE_RATE_LIMIT_PER_URL | 单个URL每分钟最大请求数 | 5               |
| WEBPAGE_RATE_LIMIT_WINDOW  | URL限流时间窗口(秒)  | 60              |

### 配置文件

复制 `.env.example` 为 `.env` 并修改配置:

```bash
cp .env.example .env
```

---

## 🔌 与 Java 应用集成

在 Java 应用的 `mcp.yml` 中配置：

```yaml
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

## 📡 传输方式

| 传输方式              | 说明                 | 适用场景              |
|-------------------|--------------------|-------------------|
| `stdio`           | 标准输入输出通信           | 本地运行、作为子进程启动      |
| `sse`             | Server-Sent Events | 需要浏览器连接、单向推送      |
| `streamable-http` | HTTP 双向流式通信        | 远程部署、需要 HTTP 基础设施 |

推荐使用 `streamable-http` 方式与 Java 应用集成。

---

## 📦 部署指南

### 开发环境

```bash
# 安装依赖
uv sync

# 运行测试
python tests/test_phase1.py
python tests/test_phase2.py
python tests/test_phase3.py

# 启动服务
python main.py
```

### 生产环境

```bash
# 使用gunicorn(需要安装)
pip install gunicorn
gunicorn -w 4 -k uvicorn.workers.UvicornWorker main:app

# 或使用docker(需创建Dockerfile)
docker build -t python-mcp-server .
docker run -p 8084:8084 python-mcp-server
```

### 健康检查

```bash
curl http://127.0.0.1:8084/mcp
```

---

## 👨‍💻 开发指南

### 添加新工具

1. 在 `tools/` 目录创建新工具文件
2. 使用 `@mcp.tool()` 装饰器注册工具
3. 在 `server/mcp_server.py` 中导入新工具

```python
# tools/my_new_tool.py
from server.mcp_server import mcp

@mcp.tool()
def my_new_tool(param1: str, param2: int) -> str:
    """工具描述"""
    return f"Result: {param1}, {param2}"
```

```python
# server/mcp_server.py
try:
    from tools import my_new_tool  # noqa: F401
    logger.info('my_new_tool 工具已加载')
except ImportError as e:
    logger.warning(f'my_new_tool工具加载失败: {e}')
```

### 添加新数据源适配器

1. 继承 `BaseDataSourceAdapter`
2. 实现 `fetch()` 和 `get_type()` 方法
3. 在 `DataSourceFactory` 中注册

```python
# datasource/my_adapter.py
from datasource.base_adapter import BaseDataSourceAdapter
from models.data_source import DataSourceResult

class MyAdapter(BaseDataSourceAdapter):
    def fetch(self, url: str, **kwargs) -> DataSourceResult:
        # 实现数据获取逻辑
        pass
    
    def get_type(self) -> str:
        return "my_source"
```

### 运行测试

```bash
# 运行所有测试
python tests/test_phase1.py
python tests/test_phase2.py
python tests/test_phase3.py
python tests/test_fetch_webpage.py
python tests/test_optimized_fetch.py
python tests/test_new_features.py
python tests/test_dual_layer_cache.py
```

---

## 📝 版本历史

### v1.2.0 (2026-07-06) - 优化增强版

在v1.1.0基础上，进一步增强了网页抓取工具的功能性和可管理性。

**新增功能：**

1. **缓存管理工具**（4个新工具）
   - `get_webpage_cache_stats` - 获取缓存统计（总条目、活跃条目、使用率等）
   - `clear_webpage_cache` - 清除缓存（支持清除所有或指定URL）
   - `reset_webpage_rate_limit` - 重置限流记录（支持单个或所有URL）
   - `get_webpage_rate_limit_stats` - 获取限流统计（监控URL总数、接近限流的URL列表）

2. **Playwright动态页面适配器（完整实现）**
   - ✅ 完整的浏览器自动化控制
   - ✅ 等待特定元素出现
   - ✅ 滚动到底部加载懒加载内容
   - ✅ 点击特定元素触发交互
   - ✅ 自动资源清理和错误处理
   - 安装：`pip install playwright && playwright install`

3. **CacheManager LRU淘汰策略增强**
   - ✅ 使用OrderedDict维护访问顺序
   - ✅ get操作自动标记为最近使用
   - ✅ set操作检查容量并触发LRU淘汰

**性能对比：**

| 功能   | v1.1.0   | v1.2.0    | 提升      |
|------|----------|-----------|---------|
| 缓存管理 | ❌ 无管理工具  | ✅ 4个管理工具  | **可监控** |
| 动态页面 | ⚠️ 占位接口  | ✅ 完整实现    | **可用**  |
| 缓存淘汰 | ❌ 仅TTL过期 | ✅ LRU+TTL | **智能**  |
| 限流管理 | ❌ 无法重置   | ✅ 可查询可重置  | **可控**  |

### v1.1.0 (2026-07-06) - 功能优化版

为 `fetch_webpage` 工具添加了三大核心功能：**智能缓存**、**URL级别速率限制**和**动态页面扩展支持**。

**新增功能：**

1. **智能缓存机制**
   - ✅ 自动缓存：首次请求后自动缓存结果
   - ✅ TTL过期：默认10分钟TTL，可通过环境变量配置
   - ✅ 参数感知：不同提取模式(mode)、参数组合使用独立缓存键
   - ✅ 容量控制：最大100条缓存条目，防止内存溢出
   - ✅ 缓存标记：命中缓存时返回结果包含 `[注: 此结果来自缓存]` 标记

2. **URL级别速率限制**
   - ✅ 滑动窗口算法：精确控制每个URL的访问频率
   - ✅ 独立计数：每个URL独立的请求计数器
   - ✅ 友好提示：超限时返回详细的等待时间信息
   - ✅ 统计监控：提供限流器状态统计接口

3. **动态页面扩展支持**
   - ✅ 抽象基类：定义统一的动态页面抓取接口
   - ✅ 双适配器：SeleniumAdapter + PlaywrightAdapter
   - ✅ 自动检测：工厂模式根据可用性自动选择

**性能对比：**

| 场景           | 优化前     | 优化后          | 提升           |
|--------------|---------|--------------|--------------||
| 重复访问同一URL    | ~2-5秒/次 | ~0.01秒/次(缓存) | **200-500倍** |
| 频繁访问同一URL    | 无限制     | 5次/分钟后限流     | **避免被封禁**    |
| JavaScript页面 | 无法处理    | 预留扩展接口       | **可扩展**      |

### v1.0.0 (2024-07-05)

- Phase 1: 网络搜索能力
- Phase 2: 数据源适配与基础分析
- Phase 3: 高级分析与可视化
- Phase 4: 优化与文档

---

## ❓ 常见问题

### Q: MCP Server 启动失败?

A: 请检查:
- Python 版本是否 >= 3.10
- 依赖是否已安装(`pip install -e .`)
- 端口是否被占用

### Q: Java 应用无法连接到 MCP Server?

A: 请检查:
- MCP Server 是否已启动
- 传输方式是否一致
- 网络是否可达
- 防火墙是否允许连接

### Q: fetch_webpage 抓取动态页面失败?

A: JavaScript渲染的页面需要安装Playwright:
```bash
pip install playwright
playwright install
```

### Q: 如何调整缓存和限流参数?

A: 修改 `.env` 文件或设置环境变量:
```bash
WEBPAGE_CACHE_TTL=300              # 改为5分钟
WEBPAGE_RATE_LIMIT_PER_URL=10      # 提高到10次/分钟
```

---

<div style="display: flex; justify-content: space-between; align-items: center;">
  <span style="color: #888; font-size: 0.9em;">📅 最后更新：2026-07-14</span>
  <a href="#python-mcp-server-完整技术文档">⬆️ 返回顶部</a>
</div>
