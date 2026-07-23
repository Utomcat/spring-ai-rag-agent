package com.ranyk.spring.ai.rag.tool.registry;

import com.ranyk.spring.ai.rag.tool.facade.BaseTool;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CLASS_NAME: ToolRegistry.java
 *
 * @author ranyk
 * @version V1.0
 * @description: LLM 可调用的工具注册类
 * @date: 2026-07-23
 */
@Slf4j
@SuppressWarnings("unused")
public class ToolRegistry {
    /**
     * 用来存放已注册的工具 Map 集合
     */
    private final Map<String, BaseTool> allTools = new ConcurrentHashMap<>();

    /**
     * 注册工具方法
     *
     * @param name 工具名称
     * @param tool 工具对象
     */
    public void register(String name, BaseTool tool) {
        allTools.put(name, tool);
        log.debug("注册工具: {} -> {}", name, tool.getClass().getSimpleName());
    }

    /**
     * 根据工具名称列表获取工具列表
     *
     * @param toolNames 工具名称列表
     * @return 工具列表
     */
    public List<BaseTool> getTools(List<String> toolNames) {
        if (toolNames == null || toolNames.isEmpty()) {
            return Collections.emptyList();
        }
        List<BaseTool> tools = new ArrayList<>();
        for (String name : toolNames) {
            BaseTool tool = allTools.get(name);
            if (tool != null) {
                tools.add(tool);
            } else {
                log.warn("工具 '{}' 在注册中心中未找到", name);
            }
        }
        return tools;
    }

    /**
     * 根据工具名称获取工具对象
     *
     * @param name 工具名称
     * @return 工具对象
     */
    public BaseTool getTool(String name) {
        return allTools.get(name);
    }

    /**
     * 判断工具名称是否已注册
     *
     * @param name 工具名称
     * @return 工具名称是否已注册
     */
    public boolean contains(String name) {
        return allTools.containsKey(name);
    }
}
