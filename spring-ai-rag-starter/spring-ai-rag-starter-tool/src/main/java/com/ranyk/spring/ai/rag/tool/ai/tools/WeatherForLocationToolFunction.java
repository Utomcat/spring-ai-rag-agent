package com.ranyk.spring.ai.rag.tool.ai.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

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

    private final String JUHE_WEATHER_API_KEY = "59647c777cf80d6517f4b2620b7223a0";

    /**
     * 天气工具调用工具, 用于实现依据查询天气的城市/地区名称, 获取对应的天气信息
     *
     * @param city        天气查询工具入参 - 需要查询天气的城市/地区名称
     * @param toolContext 工具上下文对象 {@link ToolContext}
     * @return 返回工具的调用结果, 返回一个字符串
     */
    @Override
    public String apply(@ToolParam(description = "需要查询天气的城市") String city, ToolContext toolContext) {


        return "";
    }
}