# MCP + Agent + Skills 使用指南

> **← 返回主文档**：[README.md](../README.md)

## 📦 已实现的核心组件

### 1. **MCP (Model Context Protocol) 层**

#### 核心类文件:
- `McpOrchestrator.java` - MCP 统一编排器
- `McpServerRegistry.java` - MCP Server 注册中心
- `McpToolRouter.java` - MCP 工具路由器
- `AbstractMcpProvider.java` - MCP Provider 抽象基类
- `RemoteMcpProvider.java` - 远程 HTTP MCP Provider
- `PythonMcpProvider.java` - Python MCP Server 专用 Provider
- `JavaMcpProvider.java` - Java MCP Server 专用 Provider

#### 模型类:
- `McpServerInfo.java` - MCP Server 元信息
- `McpCapability.java` - MCP 能力描述

---

### 2. **Agent 层**

#### 核心类文件:
- `AgentInvoker.java` - Agent 调用器

#### 功能特性:
- ✅ 注册/注销 Agent
- ✅ 同步调用 Agent
- ✅ 异步调用 Agent
- ✅ 链式调用多个 Agent
- ✅ 并行调用多个 Agent

---

### 3. **Skills 层**

#### 核心类文件:
- `SkillsExecutor.java` - Skills 执行器

#### 功能特性:
- ✅ 自动发现 Skills
- ✅ 注册/注销 Skills
- ✅ 同步执行 Skill
- ✅ 异步执行 Skill
- ✅ 批量执行 Skills
- ✅ 链式执行 Skills

---

## 🚀 快速开始

### 1. 基本使用 - MCP

```java
@Service
public class MyService {
    
    @Autowired
    private McpOrchestrator mcpOrchestrator;
    
    public void useMcp() {
        // 获取所有健康的 Server
        List<McpServerInfo> servers = mcpOrchestrator.getHealthyServers();
        
        // 执行健康检查
        HealthCheckResult result = mcpOrchestrator.healthCheck();
        System.out.println("健康状态: " + result);
        
        // 获取某个能力的工具提供者
        SyncMcpToolCallbackProvider provider = 
            mcpOrchestrator.getToolProvider("web_search");
        
        // 手动注册新的 MCP Server
        McpServerInfo serverInfo = McpServerInfo.builder()
            .id("java-document")
            .name("Java Document Server")
            .language(McpServerInfo.Language.JAVA)
            .url("http://127.0.0.1:8085")
            .endpoint("/mcp")
            .priority(2)
            .build();
        
        AbstractMcpProvider provider = new RemoteMcpProvider(serverInfo);
        mcpOrchestrator.registerServer(provider);
    }
}
```

---

### 2. 基本使用 - Agent

```java
@Service
public class MyAgentService {
    
    @Autowired
    private AgentInvoker agentInvoker;
    
    @PostConstruct
    public void init() {
        // 注册研究 Agent
        AgentInvoker.AgentConfig researchAgent = AgentInvoker.AgentConfig.builder()
            .name("research-agent")
            .type("specialized")
            .description("研究专用 Agent")
            .tools(List.of("web_search", "document_analyze"))
            .enabled(true)
            .maxRetries(3)
            .timeoutSeconds(60)
            .build();
        
        agentInvoker.registerAgent(researchAgent);
    }
    
    public String doResearch(String topic) {
        // 同步调用
        String result = agentInvoker.invoke("research-agent", 
            "请研究以下主题: " + topic);
        
        return result;
    }
    
    public void doParallelResearch(List<String> topics) {
        // 并行调用
        List<String> results = agentInvoker.parallelInvoke(
            List.of("research-agent", "coding-agent"),
            "研究: " + String.join(", ", topics)
        );
    }
    
    public String doChainResearch(String topic) {
        // 链式调用
        return agentInvoker.chainInvoke(
            List.of("research-agent", "coding-agent"),
            "研究并生成代码: " + topic
        );
    }
}
```

---

### 3. 基本使用 - Skills

```java
@Service
public class MySkillsService {
    
    @Autowired
    private SkillsExecutor skillsExecutor;
    
    @PostConstruct
    public void init() {
        // 注册一个数据处理 Skill
        SkillsExecutor.SkillDefinition dataProcessSkill = 
            SkillsExecutor.SkillDefinition.builder()
                .id("data-process")
                .name("数据处理")
                .description("处理和分析数据")
                .category("data_processing")
                .tags(List.of("data", "analysis"))
                .requiredParams(List.of("input_data"))
                .optionalParams(List.of("format", "options"))
                .enabled(true)
                .build();
        
        // 注册处理器
        skillsExecutor.registerSkill(dataProcessSkill, params -> {
            Object inputData = params.get("input_data");
            // 处理逻辑
            return "处理结果: " + inputData;
        });
    }
    
    public Object processData(Object data) {
        Map<String, Object> params = Map.of("input_data", data);
        return skillsExecutor.execute("data-process", params);
    }
    
    public void batchProcess(List<Object> dataList) {
        List<Map<String, Object>> paramsList = dataList.stream()
            .map(data -> Map.<String, Object>of("input_data", data))
            .toList();
        
        List<Object> results = skillsExecutor.batchExecute(
            List.of("data-process", "data-process", "data-process"),
            paramsList
        );
    }
}
```

---

## 🔧 配置说明

### mcp.yml 配置示例

```yaml
spring:
  ai:
    mcp:
      client:
        enabled: true
        type: sync
        request-timeout: 120s
        toolcallback:
          enabled: true
        streamable-http:
          connections:
            python-web-search:
              url: http://127.0.0.1:8084
              endpoint: /mcp
              capabilities:
                - id: web_search
                  name: 网络搜索
                  priority: 1

ai:
  agent:
    enabled: true
    sub-agents:
      - name: research-agent
        type: specialized
        tools: [web_search, document_analyze]
        enabled: true
  
  skills:
    enabled: true
    registry-path: classpath:skills/
    auto-discover: true
```

---

## 🎯 路由策略

McpToolRouter 支持多种路由策略:

### 1. 基于优先级路由
```java
// 自动选择优先级最高的 Server
AbstractMcpProvider provider = router.route("web_search");
```

### 2. 负载均衡路由
```java
// 选择负载率最低的 Server
AbstractMcpProvider provider = router.routeByLoadBalance("web_search");
```

### 3. 手动指定 Server
```java
// 直接指定 Server ID
AbstractMcpProvider provider = router.routeToServer("python-web-search");
```

---

## 📊 监控和管理

### 健康检查
```java
@Autowired
private McpOrchestrator orchestrator;

// 执行健康检查
HealthCheckResult result = orchestrator.healthCheck();
System.out.println("总计: " + result.getTotal());
System.out.println("健康: " + result.getHealthy());
System.out.println("异常: " + result.getUnhealthy());
```

### 查看 Server 状态
```java
List<McpServerInfo> servers = orchestrator.getAllServers();
for (McpServerInfo server : servers) {
    System.out.println("Server: " + server.getName());
    System.out.println("  语言: " + server.getLanguage());
    System.out.println("  健康: " + server.getHealthy());
    System.out.println("  负载: " + server.getLoadFactor() * 100 + "%");
}
```

---

## 🔮 未来扩展方向

### 第二阶段 (3-6个月)
1. 实现真实的 Spring AI MCP Client 集成
2. 添加更多语言的 Provider (Go, C++, C#)
3. 实现 Sampling 和 Elicitation 功能
4. 添加故障转移机制

### 第三阶段 (6-12个月)
1. 实现完整的 Agent 工作流引擎
2. 添加 Skills 市场/仓库
3. 实现分布式 MCP Server 集群
4. 添加性能监控和告警

---

## ⚠️ 注意事项

1. **TODO 标记**: 代码中标记了 TODO 的地方需要根据实际的 Spring AI MCP API 进行实现
2. **依赖注入**: 确保 ChatClient 等 Bean 已正确配置
3. **线程安全**: 所有组件都使用了 ConcurrentHashMap 保证线程安全
4. **资源管理**: McpOrchestrator 会在 Spring Bean 销毁时自动关闭所有连接

---

## 📝 常见问题

### Q1: 如何添加新的 MCP Server?
A: 创建 McpServerInfo 和对应的 Provider,然后调用 `orchestrator.registerServer()`

### Q2: 如何实现自定义路由策略?
A: 继承或修改 `McpToolRouter` 类,添加自己的路由逻辑

### Q3: Skills 配置文件格式是什么?
A: 目前预留了 YAML 格式,具体实现需要在 `SkillsExecutor.autoDiscoverSkills()` 中解析

### Q4: 如何调试?
A: 所有组件都有详细的日志输出,设置日志级别为 DEBUG 可以看到详细信息

---

## 🎉 总结

第一阶段已完成:
- ✅ MCP 统一管理层
- ✅ Agent 调用框架
- ✅ Skills 执行引擎
- ✅ 多语言扩展预留
- ✅ 配置化支持

您现在可以:
1. 启动应用测试基本功能
2. 根据实际需求调整配置
3. 逐步实现 TODO 标记的部分
4. 扩展更多的 Provider 和 Skills

祝您使用愉快! 🚀

---

<div style="display: flex; justify-content: space-between; align-items: center;">
  <span style="color: #888; font-size: 0.9em;">📅 最后更新：2026-07-06</span>
  <a href="#mcp--agent--skills-使用指南">⬆️ 返回顶部</a>
</div>
