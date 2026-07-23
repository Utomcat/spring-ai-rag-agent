package com.ranyk.spring.ai.rag.tool.strategy.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ranyk.spring.ai.rag.common.constant.AiFactoryOwnerTypeEnum;
import com.ranyk.spring.ai.rag.common.exception.ServiceException;
import com.ranyk.spring.ai.rag.tool.config.properties.ImageGenerationProperties;
import com.ranyk.spring.ai.rag.tool.strategy.TextToImageStrategy;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * CLASS_NAME: DashScopeTextToImageStrategyImpl.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 阿里云百炼策略实现类
 * @date: 2026-07-23
 */
@Slf4j
public class DashScopeTextToImageStrategyImpl implements TextToImageStrategy {

    /**
     * Jackson 对象映射器
     */
    private final ObjectMapper objectMapper;

    /**
     * 构造函数
     *
     * @param objectMapper Jackson 对象映射器
     */
    public DashScopeTextToImageStrategyImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    /**
     * 获取策略类型
     *
     * @return 策略类型, {@link AiFactoryOwnerTypeEnum}
     */
    @Override
    public AiFactoryOwnerTypeEnum getType() {
        return AiFactoryOwnerTypeEnum.DASH_SCOPE;
    }

    /**
     * 执行策略
     *
     * @return 执行结果, 文字转图片后生成的图片 URL 下载路径
     */
    @Override
    public String execute(ImageGenerationProperties.ApiConfig apiConfig, String prompt, String size) {

        try {
            String requestBody = buildRequestBody(apiConfig, prompt, size);
            log.debug("厂商 {} 使用模型 {} 执行文字转图像, 生成的请求体: {}", apiConfig.getType(), apiConfig.getModel(), requestBody);
            try (HttpResponse response = HttpRequest.post(apiConfig.getBaseUrl())
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiConfig.getApiKey())
                    .body(requestBody)
                    .timeout(apiConfig.getTimeout())
                    .execute()) {

                String responseBody = response.body();
                log.debug("图像生成响应: {}", responseBody);

                if (response.getStatus() != 200) {
                    log.error("图像生成请求失败, HTTP状态码: {}, 响应: {}", response.getStatus(), responseBody);
                    return "图像生成失败: HTTP " + response.getStatus() + ", " + extractErrorMessage(responseBody);
                }

                return parseSuccessResponse(responseBody);
            }

        } catch (Exception e) {
            log.error("图像生成工具执行异常: {}", e.getMessage(), e);
            return "图像生成异常: " + e.getMessage();
        }
    }

    /**
     * 构建 DashScope 原生接口请求体
     *
     * @param prompt 图像描述文本
     * @param size   图像分辨率
     * @return JSON 格式请求体字符串
     */
    private String buildRequestBody(ImageGenerationProperties.ApiConfig apiConfig, String prompt, String size) {
        Map<String, Object> textContent = new LinkedHashMap<>();
        textContent.put("text", prompt);

        Map<String, Object> message = new LinkedHashMap<>();
        message.put("role", "user");
        message.put("content", List.of(textContent));

        Map<String, Object> input = new LinkedHashMap<>();
        input.put("messages", List.of(message));

        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("size", (size != null && !size.isBlank()) ? size : apiConfig.getSize());
        parameters.put("prompt_extend", apiConfig.getPromptExtend());
        parameters.put("watermark", apiConfig.getWatermark());

        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("model", apiConfig.getModel());
        requestBody.put("input", input);
        requestBody.put("parameters", parameters);

        try {
            return objectMapper.writeValueAsString(requestBody);
        } catch (Exception e) {
            throw new ServiceException("构建请求体失败", e);
        }
    }

    /**
     * 提取错误信息
     *
     * @param responseBody 响应体
     * @return 错误信息
     */
    private String extractErrorMessage(String responseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            if (rootNode.has("message")) {
                return rootNode.get("message").asString();
            }
        } catch (Exception ignored) {
        }
        return responseBody;
    }


    /**
     * 解析成功响应, 提取图像 URL
     *
     * @param responseBody 响应体
     * @return 包含图像 URL 的结果描述
     */
    private String parseSuccessResponse(String responseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode codeNode = rootNode.get("code");
            if (Objects.nonNull(codeNode) && !codeNode.asString().isEmpty()) {
                String errorMsg = rootNode.has("message") ? rootNode.get("message").asString() : "未知错误";
                log.error("图像生成API返回错误: code={}, message={}", codeNode.asString(), errorMsg);
                return "图像生成失败: " + errorMsg;
            }
            JsonNode choicesNode = rootNode.path("output").path("choices");
            if (choicesNode.isArray() && !choicesNode.isEmpty()) {
                JsonNode contentNode = choicesNode.get(0).path("message").path("content");
                if (contentNode.isArray() && !contentNode.isEmpty()) {
                    String imageUrl = contentNode.get(0).path("image").asString("");
                    if (!imageUrl.isEmpty()) {
                        log.info("图像生成成功, URL: {}", imageUrl);
                        return "图像生成成功！图像URL(有效期24小时): " + imageUrl;
                    }
                }
            }
            log.warn("图像生成响应中未找到图像URL, 响应: {}", responseBody);
            return "图像生成完成, 但未能解析到图像URL, 原始响应: " + responseBody;
        } catch (Exception e) {
            log.error("解析图像生成响应异常: {}", e.getMessage(), e);
            return "图像生成响应解析异常: " + e.getMessage();
        }
    }
}
