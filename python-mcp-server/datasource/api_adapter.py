"""API数据源适配器。

从REST API端点获取JSON数据并转换为结构化格式。
"""
import logging
from typing import Dict, Optional

import pandas as pd
import requests

from datasource.base_adapter import BaseDataSourceAdapter
from models.data_source import DataSourceResult
from utils.http_client import HttpClient

# 配置日志
logger = logging.getLogger(__name__)


class ApiAdapter(BaseDataSourceAdapter):
    """API数据源适配器 - 从REST API获取JSON数据。"""

    def __init__(self, timeout: tuple = (5, 10)):
        """
        初始化API适配器。

        Args:
            timeout: HTTP超时配置（连接超时, 读取超时）
        """
        self.http_client = HttpClient(timeout=timeout)

    def fetch(self, url: str, **kwargs) -> DataSourceResult:
        """
        从API获取数据。

        Args:
            url: API端点URL
            **kwargs: 额外参数
                - method: HTTP方法（GET/POST），默认GET
                - headers: 请求头字典
                - params: URL查询参数字典
                - json: POST请求体（JSON）
                - data_path: JSON数据路径（如 'data.items'）

        Returns:
            DataSourceResult: 包含DataFrame或Dict的查询结果
        """
        # 验证URL
        if not self.validate_url(url):
            return self._create_error_result("无效的URL格式", "api")

        try:
            # 提取参数
            method = kwargs.get('method', 'GET').upper()
            headers = kwargs.get('headers', {})
            params = kwargs.get('params', {})
            json_body = kwargs.get('json', None)
            data_path = kwargs.get('data_path', None)

            logger.info(f"正在调用API: {method} {url}")

            # 发送HTTP请求
            response = self.http_client.session.request(
                method=method,
                url=url,
                headers=headers,
                params=params,
                json=json_body,
                timeout=self.http_client.timeout
            )

            # 检查响应状态
            response.raise_for_status()

            # 解析JSON
            json_data = response.json()
            
            # 如果指定了数据路径，提取嵌套数据
            if data_path:
                json_data = self._extract_by_path(json_data, data_path)

            # 转换为DataFrame（如果是列表或字典列表）
            if isinstance(json_data, list):
                if len(json_data) > 0 and isinstance(json_data[0], dict):
                    df = pd.DataFrame(json_data)
                    logger.info(f"成功转换API数据: {len(df)} 行 x {len(df.columns)} 列")
                    
                    return self._create_success_result(
                        data=df,
                        source_type="api",
                        metadata={
                            'url': url,
                            'method': method,
                            'status_code': response.status_code,
                            'rows': len(df),
                            'columns': list(df.columns)
                        }
                    )
            
            # 如果不是列表，返回原始JSON
            return self._create_success_result(
                data=json_data,
                source_type="api",
                metadata={
                    'url': url,
                    'method': method,
                    'status_code': response.status_code,
                    'data_type': type(json_data).__name__
                }
            )

        except requests.exceptions.RequestException as e:
            logger.error(f"API请求失败: {e}", exc_info=True)
            return self._create_error_result(f"API请求失败: {str(e)}", "api")
        except Exception as e:
            logger.error(f"API数据处理失败: {e}", exc_info=True)
            return self._create_error_result(str(e), "api")

    def get_type(self) -> str:
        return "api"

    def _extract_by_path(self, data: Dict, path: str) -> any:
        """
        根据路径提取嵌套JSON数据。

        Args:
            data: JSON数据
            path: 点分隔的路径（如 'data.items'）

        Returns:
            提取的数据
        """
        keys = path.split('.')
        current = data
        
        for key in keys:
            if isinstance(current, dict) and key in current:
                current = current[key]
            else:
                logger.warning(f"路径 '{path}' 不存在，返回完整数据")
                return data
        
        return current
