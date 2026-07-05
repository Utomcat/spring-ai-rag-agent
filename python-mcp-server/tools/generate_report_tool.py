"""报告生成MCP工具。

提供generate_report工具自动生成分析报告。
"""
import logging
from typing import Optional

from server.mcp_server import mcp
from analyzer.report_generator import ReportGenerator

# 配置日志
logger = logging.getLogger(__name__)


@mcp.tool()
def generate_report(data_description: str, report_type: str = 'full',
                   title: Optional[str] = None) -> str:
    """
    根据已获取的数据和分析结果生成结构化报告。

    Args:
        data_description: 数据描述或数据源标识
        report_type: 报告类型
            - 'full': 完整报告（默认）
            - 'summary': 执行摘要（简短版）
            - 'trend': 趋势分析报告
            - 'statistical': 统计分析报告
        title: 自定义报告标题（可选）

    Returns:
        格式化的分析报告
    """
    if not data_description:
        return "❌ 错误: 请提供data_description参数"

    report_title = title or f"{data_description}分析报告"

    output_lines = [
        f"📄 报告生成器",
        f"━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
        f"",
        f"📋 数据源: {data_description}",
        f"📊 报告类型: {report_type}",
        f"📝 报告标题: {report_title}",
        f"",
    ]

    try:
        # 根据不同报告类型给出说明
        type_descriptions = {
            'full': {
                'name': '完整报告',
                'sections': [
                    '数据概览',
                    '基础统计分析',
                    '趋势分析',
                    '分布分析',
                    '模式检测（趋势、异常值、相关性）',
                    '建议与洞察'
                ],
                'length': '详细（约1000-2000字）'
            },
            'summary': {
                'name': '执行摘要',
                'sections': [
                    '关键指标',
                    '核心发现',
                    '主要建议'
                ],
                'length': '简洁（约200-500字）'
            },
            'trend': {
                'name': '趋势分析报告',
                'sections': [
                    '趋势方向',
                    '增长率分析',
                    '移动平均',
                    '季节性检测',
                    '预测结果'
                ],
                'length': '中等（约500-1000字）'
            },
            'statistical': {
                'name': '统计分析报告',
                'sections': [
                    '描述性统计',
                    '分布特征',
                    '分组对比',
                    '相关性分析'
                ],
                'length': '中等（约500-1000字）'
            }
        }

        desc = type_descriptions.get(report_type, type_descriptions['full'])
        
        output_lines.extend([
            f"💡 报告说明:",
            "",
            f"**{desc['name']}**",
            "",
            f"📑 包含章节:",
        ])
        
        for section in desc['sections']:
            output_lines.append(f"  ✓ {section}")
        
        output_lines.extend([
            "",
            f"📏 报告长度: {desc['length']}",
            "",
            "📝 调用示例:",
            "```python",
        ])

        if report_type == 'full':
            output_lines.extend([
                f"# 生成完整报告",
                f"generate_report(",
                f"    data_description='{data_description}',",
                f"    report_type='full',",
                f"    title='{report_title}'",
                f")",
            ])
        elif report_type == 'summary':
            output_lines.extend([
                f"# 生成执行摘要",
                f"generate_report(",
                f"    data_description='{data_description}',",
                f"    report_type='summary'",
                f")",
            ])
        else:
            output_lines.extend([
                f"# 生成{desc['name']}",
                f"generate_report(",
                f"    data_description='{data_description}',",
                f"    report_type='{report_type}'",
                f")",
            ])

        output_lines.extend([
            "```",
            "",
            "🎯 使用建议:",
            "",
            "完整工作流程:",
            "  1. fetch_data() → 获取数据",
            "  2. analyze_data() → 基础分析",
            "  3. trend_analysis() → 趋势分析（可选）",
            "  4. generate_report() → 生成报告",
            "",
            "输出格式:",
            "  - Markdown格式，结构清晰",
            "  - 包含emoji图标，易于阅读",
            "  - 可直接用于演示或分享",
            "",
        ])

        return "\n".join(output_lines)

    except Exception as e:
        logger.error(f"报告生成失败: {e}", exc_info=True)
        return f"❌ 错误: 报告生成失败 - {str(e)}"
