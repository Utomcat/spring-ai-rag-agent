package com.ranyk.spring.ai.rag.llm.config.properties;

import com.ranyk.spring.ai.rag.common.constant.AiTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * CLASS_NAME: MultiModelProperties.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 多模型配置属性类
 * @date: 2026-07-21
 */
@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = MultiModelProperties.CONFIG_PREFIX)
public class MultiModelProperties {
    /**
     * 配置属性前缀
     */
    public static final String CONFIG_PREFIX = "llm.multi-model";
    /**
     * 是否启用多模型配置
     */
    private boolean enabled = Boolean.FALSE;
    /**
     * 模型配置列表
     */
    private List<ModelConfig> models = new ArrayList<>(10);

    /**
     * 模型配置类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModelConfig {
        /**
         * 模型名称 - 配置时的模型名称, 用于路由时确定调用类型
         */
        private String name = "";
        /**
         * AI 模型接入方式, 参见 {@link AiTypeEnum} 枚举类, 当前接入方式有 openai, ollama, other(此方式不支持, 仅作占位用)
         */
        private String type = "";
        /**
         * 模型基础URL
         */
        private String baseUrl = "";
        /**
         * 模型API密钥
         */
        private String apiKey = "";
        /**
         * 模型名称 - 使用的模型名称
         */
        private String model = "";
        /**
         * 模型描述
         */
        private String description = "";
        /**
         * 配置的模型系统提示词 - 用于初始化模型, 作为模型的默认行为
         */
        private String systemPrompt = """
                
                你是个有用的助手.
                
                你可以回答问题, 提供信息, 并协助各种主题.
                
                你还可以根据收到的输入生成文本.
                """;
        /**
         * 模型超时时间, 单位 秒
         */
        private Long timeout = 120L;
        /**
         * 该模型支持的工具 Bean 名称列表 - 对应 Spring 容器中的工具 Bean name
         * 为空时表示使用全局默认工具集
         */
        private List<String> tools = new ArrayList<>();
        /**
         * 该模型是否支持 MCP 工具
         */
        private Boolean mcpEnabled = Boolean.FALSE;
        /**
         * 该模型是否支持 Skills 技能
         */
        private Boolean skillEnabled = Boolean.FALSE;
        /**
         * 该模型是否支持系统提示词, 当使用 图像、音频等非文本输入时, 需要设置为 False
         */
        private Boolean systemPromptEnabled = Boolean.TRUE;
    }

}
