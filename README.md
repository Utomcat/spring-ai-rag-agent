# Spring AI RAG Agent

<p style="text-align: center;">
  <img src="https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white" alt="Java Version" />
  <img src="https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?logo=spring-boot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/Spring%20AI-2.0.0-6DB33F?logo=spring&logoColor=white" alt="Spring AI" />
  <img src="https://img.shields.io/badge/Spring%20AI%20Alibaba-2.0.0-6DB33F?logo=spring&logoColor=white" alt="Spring AI Alibaba" />
  <img src="https://img.shields.io/badge/Spring%20Security-6.5+-6DB33F?logo=spring-security&logoColor=white" alt="Spring Security" />
  <img src="https://img.shields.io/badge/MySQL-9.7.0-4479A1?logo=mysql&logoColor=white" alt="MySQL" />
  <img src="https://img.shields.io/badge/MariaDB-3.5.9-003545?logo=mariadb&logoColor=white" alt="MariaDB" />
  <img src="https://img.shields.io/badge/Redis-Vector%20Store-DC382D?logo=redis&logoColor=white" alt="Redis" />
  <img src="https://img.shields.io/badge/Ollama-Embedding-000000?logo=ollama&logoColor=white" alt="Ollama" />
  <img src="https://img.shields.io/badge/Maven-3.8+-C71A36?logo=apache-maven&logoColor=white" alt="Maven" />
  <img src="https://img.shields.io/badge/MyBatis%20Plus-3.5.16-000000?logo=mybatis&logoColor=white" alt="MyBatis Plus" />
  <img src="https://img.shields.io/badge/JWT-000000?logo=jsonwebtokens&logoColor=white" alt="JWT" />
  <img src="https://img.shields.io/badge/Lombok-1.18.46-EE1C25" alt="Lombok" />
  <img src="https://img.shields.io/badge/MapStruct-1.7.0-6DB33F" alt="MapStruct" />
  <img src="https://img.shields.io/badge/Hutool-5.8.46-FF6600" alt="Hutool" />
  <img src="https://img.shields.io/badge/Apache%20Tika-003366?logo=apache&logoColor=white" alt="Apache Tika" />
  <img src="https://img.shields.io/badge/MCP-WebFlux-6DB33F?logo=spring&logoColor=white" alt="MCP" />
  <img src="https://img.shields.io/badge/Python-3.14+-3776AB?logo=python&logoColor=white" alt="Python" />
  <img src="https://img.shields.io/badge/Status-开发中-yellow" alt="Status" />
  <img src="https://img.shields.io/badge/License-Apache%202.0-green" alt="License" />
</p>

基于 **Spring AI 2.0.0** + **Spring Boot 4.1.1** 构建的智能知识库问答系统，采用 **Agent 架构**实现自主工具调用和智能决策，支持文档上传解析、向量存储检索与多轮对话。

> **作者**：ranyk
>
> **项目状态**：🚧 开发中
>
> **最后更新**：2026-07-14

---

## 📚 文档导航

### 快速入门

| 文档                          | 说明                |
|-----------------------------|-------------------|
| [快速开始](md/quickstart.md)    | 环境准备、安装部署、运行指南    |
| [配置文件](md/configuration.md) | 各配置文件详解和环境变量支持    |
| [API 接口](md/api.md)         | 完整的 REST API 接口文档 |

### 核心概念

| 文档                                             | 说明                              |
|------------------------------------------------|---------------------------------|
| [架构设计](md/architecture.md)                     | 总体架构、分层设计和核心流程图                 |
| [Function Calling](md/function-calling.md)     | Agent 工具扩展和 MCP 协议支持            |
| [MCP + Agent + Skills](md/mcp-agent-skills.md) | MCP + Agent + Skills 第一阶段实现使用指南 |

### 子项目文档

| 子项目                   | 文档                                           | 说明                                              |
|-----------------------|----------------------------------------------|-------------------------------------------------|
| spring-ai-rag-starter | [Starter 模块](md/starter-modules.md)          | 可复用 Starter 组件库，18 个功能模块                        |
| spring-ai-rag-example | [示例项目](md/example-project.md)                | 知识库问答系统示例应用，演示 Starter 组件的使用                    |
| python-mcp-server     | [Python MCP Server](md/python-mcp-server.md) | Python MCP Server 子项目完整技术文档（含概览、工具列表、使用指南、版本历史） |
|                       | [Redis 双层缓存](md/redis-cache.md)              | Redis 双DB架构缓存系统（含快速开始、架构设计、配置说明）                |

---

## 📖 项目概述

本项目是一个基于 Spring AI 的智能知识库问答系统，采用 **Agent 架构**实现自主工具调用和智能决策。系统由三个子项目组成，采用多模块协同架构：

- **spring-ai-rag-starter**：可复用的 Starter 组件库，封装了 AI、安全、数据源、向量存储等 18 个功能模块
- **spring-ai-rag-example**：基于 Starter 组件构建的知识库问答示例应用，包含完整的业务逻辑
- **python-mcp-server**：独立的 Python MCP Server，提供网络搜索、数据分析等扩展工具能力

### 🎯 核心特点

- **Agent 架构**：采用 Spring AI Alibaba Agent Framework，支持自主意图识别和工具调用
- **双模型架构**：OpenAI 兼容 API（LLM 聊天）+ Ollama（Embedding 向量化）
- **向量存储**：基于 Redis Vector Store 实现文档向量存储与相似度检索
- **工具扩展**：支持 Function Calling 和 MCP 协议，可扩展知识库检索、网络搜索、数据分析等能力
- **引用提取**：自定义 Advisor 实现日志记录和引用文档自动提取
- **多模块设计**：Java Starter 组件库 + Java 示例应用 + Python MCP Server 三模块协同
- **虚拟线程**：基于 JDK 21 虚拟线程提升并发性能
- **Redis双层缓存**：Python MCP Server 中热点数据（DB 1）+ 完整数据（DB 2）分离存储，智能晋升机制

---

## 🛠️ 技术栈

| 类别        | 技术                                            |
|-----------|-----------------------------------------------|
| 框架        | Spring Boot 4.1.1-SNAPSHOT, Spring AI 2.0.0   |
| Java 版本   | Java 21                                       |
| 关系型数据库    | MySQL 9.7.0 / MariaDB 3.5.9                   |
| 向量数据库     | Redis（使用 Jedis 连接）                            |
| LLM       | OpenAI 兼容 API (小米 Mimo v2.5-pro-ultraspeed)   |
| Embedding | Ollama (embeddinggemma:latest)                |
| ORM       | MyBatis Plus 3.5.16                           |
| 安全        | Spring Security + JWT                         |
| 工具库       | Lombok, MapStruct, Hutool                     |
| 文档解析      | Tika, Markdown Reader                         |
| 数据校验      | Spring Boot Starter Validation                |
| 虚拟线程      | Spring Boot Virtual Threads                   |
| MCP       | Spring AI MCP Client (WebFlux)                |
| Agent框架   | Spring AI Alibaba Agent Framework（ReactAgent） |

---

## 📁 项目结构

```
spring-ai-rag-agent/
├── pom.xml                              # 父 POM（依赖管理、插件配置）
├── LICENSE                              # Apache 2.0 许可证
├── README.md                            # 项目主文档（本文件）
│
├── doc/                                 # 项目文档与脚本
│   └── database/
│       └── init_database.sql            # 数据库初始化脚本
│
├── md/                                  # 项目详细文档目录
│
├── spring-ai-rag-starter/               # Starter 组件库（18 个可复用模块）
│   ├── spring-ai-rag-starter-agent          # Agent 编排与 Advisor
│   ├── spring-ai-rag-starter-annotations    # 自定义注解
│   ├── spring-ai-rag-starter-base           # 基础类（Result、DTO、配置）
│   ├── spring-ai-rag-starter-chat-memory    # 聊天记忆
│   ├── spring-ai-rag-starter-common         # 公共组件（常量、异常、工具）
│   ├── spring-ai-rag-starter-datasource     # 数据源与 MyBatis Plus
│   ├── spring-ai-rag-starter-document-splitter  # 文档分割
│   ├── spring-ai-rag-starter-international  # 国际化
│   ├── spring-ai-rag-starter-llm            # LLM 模型配置
│   ├── spring-ai-rag-starter-log            # 日志
│   ├── spring-ai-rag-starter-mcp            # MCP 客户端
│   ├── spring-ai-rag-starter-redis          # Redis 配置
│   ├── spring-ai-rag-starter-security       # 安全认证（JWT + Spring Security）
│   ├── spring-ai-rag-starter-skill          # Skills 执行引擎
│   ├── spring-ai-rag-starter-task           # 任务调度
│   ├── spring-ai-rag-starter-tool           # AI 工具（Function Calling）
│   ├── spring-ai-rag-starter-vector-store   # 向量存储（Redis Vector Store）
│   └── spring-ai-rag-starter-web            # Web 配置（CORS、异常处理等）
│
├── spring-ai-rag-example/               # 示例项目
│   └── spring-ai-rag-example-knowledge-database  # 知识库问答系统示例应用
│
├── python-mcp-server/                   # Python MCP Server（扩展工具能力）
│   ├── main.py                          # MCP Server 启动入口
│   ├── pyproject.toml                   # Python 项目依赖配置
│   ├── config/                          # 配置模块
│   ├── server/                          # MCP 服务器核心
│   ├── search/                          # 搜索引擎模块
│   ├── parser/                          # HTML 解析模块
│   ├── datasource/                      # 数据源适配器
│   ├── analyzer/                        # 分析模块
│   ├── visualization/                   # 可视化模块
│   ├── tools/                           # MCP 工具实现
│   ├── models/                          # 数据模型
│   ├── utils/                           # 工具模块
│   └── tests/                           # 测试脚本
│
└── upload/                              # 文件上传存储目录
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

---

## 📜 License

Apache License 2.0

---

<div style="display: flex; justify-content: space-between; align-items: center;">
  <span style="color: #888; font-size: 0.9em;">📅 最后更新：2026-07-14</span>
  <a href="#spring-ai-rag-agent">⬆️ 返回顶部</a>
</div>
