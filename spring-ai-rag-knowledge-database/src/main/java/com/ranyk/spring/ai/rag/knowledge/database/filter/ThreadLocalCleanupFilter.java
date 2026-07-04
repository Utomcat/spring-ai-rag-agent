package com.ranyk.spring.ai.rag.knowledge.database.filter;

import com.ranyk.spring.ai.rag.knowledge.database.ai.advisor.ReferenceExtractAdvisor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * CLASS_NAME: ThreadLocalCleanupFilter.java
 *
 * @author ranyk
 * @version V1.0
 * @description: ThreadLocal 清理过滤器 - 确保每个请求结束时清理 ReferenceExtractAdvisor 的 ThreadLocal，防止内存泄漏
 * 作为兜底保障机制，即使业务代码忘记调用 clearReferences()，也能保证资源被正确释放
 * @date: 2026-07-04
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ThreadLocalCleanupFilter extends OncePerRequestFilter {

    /**
     * 引用提取 Advisor
     */
    private final ReferenceExtractAdvisor referenceExtractAdvisor;

    /**
     * 过滤器核心方法 - 确保无论请求是否成功，都清理 ThreadLocal
     *
     * @param request     HTTP 请求对象
     * @param response    HTTP 响应对象
     * @param filterChain 过滤器链
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            // 执行后续的过滤器链和业务逻辑
            filterChain.doFilter(request, response);
        } finally {
            // 无论是否发生异常，都清理 ThreadLocal，防止内存泄漏
            try {
                referenceExtractAdvisor.clearReferences();
                log.debug("ThreadLocal 清理完成 - URI: {}", request.getRequestURI());
            } catch (Exception e) {
                // 清理失败不应影响响应，仅记录日志
                log.warn("ThreadLocal 清理失败 - URI: {}, error: {}", request.getRequestURI(), e.getMessage());
            }
        }
    }
}
