"""网页数据源适配器。

从网页中提取结构化数据（表格、列表等）。
"""
import logging
from typing import Dict, Optional

import pandas as pd
from bs4 import BeautifulSoup

from datasource.base_adapter import BaseDataSourceAdapter
from models.data_source import DataSourceResult
from utils.http_client import HttpClient

# 配置日志
logger = logging.getLogger(__name__)


class WebPageAdapter(BaseDataSourceAdapter):
    """网页数据源适配器 - 从网页中提取表格和结构化数据。"""

    def __init__(self, timeout: tuple = (5, 10)):
        """
        初始化网页适配器。

        Args:
            timeout: HTTP超时配置（连接超时, 读取超时）
        """
        self.http_client = HttpClient(timeout=timeout)

    def fetch(self, url: str, **kwargs) -> DataSourceResult:
        """
        从网页提取数据。

        Args:
            url: 网页URL
            **kwargs: 额外参数
                - table_index: 表格索引（默认0，第一个表格）
                - selector: CSS选择器（可选，用于自定义提取）

        Returns:
            DataSourceResult: 包含DataFrame的查询结果
        """
        # 验证URL
        if not self.validate_url(url):
            return self._create_error_result("无效的URL格式", "webpage")

        try:
            # 获取网页内容
            logger.info(f"正在抓取网页: {url}")
            response = self.http_client.get(url)
            
            if response is None:
                return self._create_error_result("HTTP请求失败", "webpage")

            # 解析HTML
            soup = BeautifulSoup(response.text, 'html.parser')
            
            # 尝试提取表格
            table_index = kwargs.get('table_index', 0)
            tables = soup.find_all('table')
            
            if tables and len(tables) > table_index:
                # 提取表格数据
                table_data = self._extract_table(tables[table_index])
                
                if table_data:
                    df = pd.DataFrame(table_data)
                    logger.info(f"成功提取表格: {len(df)} 行 x {len(df.columns)} 列")
                    
                    return self._create_success_result(
                        data=df,
                        source_type="webpage",
                        metadata={
                            'url': url,
                            'table_count': len(tables),
                            'selected_table': table_index,
                            'rows': len(df),
                            'columns': list(df.columns)
                        }
                    )
            
            # 如果没有表格，尝试提取结构化内容
            content = self._extract_content(soup)
            
            return self._create_success_result(
                data=content,
                source_type="webpage",
                metadata={
                    'url': url,
                    'content_type': 'structured_text',
                    'length': len(content)
                }
            )

        except Exception as e:
            logger.error(f"网页数据提取失败: {e}", exc_info=True)
            return self._create_error_result(str(e), "webpage")

    def get_type(self) -> str:
        return "webpage"

    def _extract_table(self, table) -> list:
        """
        从HTML表格提取数据。

        Args:
            table: BeautifulSoup Table对象

        Returns:
            字典列表
        """
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
            
            if not cells:
                continue
            
            # 如果有表头，使用字典；否则使用列表
            if headers and len(cells) == len(headers):
                row_dict = dict(zip(headers, cells))
                rows.append(row_dict)
            else:
                rows.append(cells)
        
        return rows

    def _extract_content(self, soup: BeautifulSoup) -> Dict:
        """
        提取网页主要内容。

        Args:
            soup: BeautifulSoup对象

        Returns:
            包含标题和内容的字典
        """
        # 提取标题
        title_tag = soup.find('title')
        title = title_tag.get_text(strip=True) if title_tag else "无标题"
        
        # 提取主要文本内容
        main_content = soup.find('main') or soup.find('article') or soup.find('body')
        
        if main_content:
            # 移除脚本和样式
            for script in main_content(["script", "style"]):
                script.decompose()
            
            text = main_content.get_text(separator='\n', strip=True)
            # 限制长度
            text = text[:5000] if len(text) > 5000 else text
        else:
            text = ""
        
        return {
            'title': title,
            'content': text,
            'links': [a.get('href') for a in soup.find_all('a', href=True)[:20]]
        }
