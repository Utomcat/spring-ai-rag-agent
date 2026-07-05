# Spring AI RAG Agent

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

基于 **Spring AI 2.0.0** + **Spring Boot 4.1.1** 构建的智能知识库问答系统，采用 **Agent 架构**实现自主工具调用和智能决策，支持文档上传解析、向量存储检索与多轮对话。

> **作者**：ranyk
>
> **项目状态**：🚧 开发中
>
> **最后更新**：2026-07-06

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

### Python MCP Server

| 文档                                              | 说明                         |
|-------------------------------------------------|----------------------------|
| [MCP Server 概览](md/mcp-server.md)               | Python MCP Server 子项目介绍    |
| [完整技术文档](md/python-mcp-server.md)               | 工具列表、架构设计、配置说明等详细说明        |
| [fetch_webpage 使用指南](md/fetch-webpage-usage.md) | 网页内容抓取工具详细说明               |
| [优化说明 v1.1.0](md/optimization-summary.md)       | 缓存、限流和动态页面扩展功能             |
| [增强说明 v1.2.0](md/enhancement-summary.md)        | 管理工具、Playwright适配器和LRU淘汰策略 |

## 📖 项目概述

本项目是一个基于 Spring AI 的智能知识库问答系统，采用 **Agent 架构**实现自主工具调用和智能决策。系统支持文档上传解析、向量存储检索与多轮对话，并可通过 MCP（Model Context Protocol）协议扩展网络搜索、数据分析等高级能力。

### 🎯 核心特点

- **Agent 架构**：采用 Spring AI Alibaba Agent Framework，支持自主意图识别和工具调用
- **双模型架构**：OpenAI 兼容 API（LLM 聊天）+ Ollama（Embedding 向量化）
- **向量存储**：基于 Redis Vector Store 实现文档向量存储与相似度检索
- **工具扩展**：支持 Function Calling 和 MCP 协议，可扩展知识库检索、网络搜索、数据分析等能力
- **引用提取**：自定义 Advisor 实现日志记录和引用文档自动提取
- **多模块设计**：Java 后端 + Python MCP Server 双模块协同工作
- **虚拟线程**：基于 JDK 21 虚拟线程提升并发性能
- **Skills 执行引擎**：支持技能注册、批量执行、链式执行和异步执行

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

## 📁 项目结构

```
spring-ai-rag-agent/
├── pom.xml                              # 父 POM（依赖管理、插件配置）
├── LICENSE                              # Apache 2.0 许可证
├── .gitignore                           # Git 忽略配置
├── README.md                            # 项目主文档（本文件）
│
├── doc/                                 # 数据库脚本
│   └── init_database.sql                # 数据库初始化脚本
│
├── md/                                  # 项目详细文档
│   ├── quickstart.md                    # 快速开始指南
│   ├── architecture.md                  # 架构设计文档
│   ├── api.md                           # API 接口文档
│   ├── configuration.md                 # 配置文件说明
│   ├── function-calling.md              # Function Calling 和 MCP
│   ├── mcp-server.md                    # Python MCP Server 概览
│   ├── mcp-agent-skills.md              # MCP + Agent + Skills 使用指南
│   ├── python-mcp-server.md             # Python MCP Server 完整技术文档
│   ├── fetch-webpage-usage.md           # fetch_webpage 工具使用指南
│   ├── optimization-summary.md          # v1.1.0 优化说明
│   └── enhancement-summary.md           # v1.2.0 增强说明
│
├── python-mcp-server/                   # Python MCP Server(扩展工具能力)
│   ├── main.py                          # MCP Server 启动入口
│   ├── pyproject.toml                   # Python 项目依赖配置
│   ├── uv.lock                          # UV 锁定文件
│   ├── .env                             # 环境变量配置
│   ├── .env.example                     # 环境变量示例
│   │
│   ├── config/                          # 配置模块
│   │   ├── __init__.py
│   │   ├── constants.py                 # 常量定义
│   │   └── settings.py                  # 配置加载
│   │
│   ├── server/                          # MCP 服务器核心
│   │   ├── __init__.py
│   │   └── mcp_server.py                # MCP Server 实现
│   │
│   ├── search/                          # 搜索引擎模块
│   │   ├── __init__.py
│   │   └── engine.py                    # 搜索引擎封装
│   │
│   ├── parser/                          # HTML 解析模块
│   │   ├── __init__.py
│   │   └── html_parser.py               # HTML 解析器
│   │
│   ├── datasource/                      # 数据源适配器
│   │   ├── __init__.py
│   │   ├── base_adapter.py              # 适配器基类
│   │   ├── webpage_adapter.py           # 网页适配器
│   │   ├── api_adapter.py               # API 适配器
│   │   ├── file_adapter.py              # 文件适配器
│   │   ├── dynamic_page_adapter.py      # 动态页面适配器(Playwright)
│   │   └── factory.py                   # 工厂模式
│   │
│   ├── analyzer/                        # 分析模块
│   │   ├── __init__.py
│   │   ├── data_extractor.py            # 数据提取器
│   │   ├── statistic_calculator.py      # 统计计算器
│   │   ├── trend_analyzer.py            # 趋势分析器
│   │   └── report_generator.py          # 报告生成器
│   │
│   ├── visualization/                   # 可视化模块
│   │   ├── __init__.py
│   │   └── chart_generator.py           # 图表生成器
│   │
│   ├── tools/                           # MCP 工具实现
│   │   ├── __init__.py
│   │   ├── web_search_tool.py           # 网络搜索工具
│   │   ├── fetch_webpage_tool.py        # 网页抓取工具
│   │   ├── fetch_data_tool.py           # 数据获取工具
│   │   ├── analyze_data_tool.py         # 数据分析工具
│   │   ├── generate_chart_data_tool.py  # 图表数据生成工具
│   │   ├── generate_report_tool.py      # 报告生成工具
│   │   └── trend_analysis_tool.py       # 趋势分析工具
│   │
│   ├── models/                          # 数据模型
│   │   ├── __init__.py
│   │   ├── data_source.py               # 数据源模型
│   │   └── search_result.py             # 搜索结果模型
│   │
│   ├── utils/                           # 工具模块
│   │   ├── __init__.py
│   │   ├── http_client.py               # HTTP 客户端
│   │   ├── cache_manager.py             # 缓存管理器
│   │   ├── url_rate_limiter.py          # URL 限流器
│   │   ├── url_validator.py             # URL 验证器
│   │   ├── validator.py                 # 通用验证器
│   │   ├── content_cleaner.py           # 内容清洗器
│   │   ├── logger.py                    # 日志工具
│   │   ├── banner.py                    # 启动横幅
│   │   └── launcher.py                  # 启动器
│   │
│   ├── script/                          # 脚本模块
│   │   ├── __init__.py
│   │   └── MCP/
│   │       ├── __init__.py
│   │       └── web_search_server.py     # Web 搜索服务器
│   │
│   └── tests/                           # 测试脚本
│       ├── __init__.py
│       ├── test_phase1.py               # Phase 1 测试
│       ├── test_phase2.py               # Phase 2 测试
│       ├── test_phase3.py               # Phase 3 测试
│       ├── test_fetch_webpage.py        # 网页抓取测试
│       ├── test_new_features.py         # 新功能测试
│       └── test_optimized_fetch.py      # 优化功能测试
│
└── spring-ai-rag-knowledge-database/    # 知识库系统主模块
    ├── pom.xml                          # Maven 配置文件
    ├── upload/avatar/                   # 默认头像资源
    │   ├── default.png
    │   ├── default1.jpg
    │   └── default2.jpg
    │
    └── src/main/
        ├── java/com/ranyk/spring/ai/rag/knowledge/database/
        │   ├── SpringAiRagKnowledgeDatabaseApplication.java  # 启动类
        │   │
        │   ├── ai/                      # AI 相关
        │   │   ├── advisor/             # Advisor 拦截器
        │   │   │   ├── LoggingAdvisor.java              # 日志记录 Advisor
        │   │   │   └── ReferenceExtractionAdvisor.java  # 引用提取 Advisor
        │   │   ├── agent/               # Agent 编排
        │   │   └── tools/               # Function Calling 工具
        │   │       ├── KnowledgeBaseListTool.java       # 知识库列表工具
        │   │       └── KnowledgeBaseSearchTool.java     # 知识库搜索工具
        │   │
        │   ├── api/                     # REST API 接口层
        │   │   ├── auth/                # 认证接口
        │   │   ├── user/                # 用户接口
        │   │   ├── category/            # 分类接口
        │   │   ├── document/            # 文档接口
        │   │   ├── chat/                # 聊天接口
        │   │   ├── stats/               # 统计接口
        │   │   └── system/              # 系统接口
        │   │
        │   ├── base/                    # 基础类
        │   │   └── domain/              # 基础领域对象
        │   │
        │   ├── common/                  # 公共组件
        │   │   ├── annotation/          # 自定义注解
        │   │   └── exception/           # 异常处理
        │   │
        │   ├── config/                  # 配置类 (20+ 配置类)
        │   │   ├── AiConfig.java        # AI 配置
        │   │   ├── McpConfig.java       # MCP 配置
        │   │   ├── SecurityConfig.java  # 安全配置
        │   │   └── ...                  # 其他配置
        │   │
        │   ├── domain/                  # 业务领域层
        │   │   ├── entity/              # 实体类
        │   │   ├── dto/                 # 数据传输对象
        │   │   └── vo/                  # 视图对象
        │   │
        │   ├── filter/                  # 过滤器
        │   │   ├── JwtAuthenticationFilter.java         # JWT 认证过滤器
        │   │   └── RequestLoggingFilter.java            # 请求日志过滤器
        │   │
        │   ├── handle/                  # 全局处理器
        │   │   ├── GlobalExceptionHandler.java          # 全局异常处理器
        │   │   └── AuthenticationEntryPoint.java        # 认证入口点
        │   │
        │   ├── repository/              # 数据访问层
        │   │   ├── user/                # 用户仓库
        │   │   ├── category/            # 分类仓库
        │   │   ├── document/            # 文档仓库
        │   │   ├── chat/                # 聊天仓库
        │   │   ├── log/                 # 日志仓库
        │   │   └── system/              # 系统仓库
        │   │
        │   ├── service/                 # 业务服务层
        │   │   ├── auth/                # 认证服务
        │   │   ├── user/                # 用户服务
        │   │   ├── category/            # 分类服务
        │   │   ├── document/            # 文档服务
        │   │   ├── chat/                # 聊天服务
        │   │   ├── stats/               # 统计服务
        │   │   ├── vector/              # 向量服务
        │   │   ├── file/                # 文件服务
        │   │   └── system/              # 系统服务
        │   │
        │   └── utils/                   # 工具类
        │       ├── JwtUtil.java         # JWT 工具
        │       ├── FileUtil.java        # 文件工具
        │       └── ...                  # 其他工具
        │
        └── resources/
            ├── application.yml          # 主配置
            ├── mybatis-plus.yml         # MyBatis Plus 配置
            ├── rdb-datasource.yml       # 关系型数据库配置
            ├── nrdb-datasource.yml      # 非关系型数据库配置
            ├── vdb-datasource.yml       # 向量数据库配置
            ├── llm-model.yml            # LLM 模型配置
            ├── jwt.yml                  # JWT 配置
            ├── file.yml                 # 文件上传配置
            ├── language.yml             # 国际化配置
            ├── log.yml                  # 日志配置
            ├── rdb.yml                  # 关系型数据库 ORM 配置
            ├── doc-splitter.yml         # 文档分割配置
            ├── system.yml               # 系统配置
            ├── tomcat.yml               # Tomcat 配置
            ├── mcp.yml                  # MCP 配置
            ├── mcp-servers.json         # MCP Server 配置
            │
            ├── i18n/                    # 国际化资源文件
            │   ├── messages_zh_CN.properties
            │   ├── messages_en_US.properties
            │   └── messages.properties
            │
            ├── repository/              # MyBatis XML 映射文件
            │   ├── user/                # 用户映射
            │   ├── category/            # 分类映射
            │   ├── document/            # 文档映射
            │   ├── chat/                # 聊天映射
            │   ├── log/                 # 日志映射
            │   └── system/              # 系统映射
            │
            └── static/                  # 静态资源
                └── index.html           # 首页
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

### 基础功能

- **用户认证**：JWT Token 认证，支持登录、密码加密存储
- **用户管理**：用户 CRUD、头像上传、密码修改、个人资料更新
- **知识库分类**：分类的增删查，支持排序
- **文档管理**：支持多文件上传、Tika 解析、Markdown 解析、文档向量化、向量存储
- **聊天会话**：支持多轮对话、会话列表、会话删除、上下文记忆
- **统计仪表盘**：管理员概览统计数据
- **系统日志**：操作日志记录
- **国际化**：支持中英文多语言
- **RBAC 权限**：基于角色的访问控制（ADMIN / USER）

### AI 能力

- **智能问答**：基于 Agent 架构的智能对话，支持自主工具调用和知识库检索
- **向量检索**：基于 Redis Vector Store 实现相似度检索，提供引用文档展示
- **Function Calling**：支持 AI 工具调用，包括知识库文件列表查询、知识库语义检索等
- **MCP 客户端**：支持 MCP 协议，可连接外部 MCP Server 扩展工具能力
- **Advisor 拦截**：自定义 Advisor 实现日志记录和引用文档提取
- **Agent 编排**：支持注册/注销 Agent、同步/异步调用、链式调用、并行调用
- **Skills 引擎**：支持技能自动发现、注册/注销、同步/异步执行、批量执行、链式执行

## 🐍 Python MCP Server 子项目

独立的 Python MCP Server 为系统提供强大的数据获取和分析能力：

### 核心能力

- **网络搜索**：支持 DuckDuckGo 和 Bing 搜索引擎，智能降级机制
- **网页抓取**：智能摘要提取、结构化数据解析、缓存和限流保护、动态页面支持
- **数据分析**：统计分析、趋势检测、移动平均、季节性分析、简单预测
- **可视化**：图表数据生成（折线图/柱状图/饼图/散点图），ECharts/AntV 兼容
- **报告生成**：Markdown 格式结构化分析报告
- **管理工具**：缓存统计/清除、限流重置/统计等运维功能

### 版本演进

- **v1.0.0**：基础网络搜索、数据源适配（网页/API/文件）、统计分析
- **v1.1.0**：智能缓存（TTL过期策略）、URL 限流（滑动窗口算法）、动态页面扩展预留
- **v1.2.0**：管理工具（4个运维工具）、Playwright 完整实现、LRU 淘汰策略、缓存容量管理

详细文档请参考 [Python MCP Server 完整技术文档](md/python-mcp-server.md)

## 📜 License

Apache License 2.0

---

<div style="display: flex; justify-content: space-between; align-items: center;">
  <span style="color: #888; font-size: 0.9em;">📅 最后更新：2026-07-06</span>
  <a href="#spring-ai-rag-agent">⬆️ 返回顶部</a>
</div>
