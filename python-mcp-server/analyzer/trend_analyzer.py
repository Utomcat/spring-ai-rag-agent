"""趋势分析器模块。

提供时间序列数据的趋势检测、预测和季节性分析功能。
"""
import logging
from typing import Any, Dict, List, Optional, Tuple

import pandas as pd
import numpy as np

# 配置日志
logger = logging.getLogger(__name__)


class TrendAnalyzer:
    """趋势分析器 - 检测数据趋势、计算增长率、识别周期性模式。"""

    @staticmethod
    def detect_trend(df: pd.DataFrame, date_column: str, 
                    value_column: str) -> Dict[str, Any]:
        """
        检测时间序列数据的整体趋势。

        Args:
            df: 包含时间序列的DataFrame
            date_column: 日期列名
            value_column: 数值列名

        Returns:
            趋势分析结果
        """
        if df is None or df.empty:
            return {'error': '数据为空'}

        if date_column not in df.columns or value_column not in df.columns:
            return {'error': f'列不存在: {date_column} 或 {value_column}'}

        # 确保日期列是datetime类型并排序
        df_copy = df.copy()
        df_copy[date_column] = pd.to_datetime(df_copy[date_column])
        df_copy = df_copy.sort_values(date_column).dropna(subset=[value_column])

        if len(df_copy) < 3:
            return {'error': '数据点不足（至少需要3个点）'}

        values = df_copy[value_column].values
        
        # 使用线性回归检测趋势
        x = np.arange(len(values))
        coefficients = np.polyfit(x, values, 1)
        slope = coefficients[0]
        
        # 计算趋势方向
        if abs(slope) < 1e-10:
            trend_direction = "平稳"
        elif slope > 0:
            trend_direction = "上升"
        else:
            trend_direction = "下降"

        # 计算增长率
        first_value = values[0]
        last_value = values[-1]
        total_growth_rate = ((last_value - first_value) / first_value * 100) if first_value != 0 else 0
        
        # 计算平均增长率
        avg_growth_rate = total_growth_rate / (len(values) - 1) if len(values) > 1 else 0

        # 计算R²值（拟合优度）
        y_pred = np.polyval(coefficients, x)
        ss_res = np.sum((values - y_pred) ** 2)
        ss_tot = np.sum((values - np.mean(values)) ** 2)
        r_squared = 1 - (ss_res / ss_tot) if ss_tot != 0 else 0

        return {
            'trend_direction': trend_direction,
            'slope': round(float(slope), 6),
            'total_growth_rate': round(total_growth_rate, 2),
            'avg_growth_rate': round(avg_growth_rate, 2),
            'r_squared': round(float(r_squared), 4),
            'data_points': len(values),
            'start_value': round(float(first_value), 2),
            'end_value': round(float(last_value), 2),
            'interpretation': TrendAnalyzer._interpret_trend(trend_direction, total_growth_rate, r_squared)
        }

    @staticmethod
    def _interpret_trend(direction: str, growth_rate: float, r_squared: float) -> str:
        """解释趋势分析结果。"""
        interpretations = []
        
        interpretations.append(f"整体趋势：{direction}")
        
        if abs(growth_rate) > 50:
            interpretations.append(f"变化幅度较大（总增长率={growth_rate:.2f}%）")
        elif abs(growth_rate) > 10:
            interpretations.append(f"中等变化（总增长率={growth_rate:.2f}%）")
        else:
            interpretations.append(f"变化较小（总增长率={growth_rate:.2f}%）")
        
        if r_squared > 0.8:
            interpretations.append("趋势拟合度高，规律性强")
        elif r_squared > 0.5:
            interpretations.append("趋势拟合度中等")
        else:
            interpretations.append("趋势拟合度较低，波动较大")
        
        return "；".join(interpretations)

    @staticmethod
    def calculate_moving_average(df: pd.DataFrame, date_column: str, 
                                value_column: str, window: int = 7) -> Dict[str, Any]:
        """
        计算移动平均线。

        Args:
            df: DataFrame
            date_column: 日期列名
            value_column: 数值列名
            window: 窗口大小（天数）

        Returns:
            移动平均结果
        """
        if df is None or df.empty:
            return {'error': '数据为空'}

        df_copy = df.copy()
        df_copy[date_column] = pd.to_datetime(df_copy[date_column])
        df_copy = df_copy.sort_values(date_column)

        # 计算移动平均
        df_copy['ma'] = df_copy[value_column].rolling(window=window, min_periods=1).mean()

        result = {
            'window_size': window,
            'data': df_copy[[date_column, value_column, 'ma']].tail(20).to_dict('records'),
            'latest_ma': round(float(df_copy['ma'].iloc[-1]), 2) if len(df_copy) > 0 else None
        }

        return result

    @staticmethod
    def detect_seasonality(df: pd.DataFrame, date_column: str, 
                          value_column: str) -> Dict[str, Any]:
        """
        检测数据的季节性模式。

        Args:
            df: DataFrame
            date_column: 日期列名
            value_column: 数值列名

        Returns:
            季节性分析结果
        """
        if df is None or df.empty:
            return {'error': '数据为空'}

        df_copy = df.copy()
        df_copy[date_column] = pd.to_datetime(df_copy[date_column])
        
        # 提取月份
        df_copy['month'] = df_copy[date_column].dt.month
        
        # 按月统计
        monthly_stats = df_copy.groupby('month')[value_column].agg(['mean', 'sum', 'count']).round(2)
        
        # 检测峰值月份
        peak_month = monthly_stats['mean'].idxmax()
        trough_month = monthly_stats['mean'].idxmin()
        
        # 计算变异系数（衡量季节性强度）
        cv = monthly_stats['mean'].std() / monthly_stats['mean'].mean() if monthly_stats['mean'].mean() != 0 else 0
        
        seasonality_strength = "强" if cv > 0.3 else ("中等" if cv > 0.1 else "弱")

        return {
            'seasonality_strength': seasonality_strength,
            'coefficient_of_variation': round(float(cv), 4),
            'peak_month': int(peak_month),
            'trough_month': int(trough_month),
            'monthly_pattern': monthly_stats.to_dict('index'),
            'interpretation': f"季节性强度：{seasonality_strength}（CV={cv:.4f}），峰值月份={peak_month}月，谷值月份={trough_month}月"
        }

    @staticmethod
    def forecast_simple(df: pd.DataFrame, date_column: str, 
                       value_column: str, periods: int = 5) -> Dict[str, Any]:
        """
        简单预测（基于线性趋势外推）。

        Args:
            df: DataFrame
            date_column: 日期列名
            value_column: 数值列名
            periods: 预测期数

        Returns:
            预测结果
        """
        if df is None or df.empty:
            return {'error': '数据为空'}

        df_copy = df.copy()
        df_copy[date_column] = pd.to_datetime(df_copy[date_column])
        df_copy = df_copy.sort_values(date_column)

        values = df_copy[value_column].dropna().values
        
        if len(values) < 3:
            return {'error': '数据点不足'}

        # 线性回归
        x = np.arange(len(values))
        coefficients = np.polyfit(x, values, 1)
        
        # 预测未来值
        future_x = np.arange(len(values), len(values) + periods)
        forecasted_values = np.polyval(coefficients, future_x)
        
        # 生成预测日期
        last_date = df_copy[date_column].iloc[-1]
        date_range = pd.date_range(start=last_date, periods=periods + 1, freq='D')[1:]

        forecast_data = [
            {
                'date': str(date),
                'forecasted_value': round(float(val), 2)
            }
            for date, val in zip(date_range, forecasted_values)
        ]

        return {
            'method': 'linear_regression',
            'periods': periods,
            'forecast': forecast_data,
            'trend_slope': round(float(coefficients[0]), 6),
            'warning': '简单线性预测，仅适用于短期趋势，长期预测需考虑更多因素'
        }
