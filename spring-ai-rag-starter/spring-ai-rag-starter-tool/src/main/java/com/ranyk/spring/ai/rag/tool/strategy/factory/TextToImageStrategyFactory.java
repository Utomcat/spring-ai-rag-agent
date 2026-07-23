package com.ranyk.spring.ai.rag.tool.strategy.factory;

import cn.hutool.core.util.StrUtil;
import com.ranyk.spring.ai.rag.common.constant.AiFactoryOwnerTypeEnum;
import com.ranyk.spring.ai.rag.common.exception.ServiceException;
import com.ranyk.spring.ai.rag.tool.config.properties.ImageGenerationProperties;
import com.ranyk.spring.ai.rag.tool.strategy.TextToImageStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CLASS_NAME: TextToImageStrategyFactory.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 文本转图片策略工厂
 * @date: 2026-07-23
 */
@Slf4j
@Component
public class TextToImageStrategyFactory {

    /**
     * 静态缓存策略对象
     */
    private final ConcurrentHashMap<AiFactoryOwnerTypeEnum, TextToImageStrategy> STRATEGY_CACHE_MAP = new ConcurrentHashMap<>(AiFactoryOwnerTypeEnum.values().length + 1);

    /**
     * 构造方法, 用于初始化策略缓存
     *
     * @param strategies 策略列表
     */
    @Autowired
    public TextToImageStrategyFactory(List<TextToImageStrategy> strategies) {
        strategies.forEach(strategy -> STRATEGY_CACHE_MAP.put(strategy.getType(), strategy));
    }

    /**
     * 根据类型执行策略
     *
     * @param apiConfig   策略配置, 参见 {@link ImageGenerationProperties.ApiConfig}
     * @param prompt 提示信息, 用户传入的提示信息
     * @param size   图片大小, 图片大小
     * @return 执行结果, 正常的为图片 URL 下载地址, （24 小时有效）
     */
    public String execute(ImageGenerationProperties.ApiConfig apiConfig, String prompt, String size) {
        // 判断提示信息是否为空
        if (StrUtil.isBlank(prompt)) {
            log.error("传入的用户提示信息为空, 不进行图片生成!");
            throw new ServiceException("user.prompt.empty", new Object[]{});
        }
        // 判断图片大小是否为空, 为空则使用默认值
        if (StrUtil.isBlank(size)) {
            size = apiConfig.getSize();
        }
        // 根据类型执行策略
        String type = apiConfig.getType();
        return switch (AiFactoryOwnerTypeEnum.getByCode(type)) {
            case DASH_SCOPE ->
                    Optional.of(STRATEGY_CACHE_MAP.get(AiFactoryOwnerTypeEnum.DASH_SCOPE)).orElseThrow(() -> new ServiceException("no.support.ai.factory.owner", new Object[]{type})).execute(apiConfig, prompt, size);
            case XIAO_MI ->
                    Optional.of(STRATEGY_CACHE_MAP.get(AiFactoryOwnerTypeEnum.XIAO_MI)).orElseThrow(() -> new ServiceException("no.support.ai.factory.owner", new Object[]{type})).execute(apiConfig, prompt, size);
            case TENCENT ->
                    Optional.of(STRATEGY_CACHE_MAP.get(AiFactoryOwnerTypeEnum.TENCENT)).orElseThrow(() -> new ServiceException("no.support.ai.factory.owner", new Object[]{type})).execute(apiConfig, prompt, size);
            case BAIDU ->
                    Optional.of(STRATEGY_CACHE_MAP.get(AiFactoryOwnerTypeEnum.BAIDU)).orElseThrow(() -> new ServiceException("no.support.ai.factory.owner", new Object[]{type})).execute(apiConfig, prompt, size);
            default -> throw new ServiceException("ai.factory.owner.type.invalid", new Object[]{type});
        };
    }
}
