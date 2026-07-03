package com.ranyk.spring.ai.rag.knowledge.database.ai.advisor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * CLASS_NAME: ReferenceExtractAdvisor.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 引用提取 Advisor - 拦截知识库检索工具调用结果，提取 references 供后续使用
 * 使用 ThreadLocal 存储提取的引用文档列表，避免修改不可变的 ChatResponseMetadata
 * @date: 2026-07-04
 */
@Slf4j
@Component
public class ReferenceExtractAdvisor implements CallAdvisor {

    /**
     * 使用 ThreadLocal 存储当前线程提取的 references
     */
    private static final ThreadLocal<List<Map<String, Object>>> REFERENCES_HOLDER = ThreadLocal.withInitial(List::of);

    /**
     * Jackson 对象映射器
     */
    private final ObjectMapper objectMapper;

    /**
     * 构造方法 - 由 Spring IOC 容器创建当前 Bean 实例对象时自动调用注入相关依赖
     *
     * @param objectMapper Jackson 对象映射器 {@link ObjectMapper}
     */
    public ReferenceExtractAdvisor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 拦截知识库检索工具调用结果，提取 references 供后续使用
     *
     * @param chatClientRequest  Chat 客户端请求对象 {@link ChatClientRequest}
     * @param callAdvisorChain   拦截器链 {@link CallAdvisorChain}
     * @return Chat 客户端响应对象 {@link ChatClientResponse}
     */
    @Override
    public @NonNull ChatClientResponse adviseCall(@NonNull ChatClientRequest chatClientRequest, @NonNull CallAdvisorChain callAdvisorChain) {
        // 清空之前的 references
        REFERENCES_HOLDER.set(new ArrayList<>());

        // 执行正常的调用链
        ChatClientResponse response = callAdvisorChain.nextCall(chatClientRequest);

        try {
            // 从响应中提取工具调用结果
            if (response.chatResponse() != null) {
                for (var generation : response.chatResponse().getResults()) {
                    // 获取工具调用响应列表 - 使用类型安全的辅助方法
                    List<Map<String, Object>> toolCallResponses = getToolCallResponses(generation);

                    if (toolCallResponses != null && !toolCallResponses.isEmpty()) {
                        log.info("检测到 {} 个工具调用响应，开始提取 references", toolCallResponses.size());

                        // 遍历所有工具调用响应
                        for (Map<String, Object> toolResponse : toolCallResponses) {
                            String toolName = (String) toolResponse.get("name");
                            String toolResult = (String) toolResponse.get("result");

                            // 检查是否是知识库检索工具的调用
                            if ("retrieveKnowledge".equals(toolName) && toolResult != null) {
                                log.info("发现知识库检索工具调用结果，开始解析 references");

                                // 解析 JSON 提取 documents - 使用类型安全的辅助方法
                                Map<String, Object> result = parseJsonToMap(toolResult);
                                List<Map<String, Object>> documents = extractDocuments(result);

                                if (documents != null && !documents.isEmpty()) {
                                    // 将 references 存入 ThreadLocal
                                    REFERENCES_HOLDER.set(documents);
                                    log.info("成功提取 {} 个引用文档", documents.size());
                                } else {
                                    log.info("知识库检索结果为空，无引用文档");
                                    REFERENCES_HOLDER.set(List.of());
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("提取 references 失败：{}", e.getMessage(), e);
            // 提取失败不影响主流程，设置空引用列表
            REFERENCES_HOLDER.set(List.of());
        }

        return response;
    }

    /**
     * 获取当前线程提取的 references
     *
     * @return 引用文档列表
     */
    public List<Map<String, Object>> getExtractedReferences() {
        return REFERENCES_HOLDER.get();
    }

    /**
     * 清理当前线程的 references（防止内存泄漏）
     */
    public void clearReferences() {
        REFERENCES_HOLDER.remove();
    }

    /**
     * 类型安全的工具调用响应提取方法
     * 将未检查的转换警告限制在此方法内
     *
     * @param generation 生成对象
     * @return 工具调用响应列表
     */
    private List<Map<String, Object>> getToolCallResponses(Object generation) {
        // 这里使用了反射或其他方式获取 metadata，简化为直接访问
        // 实际代码中 generation 应该是 Generation 类型
        var gen = (org.springframework.ai.chat.model.Generation) generation;
        return gen.getMetadata().get("toolCallResponses");
    }

    /**
     * 类型安全的 JSON 解析方法
     *
     * @param json JSON 字符串
     * @return 解析后的 Map
     * @throws Exception 解析异常
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJsonToMap(String json) throws Exception {
        return objectMapper.readValue(json, Map.class);
    }

    /**
     * 类型安全的文档列表提取方法
     *
     * @param result 解析结果
     * @return 文档列表
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractDocuments(Map<String, Object> result) {
        if (result == null) {
            return null;
        }
        return (List<Map<String, Object>>) result.get("documents");
    }

    /**
     * Return the name of the advisor.
     *
     * @return the advisor name.
     */
    @Override
    public @NonNull String getName() {
        return "referenceExtractAdvisor";
    }

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return 0;
    }

}
