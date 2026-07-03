package com.ranyk.spring.ai.rag.knowledge.database.service.chat.message;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ranyk.spring.ai.rag.knowledge.database.ai.advisor.ReferenceExtractAdvisor;
import com.ranyk.spring.ai.rag.knowledge.database.common.exception.ServiceException;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.message.dto.ChatMessageDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.message.entity.ChatMessage;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.message.mapstruct.ChatMessageMapper;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.session.dto.ChatSessionDTO;
import com.ranyk.spring.ai.rag.knowledge.database.repository.chat.message.ChatMessageRepository;
import com.ranyk.spring.ai.rag.knowledge.database.service.chat.session.ChatSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AbstractMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * CLASS_NAME: ChatMessageService.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 聊天消息业务逻辑处理类
 * @date: 2026-06-27
 */
@Slf4j
@Service
public class ChatMessageService extends ServiceImpl<ChatMessageRepository, ChatMessage> {

    /**
     * 聊天消息数据访问层接口
     */
    private final ChatMessageRepository chatMessageRepository;
    /**
     * 聊天会话业务逻辑处理类
     */
    private final ChatSessionService chatSessionService;
    /**
     * RAG 大模型聊天客户端 - 集成 Agent 能力
     */
    private final ChatClient chatClient;
    /**
     * Jackson 对象映射器
     */
    private final ObjectMapper objectMapper;
    /**
     * 引用提取 Advisor - 用于从工具调用结果中提取 references
     */
    private final ReferenceExtractAdvisor referenceExtractAdvisor;
    /**
     * 系统提示：Agent 模式下的系统提示词 - 告知 LLM 可用工具及使用规则
     */
    public static final String SYSTEM_PROMPT = """
            
            你是「Ranyk RAG 企业知识库」的智能助手。
            
            你可以使用以下工具：
            1. 知识库检索工具 - 从知识库中语义检索与问题相关的文档片段（遇到知识库相关问题时优先使用）
            2. 文档列表查询工具 - 查询知识库中已上传的文件列表
            3. 网络搜索工具 - 当知识库中信息不足时进行网络搜索
            
            回答规则：
            - 遇到知识库相关问题时，请先使用知识库检索工具获取信息后再回答
            - 若知识库中未找到相关信息，请明确说明「知识库中未找到相关信息」，不要编造
            - 回答使用清晰的 Markdown 格式（可适当使用标题、列表）
            - 结尾简要列出依据的文档标题
            
            """;
    private final ChatMessageMapper chatMessageMapper;

    /**
     * 构造方法 - 由 Spring IOC 容器创建当前 Bean 实例对象时，自动注入相关依赖的 Bean 实例对象
     *
     * @param chatMessageRepository      聊天消息数据访问层接口 {@link ChatMessageRepository}
     * @param chatSessionService         聊天会话业务逻辑处理类 {@link ChatSessionService}
     * @param chatClient                 RAG 大模型聊天客户端 {@link ChatClient}
     * @param objectMapper               Jackson 对象映射器 {@link ObjectMapper}
     * @param chatMessageMapper          聊天消息数据转换 MapStruct 接口对象 {@link ChatMessageMapper}
     * @param referenceExtractAdvisor    引用提取 Advisor {@link ReferenceExtractAdvisor}
     */
    @Autowired
    public ChatMessageService(ChatMessageRepository chatMessageRepository,
                              ChatSessionService chatSessionService,
                              ChatClient chatClient,
                              ObjectMapper objectMapper,
                              ChatMessageMapper chatMessageMapper,
                              ReferenceExtractAdvisor referenceExtractAdvisor) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatSessionService = chatSessionService;
        this.chatClient = chatClient;
        this.objectMapper = objectMapper;
        this.chatMessageMapper = chatMessageMapper;
        this.referenceExtractAdvisor = referenceExtractAdvisor;
    }

    /**
     * 获取今日助手消息数量
     *
     * @return 今日助手消息数量
     */
    public Long countTodayAssistantMessages() {
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getRole, "ASSISTANT")
                .ge(ChatMessage::getCreateTime, today.atStartOfDay())
                .lt(ChatMessage::getCreateTime, today.plusDays(1).atStartOfDay());
        return count(wrapper);
    }

    /**
     * 统计近7日每日助手回复消息数量
     *
     * @return 每日统计数据，包含 dayKey（日期）和 cnt（数量）
     */
    public List<Map<String, Long>> countAssistantByDayLast7() {
        return chatMessageRepository.countAssistantByDayLast7();
    }

    /**
     * 根据用户问题，获取 Agent 生成的回复 - Agent 自主决策模式
     *
     * @param chatMessageDTO 聊天消息数据传输对象 {@link ChatMessageDTO}
     * @return 聊天消息数据传输对象 {@link ChatMessageDTO}
     */
    public ChatMessageDTO ask(ChatMessageDTO chatMessageDTO) {
        // 1. 会话管理：检查或创建会话
        Long sessionId = chatMessageDTO.getSessionId();
        // 不存在会话 ID，则创建一个会话
        if (Objects.isNull(sessionId) || Objects.equals(0L, sessionId)) {
            String question = chatMessageDTO.getQuestion().trim();
            question = question.length() > 30 ? question.substring(0, 30) + "…" : question;
            ChatSessionDTO chatSessionDTO = chatSessionService.saveSessionInfo(ChatSessionDTO.builder().userId(chatMessageDTO.getUserId()).title(question).build());
            sessionId = chatSessionDTO.getId();
        } else {
            ChatSessionDTO chatSessionDTO = chatSessionService.queryById(ChatSessionDTO.builder().id(sessionId).build());
            if (Objects.isNull(chatSessionDTO) || !Objects.equals(chatSessionDTO.getUserId(), chatMessageDTO.getUserId())) {
                throw new ServiceException("sessions.no.permissions", new String[]{"会话不存在或无权限！"});
            }
        }

        // 2. Agent 调用：LLM 自主决定调用工具并生成回答
        long t0 = System.nanoTime();
        ChatResponse chatResponse;
        try {
            // 将 sessionId 赋给一个 final 变量，以便在 lambda 中使用
            final Long conversationId = sessionId;
            chatResponse = chatClient.prompt()
                    .system(SYSTEM_PROMPT)
                    .user(chatMessageDTO.getQuestion())
                    .advisors(a -> a.param("conversation_id", conversationId))
                    .call()
                    .chatResponse();
        } catch (Exception e) {
            log.error("Agent 调用异常：{}", e.getMessage(), e);
            return ChatMessageDTO.builder()
                    .sessionId(sessionId)
                    .answer("系统异常，请稍后重试")
                    .references(List.of())
                    .build();
        }
        long agentMs = (System.nanoTime() - t0) / 1_000_000L;
        log.info("Agent 调用完成 sessionId={} 耗时={}ms", sessionId, agentMs);

        // 3. 提取 answer 和 references
        String answer = Optional.ofNullable(chatResponse)
                .map(ChatResponse::getResult)
                .map(Generation::getOutput)
                .map(AbstractMessage::getText)
                .orElse("");

        // 从 Advisor 的 ThreadLocal 中获取 references
        List<Map<String, Object>> refs = referenceExtractAdvisor.getExtractedReferences();
        // 清理 ThreadLocal，防止内存泄漏
        referenceExtractAdvisor.clearReferences();

        // 4. 序列化 references
        String refsJson;
        try {
            refsJson = objectMapper.writeValueAsString(refs);
        } catch (Exception e) {
            log.error("序列化 references 失败：{}", e.getMessage(), e);
            refsJson = "[]";
            refs = List.of();
        }

        // 5. 保存用户消息
        ChatMessage userChatMessage = ChatMessage.builder()
                .sessionId(sessionId)
                .role("USER")
                .content(chatMessageDTO.getQuestion())
                // 用户消息没有引用，使用空 JSON 数组
                .refs("[]")
                .createBy(chatMessageDTO.getUserId())
                .createTime(LocalDateTime.now())
                .updateBy(chatMessageDTO.getUserId())
                .updateTime(LocalDateTime.now())
                .build();
        this.saveOrUpdate(userChatMessage);
        log.info("完成用户信息保存！");

        // 6. 保存助手消息
        ChatMessage assistantChatMessage = ChatMessage.builder()
                .sessionId(sessionId)
                .role("ASSISTANT")
                .content(answer)
                .refs(refsJson)
                .createBy(chatMessageDTO.getUserId())
                .createTime(LocalDateTime.now())
                .updateBy(chatMessageDTO.getUserId())
                .updateTime(LocalDateTime.now())
                .build();
        this.saveOrUpdate(assistantChatMessage);
        log.info("完成助手信息保存！");

        // 7. 更新会话时间
        chatSessionService.touchUpdateTime(ChatSessionDTO.builder()
                .id(sessionId)
                .createBy(null)
                .createTime(null)
                .updateTime(LocalDateTime.now())
                .updateBy(chatMessageDTO.getUserId())
                .build()
        );
        log.info("完成会话时间更新！");

        // 8. 返回结果
        return ChatMessageDTO.builder().sessionId(sessionId).answer(answer).references(refs).build();
    }

    /**
     * 列出会话消息
     *
     * @param chatMessageDTO 会话消息DTO
     * @return 会话消息列表
     */
    public ChatMessageDTO listMessages(ChatMessageDTO chatMessageDTO) {
        ChatSessionDTO chatSessionDTO = chatSessionService.queryById(ChatSessionDTO.builder().id(chatMessageDTO.getSessionId()).build());
        if (Objects.isNull(chatSessionDTO) || Objects.isNull(chatSessionDTO.getId()) || !Objects.equals(chatSessionDTO.getUserId(), chatMessageDTO.getUserId())) {
            log.error("会话不存在或者无权限查询此会话的会话消息 List!");
            throw new ServiceException("sessions.no.permissions", new String[]{"会话不存在或者无权限查询此会话的会话消息 List!"});
        }

        List<ChatMessage> chatMessageList = this.list(Wrappers.<ChatMessage>lambdaQuery()
                .eq(ChatMessage::getSessionId, chatMessageDTO.getSessionId())
                .orderByAsc(ChatMessage::getId));

        return ChatMessageDTO.builder()
                .dataList(chatMessageMapper.chatMessageListToChatMessageDTOList(chatMessageList))
                .page(1)
                .size(chatMessageList.size())
                .total(Long.valueOf(String.valueOf(chatMessageList.size())))
                .build();
    }

    /**
     * 根据会话 ID 删除会话消息
     *
     * @param chatMessageDTO 会话消息DTO {@link ChatMessageDTO}
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteBySessionId(ChatMessageDTO chatMessageDTO) {
        this.remove(Wrappers.<ChatMessage>lambdaQuery().eq(ChatMessage::getSessionId, chatMessageDTO.getSessionId()));
    }
}
