"""动态页面抓取适配器接口。

为支持JavaScript渲染的动态网页预留扩展接口。
当前版本仅定义接口,实际实现需要集成Selenium或Playwright。
"""
import logging
from abc import ABC, abstractmethod
from typing import Optional, Dict

# 配置日志
logger = logging.getLogger(__name__)


class DynamicPageAdapter(ABC):
    """动态页面抓取适配器抽象基类。
    
    用于处理JavaScript渲染的网页内容。
    子类需要实现具体的浏览器自动化逻辑。
    """

    @abstractmethod
    def fetch(self, url: str, wait_time: int = 3, **kwargs) -> Optional[str]:
        """
        抓取动态渲染的网页内容。

        Args:
            url: 网页URL
            wait_time: 等待JavaScript渲染的时间(秒)
            **kwargs: 额外参数
                - selector: CSS选择器,等待特定元素出现
                - scroll_to_bottom: 是否滚动到底部加载更多内容
                - click_elements: 需要点击的元素选择器列表

        Returns:
            渲染后的HTML内容,失败返回None
        """
        pass

    @abstractmethod
    def get_type(self) -> str:
        """
        获取适配器类型标识。

        Returns:
            适配器类型字符串
        """
        pass

    @abstractmethod
    def is_available(self) -> bool:
        """
        检查适配器是否可用(依赖是否已安装)。

        Returns:
            是否可用
        """
        pass


class SeleniumAdapter(DynamicPageAdapter):
    """基于Selenium的动态页面适配器(占位实现)。
    
    注意: 此实现需要安装selenium和对应的浏览器驱动。
    目前仅提供接口框架,实际使用时需要完整实现。
    """

    def __init__(self, headless: bool = True):
        """
        初始化Selenium适配器。

        Args:
            headless: 是否使用无头模式
        """
        self.headless = headless
        self.driver = None
        logger.warning(
            'SeleniumAdapter是占位实现,需要安装selenium包并配置浏览器驱动才能使用'
        )

    def fetch(self, url: str, wait_time: int = 3, **kwargs) -> Optional[str]:
        """抓取动态页面(占位实现)。"""
        logger.warning('SeleniumAdapter未完全实现,请安装selenium并配置驱动')
        return None

    def get_type(self) -> str:
        return "selenium"

    def is_available(self) -> bool:
        """检查Selenium是否可用。"""
        try:
            import selenium  # noqa: F401
            return True
        except ImportError:
            return False


class PlaywrightAdapter(DynamicPageAdapter):
    """基于Playwright的动态页面适配器。
    
    支持JavaScript渲染的网页抓取,包括SPA应用、懒加载内容等。
    需要安装playwright包并执行playwright install安装浏览器。
    """

    def __init__(self, headless: bool = True):
        """
        初始化Playwright适配器。

        Args:
            headless: 是否使用无头模式(默认True)
        """
        self.headless = headless
        self.browser = None
        self.context = None

    def _ensure_browser(self):
        """确保浏览器实例已创建。"""
        if self.browser is None:
            try:
                from playwright.sync_api import sync_playwright
                
                playwright = sync_playwright().start()
                self.browser = playwright.chromium.launch(headless=self.headless)
                self.context = self.browser.new_context(
                    viewport={'width': 1920, 'height': 1080},
                    user_agent='Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
                )
                logger.info('Playwright浏览器已启动')
            except ImportError:
                logger.error('Playwright未安装,请执行: pip install playwright && playwright install')
                raise
            except Exception as e:
                logger.error(f'Playwright浏览器启动失败: {e}')
                raise

    def fetch(self, url: str, wait_time: int = 3, **kwargs) -> Optional[str]:
        """
        抓取动态渲染的网页内容。

        Args:
            url: 网页URL
            wait_time: 等待JavaScript渲染的时间(秒)
            **kwargs: 额外参数
                - selector: CSS选择器,等待特定元素出现
                - scroll_to_bottom: 是否滚动到底部加载更多内容
                - click_elements: 需要点击的元素选择器列表

        Returns:
            渲染后的HTML内容,失败返回None
        """
        try:
            self._ensure_browser()
            
            # 创建新页面
            page = self.context.new_page()
            
            # 设置超时
            page.set_default_timeout(wait_time * 1000 + 5000)
            
            logger.info(f'开始抓取动态页面: {url}')
            
            # 导航到目标页面
            response = page.goto(url, wait_until='networkidle')
            
            if not response or response.status != 200:
                status = response.status if response else 'Unknown'
                logger.warning(f'页面加载失败: HTTP {status}')
                page.close()
                return None
            
            # 等待指定时间让JS执行
            if wait_time > 0:
                page.wait_for_timeout(wait_time * 1000)
            
            # 等待特定元素(如果提供)
            selector = kwargs.get('selector')
            if selector:
                try:
                    page.wait_for_selector(selector, timeout=5000)
                    logger.debug(f'等待到目标元素: {selector}')
                except Exception as e:
                    logger.warning(f'未找到目标元素 {selector}: {e}')
            
            # 滚动到底部(如果需要)
            if kwargs.get('scroll_to_bottom'):
                page.evaluate('window.scrollTo(0, document.body.scrollHeight)')
                page.wait_for_timeout(1000)  # 等待懒加载
                logger.debug('已滚动到页面底部')
            
            # 点击特定元素(如果需要)
            click_elements = kwargs.get('click_elements', [])
            for element_selector in click_elements:
                try:
                    page.click(element_selector)
                    page.wait_for_timeout(1000)  # 等待点击后的内容加载
                    logger.debug(f'已点击元素: {element_selector}')
                except Exception as e:
                    logger.warning(f'点击元素 {element_selector} 失败: {e}')
            
            # 获取渲染后的HTML
            html_content = page.content()
            
            # 关闭页面
            page.close()
            
            logger.info(f'动态页面抓取成功: {len(html_content)} 字节')
            return html_content
            
        except Exception as e:
            logger.error(f'动态页面抓取失败: {e}', exc_info=True)
            try:
                if 'page' in locals():
                    page.close()
            except:
                pass
            return None

    def get_type(self) -> str:
        return "playwright"

    def is_available(self) -> bool:
        """检查Playwright是否可用。"""
        try:
            import playwright  # noqa: F401
            return True
        except ImportError:
            return False

    def close(self):
        """关闭浏览器实例。"""
        if self.browser:
            try:
                self.browser.close()
                logger.info('Playwright浏览器已关闭')
            except Exception as e:
                logger.warning(f'关闭浏览器失败: {e}')
            finally:
                self.browser = None
                self.context = None

    def __del__(self):
        """析构时清理资源。"""
        self.close()


class DynamicPageAdapterFactory:
    """动态页面适配器工厂。
    
    根据可用性自动选择合适的适配器。
    """

    @staticmethod
    def create(preferred: str = 'auto') -> Optional[DynamicPageAdapter]:
        """
        创建动态页面适配器实例。

        Args:
            preferred: 首选适配器类型 ('selenium', 'playwright', 'auto')

        Returns:
            适配器实例,如果都不可用则返回None
        """
        if preferred == 'selenium':
            adapter = SeleniumAdapter()
            if adapter.is_available():
                return adapter
            logger.warning('Selenium不可用,请安装: pip install selenium')
            return None

        elif preferred == 'playwright':
            adapter = PlaywrightAdapter()
            if adapter.is_available():
                return adapter
            logger.warning('Playwright不可用,请安装: pip install playwright')
            return None

        elif preferred == 'auto':
            # 优先尝试Playwright(更现代)
            playwright_adapter = PlaywrightAdapter()
            if playwright_adapter.is_available():
                logger.info('使用Playwright适配器')
                return playwright_adapter

            # 降级到Selenium
            selenium_adapter = SeleniumAdapter()
            if selenium_adapter.is_available():
                logger.info('使用Selenium适配器')
                return selenium_adapter

            logger.warning(
                '没有可用的动态页面适配器\n'
                '如需抓取JavaScript渲染的页面,请安装以下任一工具:\n'
                '  - Playwright: pip install playwright && playwright install\n'
                '  - Selenium: pip install selenium'
            )
            return None

        else:
            logger.warning(f'不支持的适配器类型: {preferred}')
            return None
