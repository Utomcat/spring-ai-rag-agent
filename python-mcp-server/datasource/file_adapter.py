"""文件数据源适配器。

从CSV、Excel等文件中读取结构化数据。
"""
import logging
import os
from pathlib import Path
from typing import Optional

import pandas as pd

from datasource.base_adapter import BaseDataSourceAdapter
from models.data_source import DataSourceResult

# 配置日志
logger = logging.getLogger(__name__)


class FileAdapter(BaseDataSourceAdapter):
    """文件数据源适配器 - 从CSV/Excel文件读取数据。"""

    SUPPORTED_EXTENSIONS = {'.csv', '.xlsx', '.xls', '.tsv'}

    def __init__(self, base_dir: Optional[str] = None):
        """
        初始化文件适配器。

        Args:
            base_dir: 基础目录（可选，用于限制文件访问范围）
        """
        self.base_dir = Path(base_dir) if base_dir else None

    def fetch(self, url: str, **kwargs) -> DataSourceResult:
        """
        从文件读取数据。

        Args:
            url: 文件路径或URL
            **kwargs: 额外参数
                - encoding: 文件编码（默认utf-8）
                - sheet_name: Excel工作表名称（默认第一个）
                - delimiter: CSV分隔符（默认逗号）

        Returns:
            DataSourceResult: 包含DataFrame的查询结果
        """
        try:
            # 验证文件路径
            file_path = Path(url)
            
            # 安全检查：如果设置了base_dir，确保文件在允许范围内
            if self.base_dir:
                try:
                    file_path.resolve().relative_to(self.base_dir.resolve())
                except ValueError:
                    return self._create_error_result(
                        f"文件路径超出允许范围: {self.base_dir}",
                        "file"
                    )

            # 检查文件是否存在
            if not file_path.exists():
                return self._create_error_result(f"文件不存在: {url}", "file")

            # 检查文件扩展名
            ext = file_path.suffix.lower()
            if ext not in self.SUPPORTED_EXTENSIONS:
                return self._create_error_result(
                    f"不支持的文件格式: {ext}，支持的格式: {self.SUPPORTED_EXTENSIONS}",
                    "file"
                )

            logger.info(f"正在读取文件: {file_path}")

            # 根据文件类型读取
            if ext == '.csv':
                df = self._read_csv(file_path, **kwargs)
            elif ext in ('.xlsx', '.xls'):
                df = self._read_excel(file_path, **kwargs)
            elif ext == '.tsv':
                df = self._read_tsv(file_path, **kwargs)
            else:
                return self._create_error_result(f"未实现的文件格式: {ext}", "file")

            if df is None or df.empty:
                return self._create_error_result("文件为空或读取失败", "file")

            logger.info(f"成功读取文件: {len(df)} 行 x {len(df.columns)} 列")

            return self._create_success_result(
                data=df,
                source_type="file",
                metadata={
                    'file_path': str(file_path),
                    'file_size': file_path.stat().st_size,
                    'rows': len(df),
                    'columns': list(df.columns),
                    'dtypes': {col: str(dtype) for col, dtype in df.dtypes.items()}
                }
            )

        except Exception as e:
            logger.error(f"文件读取失败: {e}", exc_info=True)
            return self._create_error_result(str(e), "file")

    def get_type(self) -> str:
        return "file"

    def _read_csv(self, file_path: Path, **kwargs) -> Optional[pd.DataFrame]:
        """读取CSV文件。"""
        try:
            encoding = kwargs.get('encoding', 'utf-8')
            delimiter = kwargs.get('delimiter', ',')
            
            df = pd.read_csv(
                file_path,
                encoding=encoding,
                sep=delimiter,
                low_memory=False
            )
            return df
        except UnicodeDecodeError:
            # 尝试其他编码
            logger.warning("UTF-8解码失败，尝试GBK编码")
            df = pd.read_csv(file_path, encoding='gbk', sep=delimiter, low_memory=False)
            return df

    def _read_excel(self, file_path: Path, **kwargs) -> Optional[pd.DataFrame]:
        """读取Excel文件。"""
        sheet_name = kwargs.get('sheet_name', 0)
        
        df = pd.read_excel(
            file_path,
            sheet_name=sheet_name,
            engine='openpyxl' if file_path.suffix == '.xlsx' else 'xlrd'
        )
        return df

    def _read_tsv(self, file_path: Path, **kwargs) -> Optional[pd.DataFrame]:
        """读取TSV文件。"""
        encoding = kwargs.get('encoding', 'utf-8')
        
        df = pd.read_csv(
            file_path,
            encoding=encoding,
            sep='\t',
            low_memory=False
        )
        return df
