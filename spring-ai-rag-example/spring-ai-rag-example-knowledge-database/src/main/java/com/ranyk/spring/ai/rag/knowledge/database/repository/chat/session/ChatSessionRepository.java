package com.ranyk.spring.ai.rag.knowledge.database.repository.chat.session;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.session.entity.ChatSession;
import org.apache.ibatis.annotations.Mapper;

/**
 * CLASS_NAME: ChatSessionRepository.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 聊天会话数据库操作接口
 * @date: 2026-06-27
 */
@Mapper
public interface ChatSessionRepository extends BaseMapper<ChatSession> {
}
