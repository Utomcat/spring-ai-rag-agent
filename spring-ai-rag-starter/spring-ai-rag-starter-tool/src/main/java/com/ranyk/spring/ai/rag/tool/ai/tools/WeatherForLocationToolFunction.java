package com.ranyk.spring.ai.rag.tool.ai.tools;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.ranyk.spring.ai.rag.common.constant.FactoryOwnerTypeEnum;
import com.ranyk.spring.ai.rag.tool.domain.bean.WeatherApiDefinitionBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;

/**
 * CLASS_NAME: WeatherForLocationToolFunction.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 天气查询 AI 工具类 - 供 Agent 自主调用进行天气查询 - Spring AI Alibaba 方式
 * @date: 2026-07-15
 */
@Slf4j
@Component
@SuppressWarnings("unused")
public class WeatherForLocationToolFunction {

    /**
     * JSON 工具对象
     */
    private final ObjectMapper objectMapper;
    /**
     * 天气查询接口定义对象
     */
    private final WeatherApiDefinitionBean weatherApiDefinitionBean;

    /**
     * 构造方法
     *
     * @param objectMapper             JSON 工具对象
     * @param weatherApiDefinitionBean 天气查询接口定义对象
     */
    @Autowired
    public WeatherForLocationToolFunction(ObjectMapper objectMapper,
                                          WeatherApiDefinitionBean weatherApiDefinitionBean) {
        this.objectMapper = objectMapper;
        this.weatherApiDefinitionBean = weatherApiDefinitionBean;
    }

    /**
     * 天气工具调用工具, 用于实现依据查询天气的城市/地区名称, 获取对应的天气信息 - 此工具目前使用的是 聚合数据 的天气查询接口
     *
     * @param city 天气查询工具入参 - 需要查询天气的城市/地区名称
     * @return 返回工具的调用结果, 返回一个字符串
     */
    @Tool(description = "传入需要查询天气信息的城市名称, 获取对应城市的天气信息, 当需要查询天气信息时使用此工具")
    public String getWeatherForLocation(@ToolParam(description = "需要查询天气的城市") String city) {
        log.info("调用天气查询工具 - getWeatherForLocation, 入参: city => {}", city);
        return switch (FactoryOwnerTypeEnum.getFactoryOwnerTypeEnumByName(getWeatherApiFactoryOwnerName())) {
            case FACTORY_OWNER_WEATHER_JU_HE -> useJuHeDataQueryWeather(city);
            case FACTORY_OWNER_CHINA_METEOROLOGICAL_NETWORK -> useChinaMeteorologicalNetwork(city);
            default -> "当前不支持的天气查询接口";
        };
    }

    /**
     * 获取天气查询接口的厂商名称
     *
     * @return 天气查询接口的厂商名称
     */
    private String getWeatherApiFactoryOwnerName() {
        // 获取当前的 天气 API 定义 Bean 对象是否 存在可用的 API 厂商
        if (StrUtil.isNotBlank(weatherApiDefinitionBean.getName())) {
            // 存在则直接返回
            return weatherApiDefinitionBean.getName();
        }
        // 不存在则从备用列表中获取第一个可用的 API 厂商
        WeatherApiDefinitionBean alternativeApi = weatherApiDefinitionBean.getAlternativeList().stream().filter(WeatherApiDefinitionBean::getEnable).findFirst().orElse(WeatherApiDefinitionBean.builder().build());
        // 如果备用列表中不存在可用的 API 厂商则返回空字符串
        if (StrUtil.isNotBlank(alternativeApi.getName())) {
            return alternativeApi.getName();
        }
        // 存在可用厂商则将其赋值给当前的 天气 API 定义 Bean 对象, 以便后续使用
        weatherApiDefinitionBean.setName(alternativeApi.getName());
        weatherApiDefinitionBean.setEnable(alternativeApi.getEnable());
        weatherApiDefinitionBean.setApiKey(alternativeApi.getApiKey());
        weatherApiDefinitionBean.setBaseUrl(alternativeApi.getBaseUrl());
        // 返回当前使用的 API 厂商名称
        return alternativeApi.getName();
    }

    /**
     * 使用聚合数据的天气查询接口, 获取天气信息
     *
     * @param city 需要查询天气的城市名称
     * @return 天气信息
     */
    private String useJuHeDataQueryWeather(String city) {
        HashMap<String, Object> paramMap = MapUtil.newHashMap();
        paramMap.put("city", city);
        paramMap.put("key", weatherApiDefinitionBean.getApiKey());

        try {
            String response = HttpUtil.post(weatherApiDefinitionBean.getBaseUrl(), paramMap);
            log.debug("聚合数据天气接口返回: {}", response);

            JsonNode rootNode = objectMapper.readTree(response);
            int errorCode = rootNode.path("error_code").asInt(-1);

            if (errorCode != 0) {
                String reason = rootNode.path("reason").asString("未知错误");
                log.error("天气查询失败, error_code: {}, reason: {}", errorCode, reason);
                return "天气查询失败: " + reason;
            }

            JsonNode resultNode = rootNode.path("result");
            String resultCity = resultNode.path("city").asString("");
            JsonNode realtimeNode = resultNode.path("realtime");

            String info = realtimeNode.path("info").asString("");
            String temperature = realtimeNode.path("temperature").asString("");
            String humidity = realtimeNode.path("humidity").asString("");
            String windDirect = realtimeNode.path("direct").asString("");
            String windPower = realtimeNode.path("power").asString("");
            String aqi = realtimeNode.path("aqi").asString("");

            StringBuilder sb = new StringBuilder();
            sb.append("城市: ").append(resultCity).append("\n");
            sb.append("天气: ").append(info).append("\n");
            sb.append("温度: ").append(temperature).append("°C\n");
            sb.append("湿度: ").append(humidity).append("%\n");
            sb.append("风向: ").append(windDirect).append("\n");
            sb.append("风力: ").append(windPower).append("\n");
            sb.append("空气质量指数(AQI): ").append(aqi).append("\n");

            JsonNode futureNode = resultNode.path("future");
            if (futureNode.isArray()) {
                sb.append("\n未来天气预报:\n");
                for (JsonNode dayNode : futureNode) {
                    sb.append("  日期: ").append(dayNode.path("date").asString(""))
                            .append(", 天气: ").append(dayNode.path("weather").asString(""))
                            .append(", 温度: ").append(dayNode.path("temperature").asString(""))
                            .append(", 风向: ").append(dayNode.path("direct").asString(""))
                            .append("\n");
                }
            }
            String result = sb.toString();
            log.info("天气查询成功: {}", resultCity);
            return result;
        } catch (Exception e) {
            log.error("天气查询异常", e);
            return "天气查询异常: " + e.getMessage();
        }
    }

    /**
     * 使用中国气象网络的天气查询接口, 获取天气信息
     *
     * @param city 需要查询天气的城市名称
     * @return 天气信息
     */
    private String useChinaMeteorologicalNetwork(String city) {
        // TODO 此处需要实现具体逻辑, 当前暂未实现
        return "功能开发中,敬请期待...";
    }
}