package com.ranyk.spring.ai.rag.tool.ai.tools;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ranyk.spring.ai.rag.common.exception.ServiceException;
import com.ranyk.spring.ai.rag.tool.config.properties.ImageGenerationProperties;
import com.ranyk.spring.ai.rag.tool.facade.BaseTool;
import com.ranyk.spring.ai.rag.tool.registry.ToolRegistry;
import com.ranyk.spring.ai.rag.tool.strategy.factory.TextToImageStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * CLASS_NAME: ImageGenerationToolFunction.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 图像生成 AI 工具类 - 通过 DashScope 原生接口调用 指定模型生成图像
 * @date: 2026-07-23
 */
@Slf4j
@SuppressWarnings("unused")
public class ImageGenerationToolFunction implements BaseTool {


    /**
     * 图像生成 API 配置属性
     */
    private final ImageGenerationProperties imageGenerationProperties;
    /**
     * 文本转图像策略工厂
     */
    private final TextToImageStrategyFactory textToImageStrategyFactory;

    /**
     * 构造方法
     *
     * @param imageGenerationProperties  图像生成 API 配置属性
     * @param textToImageStrategyFactory 文本转图像策略工厂
     */
    public ImageGenerationToolFunction(ImageGenerationProperties imageGenerationProperties,
                                       TextToImageStrategyFactory textToImageStrategyFactory) {
        this.imageGenerationProperties = imageGenerationProperties;
        this.textToImageStrategyFactory = textToImageStrategyFactory;
    }

    /**
     * 获取工具名称
     *
     * @return 工具名称 - 返回对应的实现类 Bean 名称
     */
    @Override
    public String getName() {
        return "imageGenerationToolFunction";
    }

    /**
     * 获取工具描述
     *
     * @return 工具描述 - 返回对应的实现类的描述信息
     */
    @Override
    public String getDescription() {
        return "- 图像生成工具, 按需使用, 用于根据文本描述生成图像并返回图像URL";
    }

    /**
     * 根据文本描述生成图像
     *
     * @param prompt 图像描述文本
     * @param size   可选的图像分辨率
     * @return 生成结果描述, 包含图像 URL
     */
    @Tool(description = "根据文本描述生成图像. 当用户需要生成图片、绘画、图像创作、画图等场景时使用此工具. 返回生成图像的URL链接(有效期24小时)")
    public String generateImage(
            @ToolParam(description = "图像描述文本, 描述期望生成的图像内容、风格和构图, 支持中英文") String prompt,
            @ToolParam(description = "可选的图像分辨率, 格式为 宽*高, 可选值: 1664*928(16:9), 1472*1104(4:3), 1328*1328(1:1), 1104*1472(3:4), 928*1664(9:16). 不传则使用默认值", required = false) String size
    ) {
        log.info("调用图像生成工具 - generateImage, 入参: prompt => {}, size => {}", prompt, size);
        // 遍历所有可用的 API 配置, 尝试生成图像, 当第一个生成后返回结果, 当所有生成都失败后, 抛出异常
        for (ImageGenerationProperties.ApiConfig apiConfig : imageGenerationProperties.getApiConfigs()) {
            String result = textToImageStrategyFactory.execute(apiConfig, prompt, size);
            if (StrUtil.isNotBlank(result)){
                return result;
            }
        }
        throw new ServiceException("ai.image.generate.fail", new Object[]{});
    }


}
