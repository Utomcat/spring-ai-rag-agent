package com.ranyk.spring.ai.rag.knowledge.database.domain.login.po;

import jakarta.validation.constraints.NotBlank;

/**
 * CLASS_NAME: LoginPO.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 登录请求用户通过前端传入的数据封装 PO 类, 其字段说明如下:
 * <ul>
 *     <li>username: 用户名</li>
 *     <li>password: 密码</li>
 * </ul>
 * @date: 2026-06-27
 */
public record LoginPO(@NotBlank(message = "用户名不能为空") String username, @NotBlank(message = "密码不能为空") String password) {
}
