# Spring AI RAG Example 示例项目

> **← 返回主文档**：[README.md](../README.md)

`spring-ai-rag-example` 是基于 `spring-ai-rag-starter` 组件库构建的知识库问答示例项目，演示了如何使用 Starter 模块搭建一个完整的智能知识库问答系统。

---

## 🧩 子模块

| 子模块                                      | 说明          |
|------------------------------------------|-------------|
| spring-ai-rag-example-knowledge-database | 知识库问答系统示例应用 |

---

## 🏗️ 项目架构

```
spring-ai-rag-example-knowledge-database/
└── src/main/java/com/ranyk/spring/ai/rag/knowledge/database/
    ├── SpringAiRagExampleKnowledgeDatabaseApplication.java  # 启动类
    │
    ├── ai/                          # AI 能力层
    │   └── tools/                   # Function Calling 工具
    │       └── DocumentToolFunction.java        # 知识库文件列表查询工具
    │
    ├── api/                         # REST API 接口层
    │   ├── auth/AuthApi.java                    # 认证接口
    │   ├── user/AppUserApi.java                 # 用户接口
    │   ├── category/CategoryApi.java            # 分类接口
    │   ├── document/DocumentApi.java            # 文档接口
    │   ├── chat/                                # 聊天接口
    │   │   ├── ChatSessionApi.java              # 会话接口
    │   │   └── ChatMessageApi.java              # 消息接口
    │   └── stats/StatsApi.java                  # 统计接口
    │
    ├── service/                     # 业务服务层
    │   ├── auth/AuthService.java                # 认证服务
    │   ├── user/AppUserService.java             # 用户服务
    │   ├── user/CustomUserDetailsService.java   # UserDetailsService 实现
    │   ├── category/CategoryService.java        # 分类服务
    │   ├── document/DocumentService.java        # 文档服务
    │   ├── file/FileStorageService.java         # 文件存储服务
    │   ├── rag/RagIngestService.java            # RAG 摄入服务
    │   ├── chat/session/ChatSessionService.java # 会话服务
    │   ├── chat/message/ChatMessageService.java # 消息服务
    │   ├── stats/StatsService.java              # 统计服务
    │   ├── log/SystemLogService.java            # 日志服务
    │   └── task/ChatMessageAsyncTask.java       # 异步任务
    │
    ├── repository/                  # 数据访问层
    │   ├── user/AppUserRepository.java
    │   ├── category/CategoryRepository.java
    │   ├── document/DocumentRepository.java
    │   ├── chat/session/ChatSessionRepository.java
    │   ├── chat/message/ChatMessageRepository.java
    │   ├── log/SystemLogRepository.java
    │   └── stats/StatsRepository.java
    │
    └── domain/                      # 领域层
        ├── user/                    # 用户领域（entity/dto/po/vo/mapstruct）
        ├── category/                # 分类领域
        ├── document/                # 文档领域
        ├── chat/                    # 聊天领域（session/message）
        ├── log/                     # 日志领域
        └── login/                   # 登录领域
```

---

## ✨ 核心功能

### 基础功能

| 功能      | 说明                                     |
|---------|----------------------------------------|
| 用户认证    | JWT Token 认证，支持登录、密码加密存储               |
| 用户管理    | 用户 CRUD、头像上传、密码修改、个人资料更新               |
| 知识库分类   | 分类的增删查，支持排序                            |
| 文档管理    | 支持多文件上传、Tika 解析、Markdown 解析、文档向量化、向量存储 |
| 聊天会话    | 支持多轮对话、会话列表、会话删除、上下文记忆                 |
| 统计仪表盘   | 管理员概览统计数据                              |
| 系统日志    | 操作日志记录                                 |
| 国际化     | 支持中英文多语言                               |
| RBAC 权限 | 基于角色的访问控制（ADMIN / USER）                |

### AI 能力

| 功能               | 说明                                     |
|------------------|----------------------------------------|
| 智能问答             | 基于 Agent 架构的智能对话，支持自主工具调用和知识库检索        |
| 向量检索             | 基于 Redis Vector Store 实现相似度检索，提供引用文档展示 |
| Function Calling | 支持 AI 工具调用，包括知识库文件列表查询、知识库语义检索等        |
| MCP 客户端          | 支持 MCP 协议，可连接外部 MCP Server 扩展工具能力      |
| Advisor 拦截       | 自定义 Advisor 实现日志记录和引用文档提取              |

---

## 🔧 依赖的 Starter 模块

示例项目通过引入 `spring-ai-rag-starter` 中的各模块来组装完整功能：

| Starter 模块                              | 在示例项目中的用途                |
|-----------------------------------------|--------------------------|
| spring-ai-rag-starter-base              | 统一响应体、基础 DTO/VO          |
| spring-ai-rag-starter-common            | 常量枚举、异常体系                |
| spring-ai-rag-starter-datasource        | MyBatis Plus 数据源         |
| spring-ai-rag-starter-security          | JWT 认证 + Spring Security |
| spring-ai-rag-starter-web               | Web 配置、全局异常处理            |
| spring-ai-rag-starter-llm               | LLM 模型配置                 |
| spring-ai-rag-starter-redis             | Redis 连接配置               |
| spring-ai-rag-starter-vector-store      | 向量存储                     |
| spring-ai-rag-starter-agent             | Agent 编排与 Advisor        |
| spring-ai-rag-starter-tool              | Function Calling 工具      |
| spring-ai-rag-starter-chat-memory       | 聊天记忆                     |
| spring-ai-rag-starter-document-splitter | 文档分割                     |
| spring-ai-rag-starter-international     | 国际化                      |
| spring-ai-rag-starter-task              | 异步任务                     |
| spring-ai-rag-starter-mcp               | MCP 客户端                  |

---

## 📝 配置文件

示例项目的配置文件位于 `src/main/resources/` 下，采用多配置文件拆分模式：

| 配置文件                  | 配置内容               |
|-----------------------|--------------------|
| `application.yml`     | 主配置（端口、应用名、引入其他配置） |
| `rdb-datasource.yml`  | 关系型数据库数据源          |
| `nrdb-datasource.yml` | Redis 数据源          |
| `vdb-datasource.yml`  | 向量数据库配置            |
| `llm-model.yml`       | LLM 模型配置           |
| `mybatis-plus.yml`    | MyBatis Plus 配置    |
| `jwt.yml`             | JWT 配置             |
| `file.yml`            | 文件上传配置             |
| `language.yml`        | 国际化配置              |
| `log.yml`             | 日志配置               |
| `rdb.yml`             | ORM 配置             |
| `doc-splitter.yml`    | 文档分割配置             |
| `system.yml`          | 系统配置               |
| `mcp.yml`             | MCP 配置             |
| `tomcat.yml`          | Tomcat 服务器配置       |

详细配置说明请参考 [配置文件说明](configuration.md)。

---

## 🚀 快速开始

### 1. 初始化数据库

请参考 [快速开始 - 初始化数据库](quickstart.md#初始化数据库) 完成数据库初始化和默认管理员账号创建。

### 2. 修改配置

编辑 `spring-ai-rag-example/spring-ai-rag-example-knowledge-database/src/main/resources/` 下的配置文件，主要需要配置：
- 数据库连接信息（`rdb-datasource.yml`）
- Redis 连接信息（`nrdb-datasource.yml`）
- LLM 模型配置（`llm-model.yml`）

详细配置说明请参考 [配置文件说明](configuration.md)。

### 3. 构建运行

请参考 [快速开始 - 构建运行](quickstart.md#构建运行) 了解完整的构建和运行方式。示例项目的 Maven 模块路径为 `spring-ai-rag-example/spring-ai-rag-example-knowledge-database`。

### 4. 访问服务

服务默认运行在 `http://localhost:8083`，登录和使用方式请参考 [快速开始 - 使用流程](quickstart.md#使用流程)。

---

## 🗄️ 数据库

示例项目使用与主项目相同的数据库表结构，包含 6 张核心数据表（用户表、知识库分类表、知识文档表、聊天会话表、聊天消息表、系统日志表）。

详细数据库表说明请参考 [README.md - 数据库表](../README.md#数据库表)，数据库初始化脚本位于 `doc/database/init_database.sql`。

---

<div style="display: flex; justify-content: space-between; align-items: center;">
  <span style="color: #888; font-size: 0.9em;">📅 最后更新：2026-07-14</span>
  <a href="#spring-ai-rag-example-示例项目">⬆️ 返回顶部</a>
</div>
