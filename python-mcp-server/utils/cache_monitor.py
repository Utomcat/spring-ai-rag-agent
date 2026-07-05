"""缓存监控模块 - 集成 Prometheus。

导出缓存相关的监控指标，用于 Grafana 可视化展示。
"""
import logging

from prometheus_client import Counter, Gauge, Histogram, start_http_server

from config.constants import PROMETHEUS_PORT

logger = logging.getLogger(__name__)

# ==================== Prometheus 指标定义 ====================

# 缓存命中次数
cache_hits_total = Counter(
    'cache_hits_total',
    'Total number of cache hits',
    ['cache_layer']  # hot 或 full
)

# 缓存未命中次数
cache_misses_total = Counter(
    'cache_misses_total',
    'Total number of cache misses',
    ['cache_layer']
)

# 缓存操作次数
cache_operations_total = Counter(
    'cache_operations_total',
    'Total number of cache operations',
    ['operation', 'cache_layer']  # get/set/delete, hot/full
)

# 当前缓存大小
cache_size_gauge = Gauge(
    'cache_size_current',
    'Current number of items in cache',
    ['cache_layer']
)

# 缓存操作延迟
cache_latency_histogram = Histogram(
    'cache_latency_seconds',
    'Cache operation latency in seconds',
    ['operation', 'cache_layer']
)


def record_cache_hit(cache_layer: str) -> None:
    """记录缓存命中。
    
    Args:
        cache_layer: 缓存层名称 (hot 或 full)
    """
    cache_hits_total.labels(cache_layer=cache_layer).inc()


def record_cache_miss(cache_layer: str) -> None:
    """记录缓存未命中。
    
    Args:
        cache_layer: 缓存层名称 (hot 或 full)
    """
    cache_misses_total.labels(cache_layer=cache_layer).inc()


def record_cache_operation(operation: str, cache_layer: str) -> None:
    """记录缓存操作。
    
    Args:
        operation: 操作类型 (get/set/delete)
        cache_layer: 缓存层名称 (hot 或 full)
    """
    cache_operations_total.labels(operation=operation, cache_layer=cache_layer).inc()


def update_cache_size(cache_layer: str, size: int) -> None:
    """更新缓存大小指标。
    
    Args:
        cache_layer: 缓存层名称 (hot 或 full)
        size: 当前缓存条目数
    """
    cache_size_gauge.labels(cache_layer=cache_layer).set(size)


def start_monitor_server(port: int = PROMETHEUS_PORT) -> None:
    """启动 Prometheus 监控服务器。
    
    Args:
        port: Prometheus HTTP 服务器端口
    """
    try:
        start_http_server(port)
        logger.info(f"Prometheus 监控已启动: http://localhost:{port}/metrics")
    except Exception as e:
        logger.error(f"启动 Prometheus 监控失败: {e}", exc_info=True)
