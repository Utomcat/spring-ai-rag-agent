# Function Calling 工具扩展

> **← 返回主文档**：[README.md](../README.md)

本项目支持 Spring AI 的 Function Calling 机制，允许 LLM 在对话过程中自动调用预定义的工具方法。

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
@Tool(description = "查询知识库中已上传的文件列表，支持按分类、文件类型筛选，支持分页")
public String getAllDocumentsFileName(
        @ToolParam(description = "文档分类ID, 可选参数") List<Long> categoryIds,
        @ToolParam(description = "文档类型, 可选参数") List<String> fileTypes,
        @ToolParam(description = "页码, 可选参数, 默认为 1") Integer page,
        @ToolParam(description = "每页大小, 可选参数, 默认为 10") Integer size
)
```

**使用方式**：

1. 用户通过自然语言提问
2. LLM 自动识别意图并调用工具
3. 返回格式化的文档名列表（自然语言）

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

## ➕ 如何扩展新工具

### 方式一：Function Callback（Java）

1. 创建新的 Function Callback 类，使用 `@Component` 注解
2. 在方法上添加 `@Tool` 注解，并提供详细的 `description`
3. 方法参数使用 `@ToolParam` 注解描述参数用途
4. 在 `ChatClientConfiguration` 中注册该工具（通过 `.defaultTools()` 方法）

**示例**：

```java
@Component
public class MyCustomFunction {

    @Tool(description = "工具的描述，告诉 LLM 这个工具的用途")
    public String myMethod(
            @ToolParam(description = "参数描述") String param
    ) {
        // 实现逻辑
        return "结果";
    }
}
```

**注册工具**：

在 `ChatClientConfiguration.java` 的 `chatClient` 方法中：

1. 将你的 Function Callback 类作为参数注入到方法中
2. 在 `.defaultTools()` 中添加该工具（可添加多个工具）

```java
@Bean
public ChatClient chatClient(
        OpenAiChatModel openAiChatModel,
        CustomSimpleLoggerAdvisor customSimpleLoggerAdvisor,
        SimpleLoggerAdvisor simpleLoggerAdvisor,
        @Lazy DocumentToolFunction documentToolFunction,
        @Lazy MyCustomFunction myCustomFunction  // ← 注入你的工具
) {
    return ChatClient
            .builder(openAiChatModel)
            .defaultAdvisors(
                    customSimpleLoggerAdvisor,
                    simpleLoggerAdvisor
            )
            // 注册多个工具
            .defaultTools(documentToolFunction, myCustomFunction)  // ← 添加你的工具
            .build();
}
```

### 方式二：MCP Server（外部服务）

1. 创建一个独立的 MCP Server（可以是 Python、Node.js 等）
2. 实现 MCP 协议的工具方法
3. 在 `mcp.yml` 中配置 MCP Server 的连接信息
4. 启动 MCP Server 和 Java 应用

详细内容请参考 [mcp-server.md](mcp-server.md)

## 🔄 工具调用流程

工具调用的完整流程图请参考 [架构设计 - Function Calling 工具调用流程](architecture.md#function-calling-工具调用流程)。

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

在 `ChatClientConfiguration` 中配置 `SimpleLoggerAdvisor` 和 `CustomSimpleLoggerAdvisor`，可以在日志中查看工具调用过程：

```log
DEBUG - Tool call requested: DocumentToolFunction.getAllDocumentsFileName
DEBUG - Tool call parameters: {"categoryIds": null, "fileTypes": null, "page": 1, "size": 10}
DEBUG - Tool call result: 知识库中包含以下文档：技术文档.pdf、产品说明.md
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
  <span style="color: #888; font-size: 0.9em;">📅 更新日期：2026-07-03</span>
  <a href="#function-calling-工具扩展">⬆️ 返回顶部</a>
</div>
