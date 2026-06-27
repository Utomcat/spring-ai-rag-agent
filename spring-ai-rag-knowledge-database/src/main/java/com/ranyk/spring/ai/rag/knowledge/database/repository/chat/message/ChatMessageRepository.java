package com.ranyk.spring.ai.rag.knowledge.database.repository.chat.message;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.message.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

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
}
