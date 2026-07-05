"""图表数据生成器模块。

生成适用于前端可视化的图表数据格式（JSON/CSV）。
"""
import logging
from typing import Any, Dict, List, Optional

import pandas as pd
import numpy as np

# 配置日志
logger = logging.getLogger(__name__)


class ChartDataGenerator:
    """图表数据生成器 - 将DataFrame转换为可视化友好的格式。"""

    @staticmethod
    def generate_line_chart_data(df: pd.DataFrame, date_column: str, 
                                value_columns: List[str], 
                                title: str = "折线图") -> Dict[str, Any]:
        """
        生成折线图数据。

        Args:
            df: DataFrame
            date_column: 日期列名
            value_columns: 数值列名列表
            title: 图表标题

        Returns:
            折线图数据（ECharts/AntV格式）
        """
        if df is None or df.empty:
            return {'error': '数据为空'}

        df_copy = df.copy()
        df_copy[date_column] = pd.to_datetime(df_copy[date_column])
        df_copy = df_copy.sort_values(date_column)

        # 构建系列数据
        series = []
        for col in value_columns:
            if col in df_copy.columns:
                series.append({
                    'name': col,
                    'type': 'line',
                    'data': [
                        {
                            'date': str(row[date_column]),
                            'value': round(float(row[col]), 2) if pd.notna(row[col]) else None
                        }
                        for _, row in df_copy.iterrows()
                    ]
                })

        return {
            'chart_type': 'line',
            'title': title,
            'x_axis': {
                'type': 'time',
                'data': [str(d) for d in df_copy[date_column]]
            },
            'series': series,
            'tooltip': {
                'trigger': 'axis',
                'format': '{seriesName}: {value}'
            }
        }

    @staticmethod
    def generate_bar_chart_data(df: pd.DataFrame, category_column: str, 
                               value_column: str, 
                               title: str = "柱状图") -> Dict[str, Any]:
        """
        生成柱状图数据。

        Args:
            df: DataFrame
            category_column: 分类列名
            value_column: 数值列名
            title: 图表标题

        Returns:
            柱状图数据
        """
        if df is None or df.empty:
            return {'error': '数据为空'}

        # 按分类聚合
        agg_data = df.groupby(category_column)[value_column].sum().reset_index()
        agg_data = agg_data.sort_values(value_column, ascending=False)

        return {
            'chart_type': 'bar',
            'title': title,
            'x_axis': {
                'type': 'category',
                'data': agg_data[category_column].tolist()
            },
            'y_axis': {
                'type': 'value',
                'name': value_column
            },
            'series': [{
                'name': value_column,
                'type': 'bar',
                'data': [round(float(v), 2) for v in agg_data[value_column]]
            }],
            'tooltip': {
                'trigger': 'axis',
                'format': '{name}: {value}'
            }
        }

    @staticmethod
    def generate_pie_chart_data(df: pd.DataFrame, category_column: str, 
                               value_column: str, 
                               title: str = "饼图") -> Dict[str, Any]:
        """
        生成饼图数据。

        Args:
            df: DataFrame
            category_column: 分类列名
            value_column: 数值列名
            title: 图表标题

        Returns:
            饼图数据
        """
        if df is None or df.empty:
            return {'error': '数据为空'}

        # 按分类聚合
        agg_data = df.groupby(category_column)[value_column].sum().reset_index()
        
        # 计算百分比
        total = agg_data[value_column].sum()
        agg_data['percentage'] = (agg_data[value_column] / total * 100).round(2)

        return {
            'chart_type': 'pie',
            'title': title,
            'data': [
                {
                    'name': str(row[category_column]),
                    'value': round(float(row[value_column]), 2),
                    'percentage': round(float(row['percentage']), 2)
                }
                for _, row in agg_data.iterrows()
            ],
            'total': round(float(total), 2),
            'tooltip': {
                'format': '{name}: {value} ({percentage}%)'
            }
        }

    @staticmethod
    def generate_scatter_chart_data(df: pd.DataFrame, x_column: str, 
                                   y_column: str, 
                                   title: str = "散点图") -> Dict[str, Any]:
        """
        生成散点图数据。

        Args:
            df: DataFrame
            x_column: X轴列名
            y_column: Y轴列名
            title: 图表标题

        Returns:
            散点图数据
        """
        if df is None or df.empty:
            return {'error': '数据为空'}

        df_clean = df.dropna(subset=[x_column, y_column])

        return {
            'chart_type': 'scatter',
            'title': title,
            'x_axis': {
                'type': 'value',
                'name': x_column
            },
            'y_axis': {
                'type': 'value',
                'name': y_column
            },
            'series': [{
                'name': f'{x_column} vs {y_column}',
                'type': 'scatter',
                'data': [
                    [round(float(row[x_column]), 2), round(float(row[y_column]), 2)]
                    for _, row in df_clean.iterrows()
                ]
            }],
            'tooltip': {
                'format': '({x}, {y})'
            }
        }

    @staticmethod
    def export_to_csv(df: pd.DataFrame, filename: Optional[str] = None) -> str:
        """
        导出DataFrame为CSV格式字符串。

        Args:
            df: DataFrame
            filename: 文件名（可选）

        Returns:
            CSV格式字符串
        """
        if df is None or df.empty:
            return ''

        csv_str = df.to_csv(index=False, encoding='utf-8-sig')
        
        if filename:
            return f"文件: {filename}\n\n{csv_str}"
        
        return csv_str

    @staticmethod
    def generate_summary_table(df: pd.DataFrame, 
                              title: str = "数据摘要表") -> Dict[str, Any]:
        """
        生成摘要表格数据。

        Args:
            df: DataFrame
            title: 表格标题

        Returns:
            摘要表格数据
        """
        if df is None or df.empty:
            return {'error': '数据为空'}

        # 计算基本统计
        summary = {
            '列名': [],
            '数据类型': [],
            '非空值数': [],
            '均值': [],
            '中位数': [],
            '标准差': [],
            '最小值': [],
            '最大值': []
        }

        for col in df.columns:
            summary['列名'].append(col)
            summary['数据类型'].append(str(df[col].dtype))
            summary['非空值数'].append(int(df[col].notna().sum()))
            
            if pd.api.types.is_numeric_dtype(df[col]):
                summary['均值'].append(round(float(df[col].mean()), 2))
                summary['中位数'].append(round(float(df[col].median()), 2))
                summary['标准差'].append(round(float(df[col].std()), 2))
                summary['最小值'].append(round(float(df[col].min()), 2))
                summary['最大值'].append(round(float(df[col].max()), 2))
            else:
                summary['均值'].append('N/A')
                summary['中位数'].append('N/A')
                summary['标准差'].append('N/A')
                summary['最小值'].append('N/A')
                summary['最大值'].append('N/A')

        return {
            'table_type': 'summary',
            'title': title,
            'columns': list(summary.keys()),
            'data': [
                {col: summary[col][i] for col in summary.keys()}
                for i in range(len(summary['列名']))
            ]
        }
