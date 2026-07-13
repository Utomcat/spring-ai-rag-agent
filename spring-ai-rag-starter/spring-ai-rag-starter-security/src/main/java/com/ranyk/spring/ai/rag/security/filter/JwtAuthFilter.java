package com.ranyk.spring.ai.rag.security.filter;

import cn.hutool.core.util.StrUtil;
import com.ranyk.spring.ai.rag.security.config.properties.JwtProperties;
import com.ranyk.spring.ai.rag.security.domain.dto.LoginUserDetailsDTO;
import com.ranyk.spring.ai.rag.security.domain.vo.ParsedTokenVO;
import com.ranyk.spring.ai.rag.security.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

/**
 * CLASS_NAME: JwtAuthFilter.java
 *
 * @author ranyk
 * @version V1.0
 * @description: JWT 认证过滤器. 解析 Bearer Token 并写入 SecurityContext 上下文中
 * @date: 2026-06-27
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    /**
     * JWT 相关属性
     */
    private final JwtProperties jwtProperties;

    /**
     * JwtAuthFilter 构造函数 - 用于 Spring IOC 容器管理在创建当前类的 Bean 实例时调用, 自动注入相关依赖的 Bean 实例对象
     *
     * @param jwtProperties JWT 相关属性
     */
    @Autowired
    public JwtAuthFilter(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }


    /**
     * 过滤器核心方法
     *
     * @param request     请求对象
     * @param response    响应对象
     * @param filterChain 过滤器链
     * @throws ServletException ServletException
     * @throws IOException      IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            ParsedTokenVO parsed = JwtUtils.parse(token, jwtProperties);
            if (Objects.nonNull(parsed.userId()) && Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {
                Long userId = parsed.userId();
                String username = StrUtil.isNotBlank(parsed.username()) ? parsed.username() : "";
                String role = parsed.role();
                LoginUserDetailsDTO details = LoginUserDetailsDTO.builder()
                        .userId(userId)
                        .username(username)
                        .passwordHash("")
                        .role(role)
                        .build();
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request, response);
    }
}
