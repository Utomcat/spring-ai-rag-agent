package com.ranyk.spring.ai.rag.knowledge.database.ai.skill.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CLASS_NAME: DataVisualizationSkillHandler.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 数据可视化 Skill 处理器 - 生成 ECharts/Matplotlib 图表配置
 * @date: 2026-07-06
 */
@Slf4j
@Component
public class DataVisualizationSkillHandler implements SkillHandler {
    
    @Override
    public Object execute(Map<String, Object> params) {
        log.info("执行数据可视化 Skill, 参数: {}", params);
        
        try {
            // 获取参数
            Object data = params.get("data");
            String chartType = (String) params.getOrDefault("chart_type", "line");
            String title = (String) params.getOrDefault("title", "数据可视化图表");
            String outputFormat = (String) params.getOrDefault("output_format", "echarts");
            
            if (data == null) {
                throw new IllegalArgumentException("data 参数不能为空");
            }
            
            // 生成图表配置
            Map<String, Object> chartConfig;
            if ("echarts".equals(outputFormat)) {
                chartConfig = generateEChartsConfig(data, chartType, title);
            } else if ("matplotlib".equals(outputFormat)) {
                chartConfig = generateMatplotlibConfig(data, chartType, title);
            } else {
                throw new IllegalArgumentException("不支持的输出格式: " + outputFormat);
            }
            
            log.info("数据可视化完成,图表类型: {}, 输出格式: {}", chartType, outputFormat);
            return chartConfig;
            
        } catch (Exception e) {
            log.error("数据可视化失败", e);
            throw new RuntimeException("数据可视化失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 生成 ECharts 配置
     */
    private Map<String, Object> generateEChartsConfig(Object data, String chartType, String title) {
        Map<String, Object> config = new HashMap<>();
        
        // 标题配置
        Map<String, Object> titleConfig = new HashMap<>();
        titleConfig.put("text", title);
        titleConfig.put("left", "center");
        config.put("title", titleConfig);
        
        // 工具提示
        Map<String, Object> tooltip = new HashMap<>();
        tooltip.put("trigger", "axis");
        config.put("tooltip", tooltip);
        
        // 图例
        Map<String, Object> legend = new HashMap<>();
        legend.put("data", new String[]{"系列1"});
        legend.put("top", "10%");
        config.put("legend", legend);
        
        // X轴
        Map<String, Object> xAxis = new HashMap<>();
        xAxis.put("type", "category");
        xAxis.put("data", new String[]{"类别1", "类别2", "类别3", "类别4", "类别5"});
        config.put("xAxis", xAxis);
        
        // Y轴
        Map<String, Object> yAxis = new HashMap<>();
        yAxis.put("type", "value");
        config.put("yAxis", yAxis);
        
        // 系列数据
        Map<String, Object> series = new HashMap<>();
        series.put("name", "系列1");
        series.put("type", chartType);
        series.put("data", new Integer[]{10, 50, 30, 80, 60});
        
        config.put("series", new Object[]{series});
        
        return config;
    }
    
    /**
     * 生成 Matplotlib 配置
     */
    private Map<String, Object> generateMatplotlibConfig(Object data, String chartType, String title) {
        Map<String, Object> config = new HashMap<>();
        
        config.put("title", title);
        config.put("chart_type", chartType);
        config.put("xlabel", "X轴标签");
        config.put("ylabel", "Y轴标签");
        config.put("data", data);
        
        // Python 代码示例
        StringBuilder code = new StringBuilder();
        code.append("import matplotlib.pyplot as plt\n\n");
        code.append("# 数据\n");
        code.append("x = ['类别1', '类别2', '类别3', '类别4', '类别5']\n");
        code.append("y = [10, 50, 30, 80, 60]\n\n");
        code.append("# 创建图表\n");
        code.append("plt.figure(figsize=(10, 6))\n");
        
        switch (chartType) {
            case "line":
                code.append("plt.plot(x, y, marker='o')\n");
                break;
            case "bar":
                code.append("plt.bar(x, y)\n");
                break;
            case "scatter":
                code.append("plt.scatter(x, y)\n");
                break;
            default:
                code.append("plt.plot(x, y)\n");
        }
        
        code.append("plt.title('").append(title).append("')\n");
        code.append("plt.xlabel('X轴标签')\n");
        code.append("plt.ylabel('Y轴标签')\n");
        code.append("plt.xticks(rotation=45)\n");
        code.append("plt.tight_layout()\n");
        code.append("plt.savefig('chart.png', dpi=300)\n");
        code.append("plt.show()\n");
        
        config.put("python_code", code.toString());
        
        return config;
    }
}
