# Function Calling 工具扩展

> **← 返回主文档**：[README.md](../README.md)

本项目基于 Spring AI Alibaba Agent Framework，支持 Agent 自主调用预定义的工具方法。Agent 会根据用户意图自动选择合适的工具进行调用。

## 🔧 已实现的工具

### 1. DocumentToolFunction - 知识库文件列表查询

**功能**：查询知识库中已上传的文件列表，支持按分类、文件类型筛选，支持分页

**触发场景**：

- "知识库中有哪些文档？"
- "列出所有文档"
- "当前有什么文件？"
- "知识库中 PDF 文件有哪些？"
- "分类 1 下有哪些文档？"
- 其他询问知识库文件的相关问题

**实现位置**：`com.ranyk.spring.ai.rag.knowledge.database.ai.tools.DocumentToolFunction`

**工具方法签名**：

```java
@Tool(description = "查询知识库中已上传的文件列表. 当用户询问 '知识库中有哪些文件'、'列出所有文档'、'当前知识库中有什么文件' 等问题时使用此工具, 分页查询, 默认传入 page 为 1, size 为 10, 下一页为之前传入的 page 值加一")
public String getAllDocumentsFileName(
        @ToolParam(description = "文档分类ID, 可选参数, 不传或传 0 则查询所有分类的文件", required = false) List<Long> categoryIds,
        @ToolParam(description = "文档类型, 可选参数, 当传入 txt 时 该值转换为 .txt, 当传入 pdf 时 该值转换为 .pdf 等依次类推, 不传或传为空则查询所有类型的文件", required = false) List<String> fileTypes,
        @ToolParam(description = "页码, 可选参数, 默认为 1", required = false) Integer page,
        @ToolParam(description = "每页大小, 可选参数, 默认为 10", required = false) Integer size
)
```

**使用方式**：

1. 用户通过自然语言提问
2. Agent 自动识别意图并调用工具
3. 返回格式化的文档名列表（自然语言）

### 2. KnowledgeRetrievalToolFunction - 知识库语义检索

**功能**：从知识库中语义检索与用户问题相关的文档片段，支持按分类过滤

**触发场景**：

- "请介绍一下 RAG 技术"
- "知识库中关于 Spring AI 的内容有哪些？"
- "查找技术文档中的相关内容"
- 其他需要从知识库中获取专业知识的问题

**实现位置**：`com.ranyk.spring.ai.rag.knowledge.database.ai.tools.KnowledgeRetrievalToolFunction`

**工具方法签名**：

```java
@Tool(description = "从知识库中语义检索与用户问题相关的文档片段。当需要查找知识库中的专业信息、技术文档、业务流程时使用此工具。")
public String retrieveKnowledge(
        @ToolParam(description = "用户的问题或检索关键词") String question,
        @ToolParam(description = "可选的分类ID列表，用于限定检索范围。不传则全库检索", required = false) List<Long> categoryIds
)
```

**返回格式**：

```json
{
  "query": "用户的问题",
  "totalHits": 5,
  "documents": [
    {
      "title": "文档标题",
      "docId": "文档ID",
      "categoryId": "分类ID",
      "snippet": "文档片段内容..."
    }
  ]
}
```

**使用方式**：

1. 用户提出知识性问题
2. Agent 自动调用 retrieveKnowledge 工具
3. 向量相似度检索 Redis Vector Store
4. 返回结构化的文档片段列表
5. ReferenceExtractAdvisor 提取引用文档供后续展示

## 🔌 MCP (Model Context Protocol) 支持

本项目还支持 MCP 协议，可以连接外部 MCP Server 来扩展工具能力。

### 配置位置

`mcp.yml`（详细配置请参考 [配置文件说明 - mcp.yml](configuration.md#14-mcpyml)）

### 默认配置

- MCP Client 已启用
- 配置了一个示例 MCP Server：`python-mcp-web-serach-server`（URL: `http://127.0.0.1:8084/mcp`）
- 支持通过 `streamable-http` 方式连接 MCP Server

### 使用方式

1. 在 `mcp.yml` 中配置外部 MCP Server 的连接信息
2. 启动应用后，MCP Client 会自动连接到配置的 MCP Server
3. LLM 在对话中可以自动发现并调用 MCP Server 提供的工具

## 🎯 Advisor 拦截器

项目实现了自定义 Advisor 来增强 Agent 能力：

### 1. CustomSimpleLoggerAdvisor - 日志记录

**功能**：拦截 ChatClient 的 call 和 stream 请求，记录详细的调用日志

**实现位置**：`com.ranyk.spring.ai.rag.knowledge.database.ai.advisor.CustomSimpleLoggerAdvisor`

**特性**：

- 记录向 LLM 发送消息前的请求信息
- 记录 LLM 响应后的内容长度
- 支持阻塞式（call）和流式（stream）两种模式

### 2. ReferenceExtractAdvisor - 引用提取

**功能**：拦截知识库检索工具调用结果，提取 references 供后续使用

**实现位置**：`com.ranyk.spring.ai.rag.knowledge.database.ai.advisor.ReferenceExtractAdvisor`

**特性**：

- 使用 ThreadLocal 存储提取的引用文档列表
- 避免修改不可变的 ChatResponseMetadata
- 自动解析 retrieveKnowledge 工具的返回结果
- 提取 documents 数组并存入 ThreadLocal

**使用方式**：

在 Service 层通过 `referenceExtractAdvisor.getExtractedReferences()` 获取引用文档列表

## ➕ 如何扩展新工具

### 方式一：Function Callback（Java）

1. 创建新的 Function Callback 类，使用 `@Component` 注解
2. 在方法上添加 `@Tool` 注解，并提供详细的 `description`
3. 方法参数使用 `@ToolParam` 注解描述参数用途
4. Agent 会自动发现并注册这些工具，无需手动配置

**示例**：

```java
@Component
public class MyCustomFunction {

    @Tool(description = "工具的描述，告诉 Agent 这个工具的用途")
    public String myMethod(
            @ToolParam(description = "参数描述") String param
    ) {
        // 实现逻辑
        return "结果";
    }
}
```

> **注意**：使用 Agent Framework 后，无需在 `ChatClientConfiguration` 中手动注册工具，Agent 会自动发现和调用所有标注了 `@Tool` 的方法。

### 方式二：MCP Server（外部服务）

1. 创建一个独立的 MCP Server（可以是 Python、Node.js 等）
2. 实现 MCP 协议的工具方法
3. 在 `mcp.yml` 中配置 MCP Server 的连接信息
4. 启动 MCP Server 和 Java 应用

详细内容请参考 [mcp-server.md](mcp-server.md)

## 🔄 工具调用流程

工具调用的完整流程图请参考 [架构设计 - Agent 工具调用流程](architecture.md#agent-工具调用流程)。

## 工具优先级

工具调用的优先级由 LLM 根据用户意图决定，以下是影响因素：

1. **工具描述**：`@Tool` 注解的 `description` 越详细，LLM 越容易匹配
2. **参数描述**：`@ToolParam` 注解的 `description` 帮助 LLM 理解如何调用
3. **上下文**：LLM 会根据对话历史决定是否需要调用工具
4. **用户意图**：明确询问信息的问题更容易触发工具调用

## 🐛 调试工具调用

### 查看日志

配置 `log.yml` 中的日志级别为 DEBUG：

```yaml
logging:
  level:
    org.springframework.ai: DEBUG
```

### 监控工具调用

在 `ChatClientConfiguration` 中配置 `CustomSimpleLoggerAdvisor`，可以在日志中查看工具调用过程：

```log
INFO - 自定义日志 Advisor , 调用 call 方法后, 向 LLM 发送消息之前, 日志输出, 即将调用 LLM 进行问题分析处理....
INFO - 检测到 1 个工具调用响应，开始提取 references
INFO - 发现知识库检索工具调用结果，开始解析 references
INFO - 成功提取 5 个引用文档
INFO - 自定义日志 Advisor, 调用 call 方法后, 向 LLM 发送消息之后, 获取到的响应内容长度: 1234
```

## ⚠️ 注意事项

1. **工具描述要详细**：工具的 `description` 是 LLM 判断是否调用的关键，需要清晰描述工具的用途和适用场景
2. **参数类型要明确**：使用正确的参数类型（如 `List<Long>`、`String` 等），帮助 LLM 正确生成参数
3. **避免循环调用**：工具返回的结果不应再次触发工具调用，否则可能导致无限循环
4. **性能考虑**：工具调用会增加响应时间，需要权衡功能和性能
5. **错误处理**：工具方法需要处理异常情况，返回友好的错误信息

## ✅ 工具扩展最佳实践

### 1. 单一职责

每个工具方法应该只做一件事，保持功能单一。

### 2. 清晰描述

工具描述应该清晰、准确，包含：
- 工具的用途
- 适用场景
- 参数说明

### 3. 合理参数

参数设计应该合理：
- 区分必选参数和可选参数
- 使用合适的数据类型
- 提供默认值（可选参数）

### 4. 格式化输出

工具返回的结果应该格式化，便于 LLM 理解和呈现给用户。

### 5. 错误处理

工具方法应该捕获异常并返回友好的错误信息，避免直接抛出异常。

### 6. 性能优化

对于耗时操作，考虑使用异步处理或缓存。

---

<div style="display: flex; justify-content: space-between; align-items: center;">
  <span style="color: #888; font-size: 0.9em;">📅 更新日期：2026-07-04</span>
  <a href="#function-calling-工具扩展">⬆️ 返回顶部</a>
</div>
