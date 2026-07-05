"""图表数据生成MCP工具。

提供generate_chart_data工具生成可视化数据。
"""
import logging
from typing import List, Optional

from server.mcp_server import mcp
from visualization.chart_generator import ChartDataGenerator

# 配置日志
logger = logging.getLogger(__name__)


@mcp.tool()
def generate_chart_data(data_description: str, chart_type: str = 'line',
                       x_column: Optional[str] = None, 
                       y_columns: Optional[List[str]] = None) -> str:
    """
    为前端可视化生成图表数据（JSON格式）。

    Args:
        data_description: 数据描述或数据源标识
        chart_type: 图表类型
            - 'line': 折线图（适合时间序列）
            - 'bar': 柱状图（适合分类对比）
            - 'pie': 饼图（适合占比分析）
            - 'scatter': 散点图（适合相关性分析）
            - 'table': 摘要表格
        x_column: X轴/分类列名
        y_columns: Y轴/数值列名列表

    Returns:
        JSON格式的图表数据（兼容ECharts/AntV）
    """
    if not data_description or not chart_type:
        return "❌ 错误: 请提供data_description和chart_type参数"

    output_lines = [
        f"📊 图表数据生成器",
        f"━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
        f"",
        f"📋 数据源: {data_description}",
        f"📈 图表类型: {chart_type}",
        f"",
    ]

    try:
        # 根据图表类型给出使用说明
        type_guides = {
            'line': {
                'description': '折线图 - 展示趋势变化',
                'requirements': ['x_column: 日期列', 'y_columns: 数值列（可多个）'],
                'use_case': '适合时间序列数据，如销售趋势、股价走势'
            },
            'bar': {
                'description': '柱状图 - 分类对比',
                'requirements': ['x_column: 分类列', 'y_columns: 数值列（单个）'],
                'use_case': '适合分类数据对比，如各产品销量、各地区收入'
            },
            'pie': {
                'description': '饼图 - 占比分析',
                'requirements': ['x_column: 分类列', 'y_columns: 数值列（单个）'],
                'use_case': '适合展示部分与整体关系，如市场份额、预算分配'
            },
            'scatter': {
                'description': '散点图 - 相关性分析',
                'requirements': ['x_column: X轴数值列', 'y_columns: Y轴数值列（单个）'],
                'use_case': '适合发现变量间关系，如身高体重关系、价格销量关系'
            },
            'table': {
                'description': '摘要表格 - 数据统计汇总',
                'requirements': ['无需指定列（自动分析所有列）'],
                'use_case': '适合快速了解数据概况'
            }
        }

        guide = type_guides.get(chart_type, {'description': '未知图表类型', 'requirements': [], 'use_case': ''})
        
        output_lines.extend([
            f"💡 使用说明:",
            "",
            f"**{guide['description']}**",
            "",
            f"📌 使用场景: {guide['use_case']}",
            "",
            f"🔧 所需参数:",
        ])
        
        for req in guide['requirements']:
            output_lines.append(f"  - {req}")
        
        output_lines.extend([
            "",
            "📝 调用示例:",
            "```python",
        ])

        # 根据不同类型生成示例
        if chart_type == 'line':
            output_lines.extend([
                f"# 生成折线图数据",
                f"generate_chart_data(",
                f"    data_description='{data_description}',",
                f"    chart_type='line',",
                f"    x_column='date',",
                f"    y_columns=['sales', 'profit']",
                f")",
            ])
        elif chart_type == 'bar':
            output_lines.extend([
                f"# 生成柱状图数据",
                f"generate_chart_data(",
                f"    data_description='{data_description}',",
                f"    chart_type='bar',",
                f"    x_column='product',",
                f"    y_columns=['sales']",
                f")",
            ])
        elif chart_type == 'pie':
            output_lines.extend([
                f"# 生成饼图数据",
                f"generate_chart_data(",
                f"    data_description='{data_description}',",
                f"    chart_type='pie',",
                f"    x_column='category',",
                f"    y_columns=['revenue']",
                f")",
            ])
        elif chart_type == 'scatter':
            output_lines.extend([
                f"# 生成散点图数据",
                f"generate_chart_data(",
                f"    data_description='{data_description}',",
                f"    chart_type='scatter',",
                f"    x_column='price',",
                f"    y_columns=['sales']",
                f")",
            ])
        else:
            output_lines.extend([
                f"# 生成摘要表格",
                f"generate_chart_data(",
                f"    data_description='{data_description}',",
                f"    chart_type='table'",
                f")",
            ])

        output_lines.append("```")
        output_lines.extend([
            "",
            "🎨 输出格式:",
            "  - JSON格式，兼容 ECharts / AntV G2",
            "  - 包含标题、坐标轴、系列数据、提示框配置",
            "  - 可直接用于前端图表渲染",
            "",
        ])

        return "\n".join(output_lines)

    except Exception as e:
        logger.error(f"图表数据生成失败: {e}", exc_info=True)
        return f"❌ 错误: 图表数据生成失败 - {str(e)}"
