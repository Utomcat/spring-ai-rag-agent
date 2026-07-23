package com.ranyk.spring.ai.rag.tool.config.properties;

import com.ranyk.spring.ai.rag.common.constant.AiFactoryOwnerTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * CLASS_NAME: ImageGenerationProperties.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 图像生成 API 配置属性类 - DashScope 原生接口配置
 * @date: 2026-07-23
 */
@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = ImageGenerationProperties.CONFIG_PREFIX)
public class ImageGenerationProperties {
    /**
     * 配置属性前缀
     */
    public static final String CONFIG_PREFIX = "image.generation.api";
    /**
     * 是否启用图像生成工具
     */
    private Boolean enabled = Boolean.FALSE;
    /**
     * API 配置属性列表
     */
    private List<ApiConfig> apiConfigs = new ArrayList<>(10);

    /**
     * API 配置属性
     */
    @Data
    public static class ApiConfig {
        /**
         * API 提供厂商类型 - 例如: Dashscope、Xiaomi、Tencent 等, 具体支持的厂商类型请参考 {@link AiFactoryOwnerTypeEnum}
         */
        private String type = "";
        /**
         * DashScope 原生接口 API Key
         */
        private String apiKey = "";
        /**
         * DashScope 原生接口基础 URL
         */
        private String baseUrl = "https://dashscope.aliyuncs.com/api/v1/services/aigc/multimodal-generation/generation";
        /**
         * 默认使用的图像生成模型名称
         */
        private String model = "qwen-image-max";
        /**
         * 默认输出图像分辨率, 格式: 宽*高
         */
        private String size = "1328*1328";
        /**
         * 是否开启 Prompt 智能改写
         */
        private Boolean promptExtend = Boolean.TRUE;
        /**
         * 是否添加水印
         */
        private Boolean watermark = Boolean.FALSE;
        /**
         * 请求超时时间, 单位毫秒
         */
        private Integer timeout = 120000;
    }
}
