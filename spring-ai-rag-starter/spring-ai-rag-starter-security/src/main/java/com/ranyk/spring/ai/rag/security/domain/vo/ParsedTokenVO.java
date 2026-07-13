package com.ranyk.spring.ai.rag.security.domain.vo;

/**
 * CLASS_NAME: ParsedTokenVO.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 解析 JWT - Token 后的载荷, 供过滤器使用, 其字段说明如下:
 * <ul>
 *     <li>userId: 用户ID</li>
 *     <li>username: 用户名</li>
 *     <li>role: 角色</li>
 * </ul>
 * @date: 2026-07-10
 */
public record ParsedTokenVO(Long userId, String username, String role) {
}
