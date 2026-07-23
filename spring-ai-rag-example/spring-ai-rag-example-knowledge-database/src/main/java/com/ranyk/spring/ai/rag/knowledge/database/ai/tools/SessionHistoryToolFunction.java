package com.ranyk.spring.ai.rag.knowledge.database.ai.tools;

import cn.hutool.json.JSONUtil;
import com.ranyk.spring.ai.rag.common.constant.MessageRoleEnum;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.message.dto.ChatMessageDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.session.dto.ChatSessionDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.user.dto.AppUserDTO;
import com.ranyk.spring.ai.rag.knowledge.database.service.chat.message.ChatMessageService;
import com.ranyk.spring.ai.rag.knowledge.database.service.chat.session.ChatSessionService;
import com.ranyk.spring.ai.rag.knowledge.database.service.user.AppUserService;
import com.ranyk.spring.ai.rag.tool.facade.BaseTool;
import com.ranyk.spring.ai.rag.tool.registry.ToolRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * CLASS_NAME: SessionHistoryToolFunction.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 业务相关的 - 会话记忆工具类(长期记忆)
 * @date: 2026-07-16
 */
@Slf4j
@Component
@SuppressWarnings("unused")
public class SessionHistoryToolFunction implements BaseTool {

    /**
     * 会话消息信息业务逻辑对象
     */
    private final ChatMessageService chatMessageService;
    /**
     * 会话信息业务逻辑对象
     */
    private final ChatSessionService chatSessionService;
    /**
     * 用户信息业务逻辑对象
     */
    private final AppUserService appUserService;

    /**
     * 构造函数
     *
     * @param chatMessageService 会话消息信息业务逻辑对象
     * @param chatSessionService 会话信息业务逻辑对象
     * @param appUserService     用户信息业务逻辑对象
     */
    @Autowired
    public SessionHistoryToolFunction(@Lazy ChatMessageService chatMessageService,
                                      @Lazy ChatSessionService chatSessionService,
                                      @Lazy AppUserService appUserService,
                                      ToolRegistry toolRegistry) {
        this.chatMessageService = chatMessageService;
        this.chatSessionService = chatSessionService;
        this.appUserService = appUserService;
        toolRegistry.register(getName(), this);
    }

    /**
     * 获取工具名称
     *
     * @return 工具名称 - 返回对应的实现类 Bean 名称
     */
    @Override
    public String getName() {
        return "sessionHistoryToolFunction";
    }

    /**
     * 获取工具描述
     *
     * @return 工具描述 - 返回对应的实现类的描述信息
     */
    @Override
    public String getDescription() {
        return "- 会话记忆查询工具, 按需使用, 用于查询当前会话的会话历史记忆或查询当前会话的会话ID";
    }

    /**
     * 根据会话ID, 查询当前这个会话的会话历史记忆
     *
     * @param sessionId 会话ID, 必传
     * @return 会话历史记忆
     */
    @Tool(description = "根据会话ID, 查询当前这个会话的会话历史记忆")
    public String getSessionHistoryInfo(
            @ToolParam(description = "会话ID, 必传") Long sessionId
    ) {
        ChatMessageDTO chatMessageDTO = chatMessageService.querySessionHistoryDetail(ChatMessageDTO.builder().sessionId(sessionId).build());
        List<ChatMessageDTO> dataList = chatMessageDTO.getDataList();
        if (Objects.isNull(dataList) || dataList.isEmpty()) {
            return "当前会话没有历史记忆";
        }
        ChatSessionDTO chatSessionDTO = chatSessionService.queryById(ChatSessionDTO.builder().id(sessionId).build());
        AppUserDTO appUserDTO = appUserService.getUserById(AppUserDTO.builder().id(chatSessionDTO.getUserId()).build());
        StringBuilder builder = new StringBuilder("当前会话的会话历史详情为: \n");
        builder.append("用户: ").append(appUserDTO.getUsername()).append("\n");
        dataList.forEach(item -> {
            switch (MessageRoleEnum.valueOfRole(item.getRole())) {
                case ROLE_USER ->
                        builder.append("用户-").append(appUserDTO.getUsername()).append(" 提问: ").append(item.getContent()).append(" 引用: ").append(JSONUtil.toJsonStr(item.getReferences())).append("\n");
                case ROLE_ASSISTANT ->
                        builder.append("助手回复: ").append(item.getContent()).append(" 引用: ").append(JSONUtil.toJsonStr(item.getReferences())).append("\n");
                default ->
                        builder.append("未知类型: ").append(item.getContent()).append(" 引用: ").append(JSONUtil.toJsonStr(item.getReferences())).append("\n");
            }
        });
        return builder.toString();
    }

    /**
     * 获取当前会话ID
     *
     * @param toolContext 会话消息上下文对象, 必传
     * @return 当前会话ID
     */
    @Tool(description = "获取当前会话ID, 查询当前的会话消息对象的会话ID")
    public String getCurrentSessionId(ToolContext toolContext) {
        String sessionId = toolContext.getContext().get(ChatMemory.CONVERSATION_ID).toString();
        log.info("获取当前用户会话的会话ID 方法中, 传入的会话消息ID为: {}", sessionId);
        return sessionId;
    }
}
