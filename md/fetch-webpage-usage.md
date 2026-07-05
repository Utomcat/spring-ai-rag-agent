# fetch_webpage 工具使用指南

> **← 返回主文档**：[README.md](../README.md)  
> **← 返回完整技术文档**：[Python MCP Server](python-mcp-server.md)

## 概述

`fetch_webpage` 是一个新增的MCP工具,用于访问指定URL并提取网页的详细内容。它提供了三种提取模式,可以智能识别主要内容区域、提取结构化数据或获取全文内容。

## 功能特性

- ✅ **智能摘要模式**: 自动识别文章主要内容区域,移除导航、广告等噪音
- ✅ **全文模式**: 提取网页所有文本内容
- ✅ **结构化模式**: 重点提取表格和列表数据
- ✅ **元数据提取**: 自动提取标题、描述、作者、发布时间等
- ✅ **可选扩展**: 支持提取页面链接和图片信息
- ✅ **安全机制**: URL验证、请求超时控制
- ✅ **智能缓存**: 自动缓存抓取结果,避免重复请求(默认10分钟TTL)
- ✅ **速率限制**: URL级别的访问频率控制,防止被目标网站封禁(默认5次/分钟)
- ✅ **动态页面扩展**: 完整实现Playwright适配器,支持JavaScript渲染页面
- ✅ **LRU淘汰**: 缓存达到最大容量时自动淘汰最近最少使用的项

## 基本用法

### 1. 智能摘要模式(推荐)

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

### 2. 全文模式

需要获取完整网页内容时使用:

```python
fetch_webpage(
    url="https://example.com/page", 
    mode="full",
    max_length=5000
)
```

### 3. 结构化模式

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

### 4. 提取链接和图片

```python
fetch_webpage(
    url="https://example.com/news",
    mode="summary",
    extract_links=True,
    extract_images=True
)
```

**输出会额外包含:**
```
页面链接 (共15个):
  1. [相关新闻](https://...)
  2. [延伸阅读](https://...)
  ...

页面图片 (共8张):
  1. 新闻配图: https://...
  2. 图表: https://...
  ...
```

## 参数说明

| 参数               | 类型   | 默认值       | 说明                                                   |
|------------------|------|-----------|------------------------------------------------------|
| `url`            | str  | 必填        | 要抓取的网页URL地址                                          |
| `mode`           | str  | 'summary' | 提取模式: 'summary'(智能摘要), 'full'(全文), 'structured'(结构化) |
| `max_length`     | int  | 10000     | 最大文本长度限制(字符数)                                        |
| `extract_tables` | bool | True      | 是否提取表格数据(structured模式有效)                             |
| `extract_links`  | bool | False     | 是否提取页面链接                                             |
| `extract_images` | bool | False     | 是否提取图片信息                                             |

## 使用场景

### 场景1: RAG知识增强

```python
# Agent先搜索相关信息
search_results = web_search(query="最新AI发展趋势")

# 从搜索结果中选择有价值的链接深入阅读
article_url = "https://example.com/ai-trends-2024"
content = fetch_webpage(url=article_url, mode="summary")

# 基于提取的内容进行分析
analysis = analyze_data(data_description=content, analysis_type="basic")
```

### 场景2: 数据表格提取

```python
# 从网页提取统计数据表格
data = fetch_webpage(
    url="https://stats.example.com/economic-data",
    mode="structured",
    extract_tables=True
)

# 对提取的数据进行趋势分析
trend = trend_analysis(
    data_description=data,
    date_column="年份",
    value_column="GDP增长率",
    analysis_type="detect"
)
```

### 场景3: 竞品分析

```python
# 抓取竞品官网信息
competitor_info = fetch_webpage(
    url="https://competitor.com/products",
    mode="summary",
    extract_links=True
)

# 生成分析报告
report = generate_report(
    data_description=competitor_info,
    report_type="summary",
    title="竞品分析报告"
)
```

## 错误处理

### 无效URL

```python
result = fetch_webpage(url="not-a-valid-url")
# 返回: 错误: 无效的URL格式 "not-a-valid-url"
#       请确保URL以 http:// 或 https:// 开头
```

### 网络超时

```python
result = fetch_webpage(url="https://slow-site.com")
# 返回: 错误: 无法访问URL "https://slow-site.com"
#       可能原因: 网站不存在、网络超时或被拒绝访问
```

### HTTP错误

```python
result = fetch_webpage(url="https://example.com/404-page")
# 返回: 错误: HTTP 404
#       URL: https://example.com/404-page
```

### 限流触发

```python
# 快速连续请求同一URL超过限制
for i in range(7):
    result = fetch_webpage(url="https://example.com")
    
# 第6次请求会被限流:
# "错误: 访问频率受限: 该URL在60秒内已访问5次
# 请等待45秒后重试,或访问其他URL"
```

## 最佳实践

1. **优先使用summary模式**: 智能摘要模式能自动识别主要内容,去除噪音,适合大多数场景
2. **合理设置max_length**: 根据实际需求调整,避免返回过多无用信息
3. **按需启用links/images**: 只在确实需要时才开启,减少返回数据量
4. **结合其他工具使用**: fetch_webpage通常与web_search配合使用,先搜索再深入阅读
5. **注意网站robots.txt**: 尊重网站的爬虫规则
6. **监控缓存使用情况**: 定期使用 `get_webpage_cache_stats()` 查看缓存状态
7. **及时清理缓存**: 对于频繁更新的网站,可使用 `clear_webpage_cache(url="...")` 清除特定缓存

## 技术实现

- **HTML解析**: 使用BeautifulSoup + lxml
- **智能提取**: 基于常见内容容器选择器(article, main等)
- **内容清洗**: 自动移除脚本、样式、导航、广告等元素
- **HTTP客户端**: 复用项目现有的HttpClient,支持超时和重试
- **缓存管理**: CacheManager with LRU淘汰策略
- **速率限制**: URLRateLimiter滑动窗口算法
- **动态页面**: Playwright浏览器自动化(需单独安装)

## 注意事项

- 某些网站可能有反爬虫机制,导致抓取失败
- JavaScript动态渲染的内容需要安装Playwright才能正确提取
- 需要登录才能访问的页面无法抓取
- 建议遵守目标网站的robots.txt协议和使用条款
- 缓存命中时会返回 `[注: 此结果来自缓存]` 标记
- 大量缓存条目可能占用较多内存,注意监控 `WEBPAGE_CACHE_MAX_SIZE`

---

<div style="display: flex; justify-content: space-between; align-items: center;">
  <span style="color: #888; font-size: 0.9em;">📅 最后更新:2026-07-06</span>
  <a href="#fetch_webpage-工具使用指南">⬆️ 返回顶部</a>
</div>
