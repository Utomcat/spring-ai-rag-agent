package com.ranyk.spring.ai.rag.knowledge.database.base.domain.po;

import com.drew.lang.annotations.NotNull;
import jakarta.validation.constraints.Min;

/**
 * CLASS_NAME: PageQuery.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 通用的分页查询包装器 PO 类, 其字段说明如下:
 * <ul>
 *     <li>page: 当前页码</li>
 *     <li>size: 每页大小</li>
 * </ul>
 * @date: 2026-06-28
 */
public record PageQueryPO(@NotNull @Min(1) Integer page, @NotNull @Min(10) Integer size) {

}
