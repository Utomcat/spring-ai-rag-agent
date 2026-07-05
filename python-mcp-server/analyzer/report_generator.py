"""报告自动生成器模块。

根据分析结果自动生成结构化分析报告。
"""
import logging
from typing import Any, Dict, List, Optional
from datetime import datetime

# 配置日志
logger = logging.getLogger(__name__)


class ReportGenerator:
    """报告自动生成器 - 将分析结果格式化为结构化报告。"""

    @staticmethod
    def generate_analysis_report(analysis_results: Dict[str, Any], 
                                data_source: str = "未知",
                                report_title: str = "数据分析报告") -> str:
        """
        生成综合分析报告。

        Args:
            analysis_results: 包含各类分析结果的字典
                - basic_stats: 基础统计
                - trend: 趋势分析
                - distribution: 分布分析
                - patterns: 模式检测
            data_source: 数据来源描述
            report_title: 报告标题

        Returns:
            格式化的报告字符串
        """
        lines = []
        
        # ==================== 报告头部 ====================
        lines.append("=" * 70)
        lines.append(f"📊 {report_title}")
        lines.append("=" * 70)
        lines.append(f"生成时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        lines.append(f"数据来源: {data_source}")
        lines.append("")

        # ==================== 数据概览 ====================
        if 'basic_stats' in analysis_results:
            lines.extend(ReportGenerator._section_basic_overview(analysis_results['basic_stats']))
        
        # ==================== 趋势分析 ====================
        if 'trend' in analysis_results:
            lines.extend(ReportGenerator._section_trend_analysis(analysis_results['trend']))
        
        # ==================== 分布分析 ====================
        if 'distribution' in analysis_results:
            lines.extend(ReportGenerator._section_distribution(analysis_results['distribution']))
        
        # ==================== 模式检测 ====================
        if 'patterns' in analysis_results:
            lines.extend(ReportGenerator._section_patterns(analysis_results['patterns']))
        
        # ==================== 建议与洞察 ====================
        lines.extend(ReportGenerator._section_insights(analysis_results))
        
        # ==================== 报告尾部 ====================
        lines.append("")
        lines.append("=" * 70)
        lines.append("报告结束")
        lines.append("=" * 70)

        return "\n".join(lines)

    @staticmethod
    def _section_basic_overview(basic_stats: Dict) -> List[str]:
        """生成数据概览部分。"""
        lines = []
        lines.append("📋 一、数据概览")
        lines.append("-" * 70)
        
        if 'row_count' in basic_stats:
            lines.append(f"  • 记录数: {basic_stats['row_count']}")
        if 'column_count' in basic_stats:
            lines.append(f"  • 字段数: {basic_stats['column_count']}")
        if 'columns' in basic_stats:
            lines.append(f"  • 字段列表: {', '.join(basic_stats['columns'][:10])}")
            if len(basic_stats['columns']) > 10:
                lines.append(f"    ... 共{len(basic_stats['columns'])}个字段")
        
        if 'missing_values' in basic_stats:
            missing = {k: v for k, v in basic_stats['missing_values'].items() if v > 0}
            if missing:
                lines.append(f"  ⚠️ 缺失值:")
                for col, count in list(missing.items())[:5]:
                    lines.append(f"    - {col}: {count}个")
        
        lines.append("")
        return lines

    @staticmethod
    def _section_trend_analysis(trend: Dict) -> List[str]:
        """生成趋势分析部分。"""
        lines = []
        lines.append("📈 二、趋势分析")
        lines.append("-" * 70)
        
        if 'trend_direction' in trend:
            direction_icon = "↑" if trend['trend_direction'] == "上升" else ("↓" if trend['trend_direction'] == "下降" else "→")
            lines.append(f"  • 整体趋势: {direction_icon} {trend['trend_direction']}")
        
        if 'total_growth_rate' in trend:
            lines.append(f"  • 总增长率: {trend['total_growth_rate']:.2f}%")
        
        if 'avg_growth_rate' in trend:
            lines.append(f"  • 平均增长率: {trend['avg_growth_rate']:.2f}%")
        
        if 'r_squared' in trend:
            lines.append(f"  • 拟合优度(R²): {trend['r_squared']:.4f}")
        
        if 'interpretation' in trend:
            lines.append(f"  💡 解读: {trend['interpretation']}")
        
        lines.append("")
        return lines

    @staticmethod
    def _section_distribution(distribution: Dict) -> List[str]:
        """生成分布分析部分。"""
        lines = []
        lines.append("📊 三、分布分析")
        lines.append("-" * 70)
        
        if isinstance(distribution, dict):
            for col, dist_info in distribution.items():
                if isinstance(dist_info, dict) and 'distribution_type' in dist_info:
                    lines.append(f"  • {col}:")
                    lines.append(f"    - 分布类型: {dist_info['distribution_type']}")
                    if 'skewness' in dist_info:
                        lines.append(f"    - 偏度: {dist_info['skewness']:.4f}")
                    if 'interpretation' in dist_info:
                        lines.append(f"    - 解读: {dist_info['interpretation']}")
        
        lines.append("")
        return lines

    @staticmethod
    def _section_patterns(patterns: Dict) -> List[str]:
        """生成模式检测部分。"""
        lines = []
        lines.append("🔍 四、模式检测")
        lines.append("-" * 70)
        
        # 趋势
        if 'trends' in patterns and patterns['trends']:
            lines.append("  📈 检测到的趋势:")
            for trend in patterns['trends']:
                lines.append(f"    - {trend.get('column', '未知')}: {trend.get('type', '未知')} ({trend.get('change_rate', 'N/A')})")
        
        # 异常值
        if 'outliers' in patterns and patterns['outliers']:
            lines.append("  ⚠️ 检测到的异常值:")
            for outlier in patterns['outliers'][:3]:
                lines.append(f"    - {outlier.get('column', '未知')}: {outlier.get('count', 0)}个异常值")
        
        # 相关性
        if 'correlations' in patterns and patterns['correlations']:
            lines.append("  🔗 强相关关系:")
            for corr in patterns['correlations'][:3]:
                lines.append(f"    - {corr.get('column1', '?')} ↔ {corr.get('column2', '?')}: {corr.get('correlation', 'N/A')} ({corr.get('strength', 'N/A')})")
        
        lines.append("")
        return lines

    @staticmethod
    def _section_insights(analysis_results: Dict) -> List[str]:
        """生成建议与洞察部分。"""
        lines = []
        lines.append("💡 五、建议与洞察")
        lines.append("-" * 70)
        
        insights = []
        
        # 基于趋势的洞察
        if 'trend' in analysis_results:
            trend = analysis_results['trend']
            if trend.get('r_squared', 0) > 0.8:
                insights.append("✓ 数据呈现明显的规律性趋势，可考虑建立预测模型")
            elif trend.get('total_growth_rate', 0) > 20:
                insights.append("⚠ 数据增长较快，建议关注增长驱动因素")
        
        # 基于缺失值的洞察
        if 'basic_stats' in analysis_results:
            missing = analysis_results['basic_stats'].get('missing_values', {})
            total_missing = sum(missing.values())
            if total_missing > 0:
                insights.append(f"⚠ 存在{total_missing}个缺失值，建议进行数据清洗或插补")
        
        # 基于模式的洞察
        if 'patterns' in analysis_results:
            patterns = analysis_results['patterns']
            if patterns.get('outliers'):
                insights.append(f"⚠ 检测到{len(patterns['outliers'])}个字段存在异常值，需进一步核查")
            if patterns.get('correlations'):
                insights.append("✓ 发现强相关关系，可深入分析因果关系")
        
        if not insights:
            insights.append("数据质量良好，未发现明显异常")
        
        for insight in insights:
            lines.append(f"  {insight}")
        
        lines.append("")
        return lines

    @staticmethod
    def generate_executive_summary(analysis_results: Dict[str, Any]) -> str:
        """
        生成执行摘要（简短版报告）。

        Args:
            analysis_results: 分析结果字典

        Returns:
            执行摘要字符串
        """
        lines = []
        lines.append("📋 执行摘要")
        lines.append("=" * 50)
        
        # 关键指标
        if 'basic_stats' in analysis_results:
            stats = analysis_results['basic_stats']
            lines.append(f"• 数据规模: {stats.get('row_count', 0)}条记录 × {stats.get('column_count', 0)}个字段")
        
        # 核心发现
        findings = []
        if 'trend' in analysis_results:
            trend = analysis_results['trend']
            findings.append(f"趋势: {trend.get('trend_direction', '未知')} (增长率:{trend.get('total_growth_rate', 0):.2f}%)")
        
        if findings:
            lines.append("• 核心发现:")
            for finding in findings:
                lines.append(f"  - {finding}")
        
        lines.append("")
        return "\n".join(lines)
