package com.ranyk.spring.ai.rag.tool.config.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * CLASS_NAME: WeatherApiProperties.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 天气预报 API 配置属性类
 * @date: 2026-07-16
 */
@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = WeatherApiProperties.CONFIG_PREFIX)
public class WeatherApiProperties {
    /**
     * 配置属性前缀
     */
    public static final String CONFIG_PREFIX = "weather.api";

    /**
     * 天气预报 API 配置属性类 - 厂商列表 - 可配置多个,但只需启用一个, 如果同时启用多个,只会选择每次扫描出的第一个启用厂商, 如果都为启用, 则默认使用扫描的第一个厂商
     */
    private List<FactoryOwner> factoryOwners = new ArrayList<>(10);

    /**
     * 天气预报 API 配置属性类 - 厂商
     */
    @Data
    public static class FactoryOwner {
        /**
         * 是否启用该厂商的天气预报 API
         */
        private Boolean enable = Boolean.FALSE;
        /**
         * 厂商名称
         */
        private String name = "";
        /**
         * 天气预报 API 的基础 URL
         */
        private String baseUrl = "";
        /**
         * 天气预报 API 的密钥
         */
        private String apiKey = "";
    }

}
