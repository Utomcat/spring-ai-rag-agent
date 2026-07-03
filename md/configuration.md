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
      - classpath:jwt.yml                 # JWT 配置
      - classpath:rdb.yml                 # 关系型数据库配置
      - classpath:doc-splitter.yml        # 文档分割配置
      - classpath:system.yml              # 系统配置
      - classpath:mcp.yml                 # MCP 配置
```

## 📖 各配置文件详解

### 1. application.yml

**位置**：`src/main/resources/application.yml`

**说明**：主配置文件，定义应用基础配置

**关键配置项**：

```yaml
server:
  port: 8083                              # 服务端口

spring:
  application:
    name: spring-ai-rag-knowledge-database
  config:
    import:                               # 引入其他配置文件
      - classpath:mybatis-plus.yml        # MyBatis-Plus 配置
      - classpath:nrdb-datasource.yml     # 非关系型数据库数据源配置
      - classpath:rdb-datasource.yml      # 关系型数据库数据源配置
      - classpath:vdb-datasource.yml      # 向量数据库数据源配置
      - classpath:llm-model.yml           # LLM 模型配置
      - classpath:file.yml                # 文件和文件上传相关配置
      - classpath:language.yml            # 语言配置
      - classpath:log.yml                 # 日志配置
      - classpath:jwt.yml                 # JWT 配置
      - classpath:rdb.yml                 # 关系型数据库配置
      - classpath:doc-splitter.yml        # 文档分割配置
      - classpath:system.yml              # 系统配置
      - classpath:mcp.yml                 # MCP 配置
```

### 2. mybatis-plus.yml

**位置**：`src/main/resources/mybatis-plus.yml`

**说明**：MyBatis Plus 配置

**关键配置项**：

```yaml
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true    # 下划线转驼峰
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto                       # 主键策略
      table-underline: true
  mapper-locations: classpath*:repository/**/*.xml  # Mapper XML 路径
```

### 3. rdb-datasource.yml

**位置**：`src/main/resources/rdb-datasource.yml`

**说明**：关系型数据库数据源配置（MySQL/MariaDB）

**关键配置项**：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/rag_database?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:root}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 30000
      connection-timeout: 30000
```

### 4. nrdb-datasource.yml

**位置**：`src/main/resources/nrdb-datasource.yml`

**说明**：非关系型数据库数据源配置（Redis）

**关键配置项**：

```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
      database: 0
      timeout: 6000ms
```

### 5. vdb-datasource.yml

**位置**：`src/main/resources/vdb-datasource.yml`

**说明**：向量数据库数据源配置（Redis Vector Store）

**关键配置项**：

```yaml
spring:
  ai:
    vectorstore:
      redis:
        index-type: HNSW
        distance-type: COSINE
        dimensions: 768
```

### 6. llm-model.yml

**位置**：`src/main/resources/llm-model.yml`

**说明**：LLM 模型配置（OpenAI 兼容 API + Ollama Embedding）

**关键配置项**：

```yaml
spring:
  ai:
    model:
      chat: openai                        # 聊天模型使用 OpenAI 兼容 API
      embedding: ollama                   # Embedding 模型使用 Ollama
    openai:
      api-key: ${XIAOMI_MIMO_OPENAI_API_KEY:}
      base-url: ${XIAOMI_MIMO_OPENAI_BASE_URL:https://api.xiaomimimo.com/v1}
      chat:
        model: ${XIAOMI_MIMO_OPENAI_CHAT_MODEL:mimo-v2.5-pro-ultraspeed}
        temperature: 0.7
    ollama:
      base-url: ${OLLAMA_BASE_URL:http://localhost:11434}
      embedding:
        model: ${OLLAMA_EMBEDDING_MODEL:embeddinggemma:latest}
```

### 7. jwt.yml

**位置**：`src/main/resources/jwt.yml`

**说明**：JWT 认证配置

**关键配置项**：

```yaml
jwt:
  secret: your-256-bit-secret-key-here   # JWT 密钥
  expiration: 86400000                    # Token 过期时间（毫秒），默认 24 小时
```

### 8. file.yml

**位置**：`src/main/resources/file.yml`

**说明**：文件上传配置

**关键配置项**：

```yaml
file:
  upload:
    path: ./uploads                       # 上传文件存储根路径
    max-size: 50MB                        # 单个文件最大大小
    max-total-size: 100MB                 # 单次请求最大总大小
```

### 9. doc-splitter.yml

**位置**：`src/main/resources/doc-splitter.yml`

**说明**：文档分割配置

**关键配置项**：

```yaml
doc-splitter:
  chunk-size: 1024                        # 文档切分块大小（字符数）
  chunk-overlap: 200                      # 重叠字符数
```

### 10. system.yml

**位置**：`src/main/resources/system.yml`

**说明**：系统配置

**关键配置项**：

```yaml
system:
  name: Spring AI RAG Knowledge Database
  version: 0.0.1-SNAPSHOT
```

### 11. language.yml

**位置**：`src/main/resources/language.yml`

**说明**：国际化配置

**关键配置项**：

```yaml
spring:
  messages:
    basename: i18n/messages
    encoding: UTF-8
    default-locale: zh_CN
    fallback-to-system-locale: false

i18n:
  supported-languages:
    - zh_CN
    - en_US
```

### 12. log.yml

**位置**：`src/main/resources/log.yml`

**说明**：日志配置

**关键配置项**：

```yaml
logging:
  level:
    root: INFO
    com.ranyk.spring.ai.rag: DEBUG
    org.springframework.security: DEBUG
    org.springframework.ai: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
```

### 13. rdb.yml

**位置**：`src/main/resources/rdb.yml`

**说明**：关系型数据库 ORM 配置

**关键配置项**：

```yaml
rdb:
  auto-fill-skip-tables:                  # 需要跳过自动填充的表
    - t_system_log
```

### 14. mcp.yml

**位置**：`src/main/resources/mcp.yml`

**说明**：MCP Client 配置

**关键配置项**：

```yaml
mcp:
  enabled: true                           # 是否启用 MCP Client
  servers:
    python-mcp-web-serach-server:
      url: http://127.0.0.1:8084/mcp
      transport: streamable-http
```

### 15. mcp-servers.json

**位置**：`src/main/resources/mcp-servers.json`

**说明**：MCP Server 配置（JSON 格式）

**关键配置项**：

```json
{
  "servers": [
    {
      "name": "python-mcp-web-serach-server",
      "url": "http://127.0.0.1:8084/mcp",
      "transport": "streamable-http"
    }
  ]
}
```

## 🌍 环境变量支持

以下配置项支持通过环境变量覆盖：

| 环境变量                            | 对应配置项                              | 说明                |
|---------------------------------|------------------------------------|-------------------|
| `DB_USERNAME`                   | `spring.datasource.username`       | 数据库用户名            |
| `DB_PASSWORD`                   | `spring.datasource.password`       | 数据库密码             |
| `REDIS_HOST`                    | `spring.data.redis.host`           | Redis 主机          |
| `REDIS_PORT`                    | `spring.data.redis.port`           | Redis 端口          |
| `REDIS_PASSWORD`                | `spring.data.redis.password`       | Redis 密码          |
| `XIAOMI_MIMO_OPENAI_API_KEY`    | `spring.ai.openai.api-key`         | OpenAI API Key    |
| `XIAOMI_MIMO_OPENAI_BASE_URL`   | `spring.ai.openai.base-url`        | OpenAI API 基础 URL |
| `XIAOMI_MIMO_OPENAI_CHAT_MODEL` | `spring.ai.openai.chat.model`      | 聊天模型名称            |
| `OLLAMA_BASE_URL`               | `spring.ai.ollama.base-url`        | Ollama 服务地址       |
| `OLLAMA_EMBEDDING_MODEL`        | `spring.ai.ollama.embedding.model` | Embedding 模型名称    |

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
├── application.yml                      # 主配置
├── mybatis-plus.yml                     # MyBatis Plus 配置
├── rdb-datasource.yml                   # 关系型数据库数据源
├── nrdb-datasource.yml                  # 非关系型数据库数据源
├── vdb-datasource.yml                   # 向量数据库数据源
├── llm-model.yml                        # LLM 模型配置
├── jwt.yml                              # JWT 配置
├── file.yml                             # 文件上传配置
├── doc-splitter.yml                     # 文档分割配置
├── system.yml                           # 系统配置
├── language.yml                         # 国际化配置
├── log.yml                              # 日志配置
├── rdb.yml                              # 关系型数据库 ORM 配置
├── mcp.yml                              # MCP 配置
├── mcp-servers.json                     # MCP Server 配置（JSON）
├── i18n/                                # 国际化资源文件
│   ├── messages.properties
│   ├── messages_en_US.properties
│   └── messages_zh_CN.properties
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
  <span style="color: #888; font-size: 0.9em;">📅 更新日期：2026-07-04</span>
  <a href="#配置文件说明">⬆️ 返回顶部</a>
</div>
