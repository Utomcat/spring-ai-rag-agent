# 快速开始

> **← 返回主文档**：[README.md](../README.md)

## ✅ 前置条件

### 必需环境

| 环境/工具             | 版本要求                     | 说明                                       |
|-------------------|--------------------------|------------------------------------------|
| **JDK**           | 21+                      | 项目基于 Java 21 开发，使用虚拟线程等特性                |
| **Maven**         | 3.8+                     | 项目构建工具                                   |
| **MySQL**         | 8.0+ 或 **MariaDB** 10.5+ | 关系型数据库，存储用户、文档元数据、聊天记录等                  |
| **Redis**         | 6.0+                     | 向量数据库（Redis Vector Store），用于文档向量存储与相似度检索 |
| **Ollama**        | 最新版                      | 本地 Embedding 模型服务（仅用于文档向量化）              |
| **OpenAI 兼容 API** | -                        | LLM 聊天模型服务（如小米 Mimo、DeepSeek、智谱等）        |

### 模型准备

#### 1. Ollama Embedding 模型（必需）

启动 Ollama 服务后，需要拉取 Embedding 模型用于文档向量化：

```bash
ollama pull embeddinggemma:latest
```

#### 2. OpenAI 兼容 API 的 LLM 模型（必需）

项目使用 OpenAI 兼容 API 调用 LLM 聊天模型（默认配置为小米 Mimo），需要：

1. 获取 API Key
2. 在 `llm-model.yml` 中配置：
    - `spring.ai.openai.api-key` - 你的 API Key（支持环境变量 `XIAOMI_MIMO_OPENAI_API_KEY`）
    - `spring.ai.openai.base-url` - API 基础 URL（默认 `https://api.xiaomimimo.com/v1`）
    - `spring.ai.openai.chat.model` - 聊天模型名称（默认 `mimo-v2.5-pro-ultraspeed`，支持环境变量 `XIAOMI_MIMO_OPENAI_CHAT_MODEL`）

> **注意**：项目默认配置使用小米 Mimo API，你可以根据需要替换为其他 OpenAI 兼容的 API 服务。Ollama 仅用于 Embedding 模型（`spring.ai.model.embedding: ollama`），LLM 聊天模型使用 OpenAI 兼容 API（`spring.ai.model.chat: openai`）。

### 可选工具

| 工具                   | 说明                          |
|----------------------|-----------------------------|
| **MySQL Client**     | 用于执行数据库初始化脚本                |
| **Postman / Apifox** | API 接口测试工具                  |
| **IDE**              | IntelliJ IDEA（推荐）、VS Code 等 |
| **MCP Server**       | 外部 MCP 服务（可选，用于扩展工具能力）      |

## 🗄️ 初始化数据库

```bash
mysql -u root -p < doc/init_database.sql
```

**默认管理员账号**：

- 用户名：`admin`
- 密码：`12345678`

> 注意：上述的账号均属于当前系统的账号, 请根据实际情况进行自行修改。

## ⚙️ 修改配置

编辑 `spring-ai-rag-knowledge-database/src/main/resources/` 下的配置文件：

| 配置文件                  | 配置内容                                                                                 |
|-----------------------|--------------------------------------------------------------------------------------|
| `rdb-datasource.yml`  | 设置 MySQL 连接信息（用户名、密码、数据库名）                                                           |
| `nrdb-datasource.yml` | 设置 Redis 连接信息（主机、端口、密码）                                                              |
| `llm-model.yml`       | 设置 LLM 模型配置：OpenAI API（api-key、base-url、chat.model）、Ollama（base-url、embedding.model） |
| `vdb-datasource.yml`  | 设置 Redis 向量数据库配置（索引类型、距离度量、维度）                                                       |
| `file.yml`            | 设置文件上传存储路径                                                                           |
| `rdb.yml`             | 设置需要跳过自动填充的表（可选）                                                                     |
| `mcp.yml`             | 设置 MCP Server 连接信息（可选）                                                               |

## 🏗️ 构建运行

### 方式一：Maven 打包运行

```bash
mvn clean package -DskipTests
cd spring-ai-rag-knowledge-database
java -jar target/spring-ai-rag-knowledge-database-0.0.1-SNAPSHOT.jar
```

### 方式二：Maven 开发模式运行

```bash
mvn spring-boot:run -pl spring-ai-rag-knowledge-database
```

### 方式三：IDE 运行

直接在 IDE 中运行 `SpringAiRagKnowledgeDatabaseApplication.java` 启动类。

## 服务访问

服务默认运行在 `http://localhost:8083`

### 健康检查

```bash
curl http://localhost:8083/api/user/me
```

未登录状态下会返回 401 错误，这是正常的。

### 登录测试

```bash
curl -X POST http://localhost:8083/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "12345678"}'
```

成功登录后会返回 JWT Token。

## 📋 使用流程

### 1. 登录获取 Token

参考上方 [登录测试](#登录测试) 获取 JWT Token。

### 2. 上传文档

```bash
curl -X POST http://localhost:8083/api/document \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -F "files=@/path/to/your/document.pdf"
```

### 3. 提问（Agent 自主调用）

```bash
curl -X POST http://localhost:8083/api/chat/ask \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{"question": "知识库中有哪些文档？"}'
```

**Agent 会自动识别意图并调用合适的工具：**

- 询问文件列表 → 调用 `getAllDocumentsFileName` 工具
- 知识性问题 → 调用 `retrieveKnowledge` 工具进行向量检索
- 最新信息 → 调用 MCP Server 的网络搜索工具

## ❓ 常见问题

### Q: 启动时连接数据库失败？

A: 请检查 `rdb-datasource.yml` 中的数据库连接配置，确保：
- MySQL 服务已启动
- 数据库用户名和密码正确
- 数据库 `rag_database` 已创建

### Q: Redis 连接失败？

A: 请检查 `nrdb-datasource.yml` 和 `vdb-datasource.yml` 中的 Redis 配置，确保：
- Redis 服务已启动
- Redis 主机和端口正确
- Redis 密码（如有）已配置

### Q: Ollama 连接失败？

A: 请确保：
- Ollama 服务已启动（默认端口 11434）
- `embeddinggemma:latest` 模型已拉取
- `llm-model.yml` 中的 `spring.ai.ollama.base-url` 配置正确

### Q: OpenAI API 调用失败？

A: 请检查：
- API Key 是否正确配置（环境变量或配置文件）
- 网络是否可以访问 API 地址
- API 余额是否充足

### Q: 文档上传后没有向量化？

A: 请检查：
- Ollama 服务是否正常运行
- 文档格式是否支持（支持 PDF、Markdown 等）
- 查看日志中的错误信息

## 🚀 启动顺序

推荐的服务启动顺序：

1. **MySQL/MariaDB** - 关系型数据库
2. **Redis** - 向量数据库和缓存
3. **Ollama** - Embedding 模型服务
4. **MCP Server**（可选）- 外部工具服务
5. **Spring Boot 应用** - 主应用

## 🐍 MCP Server 启动（可选）

如果需要使用 MCP Server 提供的网络搜索等扩展工具能力：

```bash
cd python-mcp-server
pip install -e .

# Linux/Mac
export MCP_TRANSPORT="streamable-http"
python main.py

# Windows CMD
set MCP_TRANSPORT=streamable-http
python main.py

# Windows PowerShell
$env:MCP_TRANSPORT = "streamable-http"
python main.py
```

服务默认运行在 `http://127.0.0.1:8084/mcp`。

> 完整的配置说明、传输方式对比、部署方式等，请参考 [Python MCP Server 文档](mcp-server.md)。

## Windows 环境注意事项

### 1. 环境变量设置

Windows 下设置环境变量的方式与 Linux/Mac 不同：

| 环境         | 设置方式           | 示例                                       |
|------------|----------------|------------------------------------------|
| CMD        | `set 变量名=值`    | `set MCP_TRANSPORT=streamable-http`      |
| PowerShell | `$env:变量名="值"` | `$env:MCP_TRANSPORT = "streamable-http"` |

### 2. 路径分隔符

Windows 使用反斜杠 `\` 作为路径分隔符，但在配置文件中建议使用正斜杠 `/` 以避免转义问题。

### 3. 换行符

Windows 使用 CRLF（`\r\n`）换行，Linux/Mac 使用 LF（`\n`）。Git 会自动处理，无需手动修改。

### 4. 端口占用检查

如果端口被占用，可以使用以下命令查找并结束进程：

**CMD：**
```cmd
netstat -ano | findstr :8083
taskkill /PID <进程ID> /F
```

**PowerShell：**
```powershell
netstat -ano | Select-String :8083
Stop-Process -Id <进程ID> -Force
```

## 🛑 停止服务

在运行服务的终端中按 `Ctrl + C` 即可停止服务，或直接关闭终端窗口。

## 📋 日志查看

服务启动后，日志会输出到控制台：

```bash
# Linux/Mac 实时查看日志
tail -f logs/application.log
```

```powershell
# Windows PowerShell 实时查看日志
Get-Content -Path logs\application.log -Wait
```

日志级别配置在 `log.yml` 中。

---

<div style="display: flex; justify-content: space-between; align-items: center;">
  <span style="color: #888; font-size: 0.9em;">📅 最后更新：2026-07-06</span>
  <a href="#快速开始">⬆️ 返回顶部</a>
</div>
