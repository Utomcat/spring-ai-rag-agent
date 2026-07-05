"""网络搜索MCP工具。

提供web_search工具供LLM调用进行网络搜索。
"""
import logging
from typing import List

from server.mcp_server import mcp
from search.engine import SearchEngineFactory
from config.constants import MAX_RESULTS_LIMIT, MIN_RESULTS
from utils.cache_manager import CacheManager

# 配置日志
logger = logging.getLogger(__name__)

# 全局缓存管理器
cache_manager = CacheManager()


@mcp.tool()
def web_search(query: str, max_results: int = 5, engine: str = 'duckduckgo') -> str:
    """
    通过网络搜索引擎搜索最新的信息。

    Args:
        query: 搜索关键词或问题
        max_results: 搜索结果数量（1-20），默认5
        engine: 搜索引擎（'bing' 或 'duckduckgo'），默认'duckduckgo'

    Returns:
        格式化的搜索结果字符串
    """
    # ==================== 参数验证 ====================
    if not query or not query.strip():
        logger.warning('搜索关键词为空')
        return '错误：搜索关键词不能为空'

    query = query.strip()

    # 验证max_results范围
    if max_results < MIN_RESULTS:
        logger.warning(f'max_results {max_results} 小于最小值，调整为 {MIN_RESULTS}')
        max_results = MIN_RESULTS
    elif max_results > MAX_RESULTS_LIMIT:
        logger.warning(f'max_results {max_results} 超过限制，调整为 {MAX_RESULTS_LIMIT}')
        max_results = MAX_RESULTS_LIMIT

    # 验证搜索引擎
    engine = engine.lower().strip()
    if engine not in ('bing', 'duckduckgo'):
        logger.warning(f'不支持的搜索引擎: {engine}，使用默认引擎 duckduckgo')
        engine = 'duckduckgo'

    logger.info(f'执行搜索: query="{query}", max_results={max_results}, engine={engine}')

    # ==================== 缓存检查 ====================
    cache_key = f'search:{engine}:{query}:{max_results}'
    cached_result = cache_manager.get(cache_key)
    if cached_result:
        logger.info('缓存命中，直接返回结果')
        return cached_result

    # ==================== 执行搜索 ====================
    try:
        # 创建搜索引擎
        search_engine = SearchEngineFactory.create(engine)

        # 执行搜索
        results = search_engine.search(query, max_results)

        if not results:
            logger.info('未搜索到相关结果')
            return '未搜索到相关结果。'

        # ==================== 格式化输出 ====================
        output_lines = [f'搜索结果（共 {len(results)} 条）：\n']

        for i, result in enumerate(results, 1):
            output_lines.append(f'{i}. **{result.title}**')
            output_lines.append(f'   - 摘要：{result.snippet}')
            output_lines.append(f'   - 链接：{result.url}')
            output_lines.append('')  # 空行分隔

        result_text = '\n'.join(output_lines)

        # ==================== 存入缓存 ====================
        cache_manager.set(cache_key, result_text)

        logger.info(f'搜索成功，返回 {len(results)} 条结果')
        return result_text

    except ValueError as e:
        error_msg = f'搜索引擎配置错误：{str(e)}'
        logger.error(error_msg)
        return error_msg

    except Exception as e:
        error_msg = f'网络搜索失败：{str(e)}'
        logger.error(error_msg, exc_info=True)
        return error_msg
