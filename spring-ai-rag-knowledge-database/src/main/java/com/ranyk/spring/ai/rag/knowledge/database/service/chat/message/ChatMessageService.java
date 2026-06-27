package com.ranyk.spring.ai.rag.knowledge.database.service.chat.message;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.message.entity.ChatMessage;
import com.ranyk.spring.ai.rag.knowledge.database.repository.chat.message.ChatMessageRepository;
import org.springframework.stereotype.Service;

/**
 * CLASS_NAME: ChatMessageService.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 聊天消息业务逻辑处理类
 * @date: 2026-06-27
 */
@Service
public class ChatMessageService extends ServiceImpl<ChatMessageRepository, ChatMessage> {
}
