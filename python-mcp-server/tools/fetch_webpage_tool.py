"""网页内容抓取MCP工具。

提供fetch_webpage工具供LLM调用获取网页的详细内容。
支持缓存、速率限制和动态页面扩展。
"""
import logging
import hashlib
from typing import Optional

from server.mcp_server import mcp
from utils.http_client import HttpClient
from utils.content_cleaner import ContentCleaner
from utils.url_validator import UrlValidator
from utils.cache_manager import CacheManager
from utils.url_rate_limiter import UrlRateLimiter
from config.constants import (
    CONNECT_TIMEOUT, 
    READ_TIMEOUT,
    CACHE_TTL_WEBPAGE,
    WEBPAGE_CACHE_MAX_SIZE
)

# 配置日志
logger = logging.getLogger(__name__)

# HTTP客户端实例
http_client = HttpClient(timeout=(CONNECT_TIMEOUT, READ_TIMEOUT))

# 网页内容缓存管理器
webpage_cache = CacheManager(ttl=CACHE_TTL_WEBPAGE, max_size=WEBPAGE_CACHE_MAX_SIZE)

# URL级别速率限制器
url_rate_limiter = UrlRateLimiter()


@mcp.tool()
def fetch_webpage(
    url: str, 
    mode: str = 'summary',
    max_length: int = 10000,
    extract_tables: bool = True,
    extract_links: bool = False,
    extract_images: bool = False
) -> str | None:
    """
    访问指定URL并提取网页内容。

    Args:
        url: 要抓取的网页URL地址
        mode: 提取模式
            - 'summary': 智能摘要模式(推荐),自动识别主要内容区域
            - 'full': 全文模式,提取所有文本内容
            - 'structured': 结构化模式,重点提取表格和列表
        max_length: 最大文本长度限制(默认10000字符)
        extract_tables: 是否提取表格数据(默认True)
        extract_links: 是否提取页面链接(默认False)
        extract_images: 是否提取图片信息(默认False)

    Returns:
        格式化的网页内容字符串

    Examples:
        # 智能摘要模式(推荐用于文章阅读)
        fetch_webpage(url="https://example.com/article")
        
        # 全文模式(需要完整内容)
        fetch_webpage(url="https://example.com/page", mode="full")
        
        # 结构化模式(提取表格数据)
        fetch_webpage(url="https://example.com/data", mode="structured", extract_tables=True)
    """
    # ==================== 参数验证 ====================
    if not url or not url.strip():
        logger.warning('URL为空')
        return '错误: URL不能为空'

    url = url.strip()

    # 验证URL格式
    if not UrlValidator.is_valid(url):
        logger.warning(f'无效的URL格式: {url}')
        return f'错误: 无效的URL格式 "{url}"\n请确保URL以 http:// 或 https:// 开头'

    # 验证mode参数
    valid_modes = ['summary', 'full', 'structured']
    if mode not in valid_modes:
        logger.warning(f'不支持的提取模式: {mode}, 使用默认模式 summary')
        mode = 'summary'

    logger.info(f'开始抓取网页: url={url}, mode={mode}')

    # ==================== 速率限制检查 ====================
    allowed, rate_msg = url_rate_limiter.can_access(url)
    if not allowed:
        logger.warning(f'URL访问被限流: {url}')
        return f'错误: {rate_msg}'

    # ==================== 缓存检查 ====================
    cache_key = _generate_cache_key(url, mode, max_length, extract_tables, extract_links, extract_images)
    cached_result = webpage_cache.get(cache_key)
    if cached_result is not None:
        logger.info(f'缓存命中: {url}')
        # 类型断言：确保返回值为字符串
        result_str: str = str(cached_result) + '\n\n[注: 此结果来自缓存]'
        return result_str

    # ==================== 发送HTTP请求 ====================
    try:
        response = http_client.get_with_retry(url)
        
        if response is None:
            return f'错误: 无法访问URL "{url}"\n可能原因: 网站不存在、网络超时或被拒绝访问'

        # 检查HTTP状态码
        if response.status_code != 200:
            return f'错误: HTTP {response.status_code}\nURL: {url}'

        html_content = response.text
        
        # 检查内容大小
        content_size = len(html_content)
        logger.info(f'网页内容大小: {content_size} 字节')

    except Exception as e:
        logger.error(f'HTTP请求失败: {e}', exc_info=True)
        return f'❌ 错误: 请求失败 - {str(e)}'

    # ==================== 解析和提取内容 ====================
    try:
        if mode == 'summary':
            # 智能摘要模式
            result = ContentCleaner.extract_main_content(html_content, max_length=max_length)
            
            if not result.get('content'):
                return f'警告: 未能从网页提取到主要内容\nURL: {url}'

            # 构建输出
            output_lines = [
                f'网页内容摘要',
                f'',
                f'URL: {url}',
                f'',
            ]

            # 标题
            if result.get('title'):
                output_lines.append(f'## {result["title"]}')
                output_lines.append('')

            # 元数据
            metadata = result.get('metadata', {})
            if metadata:
                output_lines.append('元数据:')
                if 'description' in metadata:
                    output_lines.append(f'  - 描述: {metadata["description"][:200]}')
                if 'author' in metadata:
                    output_lines.append(f'  - 作者: {metadata["author"]}')
                if 'published_time' in metadata:
                    output_lines.append(f'  - 发布时间: {metadata["published_time"]}')
                output_lines.append('')

            # 正文内容
            content = result.get('content', '')
            if content:
                output_lines.append('主要内容:')
                output_lines.append(content)
                output_lines.append('')

            # 词数统计
            word_count = result.get('word_count', 0)
            output_lines.append(f'统计: 约 {word_count} 个单词')

            # 链接(可选)
            if extract_links and result.get('links'):
                links = result['links']
                output_lines.append('')
                output_lines.append(f'页面链接 (共{len(links)}个):')
                for i, link in enumerate(links[:15], 1):  # 最多显示15个
                    output_lines.append(f'  {i}. [{link["text"]}]({link["url"]})')

            # 图片(可选)
            if extract_images and result.get('images'):
                images = result['images']
                output_lines.append('')
                output_lines.append(f'页面图片 (共{len(images)}张):')
                for i, img in enumerate(images[:10], 1):  # 最多显示10张
                    alt_text = img['alt'] if img['alt'] != '无描述' else '无描述'
                    output_lines.append(f'  {i}. {alt_text}: {img["src"]}')

            result_text = '\n'.join(output_lines)
            
            # 保存到缓存
            _save_to_cache(cache_key, result_text)
            
            return result_text

        elif mode == 'full':
            # 全文模式
            from bs4 import BeautifulSoup
            
            soup = BeautifulSoup(html_content, 'lxml')
            
            # 移除脚本和样式
            for tag in soup(['script', 'style', 'nav', 'header', 'footer']):
                tag.decompose()
            
            # 提取纯文本
            text = soup.get_text(separator='\n', strip=True)
            
            # 清理空白
            import re
            text = re.sub(r'\n{3,}', '\n\n', text)
            
            # 限制长度
            if len(text) > max_length:
                text = text[:max_length] + '\n...(内容已截断)'
            
            title_tag = soup.find('title')
            title = title_tag.get_text(strip=True) if title_tag else '无标题'

            output_lines = [
                f'网页全文内容',
                f'',
                f'URL: {url}',
                f'',
                f'## {title}',
                f'',
                text,
                f'',
                f'统计: 总长度 {len(text)} 字符'
            ]

            result_text = '\n'.join(output_lines)
            
            # 保存到缓存
            _save_to_cache(cache_key, result_text)
            
            return result_text

        elif mode == 'structured':
            # 结构化模式
            from bs4 import BeautifulSoup
            import pandas as pd
            
            soup = BeautifulSoup(html_content, 'lxml')
            
            output_lines = [
                f'网页结构化数据',
                f'',
                f'URL: {url}',
                f'',
            ]

            # 提取标题
            title_tag = soup.find('title')
            title = title_tag.get_text(strip=True) if title_tag else '无标题'
            output_lines.append(f'## {title}')
            output_lines.append('')

            # 提取表格
            if extract_tables:
                tables = soup.find_all('table')
                if tables:
                    output_lines.append(f'发现 {len(tables)} 个表格:')
                    output_lines.append('')
                    
                    for i, table in enumerate(tables[:3], 1):  # 最多处理3个表格
                        try:
                            rows = []
                            headers = []
                            
                            # 提取表头
                            header_row = table.find('thead')
                            if header_row:
                                headers = [th.get_text(strip=True) for th in header_row.find_all(['th', 'td'])]
                            
                            # 提取数据行
                            tbody = table.find('tbody') or table
                            for tr in tbody.find_all('tr'):
                                cells = [td.get_text(strip=True) for td in tr.find_all(['td', 'th'])]
                                if cells:
                                    if headers and len(cells) == len(headers):
                                        rows.append(dict(zip(headers, cells)))
                                    else:
                                        rows.append(cells)
                            
                            if rows:
                                output_lines.append(f'### 表格 {i}')
                                
                                if isinstance(rows[0], dict):
                                    # 转换为DataFrame显示
                                    df = pd.DataFrame(rows)
                                    output_lines.append(df.to_string(index=False))
                                else:
                                    # 纯列表格式
                                    for row in rows[:10]:  # 最多显示10行
                                        output_lines.append('  | '.join(str(cell) for cell in row))
                                
                                output_lines.append('')
                        except Exception as e:
                            logger.warning(f'表格{i}解析失败: {e}')
                            continue
                    
                    if len(tables) > 3:
                        output_lines.append(f'... 还有 {len(tables) - 3} 个表格未显示')
                        output_lines.append('')
                else:
                    output_lines.append('未检测到表格数据')
                    output_lines.append('')

            # 提取列表
            lists = soup.find_all(['ul', 'ol'])
            if lists:
                output_lines.append(f'发现 {len(lists)} 个列表:')
                output_lines.append('')
                
                for i, lst in enumerate(lists[:5], 1):  # 最多显示5个列表
                    items = lst.find_all('li')
                    if items:
                        output_lines.append(f'### 列表 {i}')
                        for j, item in enumerate(items[:10], 1):  # 每个列表最多10项
                            output_lines.append(f'  {j}. {item.get_text(strip=True)}')
                        output_lines.append('')
                
                if len(lists) > 5:
                    output_lines.append(f'... 还有 {len(lists) - 5} 个列表未显示')
                    output_lines.append('')

            result_text = '\n'.join(output_lines)
            
            # 保存到缓存
            _save_to_cache(cache_key, result_text)
            
            return result_text

    except Exception as e:
        logger.error(f'内容提取失败: {e}', exc_info=True)
        return f'错误: 内容提取失败 - {str(e)}'


@mcp.tool()
def get_webpage_cache_stats() -> str:
    """
    获取网页缓存统计信息。

    Returns:
        格式化的缓存统计信息字符串
    """
    try:
        stats = webpage_cache.get_stats()
        
        output_lines = [
            '网页缓存统计',
            '',
            f'总缓存条目: {stats["total_items"]}',
            f'活跃条目: {stats["active_items"]}',
            f'过期条目: {stats["expired_items"]}',
            f'缓存TTL: {stats["ttl_seconds"]}秒',
        ]
        
        if stats.get('max_size'):
            output_lines.append(f'最大容量: {stats["max_size"]}')
            usage_percent = (stats['active_items'] / stats['max_size']) * 100 if stats['max_size'] > 0 else 0
            output_lines.append(f'使用率: {usage_percent:.1f}%')
        
        return '\n'.join(output_lines)
    except Exception as e:
        logger.error(f'获取缓存统计失败: {e}', exc_info=True)
        return f'错误: 获取缓存统计失败 - {str(e)}'


@mcp.tool()
def clear_webpage_cache(url: Optional[str] = None) -> str:
    """
    清除网页缓存。

    Args:
        url: 可选,指定要清除的URL。如果不提供则清除所有缓存

    Returns:
        操作结果信息
    """
    try:
        if url:
            # 清除指定URL的所有缓存变体
            cleared_count = 0
            keys_to_delete = []
            
            for key in webpage_cache.cache.keys():
                if key.startswith('webpage:') and url in key:
                    keys_to_delete.append(key)
            
            for key in keys_to_delete:
                webpage_cache.delete(key)
                cleared_count += 1
            
            return f'已清除 {cleared_count} 个与 "{url}" 相关的缓存条目'
        else:
            # 清除所有缓存
            before_count = len(webpage_cache.cache)
            webpage_cache.clear()
            return f'已清除所有网页缓存(共{before_count}个条目)'
    except Exception as e:
        logger.error(f'清除缓存失败: {e}', exc_info=True)
        return f'错误: 清除缓存失败 - {str(e)}'


@mcp.tool()
def reset_webpage_rate_limit(url: Optional[str] = None) -> str:
    """
    重置网页访问限流记录。

    Args:
        url: 可选,指定要重置的URL。如果不提供则重置所有URL的限流记录

    Returns:
        操作结果信息
    """
    try:
        if url:
            url_rate_limiter.reset_url(url)
            return f'已重置 "{url}" 的访问限流记录'
        else:
            url_rate_limiter.reset_all()
            return '已重置所有URL的访问限流记录'
    except Exception as e:
        logger.error(f'重置限流记录失败: {e}', exc_info=True)
        return f'错误: 重置限流记录失败 - {str(e)}'


@mcp.tool()
def get_webpage_rate_limit_stats() -> str:
    """
    获取网页访问限流统计信息。

    Returns:
        格式化的限流统计信息字符串
    """
    try:
        stats = url_rate_limiter.get_stats()
        
        output_lines = [
            '网页访问限流统计',
            '',
            f'监控URL总数: {stats["total_urls"]}',
            f'活跃URL数: {stats["active_urls"]}',
            f'限流阈值: {url_rate_limiter.max_requests}次/{url_rate_limiter.window_seconds}秒',
            '',
        ]
        
        if stats.get('urls_near_limit'):
            output_lines.append('接近限流的URL:')
            for item in stats['urls_near_limit']:
                remaining = item['remaining']
                status = '⚠️ 警告' if remaining <= 1 else '正常'
                output_lines.append(
                    f'  - {item["url"]}: '
                    f'{item["requests"]}/{item["limit"]} '
                    f'(剩余{remaining}次) [{status}]'
                )
        else:
            output_lines.append('当前没有URL接近限流阈值')
        
        return '\n'.join(output_lines)
    except Exception as e:
        logger.error(f'获取限流统计失败: {e}', exc_info=True)
        return f'错误: 获取限流统计失败 - {str(e)}'


def _generate_cache_key(
    url: str,
    mode: str,
    max_length: int,
    extract_tables: bool,
    extract_links: bool,
    extract_images: bool
) -> str:
    """
    生成缓存键。

    Args:
        url: URL地址
        mode: 提取模式
        max_length: 最大长度
        extract_tables: 是否提取表格
        extract_links: 是否提取链接
        extract_images: 是否提取图片

    Returns:
        缓存键字符串
    """
    # 使用URL和参数组合生成唯一标识
    key_parts = [
        url,
        mode,
        str(max_length),
        str(extract_tables),
        str(extract_links),
        str(extract_images)
    ]
    key_string = '|'.join(key_parts)
    
    # 使用MD5生成固定长度的缓存键
    cache_key = hashlib.md5(key_string.encode('utf-8')).hexdigest()
    
    return f'webpage:{cache_key}'


def _save_to_cache(cache_key: str, content: str):
    """
    保存结果到缓存。

    Args:
        cache_key: 缓存键
        content: 要缓存的内容
    """
    try:
        webpage_cache.set(cache_key, content)
        logger.debug(f'结果已缓存: {cache_key[:16]}...')
    except Exception as e:
        logger.warning(f'缓存保存失败: {e}')
