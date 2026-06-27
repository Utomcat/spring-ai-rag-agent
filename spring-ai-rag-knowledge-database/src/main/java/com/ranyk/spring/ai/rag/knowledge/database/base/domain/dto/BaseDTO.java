package com.ranyk.spring.ai.rag.knowledge.database.base.domain.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * CLASS_NAME: BaseDTO.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 数据传输通用公共类
 * @date: 2026-06-27
 */
@Data
@SuperBuilder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class BaseDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 6273770133752927645L;

    /**
     * 数据主键
     */
    private Long id;
    /**
     * 创建人
     */
    private Long createBy;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新人
     */
    private Long updateBy;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
