"""URL验证与清洗工具。"""
import logging
from urllib.parse import urlparse

# 配置日志
logger = logging.getLogger(__name__)


class UrlValidator:
    """URL验证器。"""

    @staticmethod
    def is_valid(url: str) -> bool:
        """
        验证URL格式是否合法。

        Args:
            url: 待验证的URL

        Returns:
            URL是否合法
        """
        if not url or not isinstance(url, str):
            return False

        try:
            result = urlparse(url)
            # 必须有scheme和netloc
            return all([result.scheme, result.netloc])
        except Exception as e:
            logger.warning(f'URL验证失败: {url}, 错误: {e}')
            return False

    @staticmethod
    def sanitize(url: str) -> str:
        """
        清洗URL（移除危险字符）。

        Args:
            url: 原始URL

        Returns:
            清洗后的URL
        """
        if not url:
            return url

        # 移除常见的危险字符
        dangerous_chars = ['<', '>', '"', "'", '`', '\\', '\n', '\r']
        for char in dangerous_chars:
            url = url.replace(char, '')

        return url.strip()
