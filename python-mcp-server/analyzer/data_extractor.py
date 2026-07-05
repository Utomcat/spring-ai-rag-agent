"""数据提取器模块。

从DataFrame或原始数据中提取关键指标和结构化信息。
"""
import logging
from typing import Any, Dict, List, Optional

import pandas as pd
import numpy as np

from models.data_source import ExtractedData

# 配置日志
logger = logging.getLogger(__name__)


class DataExtractor:
    """数据提取器 - 从原始数据中提取关键指标和结构化信息。"""

    @staticmethod
    def extract_from_dataframe(df: pd.DataFrame, title: str = "未命名数据", 
                                source: str = "未知") -> ExtractedData:
        """
        从DataFrame提取结构化数据。

        Args:
            df: Pandas DataFrame
            title: 数据标题
            source: 数据来源

        Returns:
            ExtractedData: 提取后的结构化数据
        """
        if df is None or df.empty:
            raise ValueError("DataFrame为空")

        # 提取字段名
        fields = list(df.columns)
        
        # 转换为记录列表（限制前100条）
        records = df.head(100).to_dict('records')
        
        return ExtractedData(
            title=title,
            fields=fields,
            records=records,
            row_count=len(df),
            source=source
        )

    @staticmethod
    def extract_key_metrics(df: pd.DataFrame) -> Dict[str, Any]:
        """
        提取关键统计指标。

        Args:
            df: Pandas DataFrame

        Returns:
            包含关键指标的字典
        """
        if df is None or df.empty:
            return {}

        metrics = {
            'row_count': len(df),
            'column_count': len(df.columns),
            'columns': list(df.columns),
            'dtypes': {col: str(dtype) for col, dtype in df.dtypes.items()},
            'missing_values': df.isnull().sum().to_dict(),
        }

        # 数值列的统计信息
        numeric_cols = df.select_dtypes(include=[np.number]).columns
        if len(numeric_cols) > 0:
            metrics['numeric_summary'] = df[numeric_cols].describe().to_dict()
            
            # 检测可能的时间序列
            date_cols = df.select_dtypes(include=['datetime64']).columns
            if len(date_cols) > 0:
                metrics['date_range'] = {
                    col: {
                        'min': str(df[col].min()),
                        'max': str(df[col].max())
                    }
                    for col in date_cols
                }

        # 分类列的唯一值统计
        categorical_cols = df.select_dtypes(include=['object', 'category']).columns
        if len(categorical_cols) > 0:
            metrics['categorical_summary'] = {
                col: {
                    'unique_count': df[col].nunique(),
                    'top_values': df[col].value_counts().head(5).to_dict()
                }
                for col in categorical_cols
            }

        return metrics

    @staticmethod
    def detect_patterns(df: pd.DataFrame) -> Dict[str, Any]:
        """
        检测数据中的模式（趋势、异常值等）。

        Args:
            df: Pandas DataFrame

        Returns:
            包含检测到的模式的字典
        """
        patterns = {
            'trends': [],
            'outliers': [],
            'correlations': []
        }

        # 检测数值列的趋势
        numeric_cols = df.select_dtypes(include=[np.number]).columns
        
        for col in numeric_cols:
            series = df[col].dropna()
            
            if len(series) < 3:
                continue
            
            # 简单趋势检测（上升/下降/平稳）
            first_third = series.iloc[:len(series)//3].mean()
            last_third = series.iloc[-(len(series)//3):].mean()
            
            change_rate = (last_third - first_third) / first_third if first_third != 0 else 0
            
            if abs(change_rate) > 0.1:  # 变化超过10%
                trend_type = "上升" if change_rate > 0 else "下降"
                patterns['trends'].append({
                    'column': col,
                    'type': trend_type,
                    'change_rate': f"{change_rate * 100:.2f}%"
                })
            
            # 检测异常值（使用IQR方法）
            Q1 = series.quantile(0.25)
            Q3 = series.quantile(0.75)
            IQR = Q3 - Q1
            lower_bound = Q1 - 1.5 * IQR
            upper_bound = Q3 + 1.5 * IQR
            
            outliers = series[(series < lower_bound) | (series > upper_bound)]
            if len(outliers) > 0:
                patterns['outliers'].append({
                    'column': col,
                    'count': len(outliers),
                    'values': outliers.tolist()[:5]  # 最多返回5个异常值
                })

        # 计算相关性（如果有多个数值列）
        if len(numeric_cols) >= 2:
            corr_matrix = df[numeric_cols].corr()
            # 提取强相关对（|r| > 0.7）
            for i in range(len(numeric_cols)):
                for j in range(i+1, len(numeric_cols)):
                    corr_value = corr_matrix.iloc[i, j]
                    if abs(corr_value) > 0.7:
                        patterns['correlations'].append({
                            'column1': numeric_cols[i],
                            'column2': numeric_cols[j],
                            'correlation': round(corr_value, 3),
                            'strength': "强正相关" if corr_value > 0 else "强负相关"
                        })

        return patterns
