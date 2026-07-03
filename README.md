# spring-ai-rag-study

<p style="text-align: center;">
  <img src="https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white" alt="Java Version" />
  <img src="https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?logo=spring-boot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/Spring%20AI-2.0.0-6DB33F?logo=spring&logoColor=white" alt="Spring AI" />
  <img src="https://img.shields.io/badge/Spring%20Security-6.5+-6DB33F?logo=spring-security&logoColor=white" alt="Spring Security" />
  <img src="https://img.shields.io/badge/MySQL-9.7.0-4479A1?logo=mysql&logoColor=white" alt="MySQL" />
  <img src="https://img.shields.io/badge/MariaDB-3.5.9-003545?logo=mariadb&logoColor=white" alt="MariaDB" />
  <img src="https://img.shields.io/badge/Redis-Vector%20Store-DC382D?logo=redis&logoColor=white" alt="Redis" />
  <img src="https://img.shields.io/badge/Ollama-Embedding-000000?logo=ollama&logoColor=white" alt="Ollama" />
  <img src="https://img.shields.io/badge/Maven-3.8+-C71A36?logo=apache-maven&logoColor=white" alt="Maven" />
  <img src="https://img.shields.io/badge/MyBatis%20Plus-3.5.16-000000" alt="MyBatis Plus" />
  <img src="https://img.shields.io/badge/Python-3.14+-3776AB?logo=python&logoColor=white" alt="Python" />
  <img src="https://img.shields.io/badge/Status-开发中-yellow" alt="Status" />
  <img src="https://img.shields.io/badge/License-Apache%202.0-green" alt="License" />
</p>

Spring AI RAG（检索增强生成）知识库系统后端。

> **作者**：ranyk
>
> **项目状态**：🚧 开发中

## 📚 文档导航

| 文档 | 说明 |
|------|------|
| [快速开始](md/quickstart.md) | 项目安装、配置和运行指南 |
| [架构设计](md/architecture.md) | 总体架构、分层架构和核心流程图 |
| [API 接口](md/api.md) | 完整的 REST API 接口文档 |
| [配置文件](md/configuration.md) | 各配置文件详解和环境变量支持 |
| [Function Calling](md/function-calling.md) | 工具扩展和 MCP 支持说明 |
| [Python MCP Server](md/mcp-server.md) | Python MCP Server 子项目详细文档 |

## 📖 项目概述

基于 Spring AI 2.0.0 + Spring Boot 4.1.1 构建的 RAG
知识库系统，支持文档上传解析、向量存储检索与智能问答对话。系统采用分层架构设计，提供完整的用户认证、知识库分类管理、文档管理、聊天会话等功能，并支持
Function Calling 和 MCP（Model Context Protocol）扩展工具能力。

**项目特点**：

- **双模型架构**：使用 OpenAI 兼容 API（小米 Mimo）作为 LLM 聊天模型，Ollama 作为 Embedding 模型
- **向量存储**：基于 Redis Vector Store 实现文档向量存储与相似度检索
- **Agent 架构**：采用 Spring AI Alibaba Agent Framework，支持自主工具调用和知识库检索
- **工具扩展**：支持 Spring AI Function Calling 和 MCP 协议，可扩展网络搜索等工具能力
- **多模块设计**：Java 后端 + Python MCP Server 双模块架构
- **Advisor 拦截**：自定义 Advisor 实现日志记录和引用文档提取

## 🛠️ 技术栈

| 类别        | 技术                                          |
|-----------|---------------------------------------------|
| 框架        | Spring Boot 4.1.1-SNAPSHOT, Spring AI 2.0.0 |
| Java 版本   | Java 21                                     |
| 关系型数据库    | MySQL 9.7.0 / MariaDB 3.5.9                 |
| 向量数据库     | Redis（使用 Jedis 连接）                          |
| LLM       | OpenAI 兼容 API (小米 Mimo v2.5-pro-ultraspeed) |
| Embedding | Ollama (embeddinggemma:latest)              |
| ORM       | MyBatis Plus 3.5.16                         |
| 安全        | Spring Security + JWT                       |
| 工具库       | Lombok, MapStruct, Hutool                   |
| 文档解析      | Tika, Markdown Reader                       |
| 数据校验      | Spring Boot Starter Validation              |
| 虚拟线程      | Spring Boot Virtual Threads                 |
| MCP       | Spring AI MCP Client (WebFlux)              |
| Agent框架   | Spring AI Alibaba Agent Framework           |

## 📁 模块结构

```
spring-ai-rag-study/
├── pom.xml                          # 父 POM（依赖管理、插件配置）
├── LICENSE                          # Apache 2.0 许可证
├── .gitignore                       # Git 忽略配置
├── doc/
│   └── init_database.sql             # 数据库初始化脚本
├── md/                              # 文档目录
│   ├── architecture.md               # 架构设计
│   ├── api.md                        # API 接口文档
│   ├── configuration.md              # 配置文件说明
│   ├── quickstart.md                 # 快速开始
│   ├── function-calling.md           # Function Calling 和 MCP
│   └── mcp-server.md                 # Python MCP Server 文档
├── python-mcp-server/               # Python MCP Server（扩展工具能力）
│   ├── README.md                     # 子项目说明文档
│   ├── main.py                       # MCP Server 启动入口
│   ├── pyproject.toml               # Python 项目依赖配置
│   ├── .env                         # 环境变量配置
│   ├── .env.example                 # 环境变量示例
│   └── script/
│       ├── __init__.py
│       └── MCP/
│           ├── __init__.py
│           └── web_search_server.py  # 网络搜索 MCP 工具实现
└── spring-ai-rag-knowledge-database/ # 知识库系统主模块
    ├── pom.xml
    └── src/main/
        ├── java/com/ranyk/spring/ai/rag/knowledge/database/
        │   ├── SpringAiRagKnowledgeDatabaseApplication.java
        │   ├── ai/                   # AI 相关
        │   ├── api/                  # REST API 接口层
        │   ├── base/                 # 基础类
        │   ├── common/               # 公共组件
        │   ├── config/               # 配置类
        │   ├── domain/               # 业务领域层
        │   ├── filter/               # 过滤器
        │   ├── handle/               # 全局处理器
        │   ├── repository/           # 数据访问层
        │   ├── service/              # 业务服务层
        │   └── utils/                # 工具类
        └── resources/
            ├── application.yml       # 主配置
            ├── mybatis-plus.yml      # MyBatis Plus 配置
            ├── rdb-datasource.yml    # 关系型数据库配置
            ├── vdb-datasource.yml    # 向量数据库配置
            ├── nrdb-datasource.yml   # 非关系型数据库配置
            ├── llm-model.yml         # LLM 模型配置
            ├── jwt.yml               # JWT 配置
            ├── file.yml              # 文件上传配置
            ├── language.yml          # 国际化配置
            ├── log.yml               # 日志配置
            ├── rdb.yml               # 关系型数据库 ORM 配置
            ├── doc-splitter.yml      # 文档分割配置
            ├── system.yml            # 系统配置
            ├── mcp.yml               # MCP 配置
            ├── mcp-servers.json      # MCP Server 配置
            ├── i18n/                 # 国际化资源文件
            └── repository/           # MyBatis XML 映射文件
```

## 🗄️ 数据库表

| 表名             | 说明                    |
|----------------|-----------------------|
| t_user         | 用户表（支持 ADMIN/USER 角色） |
| t_kb_category  | 知识库分类表                |
| t_kb_document  | 知识文档表（存储文件元数据与向量状态）   |
| t_chat_session | 聊天会话表                 |
| t_chat_message | 聊天消息表（包含引用文档 JSON）    |
| t_system_log   | 系统日志表                 |

## ✨ 核心功能

- **用户认证**：JWT Token 认证，支持登录、密码加密存储
- **用户管理**：用户 CRUD、头像上传、密码修改、个人资料更新
- **知识库分类**：分类的增删查，支持排序
- **文档管理**：支持多文件上传、Tika 解析、Markdown 解析、文档向量化、向量存储、文件列表查询
- **向量检索**：基于 Redis Vector Store 实现相似度检索
- **智能问答**：基于 Agent 架构的智能对话，支持自主工具调用和知识库检索，提供引用文档展示
- **Function Calling**：支持 AI 工具调用，包括知识库文件列表查询、知识库语义检索等扩展能力
- **MCP 客户端**：支持 MCP (Model Context Protocol) 协议，可连接外部 MCP Server 扩展工具能力
- **聊天会话**：支持多轮对话、会话列表、会话删除、上下文记忆
- **统计仪表盘**：管理员概览统计数据
- **系统日志**：操作日志记录
- **国际化**：支持中英文多语言
- **RBAC 权限**：基于角色的访问控制（ADMIN / USER）

## 🐍 Python MCP Server 子项目

基于 MCP（Model Context Protocol）协议的 Python 服务器，为 Spring AI RAG 知识库系统提供网络搜索等扩展工具能力。详细说明请参考 [Python MCP Server 文档](md/mcp-server.md)。

## 📜 License

Apache License 2.0

---

<div style="display: flex; justify-content: space-between; align-items: center;">
  <span style="color: #888; font-size: 0.9em;">📅 更新日期：2026-07-04</span>
  <a href="#spring-ai-rag-study">⬆️ 返回顶部</a>
</div>
