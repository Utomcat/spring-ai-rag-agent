package com.ranyk.spring.ai.rag.knowledge.database.domain.log.dto;

import com.ranyk.spring.ai.rag.base.domain.dto.BaseDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;

/**
 * CLASS_NAME: SystemLogDTO.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 系统日志数据传输 DTO 类
 * @date: 2026-06-27
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SystemLogDTO extends BaseDTO<SystemLogDTO> {
    @Serial
    private static final long serialVersionUID = 6177698916764656815L;

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
