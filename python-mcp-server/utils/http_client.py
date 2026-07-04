"""HTTP会话工具模块。

提供HTTP Session创建和配置功能。
"""
import requests

from config.constants import DEFAULT_USER_AGENT


def create_http_session() -> requests.Session:
    """创建配置好的 HTTP Session 对象。
    
    Returns:
        requests.Session: 配置好 User-Agent 和 headers 的会话对象
    """
    session = requests.Session()
    session.headers.update({
        'User-Agent': DEFAULT_USER_AGENT,
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
        'Accept-Language': 'zh-CN,zh;q=0.9,en;q=0.8',
    })
    return session
