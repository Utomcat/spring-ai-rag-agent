package com.ranyk.spring.ai.rag.knowledge.database.ai.skill.handler;

import java.util.Map;

/**
 * INTERFACE_NAME: SkillHandler.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Skill 处理器接口
 * @date: 2026-07-06
 */
public interface SkillHandler {
    
    /**
     * 执行 Skill
     *
     * @param params 参数映射
     * @return 执行结果
     */
    Object execute(Map<String, Object> params);
}
