package com.ranyk.spring.ai.rag.tool.ai.tools;

import cn.hutool.core.map.MapUtil;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.function.BiFunction;

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
public class WeatherForLocationToolFunction implements BiFunction<String, ToolContext, String> {

    /**
     * 天气查询接口地址
     */
    private static final String JUHE_WEATHER_API_URL = "https://apis.juhe.cn/simpleWeather/query";
    /**
     * 天气查询接口密钥
     */
    private static final String JUHE_WEATHER_API_KEY = "59647c777cf80d6517f4b2620b7223a0";
    /**
     * JSON 工具对象
     */
    private final ObjectMapper objectMapper;

    /**
     * 构造方法
     *
     * @param objectMapper JSON 工具对象
     */
    @Autowired
    public WeatherForLocationToolFunction(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 天气工具调用工具, 用于实现依据查询天气的城市/地区名称, 获取对应的天气信息
     *
     * @param city        天气查询工具入参 - 需要查询天气的城市/地区名称
     * @param toolContext 工具上下文对象 {@link ToolContext}
     * @return 返回工具的调用结果, 返回一个字符串
     */
    @Override
    public String apply(@ToolParam(description = "需要查询天气的城市") String city, ToolContext toolContext) {
        log.info("天气查询工具被调用, 查询城市: {}", city);

        HashMap<String, Object> paramMap = MapUtil.newHashMap();
        paramMap.put("city", city);
        paramMap.put("key", JUHE_WEATHER_API_KEY);

        try {
            String response = HttpUtil.post(JUHE_WEATHER_API_URL, paramMap);
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
}