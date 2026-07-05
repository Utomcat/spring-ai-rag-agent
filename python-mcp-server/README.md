# Python MCP Server - 网络搜索与数据分析服务

基于MCP协议的Python智能数据服务引擎，提供**网络搜索**、**多源数据获取**、**统计分析**和**可视化**能力，为Spring AI Agent提供实时信息获取和深度数据洞察支持。

## 📋 目录

- [核心功能](#核心功能)
- [技术栈](#技术栈)
- [快速开始](#快速开始)
- [工具列表](#工具列表)
- [使用示例](#使用示例)
- [架构设计](#架构设计)
- [配置说明](#配置说明)
- [部署指南](#部署指南)
- [开发指南](#开发指南)

## ✨ 核心功能

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

## 🛠️ 技术栈

- **Python**: 3.10+
- **MCP框架**: mcp[cli] >= 1.0.0
- **HTTP客户端**: requests >= 2.31.0
- **HTML解析**: BeautifulSoup4 + lxml
- **数据处理**: Pandas >= 2.0.0, NumPy >= 1.24.0
- **数据验证**: Pydantic >= 2.0.0
- **Web服务器**: Uvicorn >= 0.24.0
- **包管理**: UV

## 🚀 快速开始

### 前置要求

- Python 3.10+
- UV包管理器（或pip）

### 安装步骤

```bash
# 进入项目目录
cd python-mcp-server

# 使用UV安装依赖（推荐）
uv sync

# 或使用pip
pip install -e .
```

### 启动服务

```bash
# 方式1: 直接运行main.py
python main.py

# 方式2: 使用uv run
uv run python main.py
```

服务将在 `http://127.0.0.1:8084/mcp` 启动。

## 🔧 工具列表

### 1. web_search - 网络搜索

通过网络搜索引擎搜索最新的信息。

**参数:**
- `query` (str): 搜索关键词或问题
- `max_results` (int): 搜索结果数量（1-20），默认5
- `engine` (str): 搜索引擎（'bing' 或 'duckduckgo'），默认'duckduckgo'

**示例:**
```python
web_search(query="AI发展趋势 2024", max_results=10, engine="bing")
```

### 2. fetch_data - 数据获取

从指定数据源获取结构化数据（网页/API/文件）。

**参数:**
- `source_url` (str): 数据源URL或路径
- `source_type` (str): 数据源类型（'webpage', 'api', 'file', 'auto'），默认'auto'
- `**kwargs`: 额外参数（table_index, method, headers, encoding等）

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
- `analysis_type` (str): 分析类型（'basic', 'distribution', 'patterns', 'time_series', 'comparison'）
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
- `analysis_type` (str): 分析类型（'detect', 'moving_average', 'seasonality', 'forecast'）

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

为前端可视化生成图表数据（JSON格式）。

**参数:**
- `data_description` (str): 数据描述或数据源标识
- `chart_type` (str): 图表类型（'line', 'bar', 'pie', 'scatter', 'table'）
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
- `report_type` (str): 报告类型（'full', 'summary', 'trend', 'statistical'）
- `title` (str, optional): 自定义报告标题

**示例:**
```python
generate_report(
    data_description="季度销售数据",
    report_type="full",
    title="2024年Q1销售分析报告"
)
```

## 💡 使用示例

### 完整工作流程

```python
# 1. 网络搜索获取最新信息
search_results = web_search(
    query="2024年人工智能发展趋势",
    max_results=10
)

# 2. 从网页获取详细数据
data = fetch_data(
    source_url="https://example.com/ai-report-2024",
    source_type="webpage"
)

# 3. 基础统计分析
stats = analyze_data(
    data_description="AI行业报告数据",
    analysis_type="basic"
)

# 4. 趋势分析
trend = trend_analysis(
    data_description="AI投资数据",
    date_column="year",
    value_column="investment",
    analysis_type="detect"
)

# 5. 生成可视化数据
chart = generate_chart_data(
    data_description="AI投资趋势",
    chart_type="line",
    x_column="year",
    y_columns=["investment"]
)

# 6. 生成完整报告
report = generate_report(
    data_description="AI行业分析",
    report_type="full",
    title="2024年AI行业发展报告"
)
```

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
│   ├── analyze_data_tool.py
│   ├── trend_analysis_tool.py
│   ├── generate_chart_data_tool.py
│   └── generate_report_tool.py
├── models/                    # 数据模型
│   ├── search_result.py
│   └── data_source.py
├── utils/                     # 工具模块
│   ├── http_client.py        # HTTP客户端
│   ├── cache_manager.py      # 缓存管理器
│   ├── url_validator.py      # URL验证器
│   └── exceptions.py         # 异常管理
└── tests/                     # 测试
    ├── test_phase1.py
    ├── test_phase2.py
    └── test_phase3.py
```

## ⚙️ 配置说明

### 环境变量

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| MCP_HOST | 监听地址 | 127.0.0.1 |
| MCP_PORT | 监听端口 | 8084 |
| MCP_MOUNT_PATH | 挂载路径 | /mcp |
| MCP_TRANSPORT | 传输方式 | streamable-http |
| SEARCH_CONNECT_TIMEOUT | 连接超时（秒） | 5 |
| SEARCH_READ_TIMEOUT | 读取超时（秒） | 10 |
| SEARCH_MAX_RETRIES | 最大重试次数 | 3 |

### 配置文件

复制 `.env.example` 为 `.env` 并修改配置：

```bash
cp .env.example .env
```

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
# 使用gunicorn（需要安装）
pip install gunicorn
gunicorn -w 4 -k uvicorn.workers.UvicornWorker main:app

# 或使用docker（需创建Dockerfile）
docker build -t python-mcp-server .
docker run -p 8084:8084 python-mcp-server
```

### 健康检查

```bash
# 检查服务状态
curl http://127.0.0.1:8084/mcp
```

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
```

## 📝 版本历史

- **v1.0.0** (2024-07-05)
  - Phase 1: 网络搜索能力
  - Phase 2: 数据源适配与基础分析
  - Phase 3: 高级分析与可视化
  - Phase 4: 优化与文档

## 🤝 贡献指南

欢迎提交Issue和Pull Request！

## 📄 许可证

MIT License

## 📧 联系方式

如有问题，请提交Issue或联系维护者。
