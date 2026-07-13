package com.ranyk.spring.ai.rag.knowledge.database.repository.log;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ranyk.spring.ai.rag.knowledge.database.domain.log.entity.SystemLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * CLASS_NAME: SystemLogRepository.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 系统日志数据库操作接口
 * @date: 2026-06-27
 */
@Mapper
public interface SystemLogRepository extends BaseMapper<SystemLog> {
}
