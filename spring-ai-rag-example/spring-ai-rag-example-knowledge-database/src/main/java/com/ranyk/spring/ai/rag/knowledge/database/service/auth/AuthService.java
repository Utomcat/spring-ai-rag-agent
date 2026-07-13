package com.ranyk.spring.ai.rag.knowledge.database.service.auth;

import com.ranyk.spring.ai.rag.knowledge.database.domain.log.dto.SystemLogDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.login.dto.LoginDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.user.dto.AppUserDTO;
import com.ranyk.spring.ai.rag.knowledge.database.service.log.SystemLogService;
import com.ranyk.spring.ai.rag.knowledge.database.service.user.AppUserService;
import com.ranyk.spring.ai.rag.web.utils.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * CLASS_NAME: AuthService.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 认证登录 业务逻辑 Service
 * @date: 2026-06-27
 */
@Slf4j
@Service
public class AuthService {
    /**
     * 用户服务类对象
     */
    private final AppUserService appUserService;
    /**
     * 系统日志服务类对象
     */
    private final SystemLogService systemLogService;

    /**
     * 构造函数 - 由 Spring IOC 容器向当前 Bean 自动注入 用户服务类对象 {@link AppUserService} 和 系统日志服务类对象 {@link SystemLogService}
     *
     * @param appUserService   用户服务类对象 {@link AppUserService}
     * @param systemLogService 系统日志服务类对象 {@link SystemLogService}
     */
    @Autowired
    public AuthService(AppUserService appUserService, SystemLogService systemLogService) {
        this.appUserService = appUserService;
        this.systemLogService = systemLogService;
    }

    /**
     * 系统登录认证处理逻辑
     *
     * @param loginDTO 登录信息数据封装 DTO 对象 {@link LoginDTO}
     * @param request  HTTP 请求对象 {@link HttpServletRequest}
     * @return 登录信息数据封装 DTO 对象 {@link LoginDTO}, 注意方法返回的 DTO 对象, 和参数 DTO 对象不是同一个对象
     */
    public LoginDTO login(LoginDTO loginDTO, HttpServletRequest request) {
        AppUserDTO appUserDTO = appUserService.login(AppUserDTO.builder().username(loginDTO.getUsername()).password(loginDTO.getPassword()).build());
        systemLogService.saveSystemLog(SystemLogDTO.builder()
                .userId(appUserDTO.getId())
                .action("系统登录")
                .ip(WebUtils.clientIp(request))
                .createBy(appUserDTO.getId())
                .createTime(LocalDateTime.now())
                .updateBy(appUserDTO.getId())
                .updateTime(LocalDateTime.now())
                .build());
        return LoginDTO.builder()
                .token(appUserDTO.getToken())
                .user(AppUserDTO.builder()
                        .id(appUserDTO.getId())
                        .username(appUserDTO.getUsername())
                        .realName(appUserDTO.getRealName())
                        .role(appUserDTO.getRole())
                        .avatar(appUserDTO.getAvatar())
                        .build())
                .build();
    }
}
