package com.ranyk.spring.ai.rag.knowledge.database.domain.log.mapstruct;

import com.ranyk.spring.ai.rag.knowledge.database.domain.log.dto.SystemLogDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.log.entity.SystemLog;
import org.mapstruct.Mapper;

/**
 * CLASS_NAME: SystemLogMapper.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 系统日志数据转换 MapStruct 接口
 * @date: 2026-06-27
 */
@SuppressWarnings("unused")
@Mapper(componentModel = "spring")
public interface SystemLogMapper {

    /**
     * 将 系统日志数据传输对象 {@link SystemLogDTO} 转换对象转换为 系统日志实体对象 {@link SystemLog}
     *
     * @param systemLogDTO 系统日志数据传输对象 {@link SystemLogDTO}
     * @return 系统日志实体对象 {@link SystemLog}
     */
    SystemLog systemLogDTOToSystemLog(SystemLogDTO systemLogDTO);
}
