package com.ranyk.spring.ai.rag.knowledge.database.domain.user.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

/**
 * CLASS_NAME: AppUserVO.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 用户数据返回前端数据封装 VO 类, 其字段说明如下:
 * <ul>
 *     <li>id: 用户ID</li>
 *     <li>username: 用户名</li>
 *     <li>password: 密码</li>
 *     <li>realName: 真实姓名</li>
 *     <li>avatar: 头像</li>
 *     <li>role: 角色</li>
 *     <li>status: 状态</li>
 *     <li>createTime: 创建时间</li>
 * </ul>
 * @date: 2026-06-29
 */
public record AppUserVO(Long id,
                        String username,
                        @JsonIgnore String password,
                        String realName,
                        String avatar,
                        String role,
                        Integer status,
                        LocalDateTime createTime) {
}
