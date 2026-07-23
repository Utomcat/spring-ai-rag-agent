package com.ranyk.spring.ai.rag.common.constant;

import lombok.Getter;

/**
 * CLASS_NAME: JuHeWeatherEnum.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 聚合数据天气枚举类
 * @date: 2026-07-15
 */
@Getter
@SuppressWarnings("unused")
public enum JuHeWeatherEnum {
    /**
     * 晴
     */
    SUNNY("00", "晴"),
    /**
     * 多云
     */
    CLOUDY("01", "多云"),
    /**
     * 阴
     */
    OVERCAST("02", "阴"),
    /**
     * 阵雨
     */
    SHOWER("03", "阵雨"),
    /**
     * 雷阵雨
     */
    THUNDERSHOWER("04", "雷阵雨"),
    /**
     * 雷阵雨伴有冰雹
     */
    THUNDERSHOWER_WITH_HAIL("05", "雷阵雨伴有冰雹"),
    /**
     * 雨夹雪
     */
    SLEET("06", "雨夹雪"),
    /**
     * 小雨
     */
    LIGHT_RAIN("07", "小雨"),
    /**
     * 中雨
     */
    MODERATE_RAIN("08", "中雨"),
    /**
     * 大雨
     */
    HEAVY_RAIN("09", "大雨"),
    /**
     * 暴雨
     */
    STORM("10", "暴雨"),
    /**
     * 大暴雨
     */
    SEVERE_STORM("11", "大暴雨"),
    /**
     * 特大暴雨
     */
    EXTRAORDINARY_STORM("12", "特大暴雨"),
    /**
     * 阵雪
     */
    SNOW_FLURRY("13", "阵雪"),
    /**
     * 小雪
     */
    LIGHT_SNOW("14", "小雪"),
    /**
     * 中雪
     */
    MODERATE_SNOW("15", "中雪"),
    /**
     * 大雪
     */
    HEAVY_SNOW("16", "大雪"),
    /**
     * 暴雪
     */
    SNOWSTORM("17", "暴雪"),
    /**
     * 雾
     */
    FOGGY("18", "雾"),
    /**
     * 冻雨
     */
    FREEZING_RAIN("19", "冻雨"),
    /**
     * 沙尘暴
     */
    SANDSTORM("20", "沙尘暴"),
    /**
     * 小到中雨
     */
    LIGHT_TO_MODERATE_RAIN("21", "小到中雨"),
    /**
     * 中到大雨
     */
    MODERATE_TO_HEAVY_RAIN("22", "中到大雨"),
    /**
     * 大到暴雨
     */
    HEAVY_RAIN_TO_STORM("23", "大到暴雨"),
    /**
     * 暴雨到大暴雨
     */
    STORM_TO_SEVERE_STORM("24", "暴雨到大暴雨"),
    /**
     * 大暴雨到特大暴雨
     */
    SEVERE_STORM_TO_EXTRAORDINARY_STORM("25", "大暴雨到特大暴雨"),
    /**
     * 小到中雪
     */
    LIGHT_TO_MODERATE_SNOW("26", "小到中雪"),
    /**
     * 中到大雪
     */
    MODERATE_TO_HEAVY_SNOW("27", "中到大雪"),
    /**
     * 大到暴雪
     */
    HEAVY_SNOW_TO_SNOWSTORM("28", "大到暴雪"),
    /**
     * 浮尘
     */
    DUST("29", "浮尘"),
    /**
     * 扬沙
     */
    BLOWING_SAND("30", "扬沙"),
    /**
     * 强沙尘暴
     */
    SEVERE_SANDSTORM("31", "强沙尘暴"),
    /**
     * 霾
     */
    HAZE("53", "霾"),
    /**
     * 其他
     */
    OTHER("99", "其他");

    /**
     * 天气代码
     */
    private final String wid;
    /**
     * 天气描述
     */
    private final String weather;

    /**
     * 枚举构造方法
     *
     * @param wid     天气代码
     * @param weather 天气描述
     */
    JuHeWeatherEnum(String wid, String weather) {
        this.wid = wid;
        this.weather = weather;
    }

    /**
     * 根据天气代码获取天气枚举
     *
     * @param wid 天气代码
     * @return 天气枚举, 未匹配返回 {@link JuHeWeatherEnum#OTHER}
     */
    public static JuHeWeatherEnum getWeatherEnumByWid(String wid) {
        for (JuHeWeatherEnum weatherEnum : JuHeWeatherEnum.values()) {
            if (weatherEnum.getWid().equals(wid)) {
                return weatherEnum;
            }
        }
        return OTHER;
    }

    /**
     * 根据天气描述获取天气枚举
     *
     * @param weather 天气描述
     * @return 天气枚举, 未匹配返回 {@link JuHeWeatherEnum#OTHER}
     */
    public static JuHeWeatherEnum getWeatherEnumByWeather(String weather) {
        for (JuHeWeatherEnum weatherEnum : JuHeWeatherEnum.values()) {
            if (weatherEnum.getWeather().equals(weather)) {
                return weatherEnum;
            }
        }
        return OTHER;
    }
}
