package com.ranyk.spring.ai.rag.knowledge.database.service.chat.session;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ranyk.spring.ai.rag.common.exception.ServiceException;
import com.ranyk.spring.ai.rag.datasource.domain.dto.PageBaseDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.message.dto.ChatMessageDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.session.dto.ChatSessionDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.session.entity.ChatSession;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.session.mapstruct.ChatSessionMapper;
import com.ranyk.spring.ai.rag.knowledge.database.repository.chat.session.ChatSessionRepository;
import com.ranyk.spring.ai.rag.knowledge.database.service.chat.message.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * CLASS_NAME: ChatSessionService.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 聊天会话业务逻辑处理类
 * @date: 2026-06-27
 */
@Service
public class ChatSessionService extends ServiceImpl<ChatSessionRepository, ChatSession> {

    /**
     * 聊天会话数据转换 MapStruct 接口对象
     */
    private final ChatSessionMapper chatSessionMapper;
    /**
     * 聊天消息业务逻辑处理类
     */
    private final ChatMessageService chatMessageService;

    /**
     * 构造函数 - 由 Spring IOC 容器在创建当前对象 Bean 实例时自动注入相关依赖的 Bean 对象
     *
     * @param chatSessionMapper  聊天会话数据转换 MapStruct 接口对象 {@link ChatSessionMapper}
     * @param chatMessageService 聊天消息业务逻辑处理类 {@link ChatMessageService}
     */
    @Autowired
    public ChatSessionService(ChatSessionMapper chatSessionMapper,
                              @Lazy ChatMessageService chatMessageService) {
        this.chatSessionMapper = chatSessionMapper;
        this.chatMessageService = chatMessageService;
    }

    /**
     * 保存会话信息
     *
     * @param chatSessionDTO 会话信息数据 {@link ChatSessionDTO}
     * @return 会话信息数据 {@link ChatSessionDTO}
     */
    @Transactional(rollbackFor = Exception.class)
    public ChatSessionDTO saveSessionInfo(ChatSessionDTO chatSessionDTO) {
        ChatSession chatSession = chatSessionMapper.chatSessionDTOToChatSession(chatSessionDTO);
        this.saveOrUpdate(chatSession);
        return ChatSessionDTO.builder().id(chatSession.getId()).build();
    }

    /**
     * 根据会话 ID 查询会话信息
     *
     * @param chatSessionDTO 会话信息数据 {@link ChatSessionDTO}
     * @return 会话信息数据 {@link ChatSessionDTO}
     */
    public ChatSessionDTO queryById(ChatSessionDTO chatSessionDTO) {
        LambdaQueryWrapper<ChatSession> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatSession::getId, chatSessionDTO.getId());
        return chatSessionMapper.chatSessionToChatSessionDTO(this.getOne(queryWrapper));
    }

    /**
     * 更新聊天会话的会话时间
     *
     * @param chatSessionDTO 会话信息数据 {@link ChatSessionDTO}
     */
    @Transactional(rollbackFor = Exception.class)
    public void touchUpdateTime(ChatSessionDTO chatSessionDTO) {
        this.updateById(ChatSession.builder()
                .id(chatSessionDTO.getId())
                .createBy(null)
                .createTime(null)
                .updateBy(chatSessionDTO.getUpdateBy())
                .updateTime(chatSessionDTO.getUpdateTime())
                .build()
        );
    }

    /**
     * 列出会话信息
     *
     * @param chatSessionDTO 会话信息数据 {@link ChatSessionDTO}
     * @return 会话信息数据 {@link ChatSessionDTO}
     */
    public ChatSessionDTO listSessions(ChatSessionDTO chatSessionDTO) {

        if (Objects.isNull(chatSessionDTO.getUserId())) {
            log.error("当前未传入会话用户 ID , 直接返回空数据");
            return ChatSessionDTO.builder().build();
        }
        Page<ChatSession> page = PageBaseDTO.buildPage(chatSessionDTO);
        LambdaQueryWrapper<ChatSession> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatSession::getUserId, chatSessionDTO.getUserId());
        Page<ChatSession> chatSessionPage = this.page(page, queryWrapper);
        return ChatSessionDTO.builder()
                .dataList(chatSessionMapper.chatSessionListToChatSessionDTOList(chatSessionPage.getRecords()))
                .total(chatSessionPage.getTotal())
                .page(Long.valueOf(chatSessionPage.getPages()).intValue())
                .size(Long.valueOf(chatSessionPage.getSize()).intValue())
                .build();
    }

    /**
     * 删除会话信息
     *
     * @param chatSessionDTO 会话信息数据 {@link ChatSessionDTO}
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteSession(ChatSessionDTO chatSessionDTO) {
        ChatSessionDTO sessionDTO = this.queryById(ChatSessionDTO.builder().id(chatSessionDTO.getId()).build());
        if (Objects.isNull(sessionDTO) || Objects.isNull(sessionDTO.getId()) || !Objects.equals(sessionDTO.getUserId(), chatSessionDTO.getUserId())) {
            log.error("当前会话信息不存在，或者当前用户无权限删除该会话信息");
            throw new ServiceException("sessions.no.permissions", new String[]{"当前会话信息不存在，或者当前用户无权限删除该会话信息"});
        }
        chatMessageService.deleteBySessionId(ChatMessageDTO.builder().sessionId(chatSessionDTO.getId()).build());
        this.removeById(chatSessionDTO.getId());
    }
}
