"""统一异常管理模块。

定义项目中使用的自定义异常类，提供统一的错误处理机制。
"""


class MCPServerError(Exception):
    """MCP服务器基础异常。"""
    
    def __init__(self, message: str, code: str = "UNKNOWN_ERROR"):
        self.message = message
        self.code = code
        super().__init__(self.message)


class DataSourceError(MCPServerError):
    """数据源相关异常。"""
    
    def __init__(self, message: str, source_type: str = "", url: str = ""):
        self.source_type = source_type
        self.url = url
        error_msg = f"数据源错误"
        if source_type:
            error_msg += f"[{source_type}]"
        if url:
            error_msg += f": {url}"
        error_msg += f" - {message}"
        super().__init__(error_msg, code="DATA_SOURCE_ERROR")


class SearchError(MCPServerError):
    """搜索引擎相关异常。"""
    
    def __init__(self, message: str, engine: str = "", query: str = ""):
        self.engine = engine
        self.query = query
        error_msg = f"搜索错误"
        if engine:
            error_msg += f"[{engine}]"
        if query:
            error_msg += f": {query}"
        error_msg += f" - {message}"
        super().__init__(error_msg, code="SEARCH_ERROR")


class AnalysisError(MCPServerError):
    """数据分析相关异常。"""
    
    def __init__(self, message: str, analysis_type: str = ""):
        self.analysis_type = analysis_type
        error_msg = f"分析错误"
        if analysis_type:
            error_msg += f"[{analysis_type}]"
        error_msg += f" - {message}"
        super().__init__(error_msg, code="ANALYSIS_ERROR")


class ValidationError(MCPServerError):
    """参数验证相关异常。"""
    
    def __init__(self, message: str, param_name: str = ""):
        self.param_name = param_name
        error_msg = f"参数验证失败"
        if param_name:
            error_msg += f": {param_name}"
        error_msg += f" - {message}"
        super().__init__(error_msg, code="VALIDATION_ERROR")


class ConfigurationError(MCPServerError):
    """配置相关异常。"""
    
    def __init__(self, message: str, config_key: str = ""):
        self.config_key = config_key
        error_msg = f"配置错误"
        if config_key:
            error_msg += f": {config_key}"
        error_msg += f" - {message}"
        super().__init__(error_msg, code="CONFIGURATION_ERROR")
