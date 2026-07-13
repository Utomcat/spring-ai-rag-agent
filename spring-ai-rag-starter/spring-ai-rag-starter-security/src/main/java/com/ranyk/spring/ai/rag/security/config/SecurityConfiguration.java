package com.ranyk.spring.ai.rag.security.config;

import com.ranyk.spring.ai.rag.security.config.properties.JwtProperties;
import com.ranyk.spring.ai.rag.security.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * CLASS_NAME: SecurityConfig.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Spring Security 安全框架配置类
 * @date: 2026-06-27
 */
@Slf4j
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfiguration {


    /**
     * 本系统密码仅存 MD5，登录处自行比对；此 Encoder 仅占位，避免其它组件注入失败。
     *
     * @return {@link PasswordEncoder} 对象
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        log.debug("============================ 配置 PasswordEncoder Bean start ========================================");
        log.debug("配置自定义的 PasswordEncoder Bean 中 ... ");
        log.debug("============================ 配置 PasswordEncoder Bean end   ========================================");
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return rawPassword.toString().equals(encodedPassword);
            }
        };
    }

    /**
     * JWT 认证过滤器。
     *
     * @param jwtProperties {@link JwtProperties} 对象
     * @return {@link JwtAuthFilter} 对象
     */
    @Bean
    public JwtAuthFilter jwtAuthFilter(JwtProperties jwtProperties) {
        log.debug("============================ 配置 JwtAuthFilter Bean start ========================================");
        log.debug("配置 JwtAuthFilter Bean 中 ... ");
        log.debug("============================ 配置 JwtAuthFilter Bean end   ========================================");
        return new JwtAuthFilter(jwtProperties);
    }

    /**
     * 安全过滤链配置。
     *
     * @param httpSecurity  {@link HttpSecurity} 对象
     * @param jwtAuthFilter {@link JwtAuthFilter} 对象
     * @return {@link SecurityFilterChain} 对象
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, JwtAuthFilter jwtAuthFilter) {
        log.debug("============================ 配置 SecurityFilterChain Bean start ========================================");
        log.debug("配置 SecurityFilterChain Bean 中 ... ");
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(reg -> reg
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/files/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        log.debug("配置 SecurityFilterChain Bean end   ========================================");
        return httpSecurity.build();
    }
}
