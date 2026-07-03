# API 接口

> **← 返回主文档**：[README.md](../README.md)

## 🔐 认证接口

| 接口                | 方法   | 权限 | 说明   |
|-------------------|------|----|------|
| `/api/auth/login` | POST | 公开 | 用户登录 |

### 登录请求

```json
{
  "username": "admin",
  "password": "admin"
}
```

### 登录响应

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "nickname": "管理员",
      "role": "ADMIN"
    }
  }
}
```

## 👤 用户接口

| 接口                      | 方法     | 权限    | 说明       |
|-------------------------|--------|-------|----------|
| `/api/user/me`          | GET    | 登录用户  | 获取当前用户信息 |
| `/api/user/me/profile`  | PUT    | 登录用户  | 更新个人资料   |
| `/api/user/me/avatar`   | POST   | 登录用户  | 上传头像     |
| `/api/user/me/password` | PUT    | 登录用户  | 修改密码     |
| `/api/user/page`        | GET    | ADMIN | 分页查询用户   |
| `/api/user`             | POST   | ADMIN | 新增/更新用户  |
| `/api/user/{id}`        | DELETE | ADMIN | 删除用户     |

### 获取当前用户信息

**请求**：`GET /api/user/me`

**响应**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "nickname": "管理员",
    "avatar": "/uploads/avatar/admin.png",
    "role": "ADMIN",
    "email": "admin@example.com",
    "createdAt": "2024-01-01T10:00:00"
  }
}
```

### 更新个人资料

**请求**：`PUT /api/user/me/profile`

```json
{
  "nickname": "新昵称",
  "email": "new@example.com"
}
```

### 修改密码

**请求**：`PUT /api/user/me/password`

```json
{
  "oldPassword": "oldPassword",
  "newPassword": "newPassword"
}
```

### 上传头像

**请求**：`POST /api/user/me/avatar`（multipart/form-data）

| 参数名  | 类型   | 必填 | 说明     |
|------|------|----|--------|
| file | File | 是  | 头像图片文件 |

**curl 示例**：

```bash
curl -X POST http://localhost:8083/api/user/me/avatar \
  -H "Authorization: Bearer <your-token>" \
  -F "file=@/path/to/avatar.png"
```

**响应**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "avatar": "/uploads/avatar/admin.png"
  }
}
```

### 分页查询用户

**请求**：`GET /api/user/page?page=1&size=10`

**响应**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [...],
    "total": 100,
    "page": 1,
    "size": 10
  }
}
```

### 新增/更新用户

**请求**：`POST /api/user`

```json
{
  "username": "newuser",
  "password": "password123",
  "nickname": "新用户",
  "role": "USER",
  "email": "newuser@example.com"
}
```

**响应**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 2,
    "username": "newuser",
    "nickname": "新用户",
    "role": "USER"
  }
}
```

### 删除用户

**请求**：`DELETE /api/user/{id}`

**curl 示例**：

```bash
curl -X DELETE http://localhost:8083/api/user/2 \
  -H "Authorization: Bearer <your-token>"
```

**响应**：

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

## 📂 分类接口

| 接口                   | 方法     | 权限    | 说明   |
|----------------------|--------|-------|------|
| `/api/category`      | GET    | 登录用户  | 分类列表 |
| `/api/category`      | POST   | ADMIN | 新增分类 |
| `/api/category/{id}` | DELETE | ADMIN | 删除分类 |

### 获取分类列表

**请求**：`GET /api/category`

**响应**：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "技术文档",
      "sortOrder": 1,
      "createdAt": "2024-01-01T10:00:00"
    }
  ]
}
```

### 新增分类

**请求**：`POST /api/category`

```json
{
  "name": "新分类",
  "sortOrder": 2
}
```

## 📄 文档接口

| 接口                   | 方法     | 权限    | 说明       |
|----------------------|--------|-------|----------|
| `/api/document`      | POST   | ADMIN | 上传文档并向量化 |
| `/api/document/list` | GET    | ADMIN | 分页查询文档   |
| `/api/document/{id}` | DELETE | ADMIN | 删除文档及向量  |

### 上传文档

**请求**：`POST /api/document`（multipart/form-data）

| 参数名        | 类型     | 必填 | 说明          |
|------------|--------|----|-------------|
| files      | File[] | 是  | 文档文件        |
| categoryId | Long   | 否  | 分类ID        |
| fileName   | String | 否  | 文件名称（单个文件时） |

**curl 示例**：

```bash
# 上传单个文件
curl -X POST http://localhost:8083/api/document \
  -H "Authorization: Bearer <your-token>" \
  -F "files=@/path/to/document.pdf"

# 上传多个文件
curl -X POST http://localhost:8083/api/document \
  -H "Authorization: Bearer <your-token>" \
  -F "files=@/path/to/document1.pdf" \
  -F "files=@/path/to/document2.md"

# 上传文件并指定分类
curl -X POST http://localhost:8083/api/document \
  -H "Authorization: Bearer <your-token>" \
  -F "files=@/path/to/document.pdf" \
  -F "categoryId=1"
```

> **Windows 用户注意**：Windows CMD 中使用 `^` 换行，PowerShell 中使用 `` ` `` 换行。路径使用 Windows 格式如 `C:\path\to\document.pdf`。

**响应**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "successCount": 2,
    "failedCount": 0,
    "documents": [...]
  }
}
```

### 删除文档

**请求**：`DELETE /api/document/{id}`

**curl 示例**：

```bash
curl -X DELETE http://localhost:8083/api/document/1 \
  -H "Authorization: Bearer <your-token>"
```

**响应**：

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 分页查询文档

**请求**：`GET /api/document/list?page=1&size=10&categoryId=1`

**响应**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "fileName": "技术文档.pdf",
        "filePath": "/uploads/documents/技术文档.pdf",
        "fileSize": 1024000,
        "categoryId": 1,
        "vectorStatus": "INDEXED",
        "chunkCount": 15,
        "createdAt": "2024-01-01T10:00:00"
      }
    ],
    "total": 50,
    "page": 1,
    "size": 10
  }
}
```

## 💬 聊天会话接口

| 接口                              | 方法     | 权限   | 说明   |
|---------------------------------|--------|------|------|
| `/api/chat/session`             | GET    | 登录用户 | 会话列表 |
| `/api/chat/session/{sessionId}` | DELETE | 登录用户 | 删除会话 |

### 获取会话列表

**请求**：`GET /api/chat/session`

**响应**：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "session-uuid",
      "title": "关于项目架构的问题",
      "messageCount": 5,
      "lastMessageAt": "2024-01-01T12:00:00",
      "createdAt": "2024-01-01T10:00:00"
    }
  ]
}
```

### 删除会话

**请求**：`DELETE /api/chat/session/{sessionId}`

**curl 示例**：

```bash
curl -X DELETE http://localhost:8083/api/chat/session/session-uuid \
  -H "Authorization: Bearer <your-token>"
```

**响应**：

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

## 📨 聊天消息接口

| 接口                                       | 方法   | 权限   | 说明      |
|------------------------------------------|------|------|---------|
| `/api/chat/ask`                          | POST | 登录用户 | 提问（RAG） |
| `/api/chat/session/{sessionId}/messages` | GET  | 登录用户 | 会话消息列表  |

### 提问（Agent 自主调用）

**请求**：`POST /api/chat/ask`

```json
{
  "question": "知识库中有哪些文档？",
  "sessionId": "session-uuid"
}
```

**响应**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "sessionId": "session-uuid",
    "answer": "知识库中包含以下文档：技术文档.pdf、产品说明.md、API 文档.pdf",
    "refs": [
      {
        "documentId": 1,
        "fileName": "技术文档.pdf",
        "relevance": 0.95
      }
    ],
    "messageId": "message-uuid",
    "createdAt": "2024-01-01T12:00:00"
  }
}
```

**Agent 自动工具调用说明**：

根据用户问题的意图，Agent 会自动选择合适的工具：

- **询问文件列表**：调用 `getAllDocumentsFileName` 工具查询数据库
- **知识性问题**：调用 `retrieveKnowledge` 工具进行向量相似度检索
- **最新信息**：调用 MCP Server 的网络搜索工具
- **直接回答**：对于简单问题，直接生成回答

### 获取会话消息列表

**请求**：`GET /api/chat/session/{sessionId}/messages`

**响应**：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "message-uuid-1",
      "role": "USER",
      "content": "知识库中有哪些文档？",
      "createdAt": "2024-01-01T12:00:00"
    },
    {
      "id": "message-uuid-2",
      "role": "ASSISTANT",
      "content": "知识库中包含以下文档：技术文档.pdf、产品说明.md、API 文档.pdf",
      "refs": [...],
      "createdAt": "2024-01-01T12:00:01"
    }
  ]
}
```

## 📊 统计接口

| 接口                    | 方法  | 权限    | 说明      |
|-----------------------|-----|-------|---------|
| `/api/stats/overview` | GET | ADMIN | 仪表盘概览统计 |

### 获取概览统计

**请求**：`GET /api/stats/overview`

**响应**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userCount": 100,
    "documentCount": 50,
    "sessionCount": 200,
    "messageCount": 1000,
    "todayNewUsers": 5,
    "todayNewDocuments": 2,
    "todayNewMessages": 50
  }
}
```

## 📋 响应格式

### 成功响应

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 失败响应

```json
{
  "code": 401,
  "message": "Unauthorized",
  "data": null
}
```

### 分页响应

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "total": 0,
    "page": 1,
    "size": 10
  }
}
```

## ⚠️ 常见错误码

| 错误码 | 说明                    | 原因            | 解决方案                        |
|-----|-----------------------|---------------|-----------------------------|
| 200 | 成功                    | 请求成功          | -                           |
| 400 | Bad Request           | 请求参数错误        | 检查请求参数是否正确                  |
| 401 | Unauthorized          | 未登录或 Token 无效 | 请先登录获取 Token，或检查 Token 是否过期 |
| 403 | Forbidden             | 无权限访问         | 检查用户角色是否有权限操作该接口            |
| 404 | Not Found             | 请求的资源不存在      | 检查请求路径是否正确                  |
| 500 | Internal Server Error | 服务器内部错误       | 查看服务端日志，联系管理员               |

### 401 未授权示例

```json
{
  "code": 401,
  "message": "Unauthorized",
  "data": null
}
```

### 403 无权限示例

```json
{
  "code": 403,
  "message": "Access Denied",
  "data": null
}
```

### 400 参数错误示例

```json
{
  "code": 400,
  "message": "Parameter validation failed",
  "data": null
}
```

---

<div style="display: flex; justify-content: space-between; align-items: center;">
  <span style="color: #888; font-size: 0.9em;">📅 更新日期：2026-07-04</span>
  <a href="#api-接口">⬆️ 返回顶部</a>
</div>
