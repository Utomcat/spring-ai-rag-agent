"""测试优化后的fetch_webpage工具。

验证缓存、速率限制等功能。
"""
import sys
import os
import time

# 添加项目根目录到路径
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from tools.fetch_webpage_tool import fetch_webpage, webpage_cache, url_rate_limiter


def test_rate_limiting():
    """测试速率限制功能。"""
    print('=' * 80)
    print('测试1: URL速率限制')
    print('=' * 80)
    
    url = 'https://example.com'
    
    # 快速连续请求同一个URL
    for i in range(7):
        print(f'\n第{i+1}次请求...')
        result = fetch_webpage(url=url, mode='summary', max_length=100)
        
        # 检查是否被限流
        if '访问频率受限' in result or '错误' in result[:10]:
            print(f'结果: {result[:200]}')
            print('速率限制生效!')
            break
        else:
            print(f'结果长度: {len(result)} 字符')
    
    # 显示限流统计
    stats = url_rate_limiter.get_stats()
    print(f'\n限流器统计:')
    print(f'  - 总URL数: {stats["total_urls"]}')
    print(f'  - 活跃URL数: {stats["active_urls"]}')
    if stats['urls_near_limit']:
        print(f'  - 接近限制的URL:')
        for item in stats['urls_near_limit']:
            print(f'    * {item["url"]}: {item["requests"]}/{item["limit"]} (剩余{item["remaining"]})')


def test_caching():
    """测试缓存功能。"""
    print('\n' + '=' * 80)
    print('测试2: 内容缓存')
    print('=' * 80)
    
    url = 'https://zh.wikipedia.org/wiki/Python'
    
    # 第一次请求(应该从网络获取)
    print('\n第一次请求(从网络获取)...')
    start_time = time.time()
    result1 = fetch_webpage(url=url, mode='summary', max_length=500)
    elapsed1 = time.time() - start_time
    print(f'耗时: {elapsed1:.2f}秒')
    print(f'结果长度: {len(result1)} 字符')
    
    # 第二次请求(应该从缓存获取)
    print('\n第二次请求(从缓存获取)...')
    start_time = time.time()
    result2 = fetch_webpage(url=url, mode='summary', max_length=500)
    elapsed2 = time.time() - start_time
    print(f'耗时: {elapsed2:.2f}秒')
    print(f'结果长度: {len(result2)} 字符')
    
    # 检查是否有缓存标记
    if '[注: 此结果来自缓存]' in result2:
        print('缓存命中!')
        print(f'性能提升: {(elapsed1 - elapsed2) / elapsed1 * 100:.1f}%')
    else:
        print('未检测到缓存标记')
    
    # 显示缓存统计
    cache_stats = webpage_cache.get_stats()
    print(f'\n缓存统计:')
    print(f'  - 总条目数: {cache_stats["total_items"]}')
    print(f'  - 活跃条目数: {cache_stats["active_items"]}')
    print(f'  - TTL: {cache_stats["ttl_seconds"]}秒')


def test_different_modes():
    """测试不同提取模式生成不同的缓存键。"""
    print('\n' + '=' * 80)
    print('测试3: 不同模式使用独立缓存')
    print('=' * 80)
    
    url = 'https://example.com'
    
    # 使用不同模式请求
    print('\n使用summary模式...')
    result_summary = fetch_webpage(url=url, mode='summary', max_length=200)
    
    print('使用full模式...')
    result_full = fetch_webpage(url=url, mode='full', max_length=200)
    
    # 检查缓存统计
    cache_stats = webpage_cache.get_stats()
    print(f'\n缓存中应该有2个不同模式的条目')
    print(f'当前缓存活跃条目数: {cache_stats["active_items"]}')
    
    if cache_stats['active_items'] >= 2:
        print('不同模式使用独立缓存!')
    else:
        print('缓存条目数不符合预期')


def test_invalid_url_handling():
    """测试无效URL处理。"""
    print('\n' + '=' * 80)
    print('测试4: 无效URL处理')
    print('=' * 80)
    
    # 空URL
    print('\n测试空URL...')
    result = fetch_webpage(url='')
    print(f'结果: {result}')
    
    # 无效格式
    print('\n测试无效URL格式...')
    result = fetch_webpage(url='not-a-url')
    print(f'结果: {result}')
    
    print('无效URL处理正常')


if __name__ == '__main__':
    print('开始测试优化后的fetch_webpage工具\n')
    
    try:
        test_invalid_url_handling()
        test_rate_limiting()
        test_caching()
        test_different_modes()
        
        print('\n' + '=' * 80)
        print('所有测试完成!')
        print('=' * 80)
        
    except Exception as e:
        print(f'\n测试失败: {e}')
        import traceback
        traceback.print_exc()
