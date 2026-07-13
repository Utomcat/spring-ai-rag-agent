package com.ranyk.spring.ai.rag.knowledge.database.api.auth;

import com.ranyk.spring.ai.rag.base.domain.vo.Result;
import com.ranyk.spring.ai.rag.knowledge.database.domain.login.mapstruct.LoginMapper;
import com.ranyk.spring.ai.rag.knowledge.database.domain.login.po.LoginPO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.login.vo.LoginVO;
import com.ranyk.spring.ai.rag.knowledge.database.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CLASS_NAME: AuthApi.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 认证登录 API 接口类
 * @date: 2026-06-27
 */
@RestController
@RequestMapping("/api/auth")
public class AuthApi {

    /**
     * 登录认证业务逻辑处理类 {@link AuthService} 对象
     */
    private final AuthService authService;
    /**
     * 登录信息数据转换映射器 {@link LoginMapper} 对象
     */
    private final LoginMapper loginMapper;

    /**
     * 构造方法 - 由 Spring IOC 容器自动注入 {@link AuthService} 对象
     *
     * @param authService 登录认证业务逻辑处理类 {@link AuthService} 对象
     * @param loginMapper 登录信息数据转换映射器 {@link LoginMapper} 对象
     */
    @Autowired
    public AuthApi(AuthService authService, LoginMapper loginMapper) {
        this.authService = authService;
        this.loginMapper = loginMapper;
    }

    /**
     * 系统登录认证 API 接口
     *
     * @param loginPO 登录信息数据封装对象
     * @param request HTTP 请求对象
     * @return 登录认证结果封装对象 {@link Result} , 具体数据为 {@link LoginVO} 对象
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginPO loginPO, HttpServletRequest request) {
        return Result.success(loginMapper.loginDTOToLoginVO(authService.login(loginMapper.loginPOToLoginDTO(loginPO), request)));
    }

}
