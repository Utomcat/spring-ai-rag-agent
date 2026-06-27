package com.ranyk.spring.ai.rag.knowledge.database.utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * CLASS_NAME: WebUtils.java
 *
 * @author ranyk
 * @version V1.0
 * @description: web 工具类
 * @date: 2026-06-27
 */
public class WebUtils {

    /**
     * 尽力获取真实客户端 IP（考虑反向代理常见头）
     *
     * @param request HTTP 请求对象 {@link HttpServletRequest}
     * @return 返回获取到的客户端 IP 地址
     */
    public static String clientIp(HttpServletRequest request) {
        String h = request.getHeader("X-Forwarded-For");
        if (h != null && !h.isBlank()) {
            return h.split(",")[0].trim();
        }
        h = request.getHeader("X-Real-IP");
        if (h != null && !h.isBlank()) {
            return h.trim();
        }
        return request.getRemoteAddr();
    }
}
