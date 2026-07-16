package com.ranyk.spring.ai.rag.tool.domain.bean;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * CLASS_NAME: WeatherApiDefinitionBean.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 天气 API 接口调用基础信息 Bean 类
 * @date: 2026-07-16
 */
@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class WeatherApiDefinitionBean implements Serializable {

    @Serial
    private static final long serialVersionUID = -998598107213271510L;

    /**
     * 厂商名称
     */
    private String name;
    /**
     * 是否启用
     */
    private Boolean enable;
    /**
     * 天气预报 API 接口访问 URL
     */
    private String baseUrl;
    /**
     * 天气预报 API 接口访问密钥
     */
    private String apiKey;
    /**
     * 其他备选的天气预报 API 接口基础信息列表
     */
    private List<WeatherApiDefinitionBean> alternativeList;
}
