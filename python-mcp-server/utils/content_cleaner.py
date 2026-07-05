"""网页内容清洗工具。

提供智能的内容提取和清洗功能,移除噪音元素(导航、广告、脚本等)。
"""
import logging
from typing import Dict, Optional

from bs4 import BeautifulSoup

# 配置日志
logger = logging.getLogger(__name__)


class ContentCleaner:
    """内容清洗器 - 从HTML中提取主要文本内容。"""

    # 需要移除的标签
    REMOVE_TAGS = [
        'script', 'style', 'nav', 'header', 'footer', 
        'aside', 'iframe', 'noscript', 'meta', 'link'
    ]

    # 常见的主要内容容器选择器
    CONTENT_SELECTORS = [
        'article', 'main', '[role="main"]',
        '.post-content', '.article-content', '.entry-content',
        '#content', '.content', '.post', '.article',
        'section.content', 'div.main-content'
    ]

    @staticmethod
    def extract_main_content(html_content: str, max_length: int = 10000) -> Dict:
        """
        从HTML中提取主要内容。

        Args:
            html_content: HTML字符串
            max_length: 最大文本长度限制

        Returns:
            包含标题、正文、元数据的字典
        """
        try:
            soup = BeautifulSoup(html_content, 'lxml')
        except Exception as e:
            logger.error(f'HTML解析失败: {e}')
            return {'title': '', 'content': '', 'error': str(e)}

        # 提取标题
        title = ContentCleaner._extract_title(soup)

        # 尝试从主要内容容器提取
        main_content = ContentCleaner._find_main_container(soup)

        if not main_content:
            # 降级: 使用body
            main_content = soup.find('body')

        if main_content:
            # 清理不需要的元素
            cleaned_content = ContentCleaner._clean_element(main_content)
            
            # 提取纯文本
            text = cleaned_content.get_text(separator='\n', strip=True)
            
            # 清理多余空白行
            text = ContentCleaner._clean_whitespace(text)
            
            # 限制长度
            if len(text) > max_length:
                text = text[:max_length] + '...'
        else:
            text = ''

        # 提取元数据
        metadata = ContentCleaner._extract_metadata(soup)

        # 提取链接
        links = ContentCleaner._extract_links(soup, limit=30)

        # 提取图片
        images = ContentCleaner._extract_images(soup, limit=20)

        return {
            'title': title,
            'content': text,
            'metadata': metadata,
            'links': links,
            'images': images,
            'word_count': len(text.split()) if text else 0
        }

    @staticmethod
    def _extract_title(soup: BeautifulSoup) -> str:
        """提取页面标题。"""
        # 优先级: h1 > title标签 > meta og:title
        h1 = soup.find('h1')
        if h1:
            return h1.get_text(strip=True)

        title_tag = soup.find('title')
        if title_tag:
            return title_tag.get_text(strip=True)

        og_title = soup.find('meta', property='og:title')
        if og_title and og_title.get('content'):
            return og_title['content']

        return '无标题'

    @staticmethod
    def _find_main_container(soup: BeautifulSoup) -> Optional:
        """查找主要内容容器。"""
        for selector in ContentCleaner.CONTENT_SELECTORS:
            element = soup.select_one(selector)
            if element:
                logger.debug(f'找到主要内容容器: {selector}')
                return element
        
        return None

    @staticmethod
    def _clean_element(element) -> BeautifulSoup:
        """清理HTML元素,移除噪音内容。"""
        # 创建副本以避免修改原始DOM
        cleaned = BeautifulSoup(str(element), 'lxml')

        # 移除不需要的标签
        for tag_name in ContentCleaner.REMOVE_TAGS:
            for tag in cleaned.find_all(tag_name):
                tag.decompose()

        # 移除常见的广告和导航类
        ad_classes = [
            'ad', 'ads', 'advertisement', 'banner', 'sidebar',
            'navigation', 'menu', 'cookie-banner', 'popup'
        ]
        
        for class_name in ad_classes:
            for tag in cleaned.find_all(class_=lambda x: x and class_name in x.lower()):
                tag.decompose()

        return cleaned

    @staticmethod
    def _clean_whitespace(text: str) -> str:
        """清理多余空白。"""
        import re
        
        # 替换多个连续空行为单个空行
        text = re.sub(r'\n{3,}', '\n\n', text)
        
        # 移除行首行尾空白
        lines = [line.strip() for line in text.split('\n')]
        
        # 过滤空行(保留段落分隔)
        cleaned_lines = []
        prev_empty = False
        for line in lines:
            if line:
                cleaned_lines.append(line)
                prev_empty = False
            elif not prev_empty:
                cleaned_lines.append('')
                prev_empty = True
        
        return '\n'.join(cleaned_lines).strip()

    @staticmethod
    def _extract_metadata(soup: BeautifulSoup) -> Dict:
        """提取页面元数据。"""
        metadata = {}

        # 描述
        desc = soup.find('meta', attrs={'name': 'description'})
        if desc and desc.get('content'):
            metadata['description'] = desc['content']

        # 作者
        author = soup.find('meta', attrs={'name': 'author'})
        if author and author.get('content'):
            metadata['author'] = author['content']

        # 发布时间
        publish_time = soup.find('meta', attrs={'property': 'article:published_time'})
        if publish_time and publish_time.get('content'):
            metadata['published_time'] = publish_time['content']

        # 关键词
        keywords = soup.find('meta', attrs={'name': 'keywords'})
        if keywords and keywords.get('content'):
            metadata['keywords'] = keywords['content']

        return metadata

    @staticmethod
    def _extract_links(soup: BeautifulSoup, limit: int = 30) -> list:
        """提取页面链接。"""
        links = []
        for a_tag in soup.find_all('a', href=True)[:limit]:
            href = a_tag['href']
            text = a_tag.get_text(strip=True)
            
            # 跳过锚点和javascript链接
            if href.startswith('#') or href.startswith('javascript:'):
                continue
            
            links.append({
                'url': href,
                'text': text or '无文本'
            })
        
        return links

    @staticmethod
    def _extract_images(soup: BeautifulSoup, limit: int = 20) -> list:
        """提取页面图片。"""
        images = []
        for img_tag in soup.find_all('img', src=True)[:limit]:
            src = img_tag['src']
            alt = img_tag.get('alt', '')
            
            images.append({
                'src': src,
                'alt': alt or '无描述'
            })
        
        return images
