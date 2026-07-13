package com.ranyk.spring.ai.rag.knowledge.database.domain.chat.message.mapstruct;

import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.message.dto.ChatMessageDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.message.entity.ChatMessage;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.message.po.ChatAskRequestPO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.message.vo.ChatAskVO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.message.vo.ChatMessagesVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * CLASS_NAME: ChatMessageMapper.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 聊天消息数据转换 MapStruct 接口类
 * @date: 2026-06-28
 */
@SuppressWarnings("unused")
@Mapper(componentModel = "spring")
public interface ChatMessageMapper {

    /**
     * 将 {@link ChatAskRequestPO} 转换为 {@link ChatMessageDTO}
     *
     * @param chatAskRequestPO 聊天问题请求PO对象 {@link ChatAskRequestPO}
     * @return 聊天消息数据传输对象 {@link ChatMessageDTO}
     */
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "answer", ignore = true),
            @Mapping(target = "references", ignore = true),
            @Mapping(target = "userId", ignore = true),
            @Mapping(target = "content", ignore = true),
            @Mapping(target = "refs", ignore = true),
            @Mapping(target = "role", ignore = true),
            @Mapping(target = "createTime", ignore = true),
            @Mapping(target = "createBy", ignore = true),
            @Mapping(target = "updateTime", ignore = true),
            @Mapping(target = "updateBy", ignore = true),
            @Mapping(target = "dataList", ignore = true),
            @Mapping(target = "page", ignore = true),
            @Mapping(target = "size", ignore = true),
            @Mapping(target = "total", ignore = true),
    })
    ChatMessageDTO chatAskRequestPOToChatMessageDTO(ChatAskRequestPO chatAskRequestPO);

    /**
     * 将 {@link ChatMessageDTO} 转换为 {@link ChatAskVO}
     *
     * @param chatMessageDTO 聊天消息数据传输对象 {@link ChatMessageDTO}
     * @return 聊天问题数据传输对象 {@link ChatAskVO}
     */
    ChatAskVO chatMessageDTOToChatAskVO(ChatMessageDTO chatMessageDTO);

    /**
     * 将 {@link ChatMessageDTO} 转换为 {@link ChatMessagesVO}
     *
     * @param chatMessageDTO 聊天消息数据传输对象 {@link ChatMessageDTO}
     * @return 聊天消息数据传输对象 {@link ChatMessagesVO}
     */
    ChatMessagesVO chatMessageDTOToChatMessagesVO(ChatMessageDTO chatMessageDTO);

    /**
     * 将 {@link List<ChatMessageDTO>} 转换为 {@link List<ChatMessagesVO>}
     *
     * @param chatMessageDTOs 聊天消息数据传输对象列表 {@link ChatMessageDTO}
     * @return 聊天消息数据传输对象列表 {@link ChatMessagesVO}
     */
    List<ChatMessagesVO> chatMessageDTOListToChatMessagesVOList(List<ChatMessageDTO> chatMessageDTOs);

    /**
     * 将 {@link ChatMessage} 转换为 {@link ChatMessageDTO}
     *
     * @param chatMessage 聊天消息数据实体对象 {@link ChatMessage}
     * @return 聊天消息数据传输对象 {@link ChatMessageDTO}
     */
    @Mappings({
            @Mapping(target = "userId", ignore = true),
            @Mapping(target = "references", ignore = true),
            @Mapping(target = "answer", ignore = true),
            @Mapping(target = "categoryIds", ignore = true),
            @Mapping(target = "question", ignore = true),
            @Mapping(target = "dataList", ignore = true),
            @Mapping(target = "page", ignore = true),
            @Mapping(target = "size", ignore = true),
            @Mapping(target = "total", ignore = true),
    })
    ChatMessageDTO chatMessageToChatMessageDTO(ChatMessage chatMessage);

    /**
     * 将 {@link List<ChatMessage>} 转换为 {@link List<ChatMessageDTO>}
     *
     * @param chatMessageList 聊天消息数据实体对象列表 {@link ChatMessage}
     * @return 聊天消息数据传输对象列表 {@link ChatMessageDTO}
     */
    List<ChatMessageDTO> chatMessageListToChatMessageDTOList(List<ChatMessage> chatMessageList);

}
