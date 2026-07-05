"""数据获取MCP工具。

提供fetch_data工具供LLM调用从各种数据源获取数据。
"""
import logging

from server.mcp_server import mcp
from datasource.factory import DataSourceFactory
from analyzer.data_extractor import DataExtractor

# 配置日志
logger = logging.getLogger(__name__)

# 全局数据源工厂
data_source_factory = DataSourceFactory()


@mcp.tool()
def fetch_data(source_url: str, source_type: str = 'auto', **kwargs) -> str:
    """
    从指定数据源获取结构化数据（网页/API/文件）。

    Args:
        source_url: 数据源URL或路径
            - 网页: https://example.com/page
            - API: https://api.example.com/data
            - 文件: /path/to/file.csv 或 data.xlsx
        source_type: 数据源类型（'webpage', 'api', 'file', 'auto'），默认'auto'自动检测
        **kwargs: 额外参数
            - table_index: 网页表格索引（仅网页类型）
            - method: HTTP方法（仅API类型）
            - headers: 请求头（仅API类型）
            - encoding: 文件编码（仅文件类型）

    Returns:
        格式化的数据摘要和关键指标
    """
    # ==================== 参数验证 ====================
    if not source_url or not source_url.strip():
        logger.warning("source_url为空")
        return "❌ 错误: 请提供有效的数据源URL"

    source_url = source_url.strip()
    
    # ==================== 选择适配器 ====================
    try:
        if source_type == 'auto':
            adapter = data_source_factory.auto_detect_adapter(source_url)
            detected_type = adapter.get_type()
            logger.info(f"自动检测到数据源类型: {detected_type}")
        else:
            adapter = data_source_factory.get_adapter(source_type)
            if adapter is None:
                return f"❌ 错误: 不支持的数据源类型 '{source_type}'，支持的类型: webpage, api, file"
            detected_type = source_type

    except Exception as e:
        logger.error(f"适配器选择失败: {e}", exc_info=True)
        return f"❌ 错误: 适配器选择失败 - {str(e)}"

    # ==================== 获取数据 ====================
    try:
        logger.info(f"正在从{detected_type}获取数据: {source_url}")
        result = adapter.fetch(source_url, **kwargs)

        if not result.success:
            return f"❌ 数据获取失败: {result.error_message}"

        # ==================== 提取关键信息 ====================
        import pandas as pd
        
        if isinstance(result.data, pd.DataFrame):
            # DataFrame数据处理
            df = result.data
            
            # 提取关键指标
            metrics = DataExtractor.extract_key_metrics(df)
            
            # 构建返回结果
            output_lines = [
                f"✅ 数据获取成功！",
                f"",
                f"📊 数据概览:",
                f"  - 来源类型: {result.source_type}",
                f"  - 数据来源: {source_url}",
                f"  - 行数: {metrics.get('row_count', 0)}",
                f"  - 列数: {metrics.get('column_count', 0)}",
                f"  - 字段: {', '.join(metrics.get('columns', []))}",
                f"",
            ]

            # 添加数值列统计
            if 'numeric_summary' in metrics:
                output_lines.append("📈 数值列统计:")
                for col, stats in metrics['numeric_summary'].items():
                    output_lines.append(f"  {col}:")
                    if 'mean' in stats:
                        output_lines.append(f"    均值: {stats['mean']:.2f}")
                        output_lines.append(f"    中位数: {stats['50%']:.2f}")
                        output_lines.append(f"    范围: [{stats['min']:.2f}, {stats['max']:.2f}]")
                output_lines.append("")

            # 添加缺失值信息
            missing = metrics.get('missing_values', {})
            if any(v > 0 for v in missing.values()):
                output_lines.append("⚠️ 缺失值:")
                for col, count in missing.items():
                    if count > 0:
                        output_lines.append(f"  - {col}: {count} 个缺失值")
                output_lines.append("")

            # 显示前5条数据预览
            output_lines.append("🔍 数据预览（前5行）:")
            preview_df = df.head(5)
            output_lines.append(preview_df.to_string(index=False))
            
            return "\n".join(output_lines)
        
        elif isinstance(result.data, dict):
            # 字典数据（API返回的非表格数据）
            import json
            
            output_lines = [
                f"✅ 数据获取成功！",
                f"",
                f"📊 数据概览:",
                f"  - 来源类型: {result.source_type}",
                f"  - 数据来源: {source_url}",
                f"  - 数据类型: {type(result.data).__name__}",
                f"",
                f"📄 数据内容:",
                json.dumps(result.data, ensure_ascii=False, indent=2)[:2000],  # 限制长度
            ]
            
            return "\n".join(output_lines)
        
        else:
            # 其他类型数据
            return f"✅ 数据获取成功！\n\n数据类型: {type(result.data).__name__}\n\n内容:\n{str(result.data)[:1000]}"

    except Exception as e:
        logger.error(f"数据获取过程出错: {e}", exc_info=True)
        return f"❌ 错误: 数据获取失败 - {str(e)}"
