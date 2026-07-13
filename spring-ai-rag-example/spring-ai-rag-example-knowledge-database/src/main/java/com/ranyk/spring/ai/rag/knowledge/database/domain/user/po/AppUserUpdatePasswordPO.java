package com.ranyk.spring.ai.rag.knowledge.database.domain.user.po;

/**
 * CLASS_NAME: AppUserUpdatePasswordPO.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 更新用户密码的请求数据封装 PO 类, 其字段属性说明如下:
 * <ul>
 *     <li>oldPassword: 旧密码</li>
 *     <li>newPassword: 新密码</li>
 * </ul>
 * @date: 2026-06-29
 */
public record AppUserUpdatePasswordPO(String oldPassword, String newPassword) {
}
