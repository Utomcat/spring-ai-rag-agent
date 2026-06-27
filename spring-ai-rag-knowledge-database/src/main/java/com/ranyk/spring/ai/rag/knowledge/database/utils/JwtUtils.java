package com.ranyk.spring.ai.rag.knowledge.database.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.ranyk.spring.ai.rag.knowledge.database.config.properties.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * CLASS_NAME: JwtUtils.java
 *
 * @author ranyk
 * @version V1.0
 * @description: JWT 工具类
 * @date: 2026-06-27
 */
@Slf4j
@Component
public class JwtUtils {

    /**
     * JWT 配置属性对象
     */
    private final JwtProperties jwtProperties;

    /**
     * 构造函数 通过 Spring IOC 容器向当前 Bean 中注入 JWT 配置属性对象
     *
     * @param jwtProperties JWT 配置属性对象
     */
    @Autowired
    public JwtUtils(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * 解析 JWT - Token 后的载荷, 供过滤器使用
     *
     * @param userId   用户 ID
     * @param username 登录名
     * @param role     角色
     */
    public record ParsedToken(Long userId, String username, String role) {
    }

    /**
     * 根据用户主键、登录名、角色签发 Token。
     *
     * @param userId   用户 ID
     * @param username 登录名
     * @param role     角色
     * @return JWT 字符串
     */
    public String createToken(Long userId, String username, String role) {
        long nowSec = System.currentTimeMillis() / 1000;
        Map<String, Object> payload = new HashMap<>();
        payload.put("uid", userId);
        payload.put("username", username);
        payload.put("role", role);
        payload.put("sub", username);
        payload.put("iat", nowSec);
        payload.put("exp", nowSec + jwtProperties.getExpireHours() * 3600L);
        return JWTUtil.createToken(payload, jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 解析并校验 Token，失败返回 null。
     *
     * @param token Bearer 后的完整 token
     */
    public ParsedToken parse(String token) {
        try {
            JWT jwt = JWTUtil.parseToken(token);
            boolean ok = jwt.setKey(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)).verify();
            if (!ok) {
                log.error("JWT 验证失败, 返回一个空的 ParsedToken 对象");
                return new ParsedToken(null, null, null);
            }
            JSONObject pl = jwt.getPayloads();
            if (pl == null) {
                log.error("JWT 载荷为空, 返回一个空的 ParsedToken 对象");
                return new ParsedToken(null, null, null);
            }
            Long uid = pl.getLong("uid");
            String username = pl.getStr("username");
            if (username == null || username.isEmpty()) {
                username = pl.getStr("sub");
            }
            String r = pl.getStr("role");
            return new ParsedToken(uid, username, r);
        } catch (Exception e) {
            log.error("JWT 解析异常, 返回一个空的 ParsedToken 对象");
            return new ParsedToken(null, null, null);
        }
    }
}
