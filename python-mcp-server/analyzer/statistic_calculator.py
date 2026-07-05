"""统计分析计算器模块。

提供描述性统计、分布分析等基础统计功能。
"""
import logging
from typing import Any, Dict, List, Optional

import pandas as pd
import numpy as np

# 配置日志
logger = logging.getLogger(__name__)


class StatisticCalculator:
    """统计分析计算器 - 提供各类统计分析功能。"""

    @staticmethod
    def descriptive_statistics(df: pd.DataFrame) -> Dict[str, Any]:
        """
        计算描述性统计量。

        Args:
            df: Pandas DataFrame

        Returns:
            包含描述性统计结果的字典
        """
        if df is None or df.empty:
            return {}

        result = {
            'basic_info': {
                'rows': len(df),
                'columns': len(df.columns),
                'column_names': list(df.columns)
            },
            'numeric_stats': {},
            'categorical_stats': {}
        }

        # 数值列统计
        numeric_cols = df.select_dtypes(include=[np.number]).columns
        for col in numeric_cols:
            series = df[col].dropna()
            if len(series) == 0:
                continue
            
            result['numeric_stats'][col] = {
                'count': int(len(series)),
                'mean': round(float(series.mean()), 4),
                'median': round(float(series.median()), 4),
                'std': round(float(series.std()), 4),
                'min': round(float(series.min()), 4),
                'max': round(float(series.max()), 4),
                'q1': round(float(series.quantile(0.25)), 4),
                'q3': round(float(series.quantile(0.75)), 4),
                'missing': int(df[col].isnull().sum())
            }

        # 分类列统计
        categorical_cols = df.select_dtypes(include=['object', 'category']).columns
        for col in categorical_cols:
            series = df[col].dropna()
            if len(series) == 0:
                continue
            
            value_counts = series.value_counts()
            result['categorical_stats'][col] = {
                'count': int(len(series)),
                'unique_count': int(series.nunique()),
                'top_5': {str(k): int(v) for k, v in value_counts.head(5).items()},
                'missing': int(df[col].isnull().sum())
            }

        return result

    @staticmethod
    def distribution_analysis(df: pd.DataFrame, columns: Optional[List[str]] = None) -> Dict[str, Any]:
        """
        分析数据分布特征。

        Args:
            df: Pandas DataFrame
            columns: 要分析的列名列表（默认所有数值列）

        Returns:
            分布分析结果
        """
        if df is None or df.empty:
            return {}

        if columns is None:
            columns = df.select_dtypes(include=[np.number]).columns.tolist()

        result = {}
        
        for col in columns:
            if col not in df.columns:
                continue
            
            series = df[col].dropna()
            if len(series) < 3:
                continue

            # 计算偏度和峰度
            skewness = float(series.skew())
            kurtosis = float(series.kurtosis())

            # 判断分布类型
            if abs(skewness) < 0.5:
                distribution_type = "近似正态分布"
            elif skewness > 0:
                distribution_type = "右偏分布"
            else:
                distribution_type = "左偏分布"

            result[col] = {
                'distribution_type': distribution_type,
                'skewness': round(skewness, 4),
                'kurtosis': round(kurtosis, 4),
                'interpretation': StatisticCalculator._interpret_distribution(skewness, kurtosis)
            }

        return result

    @staticmethod
    def _interpret_distribution(skewness: float, kurtosis: float) -> str:
        """解释分布特征。"""
        interpretations = []
        
        if abs(skewness) < 0.5:
            interpretations.append("数据分布较为对称")
        elif skewness > 0:
            interpretations.append(f"数据右偏（长尾在右侧，偏度={skewness:.2f}）")
        else:
            interpretations.append(f"数据左偏（长尾在左侧，偏度={skewness:.2f}）")
        
        if kurtosis > 0:
            interpretations.append(f"分布较陡峭（峰度={kurtosis:.2f}）")
        else:
            interpretations.append(f"分布较平坦（峰度={kurtosis:.2f}）")
        
        return "; ".join(interpretations)

    @staticmethod
    def comparison_analysis(df: pd.DataFrame, group_column: str, 
                           value_column: str) -> Dict[str, Any]:
        """
        分组对比分析。

        Args:
            df: Pandas DataFrame
            group_column: 分组列名
            value_column: 值列名

        Returns:
            分组对比结果
        """
        if df is None or df.empty:
            return {}

        if group_column not in df.columns or value_column not in df.columns:
            return {'error': f"列不存在: {group_column} 或 {value_column}"}

        # 按组统计
        grouped = df.groupby(group_column)[value_column].agg([
            'count', 'mean', 'median', 'std', 'min', 'max'
        ]).round(4)

        result = {
            'groups': grouped.to_dict('index'),
            'overall_mean': round(float(df[value_column].mean()), 4),
            'overall_median': round(float(df[value_column].median()), 4)
        }

        return result

    @staticmethod
    def time_series_summary(df: pd.DataFrame, date_column: str, 
                           value_column: str) -> Dict[str, Any]:
        """
        时间序列数据摘要。

        Args:
            df: Pandas DataFrame
            date_column: 日期列名
            value_column: 值列名

        Returns:
            时间序列摘要
        """
        if df is None or df.empty:
            return {}

        if date_column not in df.columns or value_column not in df.columns:
            return {'error': f"列不存在: {date_column} 或 {value_column}"}

        # 确保日期列是datetime类型
        df_copy = df.copy()
        df_copy[date_column] = pd.to_datetime(df_copy[date_column])
        df_copy = df_copy.sort_values(date_column)

        # 计算周期性统计
        df_copy['year'] = df_copy[date_column].dt.year
        df_copy['month'] = df_copy[date_column].dt.month
        
        yearly_stats = df_copy.groupby('year')[value_column].agg(['mean', 'sum', 'count']).round(4)
        monthly_stats = df_copy.groupby('month')[value_column].agg(['mean', 'sum', 'count']).round(4)

        result = {
            'date_range': {
                'start': str(df_copy[date_column].min()),
                'end': str(df_copy[date_column].max()),
                'days': (df_copy[date_column].max() - df_copy[date_column].min()).days
            },
            'yearly_trend': yearly_stats.to_dict('index'),
            'monthly_pattern': monthly_stats.to_dict('index')
        }

        return result
