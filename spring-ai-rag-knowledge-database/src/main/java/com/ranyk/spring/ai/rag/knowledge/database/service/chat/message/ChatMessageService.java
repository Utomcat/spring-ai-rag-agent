package com.ranyk.spring.ai.rag.knowledge.database.service.chat.message;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ranyk.spring.ai.rag.knowledge.database.ai.advisor.ReferenceExtractAdvisor;
import com.ranyk.spring.ai.rag.knowledge.database.common.constant.MessageRoleEnum;
import com.ranyk.spring.ai.rag.knowledge.database.common.constant.SymbolEnum;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

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
     * 虚拟线程执行器 - 用于异步执行数据库操作
     */
    private final ExecutorService virtualThreadExecutor;
    /**
     * Spring 编程式事务管理器 - 用于手动控制事务边界
     */
    private final PlatformTransactionManager transactionManager;
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
            - 回答使用清晰的文本格式（可适当使用标题、列表）
            - 结尾简要列出依据的文档标题
            
            """;
    /**
     * 会话标题最大长度
     */
    private static final int SESSION_TITLE_MAX_LENGTH = 30;
    /**
     * Agent 调用异常时的默认回复消息
     */
    private static final String AGENT_ERROR_MESSAGE = "系统异常，请稍后重试";
    /**
     * LLM 未生成有效回答时的默认消息
     */
    private static final String DEFAULT_ANSWER_MESSAGE = "抱歉，我暂时无法回答这个问题，请稍后再试。";
    /**
     * 聊天消息数据转换 MapStruct 接口对象
     */
    private final ChatMessageMapper chatMessageMapper;

    /**
     * 构造方法 - 由 Spring IOC 容器创建当前 Bean 实例对象时，自动注入相关依赖的 Bean 实例对象
     *
     * @param chatMessageRepository   聊天消息数据访问层接口 {@link ChatMessageRepository}
     * @param chatSessionService      聊天会话业务逻辑处理类 {@link ChatSessionService}
     * @param chatClient              RAG 大模型聊天客户端 {@link ChatClient}
     * @param objectMapper            Jackson 对象映射器 {@link ObjectMapper}
     * @param chatMessageMapper       聊天消息数据转换 MapStruct 接口对象 {@link ChatMessageMapper}
     * @param referenceExtractAdvisor 引用提取 Advisor {@link ReferenceExtractAdvisor}
     * @param virtualThreadExecutor   虚拟线程执行器 {@link ExecutorService}
     * @param transactionManager      平台事务管理器 {@link PlatformTransactionManager}
     */
    @Autowired
    public ChatMessageService(ChatMessageRepository chatMessageRepository,
                              ChatSessionService chatSessionService,
                              ChatClient chatClient,
                              ObjectMapper objectMapper,
                              ChatMessageMapper chatMessageMapper,
                              ReferenceExtractAdvisor referenceExtractAdvisor,
                              ExecutorService virtualThreadExecutor,
                              PlatformTransactionManager transactionManager) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatSessionService = chatSessionService;
        this.chatClient = chatClient;
        this.objectMapper = objectMapper;
        this.chatMessageMapper = chatMessageMapper;
        this.referenceExtractAdvisor = referenceExtractAdvisor;
        this.virtualThreadExecutor = virtualThreadExecutor;
        this.transactionManager = transactionManager;
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
    @Transactional(rollbackFor = Exception.class)
    public ChatMessageDTO ask(ChatMessageDTO chatMessageDTO) {
        // 0. 参数验证
        if (Objects.isNull(chatMessageDTO)) {
            throw new ServiceException("parameter.abnormalities.error", new String[]{"聊天消息对象不能为空"});
        }
        if (Objects.isNull(chatMessageDTO.getUserId())) {
            throw new ServiceException("parameter.abnormalities.error", new String[]{"用户 ID 不能为空"});
        }
        String question = chatMessageDTO.getQuestion();
        if (Objects.isNull(question) || question.trim().isEmpty()) {
            throw new ServiceException("parameter.abnormalities.error", new String[]{"问题内容不能为空"});
        }
        // 1. 会话管理：检查或创建会话
        Long sessionId = createOrValidateSession(chatMessageDTO);
        Long userId = chatMessageDTO.getUserId();

        // 2. Agent 调用:LLM 自主决定调用工具并生成回答
        long startTimeNanos = System.nanoTime();
        ChatResponse chatResponse;
        List<Map<String, Object>> references;
        try {
            final Long conversationId = sessionId;
            chatResponse = chatClient.prompt()
                    .system(SYSTEM_PROMPT)
                    .user(question)
                    .advisors(a -> a.param("conversation_id", conversationId))
                    .call()
                    .chatResponse();
            // 成功调用后提取 references
            references = referenceExtractAdvisor.getExtractedReferences();
        } catch (Exception e) {
            log.error("Agent 调用异常 sessionId => {}, userId => {}: {}", sessionId, userId, e.getMessage(), e);
            return ChatMessageDTO.builder()
                    .sessionId(sessionId)
                    .answer(AGENT_ERROR_MESSAGE)
                    .references(List.of())
                    .build();
        } finally {
            // 确保 ThreadLocal 被清理，防止内存泄漏
            referenceExtractAdvisor.clearReferences();
        }
                
        long agentDurationMs = (System.nanoTime() - startTimeNanos) / 1_000_000L;
        log.debug("Agent 调用完成 sessionId => {}, userId => {}, 耗时 => {}ms", sessionId, userId, agentDurationMs);
        // 3. 提取 answer
        String answer = extractAnswerFromResponse(chatResponse);
        // 处理空 answer 情况，提供默认消息
        if (answer.trim().isEmpty()) {
            answer = DEFAULT_ANSWER_MESSAGE;
            log.warn("LLM 未生成有效回答 sessionId => {}, userId => {}", sessionId, userId);
        }

        // 4. 序列化 references
        String referencesJson = serializeReferences(references);
        if (referencesJson.equals(SymbolEnum.EMPTY_JSON_ARRAY.getCode())) {
            references = List.of();
        }

        // 5. 异步保存聊天消息和更新会话时间
        LocalDateTime now = LocalDateTime.now();
        asyncSaveChatMessages(sessionId, userId, question, answer, referencesJson, now);

        // 6. 立即返回结果（不等待异步操作完成）
        return ChatMessageDTO.builder()
                .sessionId(sessionId)
                .answer(answer)
                .references(references)
                .build();
    }

    /**
     * 异步保存聊天消息和更新会话时间
     *
     * @param sessionId      会话 ID
     * @param userId         用户 ID
     * @param question       用户问题
     * @param answer         助手回答
     * @param referencesJson 引用 JSON 字符串
     * @param now            当前时间
     */
    private void asyncSaveChatMessages(Long sessionId,
                                       Long userId,
                                       String question,
                                       String answer,
                                       String referencesJson,
                                       LocalDateTime now) {
        virtualThreadExecutor.execute(() -> {
            TransactionStatus status = null;
            try {
                // 使用编程式事务确保两个数据库操作的原子性
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
                def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
                def.setTimeout(30);
                // 开始事务
                status = transactionManager.getTransaction(def);

                // 保存聊天消息
                saveChatMessages(sessionId, userId, question, answer, referencesJson, now);
                log.debug("异步保存聊天消息完成 sessionId => {}, userId => {}", sessionId, userId);

                // 更新会话时间
                updateSessionUpdateTime(sessionId, userId, now);
                log.debug("异步更新会话时间完成 sessionId => {}, userId => {}", sessionId, userId);
                // 提交事务
                transactionManager.commit(status);
                log.info("异步保存聊天消息成功 sessionId => {}, userId => {}, questionLength => {}, answerLength => {}", sessionId, userId, StrUtil.isNotBlank(question) ? question.length() : 0, StrUtil.isNotBlank(answer) ? answer.length() : 0);
            } catch (Exception e) {
                // 回滚事务
                if (Objects.nonNull(status) && !status.isCompleted()) {
                    try {
                        transactionManager.rollback(status);
                        log.warn("异步保存聊天消息失败，已回滚事务, 当前的会话和会话消息信息为 sessionId => {}, userId => {}, question => {}, answer => {} ", sessionId, userId, question, answer);
                        log.warn("异步保存聊天消息失败，已回滚事务 errorMessage => {}", e.getMessage());
                        log.warn("异步保存聊天消息失败，已回滚事务, 当前的异常栈为: \n", e);
                    } catch (Exception rollbackEx) {
                        log.warn("异步保存聊天消息失败，回滚事务也异常, 当前的会话和会话消息信息为 sessionId => {}, userId => {}, question => {}, answer => {} ", sessionId, userId, question, answer);
                        log.warn("异步保存聊天消息失败，回滚事务也异常 errorMessage => {}", e.getMessage());
                        log.warn("异步保存聊天消息失败，回滚事务也异常, 当前的异常栈为: \n", e);
                    }
                } else {
                    log.error("异步保存聊天消息失败，事务状态已完成无法回滚 sessionId => {}, userId => {}, question => {}, answer => {} ", sessionId, userId, question, answer);
                    log.error("异步保存聊天消息失败，事务状态已完成无法回滚 errorMessage => {}", e.getMessage());
                    log.error("异步保存聊天消息失败，事务状态已完成无法回滚 当前的异常栈为: \n", e);
                }
            }
        });
    }

    /**
     * 创建或验证会话
     *
     * @param chatMessageDTO 聊天消息数据传输对象
     * @return 会话 ID
     */
    private Long createOrValidateSession(ChatMessageDTO chatMessageDTO) {
        Long sessionId = chatMessageDTO.getSessionId();
        Long userId = chatMessageDTO.getUserId();

        // 不存在会话 ID，则创建一个会话
        if (Objects.isNull(sessionId) || Objects.equals(0L, sessionId)) {
            String question = chatMessageDTO.getQuestion().trim();
            String title = question.length() > SESSION_TITLE_MAX_LENGTH
                    ? question.substring(0, SESSION_TITLE_MAX_LENGTH) + SymbolEnum.ELLIPSIS.getCode()
                    : question;
            ChatSessionDTO chatSessionDTO = chatSessionService.saveSessionInfo(
                    ChatSessionDTO.builder().userId(userId).title(title).build()
            );
            return chatSessionDTO.getId();
        } else {
            ChatSessionDTO chatSessionDTO = chatSessionService.queryById(
                    ChatSessionDTO.builder().id(sessionId).build()
            );
            if (Objects.isNull(chatSessionDTO) || !Objects.equals(chatSessionDTO.getUserId(), userId)) {
                throw new ServiceException("sessions.no.permissions", new String[]{"会话不存在或无权限！"});
            }
            return sessionId;
        }
    }

    /**
     * 从 ChatResponse 中提取 answer
     *
     * @param chatResponse Chat 响应对象
     * @return 回答文本
     */
    private String extractAnswerFromResponse(ChatResponse chatResponse) {
        return Optional.ofNullable(chatResponse)
                .map(ChatResponse::getResult)
                .map(Generation::getOutput)
                .map(AbstractMessage::getText)
                .orElse("");
    }

    /**
     * 序列化 references 为 JSON 字符串
     *
     * @param references 引用列表
     * @return JSON 字符串
     */
    private String serializeReferences(List<Map<String, Object>> references) {
        try {
            return objectMapper.writeValueAsString(references);
        } catch (Exception e) {
            log.error("序列化 references 失败: {}", e.getMessage(), e);
            return SymbolEnum.EMPTY_JSON_ARRAY.getCode();
        }
    }

    /**
     * 保存用户和助手的聊天消息
     *
     * @param sessionId      会话 ID
     * @param userId         用户 ID
     * @param question       用户问题
     * @param answer         助手回答
     * @param referencesJson 引用 JSON 字符串
     * @param now            当前时间
     */
    private void saveChatMessages(Long sessionId,
                                  Long userId,
                                  String question,
                                  String answer,
                                  String referencesJson,
                                  LocalDateTime now) {
        // 保存用户消息
        ChatMessage userChatMessage = ChatMessage.builder()
                .sessionId(sessionId)
                .role(MessageRoleEnum.ROLE_USER.getRole())
                .content(question)
                .refs(SymbolEnum.EMPTY_JSON_ARRAY.getCode())
                .createBy(userId)
                .createTime(now)
                .updateBy(userId)
                .updateTime(now)
                .build();
        this.saveOrUpdate(userChatMessage);
        log.info("完成用户信息保存 sessionId={}, userId={}", sessionId, userId);

        // 保存助手消息
        ChatMessage assistantChatMessage = ChatMessage.builder()
                .sessionId(sessionId)
                .role(MessageRoleEnum.ROLE_ASSISTANT.getRole())
                .content(answer)
                .refs(referencesJson)
                .createBy(userId)
                .createTime(now)
                .updateBy(userId)
                .updateTime(now)
                .build();
        this.saveOrUpdate(assistantChatMessage);
        log.info("完成助手信息保存 sessionId={}, userId={}", sessionId, userId);
    }

    /**
     * 更新会话更新时间
     *
     * @param sessionId 会话 ID
     * @param userId    用户 ID
     * @param now       当前时间
     */
    private void updateSessionUpdateTime(Long sessionId, Long userId, LocalDateTime now) {
        chatSessionService.touchUpdateTime(ChatSessionDTO.builder()
                .id(sessionId)
                .updateTime(now)
                .updateBy(userId)
                .build()
        );
        log.info("完成会话时间更新 sessionId={}, userId={}", sessionId, userId);
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
