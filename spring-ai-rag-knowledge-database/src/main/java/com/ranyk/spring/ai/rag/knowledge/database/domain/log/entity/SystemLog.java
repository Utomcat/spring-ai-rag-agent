package com.ranyk.spring.ai.rag.knowledge.database.domain.log.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ranyk.spring.ai.rag.knowledge.database.base.domain.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;

/**
 * CLASS_NAME: SystemLog.java
 
 * @author ranyk
 * @version V1.0
 * @description: 数据库系统日志表 t_system_log 映射实体类
 * @date:   2026-06-27
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_system_log")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper=true)
public class SystemLog extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1103540573434462381L;
    /**
    * 用户ID
    */
    private Long userId;
    /**
    * 动作描述
    */
    private String action;
    /**
    * IP
    */
    private String ip;
}