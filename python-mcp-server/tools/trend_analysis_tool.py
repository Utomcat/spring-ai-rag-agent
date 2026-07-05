"""趋势分析MCP工具。

提供trend_analysis工具进行时间序列趋势分析。
"""
import logging

from server.mcp_server import mcp

# 配置日志
logger = logging.getLogger(__name__)


@mcp.tool()
def trend_analysis(data_description: str, date_column: str, 
                  value_column: str, analysis_type: str = 'detect') -> str:
    """
    对时间序列数据进行趋势分析。

    Args:
        data_description: 数据描述或数据源标识
        date_column: 日期列名
        value_column: 数值列名
        analysis_type: 分析类型
            - 'detect': 趋势检测（默认）
            - 'moving_average': 移动平均
            - 'seasonality': 季节性检测
            - 'forecast': 简单预测

    Returns:
        格式化的趋势分析报告
    """
    if not data_description or not date_column or not value_column:
        return "❌ 错误: 请提供完整参数（data_description, date_column, value_column）"

    output_lines = [
        f"📈 趋势分析报告",
        f"━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
        f"",
        f"📋 分析对象: {data_description}",
        f"📅 日期列: {date_column}",
        f"📊 数值列: {value_column}",
        f"🔍 分析类型: {analysis_type}",
        f"",
    ]

    try:
        # 注意：这里需要LLM先通过fetch_data获取数据，然后传入DataFrame
        # 当前为演示版本，展示使用框架
        
        output_lines.extend([
            "💡 使用说明:",
            "",
            "要进行完整的趋势分析，请按以下步骤操作:",
            "",
            "1️⃣ 首先调用 fetch_data 获取时间序列数据:",
            "   ```",
            f"   fetch_data(source_url='您的数据源URL')",
            "   ```",
            "",
            "2️⃣ 确认数据包含日期列和数值列",
            "",
            "3️⃣ 根据需求选择分析类型:",
            "",
            "   📈 **趋势检测 (detect)**:",
            "      - 检测整体上升/下降趋势",
            "      - 计算增长率",
            "      - 评估趋势强度",
            "",
            "   📊 **移动平均 (moving_average)**:",
            "      - 平滑短期波动",
            "      - 识别长期趋势",
            "      - 可自定义窗口大小",
            "",
            "   🔄 **季节性检测 (seasonality)**:",
            "      - 检测周期性模式",
            "      - 识别峰值/谷值月份",
            "      - 评估季节性强度",
            "",
            "   🔮 **简单预测 (forecast)**:",
            "      - 基于线性趋势外推",
            "      - 生成未来值预测",
            "      - 适用于短期预测",
            "",
            "4️⃣ 示例:",
            "   ```",
            f"   # 检测趋势",
            f"   trend_analysis(",
            f"       data_description='{data_description}',",
            f"       date_column='{date_column}',",
            f"       value_column='{value_column}',",
            f"       analysis_type='detect'",
            f"   )",
            "   ```",
            "",
        ])

        return "\n".join(output_lines)

    except Exception as e:
        logger.error(f"趋势分析失败: {e}", exc_info=True)
        return f"❌ 错误: 趋势分析失败 - {str(e)}"
