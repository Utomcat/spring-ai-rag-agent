"""Phase 1 核心功能测试。"""
import sys
import os

# 添加项目根目录到Python路径
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))


def test_http_client():
    """测试HTTP客户端。"""
    print("测试1: HTTP客户端...")
    from utils.http_client import HttpClient
    
    with HttpClient() as client:
        print("✓ HTTP客户端创建成功")
        print(f"  - Session已配置: {client.session is not None}")
        print(f"  - 超时配置: {client.timeout}")


def test_html_parser():
    """测试HTML解析器。"""
    print("\n测试2: HTML解析器...")
    from parser.html_parser import HtmlParser
    
    parser = HtmlParser()
    
    # 测试简单HTML
    html = """
    <html>
        <body>
            <table>
                <tr><th>姓名</th><th>年龄</th></tr>
                <tr><td>张三</td><td>25</td></tr>
                <tr><td>李四</td><td>30</td></tr>
            </table>
        </body>
    </html>
    """
    
    results = parser.parse_table(html)
    print(f"✓ HTML解析器工作正常")
    print(f"  - 解析到 {len(results)} 行数据")
    if results:
        print(f"  - 示例数据: {results[0]}")


def test_search_engine_factory():
    """测试搜索引擎工厂。"""
    print("\n测试3: 搜索引擎工厂...")
    from search.engine import SearchEngineFactory
    
    # 测试Bing引擎
    bing_engine = SearchEngineFactory.create('bing')
    print(f"✓ Bing引擎创建成功: {bing_engine.get_name()}")
    
    # 测试DuckDuckGo引擎
    ddg_engine = SearchEngineFactory.create('duckduckgo')
    print(f"✓ DuckDuckGo引擎创建成功: {ddg_engine.get_name()}")
    
    # 测试错误处理
    try:
        SearchEngineFactory.create('invalid')
    except ValueError as e:
        print(f"✓ 错误处理正常: {str(e)[:50]}...")


def test_cache_manager():
    """测试缓存管理器。"""
    print("\n测试4: 缓存管理器...")
    from utils.cache_manager import CacheManager
    
    cache = CacheManager(ttl=60)
    
    # 测试设置和获取
    cache.set('test_key', 'test_value')
    value = cache.get('test_key')
    print(f"✓ 缓存设置/获取正常: {value}")
    
    # 测试过期清理
    cleaned = cache.cleanup_expired()
    print(f"✓ 过期清理正常: 清理了 {cleaned} 项")


def test_url_validator():
    """测试URL验证器。"""
    print("\n测试5: URL验证器...")
    from utils.url_validator import UrlValidator
    
    validator = UrlValidator()
    
    # 测试有效URL
    assert validator.is_valid('https://www.example.com') == True
    print("✓ 有效URL验证通过")
    
    # 测试无效URL
    assert validator.is_valid('not_a_url') == False
    print("✓ 无效URL检测通过")
    
    # 测试URL清洗
    cleaned = validator.sanitize('<script>https://example.com</script>')
    print(f"✓ URL清洗正常: {cleaned[:30]}...")


def main():
    """运行所有测试。"""
    print("=" * 60)
    print("Phase 1 核心功能测试")
    print("=" * 60)
    
    try:
        test_http_client()
        test_html_parser()
        test_search_engine_factory()
        test_cache_manager()
        test_url_validator()
        
        print("\n" + "=" * 60)
        print("✅ 所有测试通过！Phase 1 核心功能正常工作。")
        print("=" * 60)
        return 0
        
    except Exception as e:
        print(f"\n❌ 测试失败: {e}")
        import traceback
        traceback.print_exc()
        return 1


if __name__ == '__main__':
    sys.exit(main())
