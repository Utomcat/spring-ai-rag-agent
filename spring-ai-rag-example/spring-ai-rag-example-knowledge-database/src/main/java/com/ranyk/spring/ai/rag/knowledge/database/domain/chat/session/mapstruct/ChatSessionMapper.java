package com.ranyk.spring.ai.rag.knowledge.database.domain.chat.session.mapstruct;

import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.session.dto.ChatSessionDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.session.entity.ChatSession;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.session.po.ChatSessionQueryPO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.session.vo.ChatSessionVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * CLASS_NAME: ChatSessionMapper.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 聊天会话数据转换 MapStruct 接口对象
 * @date: 2026-06-28
 */
@Mapper(componentModel = "spring")
public interface ChatSessionMapper {

    /**
     * 将 {@link ChatSessionDTO} 对象转换为 {@link ChatSession} 对象
     *
     * @param chatSessionDTO 聊天会话数据传输对象 {@link ChatSessionDTO}
     * @return 聊天会话数据实体对象 {@link ChatSession}
     */
    ChatSession chatSessionDTOToChatSession(ChatSessionDTO chatSessionDTO);

    /**
     * 将 {@link ChatSession} 对象转换为 {@link ChatSessionDTO} 对象
     *
     * @param chatSession 聊天会话数据实体对象 {@link ChatSession}
     * @return 聊天会话数据传输对象 {@link ChatSessionDTO}
     */
    @Mappings({
            @Mapping(target = "dataList", ignore = true),
            @Mapping(target = "page", ignore = true),
            @Mapping(target = "size", ignore = true),
            @Mapping(target = "total", ignore = true)
    })
    ChatSessionDTO chatSessionToChatSessionDTO(ChatSession chatSession);

    /**
     * 将 {@link ChatSession} 列表对象转换为 {@link ChatSessionDTO} 列表对象
     *
     * @param chatSessionList 聊天会话数据实体对象列表 {@link ChatSession}
     * @return 聊天会话数据传输对象列表 {@link ChatSessionDTO}
     */
    List<ChatSessionDTO> chatSessionListToChatSessionDTOList(List<ChatSession> chatSessionList);

    /**
     * 将 {@link ChatSessionQueryPO} 对象转换为 {@link ChatSessionDTO} 对象
     *
     * @param chatSessionQueryPO 聊天会话查询数据对象 {@link ChatSessionQueryPO}
     * @return 聊天会话数据传输对象 {@link ChatSessionDTO}
     */
    @Mappings({
            @Mapping(target = "createBy", ignore = true),
            @Mapping(target = "createTime", ignore = true),
            @Mapping(target = "updateBy", ignore = true),
            @Mapping(target = "updateTime", ignore = true),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "dataList", ignore = true),
            @Mapping(target = "page", ignore = true),
            @Mapping(target = "size", ignore = true),
            @Mapping(target = "total", ignore = true)
    })
    ChatSessionDTO chatSessionQueryPOToChatSessionDTO(ChatSessionQueryPO chatSessionQueryPO);

    /**
     * 将 {@link ChatSessionDTO} 对象转换为 {@link ChatSessionVO} 对象
     *
     * @param chatSessionDTO 聊天会话数据传输对象 {@link ChatSessionDTO}
     * @return 聊天会话数据视图对象 {@link ChatSessionVO}
     */
    ChatSessionVO chatSessionDTOToChatSessionVO(ChatSessionDTO chatSessionDTO);

    /**
     * 将 {@link ChatSessionDTO} 列表对象转换为 {@link ChatSessionVO} 列表对象
     *
     * @param chatSessionDTOList 聊天会话数据传输对象列表 {@link ChatSessionDTO}
     * @return 聊天会话数据视图对象列表 {@link ChatSessionVO}
     */
    List<ChatSessionVO> chatSessionDTOToChatSessionVOList(List<ChatSessionDTO> chatSessionDTOList);
}
