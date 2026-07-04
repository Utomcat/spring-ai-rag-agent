"""MCP Server 主启动入口。

负责加载环境变量、配置日志并启动 MCP 服务器。
"""
import os
import sys

from dotenv import load_dotenv
from utils import setup_logging, print_banner, load_configuration, start_server



# 先加载 .env，再配置日志（支持通过 LOG_LEVEL 环境变量动态调整日志级别）
load_dotenv()
log_level = os.getenv('LOG_LEVEL', 'INFO')
logger = setup_logging(log_level)

if __name__ == '__main__':
    try:
        # 打印启动横幅
        print_banner()
        
        # 加载并验证配置
        settings = load_configuration(logger)
        
        # 启动服务器
        start_server(settings['transport'], settings['config'], logger)
        
    except ValueError as e:
        logger.error(f'配置错误: {e}')
        logger.error('请检查 .env 文件或环境变量设置')
        sys.exit(1)
    except KeyboardInterrupt:
        logger.info('\n收到中断信号，正在优雅退出...')
        logger.info('MCP 服务器已停止')
        sys.exit(0)
    except Exception as e:
        logger.error(f'启动失败: {e}', exc_info=True)
        sys.exit(1)
