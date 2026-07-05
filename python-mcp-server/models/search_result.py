"""搜索结果数据模型。"""
from dataclasses import dataclass
from typing import Optional


@dataclass
class SearchResult:
    """搜索结果数据模型。"""
    title: str          # 标题
    url: str            # URL
    snippet: str        # 摘要
    source: Optional[str] = None  # 来源标识（bing/duckduckgo）
    rank: Optional[int] = None    # 排名
