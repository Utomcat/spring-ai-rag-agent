"""HTTP客户端模块。

提供HTTP Session创建、配置和带重试机制的请求功能。
"""
import logging
import time
from typing import Optional

import requests
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry

from config.constants import (
    DEFAULT_USER_AGENT,
    REQUEST_TIMEOUT,
    MAX_RETRIES,
    RETRY_BACKOFF_FACTOR,
)

# 配置日志
logger = logging.getLogger(__name__)


class HttpClient:
    """HTTP客户端 - 管理Session和连接池，提供带重试机制的请求。"""

    def __init__(
        self,
        timeout: tuple = REQUEST_TIMEOUT,
        max_retries: int = MAX_RETRIES,
        backoff_factor: float = RETRY_BACKOFF_FACTOR,
    ):
        """
        初始化HTTP客户端。

        Args:
            timeout: 超时配置（连接超时, 读取超时）
            max_retries: 最大重试次数
            backoff_factor: 重试退避因子
        """
        self.timeout = timeout
        self.max_retries = max_retries
        self.backoff_factor = backoff_factor
        self.session = self._create_session()

    def _create_session(self) -> requests.Session:
        """创建配置好的 HTTP Session 对象。

        Returns:
            requests.Session: 配置好 User-Agent、headers 和重试策略的会话对象
        """
        session = requests.Session()

        # 设置请求头
        session.headers.update({
            'User-Agent': DEFAULT_USER_AGENT,
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
            'Accept-Language': 'zh-CN,zh;q=0.9,en;q=0.8',
        })

        # 配置重试策略
        retry_strategy = Retry(
            total=self.max_retries,
            backoff_factor=self.backoff_factor,
            status_forcelist=[500, 502, 503, 504],  # 服务器错误时重试
            allowed_methods=['GET', 'POST'],  # 允许重试的方法
        )

        # 挂载适配器到session
        adapter = HTTPAdapter(max_retries=retry_strategy)
        session.mount('http://', adapter)
        session.mount('https://', adapter)

        return session

    def get_with_retry(self, url: str, **kwargs) -> requests.Response:
        """带重试机制的GET请求。

        Args:
            url: 请求URL
            **kwargs: 额外参数（如 headers, params 等）

        Returns:
            requests.Response: HTTP响应对象

        Raises:
            requests.exceptions.RequestException: 所有重试都失败后抛出
        """
        try:
            logger.debug(f'发起GET请求: {url}')
            response = self.session.get(
                url,
                timeout=self.timeout,
                allow_redirects=True,
                **kwargs
            )
            response.raise_for_status()
            logger.debug(f'请求成功: {url}, 状态码: {response.status_code}')
            return response
        except requests.exceptions.Timeout as e:
            logger.error(f'请求超时: {url}, 超时配置: {self.timeout}')
            raise
        except requests.exceptions.RequestException as e:
            logger.error(f'请求失败: {url}, 错误: {e}')
            raise

    def post_with_retry(self, url: str, data: Optional[dict] = None, json: Optional[dict] = None, **kwargs) -> requests.Response:
        """带重试机制的POST请求。

        Args:
            url: 请求URL
            data: 表单数据
            json: JSON数据
            **kwargs: 额外参数

        Returns:
            requests.Response: HTTP响应对象

        Raises:
            requests.exceptions.RequestException: 所有重试都失败后抛出
        """
        try:
            logger.debug(f'发起POST请求: {url}')
            response = self.session.post(
                url,
                data=data,
                json=json,
                timeout=self.timeout,
                **kwargs
            )
            response.raise_for_status()
            logger.debug(f'请求成功: {url}, 状态码: {response.status_code}')
            return response
        except requests.exceptions.Timeout as e:
            logger.error(f'请求超时: {url}, 超时配置: {self.timeout}')
            raise
        except requests.exceptions.RequestException as e:
            logger.error(f'请求失败: {url}, 错误: {e}')
            raise

    def close(self):
        """关闭Session释放资源。"""
        if self.session:
            self.session.close()
            logger.debug('HTTP Session已关闭')

    def __enter__(self):
        """上下文管理器入口。"""
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        """上下文管理器出口，确保资源释放。"""
        self.close()


# 向后兼容的函数接口
def create_http_session() -> requests.Session:
    """创建配置好的 HTTP Session 对象（向后兼容）。

    Returns:
        requests.Session: 配置好 User-Agent 和 headers 的会话对象
    """
    client = HttpClient()
    return client.session
