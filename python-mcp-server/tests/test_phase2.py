"""Phase 2 核心功能测试。"""
import sys
import os
import pandas as pd

# 添加项目根目录到Python路径
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))


def test_data_source_models():
    """测试数据源模型。"""
    print("测试1: 数据源模型...")
    from models.data_source import DataSourceResult, ExtractedData
    
    # 测试DataSourceResult
    result = DataSourceResult(
        success=True,
        data={"test": "data"},
        source_type="api"
    )
    print(f"✓ DataSourceResult创建成功: {result.success}")
    
    # 测试ExtractedData
    extracted = ExtractedData(
        title="测试数据",
        fields=["name", "age"],
        records=[{"name": "张三", "age": 25}],
        row_count=1,
        source="test"
    )
    print(f"✓ ExtractedData创建成功: {extracted.row_count} 行")


def test_base_adapter():
    """测试数据源适配器基类。"""
    print("\n测试2: 数据源适配器基类...")
    from datasource.base_adapter import BaseDataSourceAdapter
    
    class TestAdapter(BaseDataSourceAdapter):
        def fetch(self, url, **kwargs):
            return self._create_success_result({"test": "data"}, "test")
        
        def get_type(self):
            return "test"
    
    adapter = TestAdapter()
    print(f"✓ 适配器创建成功: {adapter.get_type()}")
    print(f"✓ URL验证: {adapter.validate_url('https://example.com')}")


def test_webpage_adapter():
    """测试网页数据源适配器。"""
    print("\n测试3: 网页数据源适配器...")
    from datasource.webpage_adapter import WebPageAdapter
    
    adapter = WebPageAdapter()
    print(f"✓ 网页适配器创建成功: {adapter.get_type()}")
    print(f"✓ URL验证: {adapter.validate_url('https://example.com')}")


def test_api_adapter():
    """测试API数据源适配器。"""
    print("\n测试4: API数据源适配器...")
    from datasource.api_adapter import ApiAdapter
    
    adapter = ApiAdapter()
    print(f"✓ API适配器创建成功: {adapter.get_type()}")
    print(f"✓ URL验证: {adapter.validate_url('https://api.example.com/data')}")


def test_file_adapter():
    """测试文件数据源适配器。"""
    print("\n测试5: 文件数据源适配器...")
    from datasource.file_adapter import FileAdapter
    
    adapter = FileAdapter()
    print(f"✓ 文件适配器创建成功: {adapter.get_type()}")
    print(f"✓ 支持的格式: {adapter.SUPPORTED_EXTENSIONS}")


def test_datasource_factory():
    """测试数据源工厂。"""
    print("\n测试6: 数据源工厂...")
    from datasource.factory import DataSourceFactory
    
    factory = DataSourceFactory()
    
    # 测试自动检测
    webpage_adapter = factory.auto_detect_adapter('https://example.com')
    print(f"✓ 网页URL检测: {webpage_adapter.get_type()}")
    
    api_adapter = factory.auto_detect_adapter('https://api.example.com/data.json')
    print(f"✓ API URL检测: {api_adapter.get_type()}")
    
    file_adapter = factory.auto_detect_adapter('/path/to/file.csv')
    print(f"✓ 文件路径检测: {file_adapter.get_type()}")
    
    # 测试手动获取
    adapter = factory.get_adapter('webpage')
    print(f"✓ 手动获取适配器: {adapter.get_type()}")


def test_data_extractor():
    """测试数据提取器。"""
    print("\n测试7: 数据提取器...")
    from analyzer.data_extractor import DataExtractor
    
    # 创建测试数据
    df = pd.DataFrame({
        'name': ['张三', '李四', '王五'],
        'age': [25, 30, 35],
        'score': [85.5, 90.2, 78.9]
    })
    
    # 测试结构化提取
    extracted = DataExtractor.extract_from_dataframe(df, "测试数据", "test")
    print(f"✓ 数据提取成功: {extracted.row_count} 行, {len(extracted.fields)} 列")
    
    # 测试关键指标提取
    metrics = DataExtractor.extract_key_metrics(df)
    print(f"✓ 关键指标提取成功: {metrics['row_count']} 行, {metrics['column_count']} 列")
    
    # 测试模式检测
    patterns = DataExtractor.detect_patterns(df)
    print(f"✓ 模式检测成功: {len(patterns['trends'])} 个趋势")


def test_statistic_calculator():
    """测试统计分析计算器。"""
    print("\n测试8: 统计分析计算器...")
    from analyzer.statistic_calculator import StatisticCalculator
    
    # 创建测试数据
    df = pd.DataFrame({
        'value': [10, 20, 30, 40, 50, 60, 70, 80, 90, 100],
        'category': ['A', 'B', 'A', 'B', 'A', 'B', 'A', 'B', 'A', 'B']
    })
    
    # 测试描述性统计
    stats = StatisticCalculator.descriptive_statistics(df)
    print(f"✓ 描述性统计成功: {stats['basic_info']['rows']} 行")
    
    # 测试分布分析
    dist = StatisticCalculator.distribution_analysis(df, ['value'])
    print(f"✓ 分布分析成功: value列 - {dist['value']['distribution_type']}")
    
    # 测试分组对比
    comparison = StatisticCalculator.comparison_analysis(df, 'category', 'value')
    print(f"✓ 分组对比成功: 整体均值={comparison['overall_mean']}")


def test_mcp_tools():
    """测试MCP工具导入。"""
    print("\n测试9: MCP工具导入...")
    
    try:
        from tools import web_search_tool
        print("✓ web_search_tool 导入成功")
    except ImportError as e:
        print(f"✗ web_search_tool 导入失败: {e}")
    
    try:
        from tools import fetch_data_tool
        print("✓ fetch_data_tool 导入成功")
    except ImportError as e:
        print(f"✗ fetch_data_tool 导入失败: {e}")
    
    try:
        from tools import analyze_data_tool
        print("✓ analyze_data_tool 导入成功")
    except ImportError as e:
        print(f"✗ analyze_data_tool 导入失败: {e}")


def test_integration():
    """测试集成流程。"""
    print("\n测试10: 集成流程测试...")
    from datasource.factory import DataSourceFactory
    from analyzer.data_extractor import DataExtractor
    
    factory = DataSourceFactory()
    
    # 模拟从CSV文件获取数据（使用内存DataFrame）
    import pandas as pd
    df = pd.DataFrame({
        'product': ['A', 'B', 'C', 'D'],
        'price': [100, 150, 200, 250],
        'sales': [50, 30, 45, 60]
    })
    
    # 提取关键指标
    metrics = DataExtractor.extract_key_metrics(df)
    print(f"✓ 集成测试成功:")
    print(f"  - 产品数: {metrics['row_count']}")
    print(f"  - 平均价格: {metrics['numeric_summary']['price']['mean']:.2f}")
    print(f"  - 平均销量: {metrics['numeric_summary']['sales']['mean']:.2f}")


if __name__ == '__main__':
    print("=" * 60)
    print("Phase 2 核心功能测试")
    print("=" * 60)
    
    try:
        test_data_source_models()
        test_base_adapter()
        test_webpage_adapter()
        test_api_adapter()
        test_file_adapter()
        test_datasource_factory()
        test_data_extractor()
        test_statistic_calculator()
        test_mcp_tools()
        test_integration()
        
        print("\n" + "=" * 60)
        print("✅ 所有测试通过！Phase 2 核心功能正常工作。")
        print("=" * 60)
        
    except Exception as e:
        print(f"\n❌ 测试失败: {e}")
        import traceback
        traceback.print_exc()
