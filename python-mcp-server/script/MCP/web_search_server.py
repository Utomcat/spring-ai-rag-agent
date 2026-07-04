"""Web Search MCP Server Module.

提供基于 MCP 协议的网络搜索工具，支持多种搜索引擎。
"""
import logging
import time
from typing import TypedDict
from urllib.parse import quote_plus, urlparse, parse_qs

import requests
from bs4 import BeautifulSoup

from config.constants import (
    MAX_RESULTS_LIMIT,
    REQUEST_TIMEOUT,
    MAX_RETRIES,
    RETRY_BACKOFF_FACTOR, READ_TIMEOUT,
)
from server import mcp
from utils.http_client import create_http_session


# 搜索结果数据结构
class SearchResult(TypedDict):
    """搜索结果数据结构"""
    title: str
    url: str
    snippet: str

# 搜索引擎映射表（直接存储函数引用，避免 globals() 安全风险）
def search_bing(query: str, max_results: int, session: requests.Session) -> list[SearchResult]:
    """使用 Bing 搜索。"""
    return _search_engine(
        name='Bing',
        query=query,
        max_results=max_results,
        session=session,
        base_url='https://www.bing.com/search',
        selectors={
            'primary': {'item': 'li.b_algo', 'title': 'h2 a', 'snippet': 'p'},
            'fallback': {'item': 'li.b_algo', 'title': 'a', 'snippet': '.b_snippet'}
        }
    )

def search_duckduckgo(query: str, max_results: int, session: requests.Session) -> list[SearchResult]:
    """使用 DuckDuckGo 搜索。"""
    return _search_engine(
        name='DuckDuckGo',
        query=query,
        max_results=max_results,
        session=session,
        base_url='https://html.duckduckgo.com/html/',
        selectors={
            'primary': {'item': '.result', 'title': '.result__a', 'snippet': '.result__snippet'},
            'fallback': {'item': '.web-result', 'title': '.result__a', 'snippet': '.result__snippet'}
        },
        needs_url_cleanup=True
    )

SEARCH_ENGINES = {
    'bing': search_bing,
    'duckduckgo': search_duckduckgo,
}


# 配置日志
logger = logging.getLogger(__name__)


def _clean_duckduckgo_url(url: str) -> str:
    """清理 DuckDuckGo 重定向 URL。
    
    Args:
        url: 原始 URL（可能是 /l/?uddg=xxx 格式）
    
    Returns:
        str: 清理后的真实 URL
    """
    if not url:
        return url
    
    # DuckDuckGo 的重定向链接格式: /l/?uddg=https%3A%2F%2Fexample.com
    if '/l/' in url and 'uddg=' in url:
        try:
            parsed = urlparse(url)
            params = parse_qs(parsed.query)
            if 'uddg' in params:
                return params['uddg'][0]
        except Exception as e:
            logger.warning(f'URL 清理失败: {url}, 错误: {e}')
    
    return url


def _fetch_with_retry(session: requests.Session, url: str, max_retries: int = MAX_RETRIES) -> requests.Response:
    """带重试机制的 HTTP 请求。
    
    Args:
        session: HTTP Session 对象
        url: 请求 URL
        max_retries: 最大重试次数
    
    Returns:
        requests.Response: HTTP 响应对象
    
    Raises:
        requests.exceptions.RequestException: 所有重试都失败后抛出
    """
    last_exception = None
    
    for attempt in range(max_retries + 1):
        try:
            if attempt > 0:
                wait_time = RETRY_BACKOFF_FACTOR ** attempt
                logger.info(f'第 {attempt} 次重试，等待 {wait_time:.1f} 秒...')
                time.sleep(wait_time)
            
            response = session.get(url, timeout=REQUEST_TIMEOUT, allow_redirects=True)
            response.raise_for_status()
            return response
        
        except requests.exceptions.Timeout as e:
            last_exception = e
            logger.warning(f'请求超时 (尝试 {attempt + 1}/{max_retries + 1}): {url}')
        except requests.exceptions.RequestException as e:
            last_exception = e
            logger.warning(f'请求失败 (尝试 {attempt + 1}/{max_retries + 1}): {url}, 错误: {e}')
    
    raise last_exception


def _search_engine(
    name: str,
    query: str,
    max_results: int,
    session: requests.Session,
    base_url: str,
    selectors: dict,
    needs_url_cleanup: bool = False
) -> list[SearchResult]:
    """通用搜索引擎实现。
    
    Args:
        name: 搜索引擎名称（用于日志）
        query: 搜索关键词
        max_results: 最大结果数
        session: HTTP Session 对象
        base_url: 搜索基础 URL
        selectors: CSS 选择器配置 {'primary': {...}, 'fallback': {...}}
        needs_url_cleanup: 是否需要清理 URL
    
    Returns:
        list[SearchResult]: 搜索结果列表
    """
    logger.debug(f'开始 {name} 搜索: query="{query}", max_results={max_results}')
    start_time = time.time()
    
    search_url = f'{base_url}?q={quote_plus(query)}'
    results = []
    
    try:
        # 带重试的请求
        response = _fetch_with_retry(session, search_url)
        soup = BeautifulSoup(response.text, 'lxml')
        
        # 尝试主选择器，失败则使用备选选择器
        selector_config = selectors.get('primary', {})
        items = soup.select(selector_config.get('item', ''))
        
        # 如果主选择器没有找到结果，尝试备选选择器
        if not items and 'fallback' in selectors:
            logger.debug(f'{name} 主选择器未找到结果，尝试备选选择器')
            fallback_config = selectors['fallback']
            items = soup.select(fallback_config.get('item', ''))
            selector_config = fallback_config
        
        for item in items[:max_results]:
            title_elem = item.select_one(selector_config.get('title', ''))
            snippet_elem = item.select_one(selector_config.get('snippet', ''))
            
            if title_elem:
                title = title_elem.get_text(strip=True)
                url = title_elem.get('href', '')
                
                # 清理 URL（如 DuckDuckGo 的重定向链接）
                if needs_url_cleanup:
                    url = _clean_duckduckgo_url(str(url))
                
                snippet = snippet_elem.get_text(strip=True) if snippet_elem else '无摘要'
                
                # 过滤无效结果
                if title and url:
                    results.append({
                        'title': title,
                        'url': url,
                        'snippet': snippet
                    })
        
        elapsed_time = time.time() - start_time
        logger.debug(f'{name} 搜索完成，获取到 {len(results)} 条结果，耗时 {elapsed_time:.2f}秒')
        return results
    
    except Exception as e:
        elapsed_time = time.time() - start_time
        logger.error(f'{name} 搜索出错，耗时 {elapsed_time:.2f}秒: {str(e)}', exc_info=True)
        raise


@mcp.tool()
def web_search(query: str, max_results: int = 5, engine: str = 'duckduckgo') -> str:
    """通过网络搜索引擎搜索最新的信息。

    Args:
        query: 搜索关键词或问题
        max_results: 搜索结果数量，默认 5，最大 20
        engine: 搜索引擎（'bing' 或 'duckduckgo'），默认 'duckduckgo'

    Returns:
        str: 格式化的搜索结果
    """
    # 输入验证
    if not query or not query.strip():
        logger.warning('搜索关键词为空')
        return '错误：搜索关键词不能为空'
    
    if max_results < 1:
        logger.warning(f'无效的 max_results: {max_results}，使用默认值 5')
        max_results = 5
    elif max_results > MAX_RESULTS_LIMIT:
        logger.warning(f'max_results {max_results} 超过限制，调整为 {MAX_RESULTS_LIMIT}')
        max_results = MAX_RESULTS_LIMIT
    
    engine = engine.lower().strip()
    if engine not in SEARCH_ENGINES:
        logger.warning(f'不支持的搜索引擎: {engine}，使用默认引擎 duckduckgo')
        engine = 'duckduckgo'
    
    logger.info(f'执行搜索: query="{query}", max_results={max_results}, engine={engine}')
    
    # 创建 HTTP Session（自动复用连接）
    session = create_http_session()
    
    try:
        # 直接调用函数引用，避免 globals() 安全风险
        search_func = SEARCH_ENGINES[engine]
        results = search_func(query, max_results, session)

        if not results:
            logger.info('未搜索到相关结果')
            return '未搜索到相关结果。'

        # 格式化输出
        output = [f'搜索结果（共 {len(results)} 条）：\n']
        for i, result in enumerate(results, 1):
            output.append(f'{i}. **{result["title"]}**')
            output.append(f'   - 摘要：{result["snippet"]}')
            output.append(f'   - 链接：{result["url"]}')
            output.append('')

        result_text = '\n'.join(output)
        logger.info(f'搜索成功，返回 {len(results)} 条结果')
        return result_text

    except requests.exceptions.Timeout:
        error_msg = f'网络搜索超时（{READ_TIMEOUT}秒，已重试 {MAX_RETRIES} 次），请稍后重试'
        logger.error(error_msg)
        return error_msg
    except requests.exceptions.RequestException as e:
        error_msg = f'网络请求失败：{str(e)}'
        logger.error(error_msg)
        return error_msg
    except Exception as e:
        error_msg = f'网络搜索失败：{str(e)}'
        logger.error(error_msg, exc_info=True)
        return error_msg
    finally:
        # 确保 Session 被关闭，释放资源
        session.close()

