"""测试新增的缓存和限流管理工具。"""
import sys
import os

# 添加项目根目录到路径
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))


def test_cache_stats():
    """测试获取缓存统计。"""
    print("=" * 60)
    print("测试1: 获取缓存统计")
    print("=" * 60)
    
    try:
        from tools.fetch_webpage_tool import get_webpage_cache_stats
        
        # 先抓取一些页面填充缓存
        from tools.fetch_webpage_tool import fetch_webpage
        print("\n先抓取2个页面填充缓存...")
        fetch_webpage(url="https://example.com", mode="summary")
        
        # 获取统计
        result = get_webpage_cache_stats()
        print(f"\n{result}")
        
        # 验证输出包含关键字段
        assert "总缓存条目" in result
        assert "活跃条目" in result
        assert "缓存TTL" in result
        print("\n测试通过: 缓存统计功能正常")
        
    except Exception as e:
        print(f"\n测试失败: {e}")
        import traceback
        traceback.print_exc()


def test_clear_cache():
    """测试清除缓存。"""
    print("\n" + "=" * 60)
    print("测试2: 清除缓存")
    print("=" * 60)
    
    try:
        from tools.fetch_webpage_tool import clear_webpage_cache, webpage_cache
        
        # 先查看当前缓存数量
        before_count = len(webpage_cache.cache)
        print(f"\n清除前缓存条目数: {before_count}")
        
        # 清除指定URL的缓存
        result1 = clear_webpage_cache(url="https://example.com")
        print(f"\n清除指定URL缓存: {result1}")
        
        after_count_1 = len(webpage_cache.cache)
        print(f"清除后缓存条目数: {after_count_1}")
        
        # 清除所有缓存
        result2 = clear_webpage_cache()
        print(f"\n清除所有缓存: {result2}")
        
        after_count_2 = len(webpage_cache.cache)
        print(f"最终缓存条目数: {after_count_2}")
        
        assert after_count_2 == 0
        print("\n测试通过: 清除缓存功能正常")
        
    except Exception as e:
        print(f"\n测试失败: {e}")
        import traceback
        traceback.print_exc()


def test_rate_limit_reset():
    """测试重置限流记录。"""
    print("\n" + "=" * 60)
    print("测试3: 重置限流记录")
    print("=" * 60)
    
    try:
        from tools.fetch_webpage_tool import reset_webpage_rate_limit, url_rate_limiter, fetch_webpage
        
        # 先触发几次请求
        print("\n先触发3次请求...")
        for i in range(3):
            fetch_webpage(url="https://test-reset.com")
        
        # 查看限流状态
        stats_before = url_rate_limiter.get_stats()
        print(f"\n重置前活跃URL数: {stats_before['active_urls']}")
        
        # 重置指定URL
        result1 = reset_webpage_rate_limit(url="https://test-reset.com")
        print(f"\n重置指定URL: {result1}")
        
        stats_after_1 = url_rate_limiter.get_stats()
        print(f"重置后活跃URL数: {stats_after_1['active_urls']}")
        
        # 重置所有限流
        result2 = reset_webpage_rate_limit()
        print(f"\n重置所有限流: {result2}")
        
        stats_after_2 = url_rate_limiter.get_stats()
        print(f"最终活跃URL数: {stats_after_2['active_urls']}")
        
        assert stats_after_2['active_urls'] == 0
        print("\n测试通过: 重置限流功能正常")
        
    except Exception as e:
        print(f"\n测试失败: {e}")
        import traceback
        traceback.print_exc()


def test_rate_limit_stats():
    """测试获取限流统计。"""
    print("\n" + "=" * 60)
    print("测试4: 获取限流统计")
    print("=" * 60)
    
    try:
        from tools.fetch_webpage_tool import get_webpage_rate_limit_stats, fetch_webpage
        
        # 先触发几次请求
        print("\n先触发2次请求...")
        fetch_webpage(url="https://test-stats-1.com")
        fetch_webpage(url="https://test-stats-2.com")
        
        # 获取统计
        result = get_webpage_rate_limit_stats()
        print(f"\n{result}")
        
        # 验证输出包含关键字段
        assert "监控URL总数" in result
        assert "活跃URL数" in result
        assert "限流阈值" in result
        print("\n测试通过: 限流统计功能正常")
        
    except Exception as e:
        print(f"\n测试失败: {e}")
        import traceback
        traceback.print_exc()


def test_lru_eviction():
    """测试LRU淘汰策略。"""
    print("\n" + "=" * 60)
    print("测试5: LRU淘汰策略")
    print("=" * 60)
    
    try:
        from utils.cache_manager import CacheManager
        
        # 创建一个小容量的缓存(max_size=3)
        cache = CacheManager(ttl=600, max_size=3)
        
        print("\n添加3个缓存项(达到最大容量)...")
        cache.set("key1", "value1")
        cache.set("key2", "value2")
        cache.set("key3", "value3")
        
        print(f"当前缓存大小: {len(cache.cache)}")
        print(f"缓存键列表: {list(cache.cache.keys())}")
        
        # 访问key1,使其成为最近使用
        print("\n访问key1(标记为最近使用)...")
        cache.get("key1")
        
        # 添加第4个元素,应该淘汰key2(最久未使用)
        print("\n添加第4个元素(key4),触发LRU淘汰...")
        cache.set("key4", "value4")
        
        print(f"淘汰后缓存大小: {len(cache.cache)}")
        print(f"缓存键列表: {list(cache.cache.keys())}")
        
        # 验证key2被淘汰,key1仍然存在
        assert "key2" not in cache.cache, "key2应该被淘汰"
        assert "key1" in cache.cache, "key1应该保留(最近访问)"
        assert "key3" in cache.cache, "key3应该保留"
        assert "key4" in cache.cache, "key4应该存在"
        
        print("\n测试通过: LRU淘汰策略正常工作")
        
    except Exception as e:
        print(f"\n测试失败: {e}")
        import traceback
        traceback.print_exc()


def test_playwright_adapter_availability():
    """测试Playwright适配器可用性检查。"""
    print("\n" + "=" * 60)
    print("测试6: Playwright适配器可用性")
    print("=" * 60)
    
    try:
        from datasource.dynamic_page_adapter import PlaywrightAdapter, DynamicPageAdapterFactory
        
        # 检查Playwright是否可用
        adapter = PlaywrightAdapter()
        available = adapter.is_available()
        
        print(f"\nPlaywright是否可用: {available}")
        
        if available:
            print("Playwright已安装,可以使用动态页面抓取功能")
            
            # 测试工厂创建
            factory_adapter = DynamicPageAdapterFactory.create(preferred='playwright')
            if factory_adapter:
                print(f"工厂创建的适配器类型: {factory_adapter.get_type()}")
        else:
            print("警告: Playwright未安装")
            print("如需使用动态页面抓取,请执行:")
            print("  pip install playwright")
            print("  playwright install")
        
        print("\n测试通过: 适配器可用性检查正常")
        
    except Exception as e:
        print(f"\n测试失败: {e}")
        import traceback
        traceback.print_exc()


if __name__ == "__main__":
    print("\n" + "=" * 60)
    print("开始测试新增功能")
    print("=" * 60)
    
    test_cache_stats()
    test_clear_cache()
    test_rate_limit_reset()
    test_rate_limit_stats()
    test_lru_eviction()
    test_playwright_adapter_availability()
    
    print("\n" + "=" * 60)
    print("所有测试完成!")
    print("=" * 60)
