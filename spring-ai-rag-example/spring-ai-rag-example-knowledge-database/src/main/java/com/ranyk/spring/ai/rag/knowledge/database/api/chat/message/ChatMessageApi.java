package com.ranyk.spring.ai.rag.knowledge.database.api.chat.message;

import com.ranyk.spring.ai.rag.base.domain.vo.MultiResult;
import com.ranyk.spring.ai.rag.base.domain.vo.Result;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.message.dto.ChatMessageDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.message.mapstruct.ChatMessageMapper;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.message.po.ChatAskRequestPO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.message.vo.ChatAskVO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.message.vo.ChatMessagesVO;
import com.ranyk.spring.ai.rag.knowledge.database.service.chat.message.ChatMessageService;
import com.ranyk.spring.ai.rag.security.utils.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * CLASS_NAME: ChatMessageApi.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 聊天消息 API 接口类
 * @date: 2026-06-28
 */
@RestController
@RequestMapping("/api/chat")
public class ChatMessageApi {

    /**
     * 聊天消息业务逻辑处理对象
     */
    private final ChatMessageService chatMessageService;
    /**
     * 聊天消息数据转换对象
     */
    private final ChatMessageMapper chatMessageMapper;

    /**
     * 构造方法 - 由 Spring IOC 容器在创建当前 Bean 对象实例化时, 自动注入相关依赖 Bean 对象
     *
     * @param chatMessageService 聊天消息业务逻辑处理对象
     * @param chatMessageMapper  聊天消息数据转换对象
     */
    @Autowired
    public ChatMessageApi(ChatMessageService chatMessageService,
                          ChatMessageMapper chatMessageMapper) {
        this.chatMessageService = chatMessageService;
        this.chatMessageMapper = chatMessageMapper;
    }

    /**
     * 处理用户问题请求
     *
     * @param chatAskRequestPO 用户问题请求PO对象 {@link ChatAskRequestPO}
     * @return 聊天问题处理结果VO对象 {@link ChatAskVO}
     */
    @PostMapping("/ask")
    public Result<ChatAskVO> ask(@Valid @RequestBody ChatAskRequestPO chatAskRequestPO) {
        var u = SecurityUtils.requireUser();
        ChatMessageDTO chatMessageDTO = chatMessageMapper.chatAskRequestPOToChatMessageDTO(chatAskRequestPO);
        chatMessageDTO.setUserId(u.getUserId());
        return Result.success(chatMessageMapper.chatMessageDTOToChatAskVO(chatMessageService.ask(chatMessageDTO)));
    }

    /**
     * 查询某个会话消息列表
     *
     * @param sessionId 需要查询会话消息的 会话 ID
     * @return 聊天消息列表VO对象 {@link ChatMessagesVO}
     */
    @GetMapping("/session/{sessionId}/messages")
    public MultiResult<ChatMessagesVO> messages(@PathVariable Long sessionId) {
        ChatMessageDTO chatMessageDTO = chatMessageService.listMessages(ChatMessageDTO.builder().userId(SecurityUtils.requireUser().getUserId()).sessionId(sessionId).build());
        return MultiResult.successMulti(chatMessageMapper.chatMessageDTOListToChatMessagesVOList(chatMessageDTO.getDataList()),
                chatMessageDTO.getTotal(),
                chatMessageDTO.getPage(),
                chatMessageDTO.getSize());
    }
}
