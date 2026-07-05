"""双层缓存测试。

测试 Redis 双 DB 架构的缓存功能。
"""
import pytest
import time


def test_hot_data_cache():
    """测试热点数据缓存 (DB 1)。"""
    from utils.hot_data_cache_manager import HotDataCacheManager
    
    cache = HotDataCacheManager(ttl=60, max_size=10)
    
    # 测试基本操作
    cache.set("test_key", "test_value")
    assert cache.get("test_key") == "test_value"
    
    # 测试字典类型
    cache.set("dict_key", {"name": "test", "value": 123})
    result = cache.get("dict_key")
    assert isinstance(result, dict)
    assert result["name"] == "test"
    
    # 测试统计
    stats = cache.get_stats()
    assert stats['cache_type'] == 'hot_data'
    assert stats['redis_db'] == 1
    print(f"✓ 热点缓存测试通过: {stats}")


def test_full_data_cache():
    """测试完整数据缓存 (DB 2)。"""
    from utils.full_data_cache_manager import FullDataCacheManager
    
    cache = FullDataCacheManager(ttl=300, max_size=100)
    
    # 测试基本操作
    cache.set("test_key", "test_value")
    assert cache.get("test_key") == "test_value"
    
    # 测试列表类型
    cache.set("list_key", [1, 2, 3, 4, 5])
    result = cache.get("list_key")
    assert isinstance(result, list)
    assert len(result) == 5
    
    # 测试统计
    stats = cache.get_stats()
    assert stats['cache_type'] == 'full_data'
    assert stats['redis_db'] == 2
    print(f"✓ 完整缓存测试通过: {stats}")


def test_dual_layer_cache_basic():
    """测试双层缓存管理器基本功能。"""
    from utils.dual_layer_cache_manager import DualLayerCacheManager
    
    cache = DualLayerCacheManager()
    
    # 写入数据
    cache.set("test_key", {"data": "value"})
    
    # 第一次读取（应该从完整缓存命中）
    value = cache.get("test_key")
    assert value == {"data": "value"}
    print("✓ 双层缓存基本读写测试通过")


def test_dual_layer_cache_promotion():
    """测试热点数据晋升机制。"""
    from utils.dual_layer_cache_manager import DualLayerCacheManager
    from config.constants import HOT_DATA_THRESHOLD
    
    cache = DualLayerCacheManager()
    
    # 写入数据
    test_key = "promotion_test_key"
    test_value = {"important": "data"}
    cache.set(test_key, test_value)
    
    # 多次访问以触发晋升
    for i in range(HOT_DATA_THRESHOLD + 5):
        value = cache.get(test_key)
        assert value == test_value
    
    # 检查访问计数
    assert test_key in cache.access_counter
    assert cache.access_counter[test_key] >= HOT_DATA_THRESHOLD
    
    print(f"✓ 热点晋升测试通过 (访问次数: {cache.access_counter[test_key]})")


def test_cache_delete():
    """测试缓存删除功能。"""
    from utils.dual_layer_cache_manager import DualLayerCacheManager
    
    cache = DualLayerCacheManager()
    
    # 设置并删除
    cache.set("delete_test", "value")
    assert cache.get("delete_test") is not None
    
    cache.delete("delete_test")
    assert cache.get("delete_test") is None
    
    print("✓ 缓存删除测试通过")


def test_cache_clear():
    """测试清空缓存功能。"""
    from utils.dual_layer_cache_manager import DualLayerCacheManager
    
    cache = DualLayerCacheManager()
    
    # 添加多个数据
    cache.set("key1", "value1")
    cache.set("key2", "value2")
    cache.set("key3", "value3")
    
    # 清空
    cache.clear()
    
    # 验证已清空
    assert cache.get("key1") is None
    assert cache.get("key2") is None
    assert cache.get("key3") is None
    
    print("✓ 缓存清空测试通过")


def test_cache_stats():
    """测试缓存统计功能。"""
    from utils.dual_layer_cache_manager import DualLayerCacheManager
    
    cache = DualLayerCacheManager()
    
    # 添加一些数据
    for i in range(5):
        cache.set(f"stat_key_{i}", f"value_{i}")
    
    # 获取统计
    stats = cache.get_stats()
    
    assert 'hot_cache' in stats
    assert 'full_cache' in stats
    assert 'tracked_keys' in stats
    assert stats['tracked_keys'] == 5
    
    print(f"✓ 缓存统计测试通过: {stats}")


def test_namespace_isolation():
    """测试命名空间隔离。"""
    from utils.hot_data_cache_manager import HotDataCacheManager
    from utils.full_data_cache_manager import FullDataCacheManager
    from config.constants import APP_ENVIRONMENT
    
    hot_cache = HotDataCacheManager()
    full_cache = FullDataCacheManager()
    
    # 验证命名空间包含环境标识
    assert APP_ENVIRONMENT in hot_cache.namespace
    assert APP_ENVIRONMENT in full_cache.namespace
    
    # 验证命名空间前缀不同
    assert hot_cache.namespace.startswith("hot:")
    assert full_cache.namespace.startswith("full:")
    
    print(f"✓ 命名空间隔离测试通过: hot={hot_cache.namespace}, full={full_cache.namespace}")


def test_ttl_expiration():
    """测试 TTL 过期功能。"""
    from utils.hot_data_cache_manager import HotDataCacheManager
    
    # 使用很短的 TTL 进行测试
    cache = HotDataCacheManager(ttl=2, max_size=10)
    
    # 设置数据
    cache.set("ttl_test", "value")
    assert cache.get("ttl_test") == "value"
    
    # 等待过期
    time.sleep(3)
    
    # 验证已过期
    assert cache.get("ttl_test") is None
    
    print("✓ TTL 过期测试通过")


def test_fallback_mechanism():
    """测试降级机制（需要模拟 Redis 不可用）。"""
    # 这个测试需要在 Redis 不可用的环境下运行
    # 这里只做基本的结构测试
    from utils.hot_data_cache_manager import HotDataCacheManager
    
    cache = HotDataCacheManager()
    
    # 正常情况下不应该使用降级缓存
    if not cache.use_fallback:
        print("✓ Redis 连接正常，未触发降级")
    else:
        print("✓ Redis 连接失败，已降级到内存缓存")


if __name__ == "__main__":
    print("\n" + "=" * 80)
    print("开始双层缓存测试")
    print("=" * 80)
    
    try:
        test_hot_data_cache()
        test_full_data_cache()
        test_dual_layer_cache_basic()
        test_dual_layer_cache_promotion()
        test_cache_delete()
        test_cache_clear()
        test_cache_stats()
        test_namespace_isolation()
        test_ttl_expiration()
        test_fallback_mechanism()
        
        print("\n" + "=" * 80)
        print("所有测试通过! ✓")
        print("=" * 80)
    except Exception as e:
        print(f"\n✗ 测试失败: {e}")
        import traceback
        traceback.print_exc()
