"""数据源适配器模块。

提供统一的数据源访问接口，支持网页、API、文件等多种数据源。
"""
import logging
from abc import ABC, abstractmethod
from typing import Any, Dict, Optional

from models.data_source import DataSourceResult

# 配置日志
logger = logging.getLogger(__name__)


class BaseDataSourceAdapter(ABC):
    """数据源适配器抽象基类。"""

    @abstractmethod
    def fetch(self, url: str, **kwargs) -> DataSourceResult:
        """
        从数据源获取数据。

        Args:
            url: 数据源URL（网页URL/API端点/文件路径）
            **kwargs: 额外参数

        Returns:
            DataSourceResult: 数据源查询结果
        """
        pass

    @abstractmethod
    def get_type(self) -> str:
        """返回数据源类型标识。"""
        pass

    def validate_url(self, url: str) -> bool:
        """
        验证URL格式是否合法。

        Args:
            url: 待验证的URL

        Returns:
            URL是否合法
        """
        if not url or not isinstance(url, str):
            return False
        
        # 基本验证：非空且长度合理
        if len(url.strip()) == 0 or len(url) > 2048:
            return False
        
        return True

    def _create_success_result(self, data: Any, source_type: str, 
                                metadata: Optional[Dict] = None) -> DataSourceResult:
        """创建成功结果。"""
        return DataSourceResult(
            success=True,
            data=data,
            source_type=source_type,
            metadata=metadata or {}
        )

    def _create_error_result(self, error_message: str, source_type: str) -> DataSourceResult:
        """创建错误结果。"""
        logger.error(f"数据源错误 [{source_type}]: {error_message}")
        return DataSourceResult(
            success=False,
            data=None,
            source_type=source_type,
            error_message=error_message
        )
