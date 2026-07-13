package com.ranyk.spring.ai.rag.knowledge.database.service.user;

import com.ranyk.spring.ai.rag.common.exception.ServiceException;
import com.ranyk.spring.ai.rag.knowledge.database.domain.user.dto.AppUserDTO;
import com.ranyk.spring.ai.rag.security.domain.dto.LoginUserDetailsDTO;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * CLASS_NAME: CustomUserDetailsService.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 用于 Spring Security 用户详情处理逻辑 Service 类
 * @date: 2026-06-27
 */
@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * appUserService: 用户服务类
     */
    private final AppUserService appUserService;

    /**
     * CustomUserDetailsService: 自定义用户详情服务类
     *
     * @param appUserService 用户服务类
     */
    @Autowired
    public CustomUserDetailsService(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    /**
     *
     * loadUserByUsername: 根据用户名加载用户详情
     *
     * @param username 用户名
     * @return UserDetails 用户详情 {@link UserDetails}
     * @throws UsernameNotFoundException 用户未找到异常
     */
    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        log.debug("通过登录账户获取用户详情信息, 当前登录的账户为: {}", username);
        AppUserDTO appUser = appUserService.findByUsername(AppUserDTO.builder().username(username).build());
        if (Objects.isNull(appUser.getId())) {
            log.debug("用户不存在: {}", username);
            throw new ServiceException("username.not.exists", new String[]{username});
        }
        return LoginUserDetailsDTO.builder()
                .userId(appUser.getId())
                .username(appUser.getUsername())
                .passwordHash(appUser.getPassword())
                .role(appUser.getRole())
                .build();
    }
}
