package com.ranyk.spring.ai.rag.knowledge.database.common.constant;

/**
 * CLASS_NAME: CapabilityTypeEnum.java
 *
 * @author ranyk
 * @version V1.0
 * @description: MCP 能力类型枚举
 * @date: 2026-07-02
 */
public enum CapabilityTypeEnum {
    /**
     * 工具类型 - 提供具体功能调用
     */
    TOOL,
    /**
     * 采样类型 - 请求 LLM 生成内容
     */
    SAMPLING,
    /**
     * 引导类型 - 向用户询问信息
     */
    ELICITATION,
    /**
     * 其他类型
     */
    OTHER;

    /**
     * 根据能力类型名称获取能力类型枚举
     *
     * @param capabilityType 能力类型名称, 忽略大小写, 可选值: tool、 sampling、 elicitation、 other
     * @return 能力类型枚举, 默认为 OTHER 详情对象参见 {@link CapabilityTypeEnum}
     */
    public static CapabilityTypeEnum getCapabilityTypeEnum(String capabilityType) {
        for (CapabilityTypeEnum capabilityTypeEnum : CapabilityTypeEnum.values()) {
            if (capabilityTypeEnum.name().equalsIgnoreCase(capabilityType)) {
                return capabilityTypeEnum;
            }
        }
        return OTHER;
    }
}
