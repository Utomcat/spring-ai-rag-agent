package com.ranyk.spring.ai.rag.knowledge.database.domain.user.po;

import jakarta.validation.constraints.NotBlank;

/**
 * CLASS_NAME: AppUserSavePO.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 用户信息新增数据封装 PO 类, 其字段说明如下:
 * <ul>
 *     <li>id: 用户ID</li>
 *     <li>username: 用户名</li>
 *     <li>password: 密码</li>
 *     <li>realName: 真实姓名</li>
 *     <li>avatar: 头像</li>
 *     <li>role: 角色</li>
 *     <li>status: 状态</li>
 * </ul>
 * @date: 2026-06-29
 */
public record AppUserSavePO(Long id,
                            @NotBlank(message = "用户名不能为空") String username,
                            String password,
                            String realName,
                            String avatar,
                            @NotBlank(message = "角色不能为空") String role,
                            Integer status) {
}
