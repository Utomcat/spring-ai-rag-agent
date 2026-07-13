package com.ranyk.spring.ai.rag.knowledge.database.service.log;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ranyk.spring.ai.rag.knowledge.database.domain.log.dto.SystemLogDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.log.entity.SystemLog;
import com.ranyk.spring.ai.rag.knowledge.database.domain.log.mapstruct.SystemLogMapper;
import com.ranyk.spring.ai.rag.knowledge.database.repository.log.SystemLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CLASS_NAME: SystemLogService.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 系统日志业务逻辑处理类
 * @date: 2026-06-27
 */
@Slf4j
@Service
public class SystemLogService extends ServiceImpl<SystemLogRepository, SystemLog> {

    /**
     * SystemLogRepository 对象
     */
    private final SystemLogRepository systemLogRepository;
    /**
     * SystemLogMapper 对象
     */
    private final SystemLogMapper systemLogMapper;

    /**
     * 构造方法 - 通过 Spring IOC 容器向当前 Bean 中自动注入 SystemLogRepository 对象
     *
     * @param systemLogRepository 数据访问层接口对象
     * @param systemLogMapper     数据转换层接口对象
     */
    @Autowired
    public SystemLogService(SystemLogRepository systemLogRepository, SystemLogMapper systemLogMapper) {
        this.systemLogRepository = systemLogRepository;
        this.systemLogMapper = systemLogMapper;
    }

    /**
     * 新增系统日志
     *
     * @param systemLogDTO 系统日志数据传输对象 {@link SystemLogDTO}
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveSystemLog(SystemLogDTO systemLogDTO) {
        this.saveOrUpdate(systemLogMapper.systemLogDTOToSystemLog(systemLogDTO));
        log.info("保存系统日志：{}", systemLogDTO);
    }
}
