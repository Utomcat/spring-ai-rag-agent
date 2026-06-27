package com.ranyk.spring.ai.rag.knowledge.database.utils;

import cn.hutool.extra.spring.SpringUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.MessageSource;

/**
 * CLASS_NAME: MessageUtils.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 国际化信息工具类
 * @date: 2026-06-25
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageUtils {
    /**
     * 获取国际化信息 Bean 对象
     */
    private static final MessageSource MESSAGE_SOURCE = SpringUtil.getBean(MessageSource.class);

    /**
     * 根据消息键和参数 获取消息 委托给spring messageSource
     *
     * @param code 异常代码
     * @param args 异常信息参数
     * @return 获取国际化翻译值
     */
    public static String message(String code, Object... args) {
        try {
            return MESSAGE_SOURCE.getMessage(code, args, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            return code;
        }
    }
}
