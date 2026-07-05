"""网页抓取工具测试。

测试fetch_webpage工具的各项功能。
"""
import sys
import os

# 添加项目根目录到路径
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from tools.fetch_webpage_tool import fetch_webpage


def test_summary_mode():
    """测试智能摘要模式。"""
    print('=' * 80)
    print('测试1: 智能摘要模式')
    print('=' * 80)
    
    # 测试维基百科页面
    url = 'https://zh.wikipedia.org/wiki/人工智能'
    result = fetch_webpage(url=url, mode='summary', max_length=5000)
    
    print(result)
    print('\n')


def test_full_mode():
    """测试全文模式。"""
    print('=' * 80)
    print('测试2: 全文模式')
    print('=' * 80)
    
    url = 'https://example.com'
    result = fetch_webpage(url=url, mode='full', max_length=3000)
    
    print(result)
    print('\n')


def test_structured_mode():
    """测试结构化模式。"""
    print('=' * 80)
    print('测试3: 结构化模式(提取表格)')
    print('=' * 80)
    
    # 使用包含表格的页面
    url = 'https://en.wikipedia.org/wiki/List_of_countries_by_GDP_(nominal)'
    result = fetch_webpage(
        url=url, 
        mode='structured', 
        extract_tables=True,
        extract_links=False
    )
    
    print(result)
    print('\n')


def test_with_links_and_images():
    """测试提取链接和图片。"""
    print('=' * 80)
    print('测试4: 提取链接和图片')
    print('=' * 80)
    
    url = 'https://zh.wikipedia.org/wiki/机器学习'
    result = fetch_webpage(
        url=url, 
        mode='summary',
        max_length=3000,
        extract_links=True,
        extract_images=True
    )
    
    print(result)
    print('\n')


def test_invalid_url():
    """测试无效URL。"""
    print('=' * 80)
    print('测试5: 无效URL处理')
    print('=' * 80)
    
    result = fetch_webpage(url='not-a-valid-url')
    print(result)
    print('\n')


def test_empty_url():
    """测试空URL。"""
    print('=' * 80)
    print('测试6: 空URL处理')
    print('=' * 80)
    
    result = fetch_webpage(url='')
    print(result)
    print('\n')


if __name__ == '__main__':
    print('开始测试 fetch_webpage 工具\n')
    
    try:
        test_empty_url()
        test_invalid_url()
        test_summary_mode()
        test_full_mode()
        test_structured_mode()
        test_with_links_and_images()
        
        print('\n所有测试完成!')
    except Exception as e:
        print(f'测试失败: {e}')
        import traceback
        traceback.print_exc()
