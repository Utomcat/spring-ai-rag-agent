# Redis 双层缓存

> **← 返回主文档**：[README.md](../README.md)

已成功将 python-mcp-server 项目的内存缓存升级为基于 Redis 的双层分布式缓存架构，实现了热点数据和完整数据的分离存储，并集成了多项增强功能。

---

## 🚀 快速开始

### 步骤 1: 安装依赖

```bash
cd python-mcp-server
uv sync
```

### 步骤 2: 确认 Redis 服务

确保 Redis 服务正在运行：

```bash
# Linux/Mac
redis-cli ping
# 应该返回: PONG

# Docker
docker run -d -p 6379:6379 --name redis redis:latest
```

### 步骤 3: 配置环境变量

编辑 `.env` 文件，确认以下配置：

```bash
# Redis 连接（根据实际情况修改）
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=          # 如果有密码则填写
REDIS_CACHE_ENABLED=true

# 双 DB 配置
REDIS_HOT_DATA_DB=1
REDIS_FULL_DATA_DB=2
```

### 步骤 4: 运行测试

```bash
uv run pytest tests/test_dual_layer_cache.py -v
```

### 步骤 5: 启动服务

```bash
python main.py
```

预期日志：
```
==================================================
   Python MCP Server v1.0.0
==================================================

热点数据缓存已连接到 Redis DB 1
完整数据缓存已连接到 Redis DB 2
MCP 服务器已启动...
```

### 步骤 6: 验证功能

```python
# 测试网页抓取缓存
from tools.fetch_webpage_tool import fetch_webpage

# 第一次请求（会实际抓取）
result1 = fetch_webpage(url="https://example.com")

# 第二次请求（应该从缓存读取）
result2 = fetch_webpage(url="https://example.com")

# 查看缓存统计
from tools.fetch_webpage_tool import webpage_cache
stats = webpage_cache.get_stats()
print(f"缓存统计: {stats}")
```

---

## 📊 技术架构

### 数据流转图

```
新数据写入
    ↓
┌──────────────────────┐
│  FullDataCache (DB 2) │ ← 总是写入
└──────────────────────┘
    ↓
┌──────────────────────┐
│  HotDataCache (DB 1)  │ ← 条件写入（热点数据）
└──────────────────────┘

数据读取
    ↓
┌──────────────────────┐
│ HotDataCache (DB 1)   │ ← 优先查询（快速）
└──────────────────────┘
    ↓ 未命中
┌──────────────────────┐
│ FullDataCache (DB 2)  │ ← 后备查询（全量）
└──────────────────────┘
    ↓ 命中且访问频繁
┌──────────────────────┐
│ 晋升到 HotDataCache   │ ← 自动优化
└──────────────────────┘
```

### 命名空间设计

```
热点缓存: hot:{env}:{original_key}
完整缓存: full:{env}:{original_key}

示例（开发环境）:
  hot:dev:webpage:abc123def456
  full:dev:webpage:abc123def456
```

---

## ✅ 核心模块

### 基础配置

- **依赖包**: `redis>=5.0.0`、`prometheus-client>=0.19.0`
- **环境变量**: Redis 连接配置、双 DB 架构配置、热点/完整数据缓存参数、增强功能开关
- **常量定义**: 40+ 个 Redis 相关配置常量（`config/constants.py`）

### 核心缓存模块

| 模块 | 文件 | Redis DB | 说明 |
|---|---|---|---|
| 统一缓存接口 | `utils/cache_interface.py` | - | 定义所有缓存管理器必须实现的接口规范 |
| 热点数据缓存 | `utils/hot_data_cache_manager.py` | DB 1 | 小容量(50条)、短TTL(5分钟)、LRU淘汰、降级到内存 |
| 完整数据缓存 | `utils/full_data_cache_manager.py` | DB 2 | 大容量(500条)、长TTL(30分钟)、LRU淘汰、降级到内存 |
| 双层缓存管理器 | `utils/dual_layer_cache_manager.py` | - | 统一管理、读取先热点后完整、写入同时两层、自动晋升 |
| 内存缓存(降级) | `utils/cache_manager.py` | - | Redis 不可用时的降级方案 |

### 增强功能模块

| 模块 | 文件 | 说明 |
|---|---|---|
| 缓存预热 | `utils/cache_warmer.py` | 系统启动时预加载热点数据 |
| 缓存监控 | `utils/cache_monitor.py` | 集成 Prometheus 监控（命中率、延迟等指标） |
| 多实例同步 | `utils/cache_sync.py` | 使用 Redis Pub/Sub 实现多实例间缓存同步 |
| 热点追踪 | `utils/hot_data_tracker.py` | 追踪访问频率，自动晋升到热点缓存 |

---

## 🔍 监控缓存状态

### 方法 1: 使用 Redis CLI

```bash
# 查看热点缓存（DB 1）
redis-cli -n 1 keys "hot:*"

# 查看完整缓存（DB 2）
redis-cli -n 2 keys "full:*"

# 查看数据库大小
redis-cli -n 1 dbsize
redis-cli -n 2 dbsize
```

### 方法 2: 启用 Prometheus 监控

在 `.env` 中启用监控：

```bash
CACHE_MONITOR_ENABLED=true
PROMETHEUS_PORT=9090
```

重启服务后访问 `http://localhost:9090/metrics`，可查看：
- `cache_hits_total` - 缓存命中次数
- `cache_misses_total` - 缓存未命中次数
- `cache_size_current` - 当前缓存大小

### 方法 3: 代码中查看统计

```python
from tools.fetch_webpage_tool import webpage_cache

stats = webpage_cache.get_stats()
print(f"""
缓存统计信息:
- 热点缓存: {stats['hot_cache']['total_items']} 条, 使用率 {stats['hot_cache']['usage_percent']:.1f}%
- 完整缓存: {stats['full_cache']['total_items']} 条, 使用率 {stats['full_cache']['usage_percent']:.1f}%
""")
```

---

## 🔧 配置说明

### 必需配置

```bash
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_CACHE_ENABLED=true
REDIS_HOT_DATA_DB=1      # 热点数据
REDIS_FULL_DATA_DB=2     # 完整数据
```

### 可选增强功能

```bash
# 缓存预热
CACHE_WARMER_ENABLED=false
CACHE_WARMER_KEYS=webpage:https://example.com,search:AI技术

# 监控
CACHE_MONITOR_ENABLED=false
PROMETHEUS_PORT=9090

# 多实例同步
CACHE_SYNC_ENABLED=false
CACHE_SYNC_CHANNEL=mcp-cache-sync

# 热点追踪
HOT_DATA_TRACKER_ENABLED=false
HOT_DATA_THRESHOLD=10        # 访问阈值
HOT_DATA_WINDOW=3600         # 窗口期（秒）
```

---

## 🎯 高级功能

### 启用缓存预热

```bash
CACHE_WARMER_ENABLED=true
CACHE_WARMER_KEYS=webpage:https://example.com,search:Python编程
```

### 启用热点数据追踪

```bash
HOT_DATA_TRACKER_ENABLED=true
HOT_DATA_THRESHOLD=10      # 10次访问视为热点
HOT_DATA_WINDOW=3600       # 1小时窗口期
```

### 启用多实例同步

```bash
CACHE_SYNC_ENABLED=true
CACHE_SYNC_CHANNEL=mcp-cache-sync
```

---

## 📈 核心优势

| 优势 | 说明 |
|---|---|
| **性能优化** | 热点数据专用快速通道(DB 1)，智能晋升机制 |
| **资源管理** | 分层存储 + LRU 淘汰，防止内存溢出 |
| **可靠性** | Redis 不可用时自动降级到内存缓存 |
| **可观测性** | Prometheus 全指标覆盖，实时监控 |
| **多实例同步** | Redis Pub/Sub 确保分布式环境数据一致性 |
| **环境隔离** | 不同环境使用不同 Redis DB |

---

## 📊 性能调优

### 调整缓存容量

```bash
# 小流量场景
HOT_DATA_CACHE_MAX_SIZE=20
FULL_DATA_CACHE_MAX_SIZE=200

# 中等流量场景（默认）
HOT_DATA_CACHE_MAX_SIZE=50
FULL_DATA_CACHE_MAX_SIZE=500

# 大流量场景
HOT_DATA_CACHE_MAX_SIZE=100
FULL_DATA_CACHE_MAX_SIZE=1000
```

### 调整 TTL

```bash
# 频繁更新的数据
HOT_DATA_CACHE_TTL=120
FULL_DATA_CACHE_TTL=600

# 相对稳定的数据（默认）
HOT_DATA_CACHE_TTL=300
FULL_DATA_CACHE_TTL=1800

# 很少变化的数据
HOT_DATA_CACHE_TTL=600
FULL_DATA_CACHE_TTL=7200
```

---

## 🐛 常见问题排查

### Redis 连接失败

**症状**: 日志显示 "Redis 连接失败，降级到内存缓存"

**解决**: 检查 Redis 服务是否运行、`.env` 配置是否正确、防火墙是否阻止连接

### 缓存未生效

**症状**: 每次请求都重新抓取网页

**解决**: 检查日志是否有 "缓存命中" 记录、确认 TTL 是否过短、用 Redis CLI 检查数据

### 测试失败

**解决**: 确保 Redis 运行中、可访问 DB 1 和 DB 2、用 `pytest -v -s` 查看详细错误

---

## ⚠️ 注意事项

1. **Redis 服务依赖**: 确保 Redis 服务已启动并可访问
2. **DB 权限**: 确认 Redis 用户有访问 DB 1 和 DB 2 的权限
3. **密码安全**: 如果 Redis 设置了密码，务必在 `.env` 中正确配置
4. **监控端口**: Prometheus 端口（默认 9090）不要与其他服务冲突
5. **生产环境**: 务必设置 Redis 密码，启用监控和告警

---

<div style="display: flex; justify-content: space-between; align-items: center;">
  <span style="color: #888; font-size: 0.9em;">📅 最后更新：2026-07-14</span>
  <a href="#redis-双层缓存">⬆️ 返回顶部</a>
</div>
