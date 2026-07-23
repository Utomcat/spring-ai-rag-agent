# MCP + Agent + Skills

> **← 返回主文档**：[README.md](../README.md)

本文档说明项目中 MCP（Model Context Protocol）、Agent 和 Skills 三个能力的当前实现状态和后续规划。

---

## 📦 当前实现状态

### 1. MCP（Model Context Protocol）— ✅ 已实现

项目已通过 **Spring AI MCP Client（WebFlux）** 实现了 MCP 协议支持，可以连接外部 MCP Server 来扩展工具能力。

#### 当前架构

- **MCP Client**：通过 `spring-ai-starter-mcp-client-webflux` 依赖实现
- **传输方式**：支持 `streamable-http` 协议
- **外部 MCP Server**：配置了 Python MCP Server（`python-mcp-web-search-server`）和 Java MCP Server（`java-file-mcp-server`）
- **条件化加载**：通过多模型配置的 `mcpEnabled` 字段控制哪些模型可使用 MCP 工具
- **描述配置**：通过 `mcp.descriptions` 配置 MCP 工具描述，自动注入系统提示词

#### 配置方式

在 `mcp.yml` 中配置外部 MCP Server 的连接信息：

```yaml
spring:
  ai:
    mcp:
      client:
        enabled: true
        streamable-http:
          connections:
            python-mcp-web-serach-server:
              url: http://127.0.0.1:8084
              endpoint: /mcp
            java-file-mcp-server:
              url: http://127.0.0.1:8085
              endpoint: /mcp

# 自定义 MCP 服务描述（自动注入系统提示词）
mcp:
  descriptions:
    - 数据获取工具(fetch_data), 从网页/文件/API中获取结构化数据
    - 通过网络搜索引擎搜索最新的信息工具(web_search)
    - 文件操作MCP工具 FileOperateMcpTool
```

#### 使用方式

1. 启动外部 MCP Server（Python MCP Server 和/或 Java MCP Server）
2. Spring Boot 应用启动后，MCP Client 自动连接配置的 MCP Server
3. LLM 在对话中可自动发现并调用 MCP Server 提供的工具
4. 通过多模型配置的 `mcpEnabled: true` 控制哪些模型可使用 MCP 工具

#### Python MCP Server

项目包含一个独立的 Python MCP Server 子项目，提供网络搜索、网页抓取、数据分析等扩展工具能力。详细信息请参考 [Python MCP Server 文档](python-mcp-server.md)。

---

### 2. Agent Framework — ✅ 已实现

项目已基于 **Spring AI 原生 ChatClient + Function Calling + 多模型路由** 实现 Agent 自主编排与工具调用。

#### 当前架构

- **多模型路由**：`ModelRouter` 分析用户请求意图，自动选择最合适的 Worker 模型
- **ChatClient 动态工厂**：`ChatClientFactory` 根据模型配置动态创建 ChatClient，按模型分配工具
- **工具注册表**：`ToolRegistry` 统一管理工具注册与查找，支持按模型配置分配工具
- **Advisor 链**：自定义 Advisor 实现日志记录和引用文档提取
- **条件化工具加载**：每个模型可独立配置 Function Calling 工具、MCP 工具、Skills 技能

#### 已实现功能

- ✅ Agent 自主意图识别
- ✅ 多模型智能路由（ModelRouter 自动选择最优模型）
- ✅ 多工具并行调用（知识库检索、文件查询、天气查询、图像生成、MCP 工具）
- ✅ 聊天记忆支持（MessageWindowChatMemory）
- ✅ 引用文档自动提取（ReferenceExtractAdvisor）
- ✅ 调用日志记录（CustomSimpleLoggerAdvisor）
- ✅ ChatClient 实例缓存，避免重复创建
- ✅ 条件化工具/MCP/Skills 配置（按模型独立控制）

---

### 3. Skills 执行引擎 — ✅ 已实现

`spring-ai-rag-starter-skill` 模块已实现，基于 `spring-ai-agent-utils` 实现技能注册与 SKILL.md 管理。

#### 当前架构

- **技能框架**：基于 `spring-ai-agent-utils` 库实现
- **技能定义**：使用 SKILL.md 文件定义技能（包含提示词、工作流程、工具调用指引等）
- **技能加载**：从 classpath 资源中自动扫描和加载技能

#### 已包含技能

| 技能                           | 说明                                                             |
|--------------------------------|------------------------------------------------------------------|
| `intelligent-customer-service` | 智能客服技能，覆盖产品咨询、技术问题、账户相关、售后、投诉等场景 |

#### SKILL.md 结构

技能通过 SKILL.md 文件定义，包含：
- **Front Matter**：技能名称、描述、触发条件
- **核心原则**：技能行为准则
- **工作流程**：分步骤的处理逻辑（意图识别、知识检索、回答生成）
- **可用工具**：技能可调用的工具列表
- **回答模板**：不同场景的回复格式
- **边界约束**：技能的限制和规则

#### 后续规划

- 添加更多业务场景技能（如数据分析助手、代码审查等）
- 支持技能间的协作与编排
- 支持运行时动态加载技能

---

## 🔧 MCP 工具调用流程

```mermaid
flowchart TD
    Start([用户提问]) --> LLM[LLM 分析用户意图]
    LLM --> NeedTool{需要外部工具?}
    NeedTool -->|是| McpCall[MCP Client 调用外部 MCP Server]
    NeedTool -->|否| DirectAnswer[直接生成回答]
    McpCall --> PythonServer[Python MCP Server]
    PythonServer --> WebSearch[网络搜索]
    PythonServer --> WebFetch[网页抓取]
    PythonServer --> DataAnalysis[数据分析]
    WebSearch --> ReturnResults[返回结果给 LLM]
    WebFetch --> ReturnResults
    DataAnalysis --> ReturnResults
    ReturnResults --> FormatResponse[LLM 格式化回答]
    DirectAnswer --> FormatResponse
    FormatResponse --> End([返回用户])
```

---

## 📝 配置说明

### MCP Client 配置（mcp.yml）

```yaml
spring:
  ai:
    mcp:
      client:
        enabled: true                           # 是否启用 MCP Client
        name: spring-ai-mcp-client              # Client 名称
        version: 1.0.0                          # Client 版本
        type: sync                              # Client 类型
        request-timeout: 120s                   # 初始化超时
        toolcallback:
          enabled: true                         # 启用工具回调
        streamable-http:
          connections:
            python-mcp-web-serach-server:       # Python MCP Server
              url: http://127.0.0.1:8084
              endpoint: /mcp
            java-file-mcp-server:               # Java MCP Server
              url: http://127.0.0.1:8085
              endpoint: /mcp

# 自定义 MCP 服务描述（自动注入系统提示词）
mcp:
  descriptions:
    - 数据获取工具(fetch_data), 从网页/文件/API中获取结构化数据
    - 通过网络搜索引擎搜索最新的信息工具(web_search)
    - 文件操作MCP工具 FileOperateMcpTool
```

### Skills 配置（skills.yml）

```yaml
skills:
  descriptions:
    - 智能客服技能, 用于处理用户咨询问题, 提供专业的客服服务
```

---

## ⚠️ 注意事项

1. **MCP Server 需先启动**：在使用 MCP 工具前，需要先启动外部 MCP Server（Python MCP Server 和/或 Java MCP Server）
2. **网络连通性**：确保 Java 应用能够访问 MCP Server 的 URL
3. **工具发现**：MCP Server 提供的工具会被 Spring AI 自动发现并注册为可用的 Function Callback
4. **条件化加载**：MCP 和 Skills 工具通过多模型配置的 `mcpEnabled` / `skillEnabled` 控制是否加载
5. **Agent 与 Skills**：Agent Framework 基于 Spring AI 原生实现，Skills 基于 `spring-ai-agent-utils` 实现，均不影响系统核心功能

---

## 🔮 后续演进路线

### 第二阶段
1. 完善 MCP 管理层，支持多 MCP Server 注册、健康检查、故障转移
2. 添加更多 Python MCP Server 工具（图表生成、报告生成等）
3. 完善 Java MCP Server（java-file-mcp-server）的工具能力
4. 添加更多业务场景技能（数据分析、代码审查等）

### 第三阶段
1. 实现技能间的协作与编排
2. 实现完整的 Agent 工作流引擎
3. 支持分布式 MCP Server 集群
4. 支持运行时动态加载技能

---

<div style="display: flex; justify-content: space-between; align-items: center;">
  <span style="color: #888; font-size: 0.9em;">📅 最后更新：2026-07-23</span>
  <a href="#mcp--agent--skills">⬆️ 返回顶部</a>
</div>
