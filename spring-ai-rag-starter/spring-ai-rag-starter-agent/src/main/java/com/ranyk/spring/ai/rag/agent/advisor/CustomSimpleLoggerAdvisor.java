package com.ranyk.spring.ai.rag.agent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Objects;

/**
 * CLASS_NAME: CustomSimpleLoggerAdvisor.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 自定义简单的日志 Advisor 拦截器, 通过实现 {@link CallAdvisor} (阻塞式请求) 和 {@link StreamAdvisor} (流式请求) 接口
 * @date: 2026-07-11
 */
@Slf4j
@Component
public class CustomSimpleLoggerAdvisor implements CallAdvisor, StreamAdvisor {

    @Override
    public @NonNull ChatClientResponse adviseCall(@NonNull ChatClientRequest chatClientRequest, @NonNull CallAdvisorChain callAdvisorChain) {
        // log.info("自定义日志 Advisor , 调用 call 方法后, 向 LLM 发送消息之前, 用户的请求对象为: {}", chatClientRequest);
        log.info("自定义日志 Advisor , 调用 call 方法后, 向 LLM 发送消息之前, 日志输出, 即将调用 LLM 进行问题分析处理....");
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        if (Objects.nonNull(chatClientResponse.chatResponse()) && Objects.nonNull(chatClientResponse.chatResponse().getResult())) {
            String content = chatClientResponse.chatResponse().getResult().getOutput().getText();
            log.info("自定义日志 Advisor, 调用 call 方法后, 向 LLM 发送消息之后, 获取到的响应内容长度: {}", content != null ? content.length() : 0);
            // log.info("自定义日志 Advisor, 调用 call 方法后, 向 LLM 发送消息之后, 获取到的响应内容为: {}", content);
        } else {
            log.warn("自定义日志 Advisor, 调用 call 方法后, 向 LLM 发送消息之后, 获取到的响应为空");
        }

        return chatClientResponse;
    }

    @Override
    public @NonNull Flux<ChatClientResponse> adviseStream(@NonNull ChatClientRequest chatClientRequest, @NonNull StreamAdvisorChain streamAdvisorChain) {
        log.info("自定义日志 Advisor , 调用 stream 方法后, 向 LLM 发送消息之前, 用户的请求对象为: {}", chatClientRequest);
        return streamAdvisorChain.nextStream(chatClientRequest).doOnNext(response ->  {
            if (Objects.nonNull(response.chatResponse()) && Objects.nonNull(response.chatResponse().getResult())) {
                String content = response.chatResponse().getResult().getOutput().getText();
                log.info("自定义日志 Advisor, 调用 stream 方法后, 向 LLM 发送消息之后, 获取到的响应内容片段为: {}", content);
            } else {
                log.info("自定义日志 Advisor, 调用 stream 方法后, 向 LLM 发送消息之后, 获取到的响应片段为空");
            }
        });
    }

    /**
     * Return the name of the advisor.
     *
     * @return the advisor name.
     */
    @Override
    public @NonNull String getName() {
        return "customSimpleLoggerAdvisor";
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
