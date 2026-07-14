# Spring AI RAG Starter 模块

> **← 返回主文档**：[README.md](../README.md)

`spring-ai-rag-starter` 是项目的可复用 Starter 组件库，将知识库问答系统的核心能力拆分为 **18 个独立的 Starter 模块**，支持按需引入和灵活组合。

---

## 📋 模块总览

| 模块                                  | 说明                    | 核心功能                                     |
|-------------------------------------|-----------------------|------------------------------------------|
| spring-ai-rag-starter-agent         | Agent 编排与 Advisor       | 自定义 Advisor、Agent 配置                        |
| spring-ai-rag-starter-annotations   | 自定义注解（预留）           | 项目通用注解定义（预留，暂无实现）                       |
| spring-ai-rag-starter-base          | 基础类                   | 统一响应体、基础 DTO/PO/VO、Jackson 配置、虚拟线程配置       |
| spring-ai-rag-starter-chat-memory   | 聊天记忆                  | ChatMemory 配置，支持多轮对话上下文                    |
| spring-ai-rag-starter-common        | 公共组件                  | 常量枚举、异常体系、工具类                             |
| spring-ai-rag-starter-datasource    | 数据源                   | MyBatis Plus 配置、关系型数据库数据源、自动填充             |
| spring-ai-rag-starter-document-splitter | 文档分割                | 文档切分配置、文档解析工具                             |
| spring-ai-rag-starter-international | 国际化                   | 多语言消息工具                                   |
| spring-ai-rag-starter-llm           | LLM 模型配置             | OpenAI 兼容 API + Ollama Embedding 配置      |
| spring-ai-rag-starter-log           | 日志（预留）                | 日志配置（预留，暂无实现）                              |
| spring-ai-rag-starter-mcp           | MCP 客户端（预留）           | MCP 协议客户端配置（预留，暂无实现）                       |
| spring-ai-rag-starter-redis         | Redis 配置              | Redis 连接配置                                 |
| spring-ai-rag-starter-security      | 安全认证                  | Spring Security + JWT 认证                   |
| spring-ai-rag-starter-skill         | Skills 执行引擎（预留）      | 技能注册、批量执行、链式执行（预留，暂无实现）                   |
| spring-ai-rag-starter-task          | 任务调度                  | 延迟任务、异步任务                                 |
| spring-ai-rag-starter-tool          | AI 工具                 | Function Calling 工具实现                     |
| spring-ai-rag-starter-vector-store  | 向量存储                  | Redis Vector Store 配置                      |
| spring-ai-rag-starter-web           | Web 配置                | CORS、异常处理、文件上传、国际化、Tomcat 配置              |

---

## 🏗️ 模块详情

### spring-ai-rag-starter-agent

Agent 编排与 Advisor 模块，提供 AI 对话的拦截增强能力。

**核心类**：

| 类名                           | 说明                              |
|------------------------------|---------------------------------|
| `AgentConfiguration`         | Agent 配置类，组装 ChatClient 和 Advisor 链 |
| `CustomSimpleLoggerAdvisor`  | 自定义日志 Advisor，拦截请求和响应记录调用日志      |
| `ReferenceExtractAdvisor`    | 引用提取 Advisor，自动提取知识库检索的引用文档     |

**特性**：
- ✅ 可插拔的 Advisor 链配置
- ✅ ThreadLocal 安全的引用文档提取
- ✅ 支持 call 和 stream 两种模式

---

### spring-ai-rag-starter-base

基础类模块，提供项目通用的基础类和配置。

**核心类**：

| 类名                       | 说明                    |
|--------------------------|-----------------------|
| `Result<T>`              | 统一 API 响应体封装          |
| `MultiResult<T>`         | 多结果响应体封装              |
| `BaseDTO`                | 基础数据传输对象              |
| `StoredFile`             | 已存储文件 DTO             |
| `PageQueryPO`            | 分页查询参数对象              |
| `JacksonConfiguration`   | Jackson ObjectMapper 配置 |
| `VirtualThreadConfiguration` | 虚拟线程配置             |
| `SystemProperties`       | 系统属性配置类               |

---

### spring-ai-rag-starter-chat-memory

聊天记忆模块，为多轮对话提供上下文记忆支持。

**核心类**：

| 类名                      | 说明                  |
|-------------------------|---------------------|
| `ChatMemoryConfiguration` | ChatMemory Bean 配置 |

**特性**：
- ✅ 基于会话 ID 的消息历史管理
- ✅ 支持上下文窗口控制

---

### spring-ai-rag-starter-common

公共组件模块，提供项目级别的常量、异常和工具类。

**常量枚举**：

| 枚举类                      | 说明          |
|--------------------------|-------------|
| `CapabilityTypeEnum`     | 能力类型枚举      |
| `DatabaseTypeEnum`       | 数据库类型枚举     |
| `DevelopmentLanguageEnum`| 开发语言枚举      |
| `FileCategoryEnum`       | 文件分类枚举      |
| `FileTypeEnum`           | 文件类型枚举      |
| `MessageRoleEnum`        | 消息角色枚举      |
| `StatusEnum`             | 状态枚举        |
| `SymbolEnum`             | 符号常量枚举      |
| `TransportTypeEnum`      | 传输类型枚举      |
| `VectorMetaKeyEnum`      | 向量元数据键枚举    |

**异常体系**：

| 异常类                    | 说明       |
|------------------------|----------|
| `BaseException`        | 基础异常类    |
| `ServiceException`     | 业务异常     |
| `FileException`        | 文件操作异常   |
| `DataSourceException`  | 数据源异常    |

---

### spring-ai-rag-starter-datasource

数据源模块，提供关系型数据库访问和 MyBatis Plus 配置。

**核心类**：

| 类名                            | 说明                 |
|-------------------------------|--------------------|
| `MyBatisPlusConfiguration`    | MyBatis Plus 配置    |
| `HikariDataSourceProperties`  | HikariCP 连接池属性    |
| `RdbProperties`               | 关系型数据库属性          |
| `BaseEntity`                  | 基础实体类（自动填充创建/更新时间） |
| `MyBatisPlusMetaObjectHandler`| 自动填充处理器           |
| `PageBaseDTO`                 | 分页基础 DTO           |

---

### spring-ai-rag-starter-document-splitter

文档分割模块，提供文档切分和解析能力。

**核心类**：

| 类名                             | 说明            |
|--------------------------------|---------------|
| `DocumentSplitterConfiguration`| 文档分割器配置       |
| `DocSplitterProperties`        | 分割参数属性（chunk-size、overlap） |
| `DocumentParseUtils`           | 文档解析工具（Tika / Markdown）   |

---

### spring-ai-rag-starter-international

国际化模块，提供多语言消息支持。

**核心类**：

| 类名           | 说明          |
|--------------|-------------|
| `MessageUtils` | 国际化消息工具类   |

**支持语言**：中文（zh_CN）、英文（en_US）

---

### spring-ai-rag-starter-llm

LLM 模型配置模块，支持双模型架构。

**核心类**：

| 类名                    | 说明                    |
|-----------------------|-----------------------|
| `OpenAiConfiguration` | OpenAI 兼容 API 聊天模型配置 |
| `OllamaConfiguration` | Ollama Embedding 模型配置 |

**双模型架构**：
- **聊天模型**：通过 OpenAI 兼容 API 调用（如小米 Mimo、DeepSeek 等）
- **Embedding 模型**：通过 Ollama 本地调用（embeddinggemma:latest）

---

### spring-ai-rag-starter-redis

Redis 配置模块，提供 Redis 连接管理。

**核心类**：

| 类名                 | 说明             |
|--------------------|----------------|
| `RedisConfiguration` | Redis 连接配置 Bean |

**用途**：
- 向量数据库存储（Redis Vector Store）
- 缓存支持（Chat Memory 等）

---

### spring-ai-rag-starter-security

安全认证模块，基于 Spring Security + JWT 实现。

**核心类**：

| 类名                         | 说明              |
|----------------------------|-----------------|
| `SecurityConfiguration`    | Spring Security 配置 |
| `JwtAuthFilter`            | JWT 认证过滤器       |
| `JwtUtils`                 | JWT 工具类         |
| `SecurityUtils`            | Security 工具类    |
| `JwtProperties`            | JWT 配置属性        |
| `LoginUserDetailsDTO`      | 登录用户详情 DTO      |
| `ParsedTokenVO`            | 解析后的 Token VO   |

**特性**：
- ✅ JWT Token 认证
- ✅ RBAC 权限控制（ADMIN / USER）
- ✅ 密码加密存储

---

### spring-ai-rag-starter-task

任务调度模块，提供延迟任务和异步任务支持。

**核心类**：

| 类名                    | 说明        |
|-----------------------|-----------|
| `TaskConfiguration`   | 任务调度配置    |
| `DelayedTaskService`  | 延迟任务服务    |

---

### spring-ai-rag-starter-tool

AI 工具模块，实现 Function Calling 工具。

**核心类**：

| 类名                               | 说明                |
|----------------------------------|-------------------|
| `ToolConfiguration`              | 工具配置              |
| `KnowledgeRetrievalToolFunction` | 知识库语义检索工具         |

**已实现工具**：
- ✅ 知识库语义检索（`retrieveKnowledge`）：基于 Redis Vector Store 的向量相似度检索

---

### spring-ai-rag-starter-vector-store

向量存储模块，基于 Redis 实现向量存储。

**核心类**：

| 类名                            | 说明               |
|-------------------------------|------------------|
| `RedisVectorStoreConfiguration` | Redis Vector Store 配置 |
| `VectorStoreProperties`       | 向量存储属性（索引类型、距离度量、维度） |

**配置**：
- 索引类型：HNSW
- 距离度量：COSINE
- 向量维度：768（embeddinggemma 模型输出维度）

---

### spring-ai-rag-starter-web

Web 配置模块，提供 Web 层通用配置。

**核心类**：

| 类名                              | 说明            |
|---------------------------------|---------------|
| `WebMvcConfiguration`           | Web MVC 配置    |
| `WebMvcConfiguration`           | CORS 跨域配置     |
| `GlobalWebExceptionHandler`     | 全局异常处理器       |
| `FaviconConfiguration`          | Favicon 配置    |
| `I18nConfiguration`             | 国际化配置         |
| `TomcatConfiguration`           | Tomcat 配置     |
| `UploadBootstrapConfiguration`  | 文件上传初始化配置    |
| `CorsProperties`                | CORS 属性       |
| `FileProperties`                | 文件上传属性        |
| `WebUtils`                      | Web 工具类       |

---

## 🔧 使用方式

### 📦 Maven 引入

在项目的 `pom.xml` 中按需引入所需的 Starter 模块：

```xml
<dependency>
    <groupId>com.ranyk</groupId>
    <artifactId>spring-ai-rag-starter-security</artifactId>
    <version>${project.version}</version>
</dependency>
```

### ⚙️ 自动配置

所有 Starter 模块均支持 Spring Boot 自动配置，引入依赖后通过 `application.yml` 或对应的配置文件即可启用。

### 🧩 模块组合

各 Starter 模块可自由组合，例如：
- 仅使用 `starter-security` + `starter-datasource` 构建基础 CRUD 应用
- 添加 `starter-llm` + `starter-vector-store` + `starter-tool` 启用 AI 能力
- 添加 `starter-mcp` + `starter-skill` 启用 MCP 和 Skills 扩展

---

## 🏗️ 模块依赖关系

```
spring-ai-rag-starter-base（基础类）
    ├── spring-ai-rag-starter-common（公共组件）
    │
    ├── spring-ai-rag-starter-datasource（数据源）
    │   └── base, common
    │
    ├── spring-ai-rag-starter-security（安全认证）
    │   └── base, common
    │
    ├── spring-ai-rag-starter-web（Web 配置）
    │   └── base, common
    │
    ├── spring-ai-rag-starter-llm（LLM 模型）
    │   └── base
    │
    ├── spring-ai-rag-starter-vector-store（向量存储）
    │   └── redis
    │
    ├── spring-ai-rag-starter-agent（Agent 编排）
    │   └── llm, tool, chat-memory
    │
    └── spring-ai-rag-starter-tool（AI 工具）
        └── vector-store, datasource
```

---

<div style="display: flex; justify-content: space-between; align-items: center;">
  <span style="color: #888; font-size: 0.9em;">📅 最后更新：2026-07-14</span>
  <a href="#spring-ai-rag-starter-模块">⬆️ 返回顶部</a>
</div>
