"""数据源工厂模块。

根据数据源类型自动选择合适的适配器。
"""
import logging
from typing import Dict, Optional

from datasource.base_adapter import BaseDataSourceAdapter
from datasource.webpage_adapter import WebPageAdapter
from datasource.api_adapter import ApiAdapter
from datasource.file_adapter import FileAdapter

# 配置日志
logger = logging.getLogger(__name__)


class DataSourceFactory:
    """数据源工厂 - 根据URL或类型创建合适的适配器。"""

    def __init__(self):
        """初始化工厂，注册所有适配器。"""
        self._adapters: Dict[str, BaseDataSourceAdapter] = {
            'webpage': WebPageAdapter(),
            'api': ApiAdapter(),
            'file': FileAdapter(),
        }

    def get_adapter(self, source_type: str) -> Optional[BaseDataSourceAdapter]:
        """
        根据数据源类型获取适配器。

        Args:
            source_type: 数据源类型（'webpage', 'api', 'file'）

        Returns:
            对应的适配器实例，如果不存在则返回None
        """
        adapter = self._adapters.get(source_type.lower())
        if adapter is None:
            logger.warning(f"未知的数据源类型: {source_type}，可用类型: {list(self._adapters.keys())}")
        return adapter

    def auto_detect_adapter(self, url: str) -> BaseDataSourceAdapter:
        """
        根据URL自动检测并返回合适的适配器。

        Args:
            url: 数据源URL或路径

        Returns:
            最合适的适配器实例
        """
        # 检查是否为文件路径
        if url.endswith(('.csv', '.xlsx', '.xls', '.tsv')):
            return self._adapters['file']
        
        # 检查是否为API端点（包含/api/或.json）
        if '/api/' in url or url.endswith('.json'):
            return self._adapters['api']
        
        # 默认为网页
        return self._adapters['webpage']

    def register_adapter(self, source_type: str, adapter: BaseDataSourceAdapter):
        """
        注册新的数据源适配器。

        Args:
            source_type: 数据源类型标识
            adapter: 适配器实例
        """
        self._adapters[source_type.lower()] = adapter
        logger.info(f"已注册数据源适配器: {source_type}")
