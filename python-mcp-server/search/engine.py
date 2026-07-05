"""搜索引擎模块。

提供多搜索引擎支持，包括Bing和DuckDuckGo。
"""
import logging
from abc import ABC, abstractmethod
from typing import List, Dict

from config.constants import MAX_RESULTS_LIMIT
from utils.http_client import HttpClient
from parser.html_parser import HtmlParser
from models.search_result import SearchResult

# 配置日志
logger = logging.getLogger(__name__)


class BaseSearchEngine(ABC):
    """搜索引擎抽象基类。"""

    @abstractmethod
    def search(self, query: str, max_results: int = 5) -> List[SearchResult]:
        """
        执行搜索。

        Args:
            query: 搜索关键词
            max_results: 最大结果数量

        Returns:
            搜索结果列表
        """
        pass

    @abstractmethod
    def get_name(self) -> str:
        """返回搜索引擎名称。"""
        pass


class BingSearchEngine(BaseSearchEngine):
    """Bing搜索引擎实现。"""

    BASE_URL = 'https://www.bing.com/search'
    SELECTORS = {
        'item': 'li.b_algo',
        'title': 'h2 a',
        'snippet': 'p',
    }

    def __init__(self):
        self.http_client = HttpClient()
        self.parser = HtmlParser()

    def search(self, query: str, max_results: int = 5) -> List[SearchResult]:
        """使用Bing搜索。"""
        logger.info(f'Bing搜索: query="{query}", max_results={max_results}')

        # 参数验证
        max_results = min(max_results, MAX_RESULTS_LIMIT)

        try:
            # 构建搜索URL
            from urllib.parse import quote_plus
            search_url = f'{self.BASE_URL}?q={quote_plus(query)}'

            # 发起请求
            response = self.http_client.get_with_retry(search_url)

            # 解析结果
            raw_results = self.parser.parse_search_results(
                response.text,
                self.SELECTORS,
                max_results,
                needs_url_cleanup=False,
            )

            # 转换为SearchResult对象
            results = [
                SearchResult(
                    title=r['title'],
                    url=r['url'],
                    snippet=r['snippet'],
                    source='bing',
                    rank=i + 1,
                )
                for i, r in enumerate(raw_results)
            ]

            logger.info(f'Bing搜索完成，获取到 {len(results)} 条结果')
            return results

        except Exception as e:
            logger.error(f'Bing搜索出错: {e}', exc_info=True)
            raise

    def get_name(self) -> str:
        return 'bing'

    def __del__(self):
        """析构时关闭HTTP客户端。"""
        if hasattr(self, 'http_client'):
            self.http_client.close()


class DuckDuckGoSearchEngine(BaseSearchEngine):
    """DuckDuckGo搜索引擎实现。"""

    BASE_URL = 'https://html.duckduckgo.com/html/'
    SELECTORS = {
        'item': '.result',
        'title': '.result__a',
        'snippet': '.result__snippet',
    }

    def __init__(self):
        self.http_client = HttpClient()
        self.parser = HtmlParser()

    def search(self, query: str, max_results: int = 5) -> List[SearchResult]:
        """使用DuckDuckGo搜索。"""
        logger.info(f'DuckDuckGo搜索: query="{query}", max_results={max_results}')

        # 参数验证
        max_results = min(max_results, MAX_RESULTS_LIMIT)

        try:
            # 构建搜索URL（POST方式）
            from urllib.parse import urlencode
            data = {'q': query}

            # 发起POST请求
            response = self.http_client.post_with_retry(
                self.BASE_URL,
                data=data,
            )

            # 解析结果
            raw_results = self.parser.parse_search_results(
                response.text,
                self.SELECTORS,
                max_results,
                needs_url_cleanup=True,  # DuckDuckGo需要URL清洗
            )

            # 转换为SearchResult对象
            results = [
                SearchResult(
                    title=r['title'],
                    url=r['url'],
                    snippet=r['snippet'],
                    source='duckduckgo',
                    rank=i + 1,
                )
                for i, r in enumerate(raw_results)
            ]

            logger.info(f'DuckDuckGo搜索完成，获取到 {len(results)} 条结果')
            return results

        except Exception as e:
            logger.error(f'DuckDuckGo搜索出错: {e}', exc_info=True)
            raise

    def get_name(self) -> str:
        return 'duckduckgo'

    def __del__(self):
        """析构时关闭HTTP客户端。"""
        if hasattr(self, 'http_client'):
            self.http_client.close()


class SearchEngineFactory:
    """搜索引擎工厂 - 根据名称创建引擎实例。"""

    ENGINES = {
        'bing': BingSearchEngine,
        'duckduckgo': DuckDuckGoSearchEngine,
    }

    @staticmethod
    def create(engine_name: str) -> BaseSearchEngine:
        """
        创建搜索引擎实例。

        Args:
            engine_name: 搜索引擎名称（bing/duckduckgo）

        Returns:
            搜索引擎实例

        Raises:
            ValueError: 不支持的搜索引擎
        """
        engine_class = SearchEngineFactory.ENGINES.get(engine_name.lower())
        if not engine_class:
            supported = list(SearchEngineFactory.ENGINES.keys())
            raise ValueError(
                f'不支持的搜索引擎: {engine_name}，支持的引擎: {supported}'
            )

        logger.debug(f'创建搜索引擎实例: {engine_name}')
        return engine_class()
