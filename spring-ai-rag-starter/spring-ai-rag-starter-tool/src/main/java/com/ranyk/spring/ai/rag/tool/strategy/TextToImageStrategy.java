package com.ranyk.spring.ai.rag.tool.strategy;

import com.ranyk.spring.ai.rag.common.constant.AiFactoryOwnerTypeEnum;
import com.ranyk.spring.ai.rag.tool.config.properties.ImageGenerationProperties;

/**
 * CLASS_NAME: TextToImageStrategy.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 文字转图片策略接口类
 * @date: 2026-07-23
 */
public interface TextToImageStrategy {

    /**
     * 获取策略类型
     *
     * @return 策略类型, {@link AiFactoryOwnerTypeEnum}
     */
    AiFactoryOwnerTypeEnum getType();

    /**
     * 执行策略
     *
     * @return 执行结果, 文字转图片后生成的图片 URL 下载路径
     */
    String execute(ImageGenerationProperties.ApiConfig apiConfig, String prompt, String size);
}
