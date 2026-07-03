# 架构设计

> **← 返回主文档**：[README.md](../README.md)

## 🏗️ 总体架构图

```mermaid
graph TB
    subgraph Client[客户端]
        Browser[Web 浏览器]
        App[移动 App]
        ThirdParty[第三方系统]
    end

    subgraph Gateway[接入层]
        Nginx[Nginx / 负载均衡]
        CORS[CORS 跨域处理]
    end

    subgraph Application[Spring Boot 应用 - spring-ai-rag-knowledge-database]
        subgraph Security[安全认证层]
            JWTFilter[JWT 认证过滤器]
            SecurityConfig[Spring Security]
        end

        subgraph API[API 接口层]
            AuthApi[认证接口 /api/auth]
            UserApi[用户接口 /api/user]
            CategoryApi[分类接口 /api/category]
            DocumentApi[文档接口 /api/document]
            ChatSessionApi[会话接口 /api/chat/session]
            ChatMessageApi[聊天接口 /api/chat]
            StatsApi[统计接口 /api/stats]
        end

        subgraph Service[业务服务层]
            AuthService[认证服务]
            UserService[用户服务]
            CategoryService[分类服务]
            DocumentService[文档服务]
            FileService[文件存储服务]
            RagIngestService[RAG 摄入服务]
            ChatSessionService[会话服务]
            ChatMessageService[聊天服务]
            StatsService[统计服务]
            LogService[日志服务]
        end

        subgraph AI[AI 能力层]
            ChatClient[ChatClient]
            FunctionCallback[Function Callback<br/>工具调用]
            VectorStoreAdvisor[VectorStoreAdvisor]
            EmbeddingModel[EmbeddingModel]
            ChatMemory[ChatMemory]
            DocumentSplitter[文档切分器]
            DocumentReader[文档读取器<br/>Tika / Markdown]
            McpClient[MCP Client<br/>外部工具调用]
        end

        subgraph Repository[数据访问层]
            UserRepo[用户 Repository]
            CategoryRepo[分类 Repository]
            DocumentRepo[文档 Repository]
            ChatSessionRepo[会话 Repository]
            ChatMessageRepo[消息 Repository]
            LogRepo[日志 Repository]
            StatsRepo[统计 Repository]
        end
    end

    subgraph Data[数据存储层]
        MySQL[(MySQL / MariaDB<br/>关系型数据库)]
        Ollama[Ollama<br/>Embedding 服务]
        Redis[(Redis<br/>向量数据库 + 缓存)]
        OpenAI[OpenAI 兼容 API<br/>LLM 服务]
        McpServer[MCP Server<br/>外部工具服务]
        FileSystem[本地文件系统<br/>文档存储]
    end

    Browser --> Nginx
    App --> Nginx
    ThirdParty --> Nginx
    Nginx --> CORS
    CORS --> JWTFilter
    JWTFilter --> API
    SecurityConfig -.-> JWTFilter
    AuthApi --> AuthService
    UserApi --> UserService
    CategoryApi --> CategoryService
    DocumentApi --> DocumentService
    ChatSessionApi --> ChatSessionService
    ChatMessageApi --> ChatMessageService
    StatsApi --> StatsService
    AuthService --> UserService
    DocumentService --> FileService
    DocumentService --> RagIngestService
    ChatMessageService --> ChatSessionService
    StatsService --> UserService
    StatsService --> DocumentService
    StatsService --> LogService
    RagIngestService --> DocumentReader
    RagIngestService --> DocumentSplitter
    RagIngestService --> EmbeddingModel
    ChatMessageService --> ChatClient
    ChatClient --> FunctionCallback
    ChatClient --> VectorStoreAdvisor
    ChatClient --> ChatMemory
    ChatClient --> McpClient
    UserService --> UserRepo
    CategoryService --> CategoryRepo
    DocumentService --> DocumentRepo
    ChatSessionService --> ChatSessionRepo
    ChatMessageService --> ChatMessageRepo
    LogService --> LogRepo
    StatsService --> StatsRepo
    UserRepo --> MySQL
    CategoryRepo --> MySQL
    DocumentRepo --> MySQL
    ChatSessionRepo --> MySQL
    ChatMessageRepo --> MySQL
    LogRepo --> MySQL
    StatsRepo --> MySQL
    EmbeddingModel --> Ollama
    VectorStoreAdvisor --> Redis
    ChatClient --> OpenAI
    McpClient --> McpServer
    FileService --> FileSystem
```

## 📊 分层架构图

```mermaid
graph TD
    subgraph L7[配置层 Config]
        ConfigClasses[配置类<br/>Security / Redis / MyBatis / Web / MCP 等]
        Properties[配置属性类<br/>Rdb / Jwt / File / Cors / McpServer 等]
    end

    subgraph L6[公共层 Common]
        Constants[常量枚举<br/>DatabaseType / FileType / FileCategory / Status / VectorMetaKey]
        Exceptions[异常定义<br/>Base / Service / File / DataSource]
        Utils[工具类<br/>Jwt / Security / DocumentParse / Math / Message / Web]
    end

    subgraph L5[基础层 Base]
        BaseEntity[基础实体 BaseEntity]
        BaseDTO[基础 DTO<br/>BaseDTO / StoredFile]
        BasePO[基础 PO<br/>PageQueryPO]
        BaseVO[基础 VO<br/>Result / MultiResult]
    end

    subgraph L4[领域层 Domain]
        UserDomain[用户领域<br/>entity / dto / po / vo / mapstruct]
        CategoryDomain[分类领域<br/>entity / dto / po / vo / mapstruct]
        DocumentDomain[文档领域<br/>entity / dto / po / vo / mapstruct]
        ChatDomain[聊天领域<br/>session / message]
        LogDomain[日志领域<br/>entity / dto / mapstruct]
        LoginDomain[登录领域<br/>dto / po / vo / mapstruct]
    end

    subgraph L3[数据访问层 Repository]
        UserRepository[AppUserRepository]
        CategoryRepository[CategoryRepository]
        DocumentRepository[DocumentRepository]
        ChatSessionRepository[ChatSessionRepository]
        ChatMessageRepository[ChatMessageRepository]
        SystemLogRepository[SystemLogRepository]
        StatsRepository[StatsRepository]
    end

    subgraph L2[业务服务层 Service]
        AuthService2[AuthService]
        UserService2[AppUserService]
        CustomUserDetails[CustomUserDetailsService]
        CategoryService2[CategoryService]
        DocumentService2[DocumentService]
        FileStorageService[FileStorageService]
        RagIngestService2[RagIngestService]
        ChatSessionService2[ChatSessionService]
        ChatMessageService2[ChatMessageService]
        StatsService2[StatsService]
        SystemLogService[SystemLogService]
        DelayedTaskService2[DelayedTaskService]
    end

    subgraph L1[接口层 API]
        AuthApi2[AuthApi]
        AppUserApi[AppUserApi]
        CategoryApi2[CategoryApi]
        DocumentApi2[DocumentApi]
        ChatSessionApi2[ChatSessionApi]
        ChatMessageApi2[ChatMessageApi]
        StatsApi2[StatsApi]
    end

    subgraph L0[接入层 Filter / Handle]
        JwtAuthFilter[JwtAuthFilter]
        GlobalExceptionHandler[GlobalWebExceptionHandler]
        MetaObjectHandler[MyBatisPlusMetaObjectHandler]
    end

    L0 --> L1
    L1 --> L2
    L2 --> L3
    L3 --> L4
    L4 --> L5
    L6 --> L2
    L6 --> L3
    L6 --> L4
    L7 --> L0
    L7 --> L2
    L7 --> L3
```

## 🔄 核心流程

### 文档上传与向量化流程

```mermaid
flowchart TD
    Start([开始]) --> Upload[用户上传文档<br/>POST /api/document]
    Upload --> Validate[参数校验 & 权限验证<br/>ADMIN 角色]
    Validate --> FilterFiles[过滤空文件]
    FilterFiles --> SaveFile[保存文件到本地磁盘<br/>FileStorageService]
    SaveFile --> ParseDoc{文档类型判断}
    ParseDoc -->|Markdown| MarkdownReader[MarkdownDocumentReader 解析]
    ParseDoc -->|其他格式| TikaReader[TikaDocumentReader 解析]
    MarkdownReader --> Split[DocumentSplitter 切分文档块]
    TikaReader --> Split
    Split --> Embedding[EmbeddingModel 生成向量]
    Embedding --> SaveVector[向量写入 Redis Vector Store]
    SaveVector --> SaveMeta[文档元数据写入 MySQL<br/>t_kb_document]
    SaveMeta --> End([结束])
```

### RAG 问答流程

```mermaid
flowchart TD
    Start([开始]) --> Ask[用户提问<br/>POST /api/chat/ask]
    Ask --> Auth[JWT 认证 & 用户校验]
    Auth --> GetSession{是否指定 sessionId?}
    GetSession -->|是| LoadSession[加载会话及历史消息<br/>ChatMemory]
    GetSession -->|否| CreateSession[创建新会话<br/>t_chat_session]
    CreateSession --> LoadSession
    LoadSession --> IntentDetect{LLM 意图识别}
    IntentDetect -->|需要工具调用| CallTool[调用 Function Callback<br/>如查询文件列表]
    IntentDetect -->|向量检索| Retrieve[VectorStoreAdvisor 向量检索<br/>从 Redis 检索相关文档]
    CallTool --> BuildPrompt[构建 Prompt<br/>问题 + 工具返回结果]
    Retrieve --> BuildPrompt
    BuildPrompt --> CallLLM[调用 OpenAI 兼容 API LLM 生成回答<br/>ChatClient]
    CallLLM --> SaveMsg[保存用户消息和 AI 回答<br/>t_chat_message]
    SaveMsg --> SaveRefs[保存引用文档 refs JSON]
    SaveRefs --> UpdateSession[更新会话时间]
    UpdateSession --> Return[返回回答及引用文档]
    Return --> End([结束])
```

### Function Calling 工具调用流程

```mermaid
flowchart TD
    Start([用户提问]) --> Input[用户输入: '知识库中有哪些文档?']
    Input --> LLM[LLM 意图分析]
    LLM --> MatchTool{匹配到工具?}
    MatchTool -->|是| CallFunc[调用 DocumentToolFunction]
    MatchTool -->|否| NormalRAG[执行常规 RAG 检索]
    CallFunc --> QueryDB[DocumentService 查询数据库]
    QueryDB --> ReturnList[返回文件名列表]
    ReturnList --> LLMFormat[LLM 格式化结果为自然语言]
    NormalRAG --> LLMFormat
    LLMFormat --> Response[返回用户]
    Response --> End([结束])
```

### 认证与授权流程

```mermaid
flowchart TD
    Start([请求开始]) --> JwtFilter[JwtAuthFilter]
    JwtFilter --> HasToken{请求头是否有 Token?}
    HasToken -->|否| Continue[放行到匿名接口<br/>或返回 401]
    HasToken -->|是| ParseToken[JwtUtils 解析 Token]
    ParseToken --> Valid{Token 有效?}
    Valid -->|否| Continue
    Valid -->|是| GetUser[获取用户信息]
    GetUser --> SetContext[设置 SecurityContext]
    SetContext --> Api[进入 API 接口]
    Api --> PreAuthorize{"@PreAuthorize<br/>权限校验"}
    PreAuthorize -->|通过| Service[执行业务逻辑]
    PreAuthorize -->|拒绝| Forbidden[返回 403 无权限]
    Service --> End([请求结束])
    Forbidden --> End
    Continue --> End
```

---

<div style="display: flex; justify-content: space-between; align-items: center;">
  <span style="color: #888; font-size: 0.9em;">📅 更新日期：2026-07-03</span>
  <a href="#架构设计">⬆️ 返回顶部</a>
</div>
