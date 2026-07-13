package com.ranyk.spring.ai.rag.knowledge.database.api.chat.session;

import com.ranyk.spring.ai.rag.base.domain.po.PageQueryPO;
import com.ranyk.spring.ai.rag.base.domain.vo.MultiResult;
import com.ranyk.spring.ai.rag.base.domain.vo.Result;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.session.dto.ChatSessionDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.session.mapstruct.ChatSessionMapper;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.session.po.ChatSessionQueryPO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.session.vo.ChatSessionVO;
import com.ranyk.spring.ai.rag.knowledge.database.service.chat.session.ChatSessionService;
import com.ranyk.spring.ai.rag.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * CLASS_NAME: ChatSessionApi.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 聊天会话 API 接口类
 * @date: 2026-06-28
 */
@RestController
@RequestMapping("/api/chat/session")
public class ChatSessionApi {

    /**
     * 聊天会话业务逻辑处理对象 {@link ChatSessionService}
     */
    private final ChatSessionService chatSessionService;
    /**
     * 聊天会话数据转换 MapStruct 接口对象 {@link ChatSessionMapper}
     */
    private final ChatSessionMapper chatSessionMapper;

    /**
     * 构造函数 - 由 Spring IOC 容器创建当前 Bean 实例对象时,自动注入相关依赖的 Bean 实例对象
     *
     * @param chatSessionService 聊天会话业务逻辑处理对象 {@link ChatSessionService}
     * @param chatSessionMapper  聊天会话数据转换 MapStruct 接口对象 {@link ChatSessionMapper}
     */
    @Autowired
    public ChatSessionApi(ChatSessionService chatSessionService, ChatSessionMapper chatSessionMapper) {
        this.chatSessionService = chatSessionService;
        this.chatSessionMapper = chatSessionMapper;
    }

    /**
     * 当前用户的会话列表
     *
     * @param pageQueryPO 分页查询参数 {@link PageQueryPO}&lt;{@link ChatSessionQueryPO}&gt;
     * @return 分页查询结果 {@link MultiResult}&lt;{@link ChatSessionVO}&gt;
     */
    @GetMapping
    public MultiResult<ChatSessionVO> sessions(PageQueryPO pageQueryPO, ChatSessionQueryPO chatSessionQueryPO) {
        var u = SecurityUtils.requireUser();
        ChatSessionDTO sessionDTO = chatSessionMapper.chatSessionQueryPOToChatSessionDTO(chatSessionQueryPO);
        sessionDTO.setUserId(u.getUserId());
        sessionDTO.setPage(pageQueryPO.page());
        sessionDTO.setSize(pageQueryPO.size());
        ChatSessionDTO chatSessionDTO = chatSessionService.listSessions(sessionDTO);
        return MultiResult.successMulti(chatSessionMapper.chatSessionDTOToChatSessionVOList(chatSessionDTO.getDataList()), chatSessionDTO.getTotal(), chatSessionDTO.getPage(), chatSessionDTO.getSize());
    }

    /**
     * 删除会话及消息。
     */
    @DeleteMapping("/{sessionId}")
    public Result<Void> deleteSession(@PathVariable Long sessionId) {
        chatSessionService.deleteSession(ChatSessionDTO.builder().userId(SecurityUtils.requireUser().getUserId()).id(sessionId).build());
        return Result.success();
    }
}
