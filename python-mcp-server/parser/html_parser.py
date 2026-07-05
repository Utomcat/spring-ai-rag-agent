"""HTML解析模块。

使用BeautifulSoup解析HTML内容，提取结构化数据。
"""
import logging
from typing import List, Dict, Optional
from urllib.parse import urlparse, parse_qs

from bs4 import BeautifulSoup

# 配置日志
logger = logging.getLogger(__name__)


class HtmlParser:
    """HTML解析器 - 使用BeautifulSoup提取搜索结果和表格数据。"""

    @staticmethod
    def parse_search_results(
        html_content: str,
        selectors: Dict[str, str],
        max_results: int = 10,
        needs_url_cleanup: bool = False,
    ) -> List[Dict]:
        """
        解析搜索引擎结果页面。

        Args:
            html_content: HTML字符串
            selectors: CSS选择器配置
                {
                    'item': '.result',           # 结果项选择器
                    'title': '.result__a',       # 标题选择器
                    'snippet': '.result__snippet', # 摘要选择器
                }
            max_results: 最大结果数量
            needs_url_cleanup: 是否需要清理URL（如DuckDuckGo重定向）

        Returns:
            搜索结果列表 [{'title': str, 'url': str, 'snippet': str}, ...]
        """
        logger.debug(f'开始解析HTML，期望最大结果数: {max_results}')

        try:
            soup = BeautifulSoup(html_content, 'lxml')
        except Exception as e:
            logger.error(f'HTML解析失败: {e}')
            return []

        results = []
        item_selector = selectors.get('item', '')
        title_selector = selectors.get('title', '')
        snippet_selector = selectors.get('snippet', '')

        if not item_selector or not title_selector:
            logger.warning('缺少必要的CSS选择器配置')
            return []

        # 查找所有结果项
        items = soup.select(item_selector)
        logger.debug(f'找到 {len(items)} 个结果项')

        for item in items[:max_results]:
            try:
                # 提取标题
                title_elem = item.select_one(title_selector)
                if not title_elem:
                    continue

                title = title_elem.get_text(strip=True)
                url = title_elem.get('href', '')

                # URL清洗（如需要）
                if needs_url_cleanup and url:
                    url = HtmlParser._clean_redirect_url(url)

                # 提取摘要
                snippet = ''
                if snippet_selector:
                    snippet_elem = item.select_one(snippet_selector)
                    if snippet_elem:
                        snippet = snippet_elem.get_text(strip=True)

                # 过滤无效结果
                if title and url:
                    results.append({
                        'title': title,
                        'url': url,
                        'snippet': snippet or '无摘要',
                    })

            except Exception as e:
                logger.warning(f'解析单个结果项失败: {e}')
                continue

        logger.debug(f'解析完成，有效结果数: {len(results)}')
        return results

    @staticmethod
    def parse_table(html_content: str, table_index: int = 0) -> List[Dict]:
        """
        从HTML中提取表格数据。

        Args:
            html_content: HTML字符串
            table_index: 表格索引（默认第一个表格）

        Returns:
            表格数据列表 [{列名: 值}, ...]
        """
        logger.debug(f'开始解析表格，索引: {table_index}')

        try:
            soup = BeautifulSoup(html_content, 'lxml')
        except Exception as e:
            logger.error(f'HTML解析失败: {e}')
            return []

        # 查找所有表格
        tables = soup.find_all('table')
        if not tables or table_index >= len(tables):
            logger.warning(f'未找到表格或索引超出范围 (共{len(tables)}个表格)')
            return []

        table = tables[table_index]
        rows = table.find_all('tr')

        if not rows:
            logger.warning('表格中没有行')
            return []

        # 提取表头
        headers = []
        header_row = rows[0]
        for th in header_row.find_all(['th', 'td']):
            headers.append(th.get_text(strip=True))

        if not headers:
            logger.warning('表格没有表头')
            return []

        # 提取数据行
        data = []
        for row in rows[1:]:  # 跳过表头
            cells = row.find_all('td')
            if len(cells) == len(headers):
                row_data = {}
                for header, cell in zip(headers, cells):
                    row_data[header] = cell.get_text(strip=True)
                data.append(row_data)

        logger.debug(f'表格解析完成，共{len(data)}行数据')
        return data

    @staticmethod
    def _clean_redirect_url(url: str) -> str:
        """
        清理重定向URL（如DuckDuckGo的/l/?uddg=xxx格式）。

        Args:
            url: 原始URL

        Returns:
            清理后的真实URL
        """
        if not url:
            return url

        # DuckDuckGo的重定向链接格式: /l/?uddg=https%3A%2F%2Fexample.com
        if '/l/' in url and 'uddg=' in url:
            try:
                parsed = urlparse(url)
                params = parse_qs(parsed.query)
                if 'uddg' in params:
                    cleaned_url = params['uddg'][0]
                    logger.debug(f'URL清洗: {url} -> {cleaned_url}')
                    return cleaned_url
            except Exception as e:
                logger.warning(f'URL清洗失败: {url}, 错误: {e}')

        return url
