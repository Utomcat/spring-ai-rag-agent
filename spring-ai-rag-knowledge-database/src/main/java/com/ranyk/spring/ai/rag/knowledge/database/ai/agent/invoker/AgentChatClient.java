package com.ranyk.spring.ai.rag.knowledge.database.ai.agent.invoker;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CLASS_NAME: AgentChatClient.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Agent ChatClient 包装器 - 封装 Spring AI ChatClient,提供统一的对话接口
 * @date: 2026-07-06
 */
@Slf4j
@Component
public class AgentChatClient {
    
    private final ChatClient chatClient;
    private final MeterRegistry meterRegistry;
    private final Map<String, List<Message>> conversationHistories = new ConcurrentHashMap<>();
    
    public AgentChatClient(ChatClient.Builder chatClientBuilder, MeterRegistry meterRegistry) {
        this.chatClient = chatClientBuilder.build();
        this.meterRegistry = meterRegistry;
        log.info("Agent ChatClient 初始化成功");
    }
    
    /**
     * 执行单次对话(无历史上下文)
     *
     * @param systemPrompt 系统提示词
     * @param userPrompt   用户提示词
     * @return AI 响应内容
     */
    public String execute(String systemPrompt, String userPrompt) {
        long startTime = System.currentTimeMillis();
        
        try {
            log.debug("执行 ChatClient 调用,系统提示词: {}, 用户提示词: {}", 
                    systemPrompt != null ? systemPrompt.substring(0, Math.min(50, systemPrompt.length())) : "null",
                    userPrompt != null ? userPrompt.substring(0, Math.min(50, userPrompt.length())) : "null");
            
            ChatResponse response = chatClient.prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .chatResponse();
            
            if (response == null || response.getResult() == null) {
                throw new RuntimeException("ChatClient 返回结果为空");
            }
            
            String content = response.getResult().getOutput().getText();
            long executionTime = System.currentTimeMillis() - startTime;
            
            // 记录指标
            recordMetrics("chat.execution", executionTime, true);
            
            log.debug("ChatClient 调用成功,耗时: {}ms, 结果长度: {}", executionTime, content.length());
            return content;
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            recordMetrics("chat.execution", executionTime, false);
            log.error("ChatClient 调用失败", e);
            throw new RuntimeException("ChatClient 调用失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 执行带工具的对话
     *
     * @param systemPrompt 系统提示词
     * @param userPrompt   用户提示词
     * @param tools        工具名称列表
     * @return AI 响应内容
     */
    public String executeWithTools(String systemPrompt, String userPrompt, List<String> tools) {
        long startTime = System.currentTimeMillis();
        
        try {
            log.debug("执行带工具的 ChatClient 调用,工具列表: {}", tools);
            
            // TODO: 集成 Function Calling - 根据 tools 参数动态注册工具
            // 这里需要在后续实现 ToolRegistry 后完善
            
            ChatResponse response = chatClient.prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .chatResponse();
            
            if (response == null || response.getResult() == null) {
                throw new RuntimeException("ChatClient 返回结果为空");
            }
            
            String content = response.getResult().getOutput().getText();
            long executionTime = System.currentTimeMillis() - startTime;
            
            // 记录指标
            recordMetrics("chat.tool.execution", executionTime, true);
            
            log.debug("带工具的 ChatClient 调用成功,耗时: {}ms", executionTime);
            return content;
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            recordMetrics("chat.tool.execution", executionTime, false);
            log.error("带工具的 ChatClient 调用失败", e);
            throw new RuntimeException("带工具的 ChatClient 调用失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 执行多轮对话(维护会话历史)
     *
     * @param conversationId 会话 ID
     * @param systemPrompt   系统提示词
     * @param userPrompt     用户提示词
     * @return AI 响应内容
     */
    public String executeWithHistory(String conversationId, String systemPrompt, String userPrompt) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 获取或创建会话历史
            List<Message> history = conversationHistories.computeIfAbsent(conversationId, k -> new ArrayList<>());
            
            // 如果是第一次对话,添加系统提示词
            if (history.isEmpty() && systemPrompt != null) {
                history.add(new SystemMessage(systemPrompt));
            }
            
            // 添加用户消息
            history.add(new UserMessage(userPrompt));
            
            log.debug("执行多轮对话,会话ID: {}, 历史消息数: {}", conversationId, history.size());
            
            ChatResponse response = chatClient.prompt()
                    .messages(history)
                    .call()
                    .chatResponse();
            
            if (response == null || response.getResult() == null) {
                throw new RuntimeException("ChatClient 返回结果为空");
            }
            
            String content = response.getResult().getOutput().getText();
            
            // 将 AI 响应添加到历史
            history.add(response.getResult().getOutput());
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            // 记录指标
            recordMetrics("chat.history.execution", executionTime, true);
            
            log.debug("多轮对话执行成功,耗时: {}ms", executionTime);
            return content;
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            recordMetrics("chat.history.execution", executionTime, false);
            log.error("多轮对话执行失败", e);
            throw new RuntimeException("多轮对话执行失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 清除会话历史
     *
     * @param conversationId 会话 ID
     */
    public void clearConversationHistory(String conversationId) {
        conversationHistories.remove(conversationId);
        log.debug("已清除会话历史,会话ID: {}", conversationId);
    }
    
    /**
     * 获取会话历史大小
     *
     * @param conversationId 会话 ID
     * @return 历史消息数量
     */
    public int getConversationHistorySize(String conversationId) {
        List<Message> history = conversationHistories.get(conversationId);
        return history != null ? history.size() : 0;
    }
    
    /**
     * 记录监控指标
     *
     * @param metricName 指标名称
     * @param duration   耗时(毫秒)
     * @param success    是否成功
     */
    private void recordMetrics(String metricName, long duration, boolean success) {
        Timer timer = meterRegistry.timer(metricName, "success", String.valueOf(success));
        timer.record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
    }
}
