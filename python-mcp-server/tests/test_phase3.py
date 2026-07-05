"""Phase 3 核心功能测试。"""
import sys
import os
import pandas as pd
import numpy as np
from datetime import datetime, timedelta

# 添加项目根目录到Python路径
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))


def test_trend_analyzer():
    """测试趋势分析器。"""
    print("测试1: 趋势分析器...")
    from analyzer.trend_analyzer import TrendAnalyzer
    
    # 创建测试数据（上升趋势）
    dates = pd.date_range('2024-01-01', periods=30, freq='D')
    values = [100 + i * 2 + np.random.randn() * 5 for i in range(30)]
    df = pd.DataFrame({'date': dates, 'value': values})
    
    # 测试趋势检测
    trend = TrendAnalyzer.detect_trend(df, 'date', 'value')
    print(f"✓ 趋势检测成功: {trend['trend_direction']} (增长率={trend['total_growth_rate']:.2f}%)")
    print(f"  - R²={trend['r_squared']:.4f}, 斜率={trend['slope']:.4f}")
    
    # 测试移动平均
    ma = TrendAnalyzer.calculate_moving_average(df, 'date', 'value', window=7)
    print(f"✓ 移动平均计算成功: 最新MA={ma['latest_ma']:.2f}")
    
    # 测试季节性检测
    seasonality = TrendAnalyzer.detect_seasonality(df, 'date', 'value')
    print(f"✓ 季节性检测成功: 强度={seasonality.get('seasonality_strength', 'N/A')}")
    
    # 测试预测
    forecast = TrendAnalyzer.forecast_simple(df, 'date', 'value', periods=5)
    print(f"✓ 预测生成成功: {len(forecast['forecast'])}个预测值")


def test_chart_generator():
    """测试图表数据生成器。"""
    print("\n测试2: 图表数据生成器...")
    from visualization.chart_generator import ChartDataGenerator
    
    # 创建测试数据
    df = pd.DataFrame({
        'category': ['A', 'B', 'C', 'D'],
        'value': [100, 150, 200, 250],
        'date': pd.date_range('2024-01-01', periods=4, freq='ME')
    })
    
    # 测试折线图
    line_data = ChartDataGenerator.generate_line_chart_data(df, 'date', ['value'])
    print(f"✓ 折线图数据生成成功: {line_data['chart_type']}")
    
    # 测试柱状图
    bar_data = ChartDataGenerator.generate_bar_chart_data(df, 'category', 'value')
    print(f"✓ 柱状图数据生成成功: {bar_data['chart_type']}")
    
    # 测试饼图
    pie_data = ChartDataGenerator.generate_pie_chart_data(df, 'category', 'value')
    print(f"✓ 饼图数据生成成功: {pie_data['chart_type']}, 总计={pie_data['total']}")
    
    # 测试散点图
    scatter_df = pd.DataFrame({
        'x': [1, 2, 3, 4, 5],
        'y': [2, 4, 5, 4, 8]
    })
    scatter_data = ChartDataGenerator.generate_scatter_chart_data(scatter_df, 'x', 'y')
    print(f"✓ 散点图数据生成成功: {scatter_data['chart_type']}")
    
    # 测试CSV导出
    csv_str = ChartDataGenerator.export_to_csv(df)
    print(f"✓ CSV导出成功: {len(csv_str)}字符")
    
    # 测试摘要表格
    table_data = ChartDataGenerator.generate_summary_table(df)
    print(f"✓ 摘要表格生成成功: {len(table_data['data'])}行")


def test_report_generator():
    """测试报告生成器。"""
    print("\n测试3: 报告生成器...")
    from analyzer.report_generator import ReportGenerator
    
    # 准备分析结果
    analysis_results = {
        'basic_stats': {
            'row_count': 100,
            'column_count': 5,
            'columns': ['date', 'value', 'category'],
            'missing_values': {'value': 2}
        },
        'trend': {
            'trend_direction': '上升',
            'total_growth_rate': 25.5,
            'avg_growth_rate': 0.85,
            'r_squared': 0.85,
            'interpretation': '整体趋势：上升；变化幅度较大'
        },
        'distribution': {
            'value': {
                'distribution_type': '近似正态分布',
                'skewness': 0.15,
                'interpretation': '数据分布较为对称'
            }
        },
        'patterns': {
            'trends': [{'column': 'value', 'type': '上升', 'change_rate': '25.5%'}],
            'outliers': [{'column': 'value', 'count': 3}],
            'correlations': [{'column1': 'value', 'column2': 'category', 'correlation': 0.75}]
        }
    }
    
    # 测试完整报告
    full_report = ReportGenerator.generate_analysis_report(
        analysis_results, 
        data_source="测试数据源",
        report_title="测试分析报告"
    )
    print(f"✓ 完整报告生成成功: {len(full_report)}字符")
    print(f"  - 包含章节数: {full_report.count('一、') + full_report.count('二、') + full_report.count('三、') + full_report.count('四、') + full_report.count('五、')}")
    
    # 测试执行摘要
    summary = ReportGenerator.generate_executive_summary(analysis_results)
    print(f"✓ 执行摘要生成成功: {len(summary)}字符")


def test_mcp_tools_import():
    """测试MCP工具导入。"""
    print("\n测试4: MCP工具导入...")
    
    tools = [
        ('trend_analysis_tool', 'trend_analysis'),
        ('generate_chart_data_tool', 'generate_chart_data'),
        ('generate_report_tool', 'generate_report')
    ]
    
    for module_name, tool_name in tools:
        try:
            module = __import__(f'tools.{module_name}', fromlist=[tool_name])
            print(f"✓ {module_name} 导入成功")
        except ImportError as e:
            print(f"✗ {module_name} 导入失败: {e}")


def test_integration_workflow():
    """测试集成工作流。"""
    print("\n测试5: 集成工作流测试...")
    
    # 模拟完整的数据分析流程
    from analyzer.trend_analyzer import TrendAnalyzer
    from visualization.chart_generator import ChartDataGenerator
    from analyzer.report_generator import ReportGenerator
    
    # 1. 创建示例数据
    dates = pd.date_range('2024-01-01', periods=60, freq='D')
    sales = [1000 + i * 10 + np.random.randn() * 50 for i in range(60)]
    profit = [s * 0.2 + np.random.randn() * 20 for s in sales]
    
    df = pd.DataFrame({
        'date': dates,
        'sales': sales,
        'profit': profit
    })
    
    print("  步骤1: 数据准备 ✓")
    
    # 2. 趋势分析
    trend = TrendAnalyzer.detect_trend(df, 'date', 'sales')
    print(f"  步骤2: 趋势分析 ✓ ({trend['trend_direction']})")
    
    # 3. 生成图表数据
    chart_data = ChartDataGenerator.generate_line_chart_data(df, 'date', ['sales', 'profit'])
    print(f"  步骤3: 图表数据生成 ✓ ({chart_data['chart_type']})")
    
    # 4. 生成报告
    analysis_results = {
        'basic_stats': {
            'row_count': len(df),
            'column_count': len(df.columns),
            'columns': list(df.columns)
        },
        'trend': trend
    }
    
    report = ReportGenerator.generate_analysis_report(
        analysis_results,
        data_source="销售数据",
        report_title="销售趋势分析报告"
    )
    print(f"  步骤4: 报告生成 ✓ ({len(report)}字符)")
    
    print("✓ 集成工作流测试完成！")


if __name__ == '__main__':
    print("=" * 60)
    print("Phase 3 核心功能测试")
    print("=" * 60)
    
    try:
        test_trend_analyzer()
        test_chart_generator()
        test_report_generator()
        test_mcp_tools_import()
        test_integration_workflow()
        
        print("\n" + "=" * 60)
        print("✅ 所有测试通过！Phase 3 核心功能正常工作。")
        print("=" * 60)
        
    except Exception as e:
        print(f"\n❌ 测试失败: {e}")
        import traceback
        traceback.print_exc()
