package com.ranyk.spring.ai.rag.knowledge.database.domain.chat.message.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

/**
 * CLASS_NAME: ChatAskVO.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 聊天消息返回前端数据封装 VO 类, 其字段说明如下:
 * <ul>
 *     <li>sessionId: 会话 ID</li>
 *     <li>answer: 回答内容</li>
 *     <li>references: 参考资料列表, 其中每个元素是一个 Map, 包含参考资源的 ID 和名称, 包含 title、docId、categoryId、snippet</li>
 * </ul>
 * @date: 2026-06-28
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChatAskVO(Long sessionId, String answer, List<Map<String, Object>> references) {
}
