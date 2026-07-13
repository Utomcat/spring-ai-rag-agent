package com.ranyk.spring.ai.rag.security.utils;

import com.ranyk.spring.ai.rag.security.domain.dto.LoginUserDetailsDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

/**
 * CLASS_NAME: SecurityUtils.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Spring Security 工具类 - 主要用于从 SecurityContext 从中获取相关的登录用户信息
 * @date: 2026-06-28
 */
public class SecurityUtils {

    /**
     * 获取当前登录用户信息
     *
     * @return 返回获取到的当前登录账户信息对象 {@link LoginUserDetailsDTO} , 当未登录则返回一个无数据的 {@link LoginUserDetailsDTO} 对象
     */
    public static LoginUserDetailsDTO currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if ( Objects.isNull(auth) || !(auth.getPrincipal() instanceof LoginUserDetailsDTO user)) {
            return LoginUserDetailsDTO.builder().build();
        }
        return user;
    }

    /**
     * 必须已登录, 否则抛 IllegalStateException 异常
     *
     * @return 返回当前登录用户信息对象 {@link LoginUserDetailsDTO}
     */
    public static LoginUserDetailsDTO requireUser() {
        LoginUserDetailsDTO user = currentUser();
        if (Objects.isNull(user.getUserId())) {
            throw new IllegalStateException("用户未登录!");
        }
        return user;
    }
}
