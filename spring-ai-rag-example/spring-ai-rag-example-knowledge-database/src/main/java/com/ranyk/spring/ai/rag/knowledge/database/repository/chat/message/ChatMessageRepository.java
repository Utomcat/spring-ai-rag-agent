package com.ranyk.spring.ai.rag.knowledge.database.repository.chat.message;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.message.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * CLASS_NAME: ChateMessageRepository.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 聊天消息数据库操作接口
 * @date: 2026-06-27
 */
@Mapper
public interface ChatMessageRepository extends BaseMapper<ChatMessage> {

    /**
     * 统计近7日每日助手回复消息数量
     *
     * @return 每日统计数据，包含 dayKey（日期）和 cnt（数量）
     */
    List<Map<String, Long>> countAssistantByDayLast7();
}
