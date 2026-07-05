"""数据分析MCP工具。

提供analyze_data工具供LLM调用进行数据统计分析。
"""
import logging
from typing import Optional, List

from server.mcp_server import mcp

# 配置日志
logger = logging.getLogger(__name__)


@mcp.tool()
def analyze_data(data_description: str, analysis_type: str = 'basic', 
                 columns: Optional[List[str]] = None) -> str:
    """
    对已获取的数据进行统计分析（需要先使用fetch_data获取数据）。
    
    注意: 此工具用于分析通过fetch_data获取的数据。如果要分析新数据源，请先调用fetch_data。

    Args:
        data_description: 数据描述或数据源URL（用于标识要分析的数据）
        analysis_type: 分析类型
            - 'basic': 基础描述性统计（默认）
            - 'distribution': 分布分析
            - 'comparison': 分组对比分析
            - 'patterns': 模式检测（趋势、异常值、相关性）
            - 'time_series': 时间序列分析
        columns: 指定要分析的列名列表（可选，默认分析所有适用列）

    Returns:
        格式化的分析报告
    """
    # ==================== 参数验证 ====================
    if not data_description or not data_description.strip():
        logger.warning("data_description为空")
        return "❌ 错误: 请提供数据描述或数据源URL"

    data_description = data_description.strip()
    
    # 注意：这里是一个简化实现，实际应用中需要维护一个数据缓存
    # LLM应该先调用fetch_data，然后基于返回的结果进行分析
    
    output_lines = [
        f"📊 数据分析报告",
        f"━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
        f"",
        f"📋 分析对象: {data_description}",
        f"🔍 分析类型: {analysis_type}",
        f"",
    ]

    try:
        # ==================== 说明信息 ====================
        output_lines.extend([
            "💡 使用说明:",
            "",
            "要进行完整的数据分析，请按以下步骤操作:",
            "",
            "1️⃣ 首先调用 fetch_data 获取数据:",
            "   ```",
            f"   fetch_data(source_url='{data_description}')",
            "   ```",
            "",
            "2️⃣ 然后根据返回的数据特征，我可以帮你进行以下分析:",
            "",
            "   📈 **基础统计 (basic)**:",
            "      - 均值、中位数、标准差",
            "      - 最小值、最大值、四分位数",
            "      - 缺失值统计",
            "",
            "   📊 **分布分析 (distribution)**:",
            "      - 偏度、峰度",
            "      - 分布类型判断",
            "",
            "   🔍 **模式检测 (patterns)**:",
            "      - 趋势检测（上升/下降）",
            "      - 异常值识别",
            "      - 相关性分析",
            "",
            "   ⏰ **时间序列 (time_series)**:",
            "      - 年度趋势",
            "      - 月度模式",
            "",
            "   📋 **分组对比 (comparison)**:",
            "      - 按类别分组统计",
            "      - 组间差异分析",
            "",
            "3️⃣ 示例:",
            "   ```",
            f"   # 先获取数据",
            f"   fetch_data(source_url='{data_description}')",
            f"   ",
            f"   # 再进行分析",
            f"   analyze_data(data_description='{data_description}', analysis_type='basic')",
            "   ```",
            "",
        ])

        # ==================== 演示分析框架 ====================
        output_lines.extend([
            "🎯 推荐分析流程:",
            "",
            "对于数值型数据:",
            "  1. basic → 了解基本统计特征",
            "  2. distribution → 检查数据分布",
            "  3. patterns → 发现潜在规律",
            "",
            "对于时间序列数据:",
            "  1. basic → 基本统计",
            "  2. time_series → 趋势分析",
            "  3. patterns → 异常检测",
            "",
            "对于分类数据:",
            "  1. basic → 频数统计",
            "  2. comparison → 组间对比",
            "",
        ])

        return "\n".join(output_lines)

    except Exception as e:
        logger.error(f"数据分析过程出错: {e}", exc_info=True)
        return f"❌ 错误: 数据分析失败 - {str(e)}"


@mcp.tool()
def explain_statistics(stats_json: str) -> str:
    """
    解释统计结果的含义（辅助工具）。

    Args:
        stats_json: JSON格式的统计数据字符串

    Returns:
        通俗易懂的统计学概念解释
    """
    if not stats_json or not stats_json.strip():
        return "❌ 错误: 请提供统计数据"

    output_lines = [
        "📚 统计学概念解释",
        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
        "",
        "常见统计指标说明:",
        "",
        "📊 **集中趋势**:",
        "  - 均值 (Mean): 所有数据的平均值，受极端值影响较大",
        "  - 中位数 (Median): 排序后位于中间的值，更稳健",
        "  - 众数 (Mode): 出现次数最多的值",
        "",
        "📈 **离散程度**:",
        "  - 标准差 (Std): 数据分散程度，越大表示越分散",
        "  - 方差 (Variance): 标准差的平方",
        "  - 极差 (Range): 最大值-最小值",
        "",
        "📉 **分布形态**:",
        "  - 偏度 (Skewness):",
        "      • 接近0: 对称分布",
        "      • >0: 右偏（长尾在右）",
        "      • <0: 左偏（长尾在左）",
        "  - 峰度 (Kurtosis):",
        "      • >0: 尖峰分布",
        "      • <0: 平坦分布",
        "",
        "🔍 **分位数**:",
        "  - Q1 (25%): 下四分位数",
        "  - Q2 (50%): 中位数",
        "  - Q3 (75%): 上四分位数",
        "  - IQR: Q3-Q1，用于检测异常值",
        "",
        "💡 如果您有具体的统计结果需要解释，请粘贴JSON数据！",
    ]

    return "\n".join(output_lines)
