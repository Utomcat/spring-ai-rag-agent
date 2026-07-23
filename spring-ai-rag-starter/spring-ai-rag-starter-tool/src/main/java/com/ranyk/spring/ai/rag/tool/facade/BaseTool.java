package com.ranyk.spring.ai.rag.tool.facade;

/**
 * CLASS_NAME: BaseTool.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 通用的 AI 工具接口
 * @date: 2026-07-23
 */
public interface BaseTool {

    /**
     * 获取工具名称
     *
     * @return 工具名称 - 返回对应的实现类 Bean 名称
     */
    String getName();

    /**
     * 获取工具描述
     *
     * @return 工具描述 - 返回对应的实现类的描述信息
     */
    String getDescription();
}
