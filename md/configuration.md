# 配置文件说明

> **← 返回主文档**：[README.md](../README.md)

项目采用多配置文件拆分模式，通过 `application.yml` 的 `spring.config.import` 引入各模块配置，避免主配置文件过于臃肿。

## 📁 配置文件结构

```yaml
spring:
  config:
    import:
      - classpath:mybatis-plus.yml        # MyBatis-Plus 配置
      - classpath:nrdb-datasource.yml     # 非关系型数据库数据源配置
      - classpath:rdb-datasource.yml      # 关系型数据库数据源配置
      - classpath:vdb-datasource.yml      # 向量数据库数据源配置
      - classpath:llm-model.yml           # LLM 模型配置
      - classpath:file.yml                # 文件和文件上传相关配置
      - classpath:language.yml            # 语言配置
      - classpath:log.yml                 # 日志配置
      - classpath:jwt.yml                 # JWT 的配置
      - classpath:rdb.yml                 # 关系型数据库配置
      - classpath:doc-splitter.yml        # 文档分割配置
      - classpath:system.yml              # 系统配置
      - classpath:mcp.yml                 # MCP 配置
      - classpath:tomcat.yml              # Tomcat 服务器配置
      - classpath:weather-api.yml         # 天气 API 配置
      - classpath:skills.yml              # Skills 技能配置
      - classpath:image-generation-api.yml # 图像生成 API 配置
```

## 📖 各配置文件详解

### 1. application.yml

**位置**：`src/main/resources/application.yml`

**说明**：主配置文件，定义应用基础配置和引入其他模块配置

**关键配置项**：

```yaml
spring:
  application:
    name: spring-ai-rag-example-knowledge-database
  config:
    import:
      - classpath:mybatis-plus.yml
      - classpath:nrdb-datasource.yml
      - classpath:rdb-datasource.yml
      - classpath:vdb-datasource.yml
      - classpath:llm-model.yml
      - classpath:file.yml
      - classpath:language.yml
      - classpath:log.yml
      - classpath:jwt.yml
      - classpath:rdb.yml
      - classpath:doc-splitter.yml
      - classpath:system.yml
      - classpath:mcp.yml
      - classpath:tomcat.yml
      - classpath:weather-api.yml
      - classpath:skills.yml
      - classpath:image-generation-api.yml
```

> 注意：服务端口号（`server.port: 8083`）配置在 `tomcat.yml` 中，不在本文件内。

### 2. mybatis-plus.yml

**位置**：`src/main/resources/mybatis-plus.yml`

**说明**：MyBatis Plus 配置

**关键配置项**：

```yaml
mybatis-plus:
  mapper-locations: classpath*:repository/**/*.xml    # Mapper XML 路径
  type-aliases-package: com.ranyk.spring.ai.rag.knowledge.database.domain.*.entity  # 别名包扫描
  configuration:
    map-underscore-to-camel-case: true                # 下划线转驼峰
    cache-enabled: false                              # 关闭二级缓存
    local-cache-scope: session                        # 一级缓存范围
  global-config:
    banner: false                                     # 关闭控制台 Banner
    db-config:
      id-type: auto                                   # 主键策略：自增
  executor-type: reuse                                # 执行器类型
```

### 3. rdb-datasource.yml

**位置**：`src/main/resources/rdb-datasource.yml`

**说明**：关系型数据库数据源配置（MySQL/MariaDB），使用 HikariCP 连接池

**关键配置项**：

```yaml
spring:
  datasource:
    hikari:
      driver-class-name: com.mysql.cj.jdbc.Driver
      jdbc-url: jdbc:mysql://<host>:<port>/knowledge_database_rag?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false
      username: <数据库用户名>
      password: <数据库密码>
      register-mbeans: false
      # 连接池优化配置（适配虚拟线程）
      pool-name: VirtualThreadHikariPool
      maximum-pool-size: 30                           # 最大连接数
      minimum-idle: 10                                # 最小空闲连接数
      connection-timeout: 30000                       # 连接超时（毫秒）
      idle-timeout: 600000                            # 空闲超时（毫秒）
      max-lifetime: 1800000                           # 最大生命周期（毫秒）
      leak-detection-threshold: 60000                 # 泄漏检测阈值（毫秒）
      connection-test-query: SELECT 1                 # 连接测试查询
```

> 数据库名为 `knowledge_database_rag`，初始化脚本位于 `doc/database/init_database.sql`。

### 4. nrdb-datasource.yml

**位置**：`src/main/resources/nrdb-datasource.yml`

**说明**：非关系型数据库数据源配置（Redis），使用 Jedis 客户端

**关键配置项**：

```yaml
spring:
  data:
    redis:
      client-type: jedis                              # Redis 客户端类型
      host: <Redis主机地址>
      port: <Redis端口>
      database: 0                                     # 使用的数据库索引
```

### 5. vdb-datasource.yml

**位置**：`src/main/resources/vdb-datasource.yml`

**说明**：向量数据库数据源配置（Redis Vector Store）及自定义向量存储配置

**关键配置项**：

```yaml
spring:
  ai:
    vectorstore:
      redis:
        index-name: knowledge_database_index          # 向量存储索引名
        prefix: knowledge_database_rag                # 向量存储前缀
        initialize-schema: true                       # 初始化向量存储模式

# 自定义向量存储配置
vector-store:
  redis:
    metadata-field:
      tags:                                           # 元数据字段标签列表
        - docId
        - categoryId
        - title
  delete:
    batch-quantity: 100                               # 删除文档时每批次数量
```

### 6. llm-model.yml

**位置**：`src/main/resources/llm-model.yml`

**说明**：LLM 模型配置（OpenAI 兼容 API + Ollama Embedding），支持双模型架构和多模型智能路由

**关键配置项**：

```yaml
spring:
  ai:
    model:
      chat: openai                                    # 聊天模型使用 OpenAI 兼容 API
      embedding: ollama                               # Embedding 模型使用 Ollama
    openai:
      api-key: ${DASHSCOPE_OPENAI_API_KEY:""}        # OpenAI API Key（百炼）
      base-url: "https://ws-xxx.cn-beijing.maas.aliyuncs.com/compatible-mode/v1"  # API 基础 URL
      chat:
        enabled: true                                 # 启用 OpenAI 聊天模型
        model: "qwen3-vl-235b-a22b-thinking"          # 路由模型名称
        timeout: 120s                                 # 请求超时
      embedding:
        enabled: false                                # 禁用 OpenAI Embedding
    ollama:
      base-url: "http://localhost:11434"              # Ollama 服务地址
      chat:
        enabled: false                                # 禁用 Ollama 聊天
        model: "phi4-mini:latest"                     # Ollama 聊天模型（备用）
      embedding:
        enabled: true                                 # 启用 Ollama Embedding
        model: "embeddinggemma:latest"                # Embedding 模型

# 多模型路由配置
llm:
  multi-model:
    enabled: true                                     # 启用多模型路由
    models:
      - name: "qwen2.5-omni-7b"                      # Worker 模型名称
        type: "OpenAI"                                # 模型类型
        base-url: "https://..."                       # 模型 API 地址
        api-key: ${DASHSCOPE_OPENAI_API_KEY:""}      # API Key
        model: "qwen2.5-omni-7b"                     # 模型名称
        description: "多模态理解生成大模型"        # 模型描述（用于路由判断）
        timeout: 120                                  # 超时时间（秒）
        mcp-enabled: true                             # 是否启用 MCP 工具
        skill-enabled: true                           # 是否启用 Skills 技能
        system-prompt-enabled: true                   # 是否启用系统提示词
        tools:                                        # 该模型可用的工具 Bean 名称列表
          - "imageGenerationToolFunction"
      - name: "qwen-plus-2025-07-28"
        type: "OpenAI"
        # ... 同上配置
        tools:
          - "knowledgeRetrievalToolFunction"
          - "weatherForLocationToolFunction"
          - "documentToolFunction"
          - "sessionHistoryToolFunction"
          - "imageGenerationToolFunction"
```

> **多模型路由说明**：启用后，路由模型（`spring.ai.openai` 配置的模型）会分析用户请求意图，自动选择最合适的 Worker 模型处理。每个 Worker 模型可独立配置工具集、MCP、Skills 和系统提示词。

### 7. jwt.yml

**位置**：`src/main/resources/jwt.yml`

**说明**：JWT 认证配置

**关键配置项**：

```yaml
jwt:
  secret: "<JWT密钥（至少32字节）>"                    # JWT 加密密钥
  expire-hours: 24                                    # Token 过期时间（小时）
```

### 8. file.yml

**位置**：`src/main/resources/file.yml`

**说明**：文件上传配置

**关键配置项**：

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 50MB                             # 单个文件最大大小
      max-request-size: 200MB                         # 整个请求最大大小
      file-size-threshold: 2KB                        # 超过此值写入临时文件

# 自定义文件上传配置
file:
  upload:
    root: ${user.dir}/upload                          # 上传文件存储根路径
```

> `${user.dir}` 在 IDE 中为项目根目录，在 Jar 包运行时为当前执行目录。

### 9. doc-splitter.yml

**位置**：`src/main/resources/doc-splitter.yml`

**说明**：文档分割配置

**关键配置项**：

```yaml
doc:
  splitter:
    chunk-size: 500                                   # 文档切分块大小（字符数）
    max-num-chunks: 1000                              # 最大切分块数
    encoding-type: cl100k_base                        # Token 化编码类型
    min-chunk-size-charts: 350                        # 最小切分块字符数
    min-chunk-length-to-embed: 5                      # 最小可嵌入切分块长度
    keep-separator: true                              # 保留分隔符
    punctuation-marks:                                # 标点符号列表
      - "."
      - "?"
      - "!"
      - "\n"
```

> 支持的编码类型：`r50k_base`、`p50k_base`、`p50k_edit`、`cl100k_base`（默认）、`o200k_base`

### 10. system.yml

**位置**：`src/main/resources/system.yml`

**说明**：系统配置（默认头像、系统提示词、Agent 名称）

**关键配置项**：

```yaml
system:
  default-avatar: "/avatar/default1.jpg"              # 默认用户头像路径
  system-prompt: |                                    # 系统提示词
    你是「Ranyk RAG 企业知识库」的智能助手。
    你可以使用以下工具:
    1. 知识库检索工具
    2. 网络搜索工具(web_search)
    3. 网页数据分析工具(analyze_web_data)
    ...
  agent-name: knowledge-database-rag-agent            # 系统 Agent 名称
```

### 11. language.yml

**位置**：`src/main/resources/language.yml`

**说明**：国际化语言配置

**关键配置项**：

```yaml
spring:
  web:
    locale: zh_CN                                     # 默认本地化语言
    locale-resolver: accept-header                    # 语言解析方式（根据请求头）
```

### 12. log.yml

**位置**：`src/main/resources/log.yml`

**说明**：日志配置

**关键配置项**：

```yaml
logging:
  level:
    org.springframework.ai.chat.client.advisor: error
    com.ranyk.spring.ai.rag.knowledge.database: debug
    org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor: error
```

### 13. rdb.yml

**位置**：`src/main/resources/rdb.yml`

**说明**：关系型数据库 ORM 配置

**关键配置项**：

```yaml
rdb:
  orm:
    skip-auto-fill-tables:                            # 需要跳过自动填充的表
      - t_kb_document
```

### 14. mcp.yml

**位置**：`src/main/resources/mcp.yml`

**说明**：MCP Client 配置（Spring AI MCP Client）及自定义 MCP 服务描述

**关键配置项**：

```yaml
spring:
  ai:
    mcp:
      client:
        enabled: true                                 # 启用 MCP Client
        name: spring-ai-mcp-client                    # Client 名称
        version: 1.0.0                                # Client 版本
        type: sync                                    # Client 类型
        request-timeout: 120s                         # 初始化超时
        toolcallback:
          enabled: true                               # 启用工具回调
        streamable-http:
          connections:
            python-mcp-web-serach-server:             # Python MCP Server
              url: http://127.0.0.1:8084
              endpoint: /mcp
            java-file-mcp-server:                     # Java MCP Server
              url: http://127.0.0.1:8085
              endpoint: /mcp

# 自定义 MCP 服务描述（自动注入系统提示词）
mcp:
  descriptions:
    - 数据获取工具(fetch_data), 从网页/文件/API中获取结构化数据
    - 通过网络搜索引擎搜索最新的信息工具(web_search)
    - 文件操作MCP工具 FileOperateMcpTool
```

### 15. tomcat.yml

**位置**：`src/main/resources/tomcat.yml`

**说明**：Tomcat 服务器配置（服务端口、线程池等）

**关键配置项**：

```yaml
server:
  port: 8083                                          # 服务端口
  tomcat:
    threads:
      max: 200                                        # 最大工作线程数
      min-spare: 10                                   # 最小空闲线程数
    connection-timeout: 20s                           # 连接超时时间
    accept-count: 100                                 # 等待队列长度
```

> Spring Boot 4.x 已默认启用虚拟线程支持。

### 16. weather-api.yml

**位置**：`src/main/resources/weather-api.yml`

**说明**：天气查询 API 配置（聚合数据）

**关键配置项**：

```yaml
weather:
  api:
    factory-owners:
      - enable: true                                  # 是否启用
        name: "juhe"                                  # API 厂商名称
        api-key: "<聚合数据 API Key>"              # API Key
        base-url: "https://apis.juhe.cn/simpleWeather/query"  # API 地址
```

> 支持配置多个 API 厂商，当主 API 失败时自动切换到备用 API。

### 17. skills.yml

**位置**：`src/main/resources/skills.yml`

**说明**：Skills 技能描述配置，用于自动注入系统提示词

**关键配置项**：

```yaml
skills:
  descriptions:
    - 智能客服技能, 用于处理用户咨询问题, 提供专业的客服服务
```

### 18. image-generation-api.yml

**位置**：`src/main/resources/image-generation-api.yml`

**说明**：图像生成 API 配置（DashScope 原生接口）

**关键配置项**：

```yaml
image:
  generation:
    api:
      enabled: true                                   # 是否启用图像生成工具
      api-configs:
        - api-key: ${DASHSCOPE_OPENAI_API_KEY:""}    # DashScope API Key
          type: DashScope                             # API 提供厂商类型
          base-url: "https://..."                     # DashScope 原生接口地址
          model: "qwen-image-max"                     # 图像生成模型
          size: "1328*1328"                           # 默认输出图像分辨率
          prompt-extend: true                         # 是否开启 Prompt 智能改写
          watermark: false                            # 是否添加水印
          timeout: 120000                             # 请求超时时间（毫秒）
        - api-key: ${DASHSCOPE_OPENAI_API_KEY:""}    # 备用 API 配置
          type: DashScope
          model: "qwen-image-plus"                    # 备用模型
          # ... 其他配置同上
```

> 支持配置多个 API，当第一个模型生成失败时自动尝试下一个。可选分辨率：1664*928(16:9)、1472*1104(4:3)、1328*1328(1:1)、1104*1472(3:4)、928*1664(9:16)。

## 🌍 环境变量支持

以下配置项支持通过环境变量覆盖：

| 环境变量                   | 对应配置项                                   | 说明                   |
|----------------------------|----------------------------------------------|------------------------|
| `DASHSCOPE_OPENAI_API_KEY` | `spring.ai.openai.api-key`                   | DashScope 百炼 API Key |
| `DASHSCOPE_OPENAI_API_KEY` | `image.generation.api.api-configs[].api-key` | 图像生成 API Key       |
| `DASHSCOPE_OPENAI_API_KEY` | `llm.multi-model.models[].api-key`           | 多模型 Worker API Key  |

> 其他配置项（数据库连接、Redis 连接等）在当前配置中为直接赋值方式，如需环境变量支持可自行修改为 `${ENV_VAR:默认值}` 格式。

## 配置文件优先级

Spring Boot 配置文件加载顺序（优先级从高到低）：

1. 命令行参数
2. 环境变量
3. `application-{profile}.yml`
4. `application.yml`
5. 引入的配置文件（通过 `spring.config.import`）

## 🗂️ 配置文件目录结构

```
src/main/resources/
├── application.yml                      # 主配置（引入其他配置文件）
├── mybatis-plus.yml                     # MyBatis Plus 配置
├── rdb-datasource.yml                   # 关系型数据库数据源（HikariCP）
├── nrdb-datasource.yml                  # 非关系型数据库数据源（Redis/Jedis）
├── vdb-datasource.yml                   # 向量数据库数据源（Redis Vector Store）
├── llm-model.yml                        # LLM 模型配置（OpenAI + Ollama + 多模型路由）
├── jwt.yml                              # JWT 认证配置
├── file.yml                             # 文件上传配置
├── doc-splitter.yml                     # 文档分割配置
├── system.yml                           # 系统配置（提示词、头像等）
├── language.yml                         # 国际化语言配置
├── log.yml                              # 日志配置
├── rdb.yml                              # 关系型数据库 ORM 配置
├── mcp.yml                              # MCP Client 配置
├── tomcat.yml                           # Tomcat 服务器配置（端口、线程池）
├── weather-api.yml                      # 天气查询 API 配置
├── skills.yml                           # Skills 技能描述配置
├── image-generation-api.yml             # 图像生成 API 配置
├── META-INF/spring/
│   └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
├── static/
│   └── favicon.ico
└── repository/                          # MyBatis XML 映射文件
    ├── user/
    ├── document/
    ├── chat/
    ├── category/
    ├── log/
    └── stats/
```

---

<div style="display: flex; justify-content: space-between; align-items: center;">
  <span style="color: #888; font-size: 0.9em;">📅 最后更新：2026-07-23</span>
  <a href="#配置文件说明">⬆️ 返回顶部</a>
</div>
