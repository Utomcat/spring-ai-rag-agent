package com.ranyk.spring.ai.rag.base.domain.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * CLASS_NAME: BaseDTO.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 数据传输通用公共类
 * @date: 2026-06-27
 */
@Data
@Slf4j
@SuperBuilder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class BaseDTO<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 6273770133752927645L;

    // 以下是数据库的通用字段

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
    @Builder.Default
    private LocalDateTime createTime = LocalDateTime.now();
    /**
     * 更新人
     */
    private Long updateBy;
    /**
     * 更新时间
     */
    @Builder.Default
    private LocalDateTime updateTime = LocalDateTime.now();

    // 以下是分页参数的通用字段

    /**
     * 分页参数 - 数据总条数
     */
    private Long total;
    /**
     * 分页参数 - 当前页码
     */
    @Builder.Default
    private Integer page = 1;
    /**
     * 分页参数 - 每页显示条数
     */
    @Builder.Default
    private Integer size = 10;

    // 以下是数据传输通用字段

    /**
     * 数据 List 集合
     */
    private List<T> dataList;
}
