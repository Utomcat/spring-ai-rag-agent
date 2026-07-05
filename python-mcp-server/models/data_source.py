"""结构化数据模型。"""
from dataclasses import dataclass, field
from typing import Any, Dict, List, Optional
from datetime import datetime


@dataclass
class DataSourceResult:
    """数据源查询结果。"""
    success: bool                    # 是否成功
    data: Any                        # 原始数据（DataFrame/Dict/List等）
    source_type: str                 # 数据源类型（webpage/api/file）
    metadata: Dict[str, Any] = field(default_factory=dict)  # 元数据
    error_message: Optional[str] = None  # 错误信息
    timestamp: datetime = field(default_factory=datetime.now)  # 时间戳


@dataclass
class ExtractedData:
    """提取后的结构化数据。"""
    title: str                       # 数据标题
    fields: List[str]                # 字段名列表
    records: List[Dict[str, Any]]    # 记录列表
    row_count: int                   # 行数
    source: str                      # 数据来源
    extracted_at: datetime = field(default_factory=datetime.now)  # 提取时间
