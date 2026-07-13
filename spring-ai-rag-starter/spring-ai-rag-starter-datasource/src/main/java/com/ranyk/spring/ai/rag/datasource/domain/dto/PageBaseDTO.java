package com.ranyk.spring.ai.rag.datasource.domain.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ranyk.spring.ai.rag.base.domain.dto.BaseDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.util.Objects;

/**
 * CLASS_NAME: PageBaseDTO.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 分页查询基础 DTO 传输类
 * @date: 2026-07-10
 */
@Data
@Slf4j
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PageBaseDTO<T> extends BaseDTO<T>{

    @Serial
    private static final long serialVersionUID = -3743615564367819175L;

    /**
     * 默认当前页码
     */
    private static final long DEFAULT_CURRENT_PAGE = 1L;
    /**
     * 默认分页大小
     */
    private static final long DEFAULT_PAGE_SIZE = 10L;

    /**
     * 构建分页对象
     *
     * @param dto 分页参数对象
     * @return 分页对象
     */
    public static <E> Page<E> buildPage(BaseDTO<?> dto) {
        // 判空处理
        if (dto == null) {
            return new Page<>(DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE);
        }
        // 获取页码和页面大小，处理空值和非法值
        long currentPage = parseOrDefault(dto.getPage(), DEFAULT_CURRENT_PAGE);
        long pageSize = parseOrDefault(dto.getSize(), DEFAULT_PAGE_SIZE);
        return new Page<>(currentPage, pageSize);
    }

    /**
     * 将输入值解析为 Long 类型，若解析失败或为 null，则返回默认值
     *
     * @param value        输入值
     * @param defaultValue 默认值
     */
    private static long parseOrDefault(Object value, long defaultValue) {
        if (Objects.isNull(value)) {
            return defaultValue;
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException e) {
            // 记录警告日志（可根据实际需求调整）
            log.error("Invalid numeric value: {}, using default: {}", value, defaultValue);
            return defaultValue;
        }
    }
}
