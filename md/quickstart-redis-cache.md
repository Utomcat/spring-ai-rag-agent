# Redis 双层缓存快速启动指南

## 🚀 5分钟快速开始

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

# Windows (如果使用 WSL)
wsl redis-cli ping
```

如果 Redis 未运行，请启动它：

```bash
# Linux
sudo systemctl start redis

# Mac (使用 Homebrew)
brew services start redis

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

预期输出：
```
✓ 热点缓存测试通过
✓ 完整缓存测试通过
✓ 双层缓存基本读写测试通过
✓ 热点晋升测试通过
...
所有测试通过! ✓
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

在另一个终端测试缓存功能：

```python
# 测试网页抓取缓存
from tools.fetch_webpage_tool import fetch_webpage

# 第一次请求（会实际抓取）
result1 = fetch_webpage(url="https://example.com")
print("第一次请求完成")

# 第二次请求（应该从缓存读取）
result2 = fetch_webpage(url="https://example.com")
print("第二次请求完成（来自缓存）")

# 查看缓存统计
from tools.fetch_webpage_tool import webpage_cache
stats = webpage_cache.get_stats()
print(f"缓存统计: {stats}")
```

---

## 🔍 监控缓存状态

### 方法 1: 使用 Redis CLI

```bash
# 查看热点缓存（DB 1）
redis-cli -n 1 keys "hot:*"

# 查看完整缓存（DB 2）
redis-cli -n 2 keys "full:*"

# 查看某个 key 的 TTL
redis-cli -n 1 ttl "hot:dev:webpage:abc123"

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

重启服务后访问：
```
http://localhost:9090/metrics
```

可以看到以下指标：
- `cache_hits_total` - 缓存命中次数
- `cache_misses_total` - 缓存未命中次数
- `cache_size_current` - 当前缓存大小

### 方法 3: 代码中查看统计

```python
from tools.fetch_webpage_tool import webpage_cache

stats = webpage_cache.get_stats()
print(f"""
缓存统计信息:
- 热点缓存:
  - 总条目: {stats['hot_cache']['total_items']}
  - 活跃条目: {stats['hot_cache']['active_items']}
  - 使用率: {stats['hot_cache']['usage_percent']:.1f}%
  
- 完整缓存:
  - 总条目: {stats['full_cache']['total_items']}
  - 活跃条目: {stats['full_cache']['active_items']}
  - 使用率: {stats['full_cache']['usage_percent']:.1f}%
""")
```

---

## 🎯 高级功能启用

### 启用缓存预热

在 `.env` 中配置：

```bash
CACHE_WARMER_ENABLED=true
CACHE_WARMER_KEYS=webpage:https://example.com,search:Python编程
```

重启服务时会自动预加载这些数据到缓存。

### 启用热点数据追踪

在 `.env` 中配置：

```bash
HOT_DATA_TRACKER_ENABLED=true
HOT_DATA_THRESHOLD=10      # 10次访问视为热点
HOT_DATA_WINDOW=3600       # 1小时窗口期
```

系统会定期分析并自动将热点数据晋升到 DB 1。

### 启用多实例同步

在 `.env` 中配置：

```bash
CACHE_SYNC_ENABLED=true
CACHE_SYNC_CHANNEL=mcp-cache-sync
```

多个 MCP 服务器实例之间会自动同步缓存变更。

---

## 🐛 常见问题排查

### 问题 1: Redis 连接失败

**症状**: 日志显示 "Redis 连接失败，降级到内存缓存"

**解决**:
1. 检查 Redis 服务是否运行: `redis-cli ping`
2. 检查 `.env` 中的 REDIS_HOST 和 REDIS_PORT 是否正确
3. 检查防火墙是否阻止连接

### 问题 2: 依赖安装失败

**症状**: `uv sync` 报错

**解决**:
```bash
# 清理缓存重试
uv cache clean
uv sync

# 或者使用 pip
pip install redis>=5.0.0 prometheus-client>=0.19.0
```

### 问题 3: 测试失败

**症状**: pytest 测试用例失败

**解决**:
1. 确保 Redis 服务正在运行
2. 检查 Redis 是否可以访问 DB 1 和 DB 2
3. 查看详细错误信息: `pytest tests/test_dual_layer_cache.py -v -s`

### 问题 4: 缓存未生效

**症状**: 每次请求都重新抓取网页

**解决**:
1. 检查日志是否有 "缓存命中" 的记录
2. 确认 CACHE_TTL 配置是否过短
3. 使用 Redis CLI 检查数据是否真的写入: `redis-cli -n 1 keys "*"`

---

## 📊 性能调优建议

### 调整缓存容量

根据实际需求调整：

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
HOT_DATA_CACHE_TTL=120      # 2分钟
FULL_DATA_CACHE_TTL=600     # 10分钟

# 相对稳定的数据（默认）
HOT_DATA_CACHE_TTL=300      # 5分钟
FULL_DATA_CACHE_TTL=1800    # 30分钟

# 很少变化的数据
HOT_DATA_CACHE_TTL=600      # 10分钟
FULL_DATA_CACHE_TTL=7200    # 2小时
```

### 调整热点阈值

```bash
# 更激进的晋升（更容易成为热点）
HOT_DATA_THRESHOLD=5
HOT_DATA_WINDOW=1800        # 30分钟

# 更保守的晋升（默认）
HOT_DATA_THRESHOLD=10
HOT_DATA_WINDOW=3600        # 1小时

# 更严格的晋升
HOT_DATA_THRESHOLD=20
HOT_DATA_WINDOW=7200        # 2小时
```

---

## 🎓 学习资源

- **Redis 官方文档**: https://redis.io/documentation
- **Prometheus 文档**: https://prometheus.io/docs/
- **Python Redis 客户端**: https://redis-py.readthedocs.io/

---

## 💡 提示

1. **开发环境**: 建议使用本地 Redis 实例
2. **测试环境**: 可以启用所有增强功能进行测试
3. **生产环境**: 
   - 务必设置 Redis 密码
   - 启用监控和告警
   - 定期备份 Redis 数据
   - 考虑使用 Redis 集群提高可用性

---

祝你使用愉快！如有问题，请查看详细实施文档 `REDIS_CACHE_IMPLEMENTATION.md`。
