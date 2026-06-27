package com.ranyk.spring.ai.rag.knowledge.database.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.nio.charset.StandardCharsets;

/**
 * CLASS_NAME: I18nConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 国际化语言配置类
 * @date: 2026-06-25
 */
@Slf4j
@Configuration
public class I18nConfiguration implements WebMvcConfigurer {

    /**
     * 配置国际化消息源 Bean
     *
     * @return MessageSource 消息源对象
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        // 设置国际化资源文件路径
        messageSource.setBasenames("classpath:i18n/messages");
        // 设置编码格式
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        // 设置缓存时间（秒），-1 表示永不过期
        messageSource.setCacheSeconds(3600);
        // 当找不到对应语言环境的资源文件时，使用默认语言环境
        messageSource.setUseCodeAsDefaultMessage(false);
        log.debug("I18nConfigure configure. MessageSource bean created with basename: classpath:i18n/messages");
        return messageSource;
    }

    /**
     * Add Spring MVC lifecycle interceptors for pre- and post-processing of
     * controller method invocations and resource handler requests.
     * Interceptors can be registered to apply to all requests or be limited
     * to a subset of URL patterns.
     *
     * @param registry 拦截器注册对象 {@link InterceptorRegistry}
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.debug("I18nConfigure configure. add LocaleChangeInterceptor");
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        // 请求参数名，如?lang=zh_CN
        interceptor.setParamName("lang");
        registry.addInterceptor(interceptor);
    }
}
