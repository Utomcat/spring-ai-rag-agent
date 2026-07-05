package com.ranyk.spring.ai.rag.knowledge.database.ai.skill.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.ranyk.spring.ai.rag.knowledge.database.common.exception.AiException;
import com.ranyk.spring.ai.rag.knowledge.database.handle.ServiceExceptionHandler;
import com.ranyk.spring.ai.rag.knowledge.database.utils.ExecutorUtils;
import com.ranyk.spring.ai.rag.knowledge.database.ai.skill.handler.SkillHandler;
import com.ranyk.spring.ai.rag.knowledge.database.ai.skill.model.SkillDefinition;
import com.ranyk.spring.ai.rag.knowledge.database.ai.skill.model.SkillExecutionResult;
import com.ranyk.spring.ai.rag.knowledge.database.ai.skill.registry.SkillRegistry;
import com.ranyk.spring.ai.rag.knowledge.database.config.properties.SkillProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * CLASS_NAME: SkillsExecutor.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Skills 执行器 - 负责执行和管理 Skills
 * @date: 2026-07-06
 */
@Slf4j
public class SkillsExecutor {
    
    private final SkillRegistry skillRegistry;
    private final SkillProperties skillProperties;
    private final ExecutorService executorService;
    private final ObjectMapper yamlMapper;
    private final SkillRetryStrategy retryStrategy;
    private final SkillCircuitBreaker circuitBreaker;
    private final SkillRateLimiter rateLimiter;
    
    public SkillsExecutor(SkillRegistry skillRegistry, SkillProperties skillProperties) {
        this.skillRegistry = skillRegistry;
        this.skillProperties = skillProperties;
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
        this.retryStrategy = SkillRetryStrategy.createDefault();
        this.circuitBreaker = SkillCircuitBreaker.createDefault();
        this.rateLimiter = new SkillRateLimiter(10); // 默认每秒10个请求
        
        // 初始化线程池
        if (skillProperties.getAsyncEnabled()) {
            this.executorService = ExecutorUtils.createFixedThreadPool(
                    skillProperties.getAsyncPoolSize(),
                    "skill-executor"
            );
            log.info("Skills 异步执行器已初始化,线程池大小: {}", skillProperties.getAsyncPoolSize());
        } else {
            this.executorService = null;
            log.info("Skills 异步执行未启用");
        }
    }
    
    /**
     * 同步执行单个 Skill(带统一异常处理)
     *
     * @param skillId Skill ID
     * @param params  参数
     * @return 执行结果
     */
    public SkillExecutionResult execute(String skillId, Map<String, Object> params) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 验证 Skill 是否存在
            if (!skillRegistry.hasSkill(skillId)) {
                String errorMsg = String.format("Skill [%s] 不存在", skillId);
                log.error(errorMsg);
                return SkillExecutionResult.failure(skillId, errorMsg, "SKILL_NOT_FOUND");
            }
            
            // 获取 Handler
            SkillHandler handler = skillRegistry.getSkillHandler(skillId);
            if (handler == null) {
                String errorMsg = String.format("Skill [%s] 的 Handler 未找到", skillId);
                log.error(errorMsg);
                return SkillExecutionResult.failure(skillId, errorMsg, "HANDLER_NOT_FOUND");
            }
            
            // 验证必需参数
            SkillDefinition definition = skillRegistry.getSkillDefinition(skillId);
            if (definition.getRequiredParams() != null) {
                for (String requiredParam : definition.getRequiredParams()) {
                    if (!params.containsKey(requiredParam)) {
                        String errorMsg = String.format("缺少必需参数: %s", requiredParam);
                        log.error(errorMsg);
                        return SkillExecutionResult.failure(skillId, errorMsg, "MISSING_REQUIRED_PARAM");
                    }
                }
            }
            
            // 执行 Skill (带限流、熔断保护和重试)
            log.debug("开始执行 Skill [{}]", skillId);
            
            // 1. 限流检查
            if (!rateLimiter.tryAcquire(skillId, 1, skillProperties.getTimeoutSeconds(), java.util.concurrent.TimeUnit.SECONDS)) {
                String errorMsg = String.format("Skill [%s] 请求频率超过限制", skillId);
                log.warn(errorMsg);
                return SkillExecutionResult.failure(skillId, errorMsg, "RATE_LIMIT_EXCEEDED");
            }
            
            // 2. 带超时的执行(使用 ServiceExceptionHandler 包装)
            Object result = ServiceExceptionHandler.safeExecute(
                () -> executeWithTimeout(skillId, () -> circuitBreaker.execute(skillId, () -> retryStrategy.executeWithRetry(() -> handler.execute(params))), skillProperties.getTimeoutSeconds()),
                "Skill 执行失败"
            );
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            log.info("Skill [{}] 执行成功,耗时: {}ms", skillId, executionTime);
            return SkillExecutionResult.success(skillId, result, executionTime);
            
        } catch (AiException e) {
            // 已处理的 AI 异常
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Skill [{}] 执行失败: {}", skillId, e.getMessage(), e);
            String errorCode = e.getCode() != null ? e.getCode() : "AI_EXCEPTION";
            return SkillExecutionResult.failure(skillId, e.getMessage(), errorCode);
        } catch (Exception e) {
            // 其他异常转换为 AiException
            long executionTime = System.currentTimeMillis() - startTime;
            String errorMsg = String.format("Skill [%s] 执行失败: %s", skillId, e.getMessage());
            log.error(errorMsg, e);
            return SkillExecutionResult.failure(skillId, errorMsg, "EXECUTION_ERROR");
        }
    }
    
    /**
     * 带超时控制的执行
     *
     * @param skillId   Skill ID
     * @param operation 要执行的操作
     * @param timeoutSeconds 超时时间(秒)
     * @return 执行结果
     */
    private Object executeWithTimeout(String skillId, java.util.function.Supplier<Object> operation, int timeoutSeconds) {
        return ExecutorUtils.executeWithTimeout(operation, executorService, timeoutSeconds, "Skill [" + skillId + "]");
    }
    
    /**
     * 异步执行 Skill
     *
     * @param skillId Skill ID
     * @param params  参数
     * @return Future
     */
    public CompletableFuture<SkillExecutionResult> executeAsync(String skillId, Map<String, Object> params) {
        if (executorService == null || executorService.isShutdown()) {
            throw new IllegalStateException("异步执行器未启用或已关闭");
        }
        
        return CompletableFuture.supplyAsync(() -> execute(skillId, params), executorService);
    }
    
    /**
     * 批量执行 Skills
     *
     * @param skillIds Skill IDs 列表
     * @param params   参数列表(与 skillIds 一一对应)
     * @return 执行结果列表
     */
    public List<SkillExecutionResult> batchExecute(List<String> skillIds, List<Map<String, Object>> params) {
        ExecutorUtils.validateListSizesMatch(skillIds, params, "skillIds", "params");
        
        List<SkillExecutionResult> results = new ArrayList<>();
        for (int i = 0; i < skillIds.size(); i++) {
            results.add(execute(skillIds.get(i), params.get(i)));
        }
        
        return results;
    }
    
    /**
     * 并行批量执行 Skills
     *
     * @param skillIds Skill IDs 列表
     * @param params   参数列表
     * @return Future 列表
     */
    public List<CompletableFuture<SkillExecutionResult>> parallelExecute(
            List<String> skillIds, 
            List<Map<String, Object>> params) {
        
        ExecutorUtils.validateListSizesMatch(skillIds, params, "skillIds", "params");
        
        List<CompletableFuture<SkillExecutionResult>> futures = new ArrayList<>();
        for (int i = 0; i < skillIds.size(); i++) {
            futures.add(executeAsync(skillIds.get(i), params.get(i)));
        }
        
        return futures;
    }
    
    /**
     * 链式执行 Skills(前一个的结果作为后一个的输入)
     *
     * @param skillIds     Skill IDs 列表
     * @param initialParams 初始参数
     * @return 最终执行结果
     */
    public SkillExecutionResult chainExecute(List<String> skillIds, Map<String, Object> initialParams) {
        if (skillIds == null || skillIds.isEmpty()) {
            throw new IllegalArgumentException("skillIds 不能为空");
        }
        
        Map<String, Object> currentParams = new HashMap<>(initialParams);
        SkillExecutionResult lastResult = null;
        
        for (String skillId : skillIds) {
            lastResult = execute(skillId, currentParams);
            
            if (!lastResult.getSuccess()) {
                log.error("链式执行在 Skill [{}] 处失败", skillId);
                return lastResult;
            }
            
            // 将结果合并到参数中,供下一个 Skill 使用
            if (lastResult.getData() instanceof Map) {
                currentParams.putAll((Map<? extends String, ?>) lastResult.getData());
            } else {
                currentParams.put("previous_result", lastResult.getData());
            }
        }
        
        return lastResult;
    }
    
    /**
     * 自动发现并加载 Skills
     */
    public void autoDiscoverSkills() {
        if (!skillProperties.getAutoDiscover()) {
            log.info("自动发现功能未启用");
            return;
        }
        
        String registryPath = skillProperties.getRegistryPath();
        log.info("开始自动发现 Skills,路径: {}", registryPath);
        
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(registryPath + "*.yaml");
            
            int loadedCount = 0;
            for (Resource resource : resources) {
                try {
                    SkillDefinition definition = yamlMapper.readValue(
                            resource.getInputStream(), 
                            SkillDefinition.class
                    );
                    
                    if (definition.getEnabled()) {
                        // 通过 Spring 容器获取 Handler Bean
                        // 这里需要在 Configuration 中注入 ApplicationContext
                        log.info("发现 Skill: {} ({})", definition.getId(), definition.getName());
                        loadedCount++;
                    }
                } catch (Exception e) {
                    log.error("加载 Skill 配置文件失败: {}", resource.getFilename(), e);
                }
            }
            
            log.info("自动发现完成,共加载 {} 个 Skills", loadedCount);
            
        } catch (IOException e) {
            log.error("自动发现 Skills 失败", e);
        }
    }
    
    /**
     * 关闭执行器
     */
    public void shutdown() {
        ExecutorUtils.safeShutdown(executorService, 5, "Skills 执行器");
    }
}
