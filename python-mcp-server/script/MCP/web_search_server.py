from mcp.server.fastmcp import FastMCP
import requests
from bs4 import BeautifulSoup
from urllib.parse import quote_plus
import os
from dotenv import load_dotenv

# 加载 .env 配置文件
load_dotenv()

# 从环境变量读取配置
host = os.getenv("MCP_HOST", "127.0.0.1")
port = int(os.getenv("MCP_PORT", "8084"))
mount_path = os.getenv("MCP_MOUNT_PATH", "/mcp")

mcp = FastMCP(
    name="Web Search Server",
    host=host,
    port=port,
    mount_path=mount_path
)

@mcp.tool()
def web_search(query: str, max_results: int = 5, engine: str = "duckduckgo") -> str:
    """
    通过网络搜索引擎搜索最新的信息。

    Args:
        query: 搜索关键词或问题
        max_results: 搜索结果数量，默认 5
        engine: 搜索引擎（bing 或 duckduckgo），默认 duckduckgo

    Returns:
        str: 格式化的搜索结果
    """
    try:
        # 选择搜索引擎
        if engine == "bing":
            results = search_bing(query, max_results)
        else:
            results = search_duckduckgo(query, max_results)

        if not results:
            return "未搜索到相关结果。"

        # 格式化输出
        output = [f"搜索结果（共 {len(results)} 条）：\n"]
        for i, result in enumerate(results, 1):
            output.append(f"{i}. **{result['title']}**")
            output.append(f"   - 摘要：{result['snippet']}")
            output.append(f"   - 链接：{result['url']}")
            output.append("")

        return "\n".join(output)

    except Exception as e:
        return f"网络搜索失败：{str(e)}"

def search_bing(query: str, max_results: int) -> list:
    """
    使用 Bing 搜索

    Args:
        query: 搜索关键词
        max_results: 搜索结果数量

    Returns:
        list: 搜索结果列表
    """
    search_url = f"https://www.bing.com/search?q={quote_plus(query)}"
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
    }

    response = requests.get(search_url, headers=headers, timeout=10)
    response.raise_for_status()

    soup = BeautifulSoup(response.text, 'html.parser')
    results = []

    for item in soup.select('li.b_algo')[:max_results]:
        title_elem = item.select_one('h2 a')
        snippet_elem = item.select_one('p')

        if title_elem:
            results.append({
                'title': title_elem.get_text(strip=True),
                'url': title_elem.get('href', ''),
                'snippet': snippet_elem.get_text(strip=True) if snippet_elem else '无摘要'
            })

    return results

def search_duckduckgo(query: str, max_results: int) -> list:
    """
    使用 DuckDuckGo 搜索

    Args:
        query: 搜索关键词
        max_results: 搜索结果数量

    Returns:
        list: 搜索结果列表
    """
    search_url = f"https://html.duckduckgo.com/html/?q={quote_plus(query)}"
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
    }

    response = requests.get(search_url, headers=headers, timeout=10)
    response.raise_for_status()

    soup = BeautifulSoup(response.text, 'html.parser')
    results = []

    for item in soup.select('.result')[:max_results]:
        title_elem = item.select_one('.result__a')
        snippet_elem = item.select_one('.result__snippet')

        if title_elem:
            results.append({
                'title': title_elem.get_text(strip=True),
                'url': title_elem.get('href', ''),
                'snippet': snippet_elem.get_text(strip=True) if snippet_elem else '无摘要'
            })

    return results