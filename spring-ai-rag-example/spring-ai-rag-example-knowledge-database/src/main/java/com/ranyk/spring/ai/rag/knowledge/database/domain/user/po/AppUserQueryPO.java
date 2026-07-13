package com.ranyk.spring.ai.rag.knowledge.database.domain.user.po;

/**
 * CLASS_NAME: AppUserQueryPO.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 用户信息查询条件数据封装 PO 类, 其字段说明如下:
 * <ul>
 *     <li>keyword: 查询条件关键字</li>
 * </ul>
 * @date: 2026-06-29
 */
public record AppUserQueryPO(String keyword) {
}
