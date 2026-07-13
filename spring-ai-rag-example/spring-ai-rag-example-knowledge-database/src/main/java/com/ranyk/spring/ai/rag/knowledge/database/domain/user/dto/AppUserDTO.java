package com.ranyk.spring.ai.rag.knowledge.database.domain.user.dto;

import com.ranyk.spring.ai.rag.base.domain.dto.BaseDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * CLASS_NAME: AppUserDTO.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 用户数据传输 DOT 类
 * @date: 2026-06-27
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AppUserDTO extends BaseDTO<AppUserDTO> {
    @Serial
    private static final long serialVersionUID = -7960461784712375879L;
    /**
     * 登录名
     */
    private String username;
    /**
     * 加密密码（BCrypt/MD5）
     */
    private String password;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 头像相对路径（对应 /files/ 下资源）
     */
    private String avatar;
    /**
     * 角色：ADMIN / USER
     */
    private String role;
    /**
     * 状态：1正常 0禁用
     */
    private String status;
    /**
     * 最后登录时间
     *
     */
    private LocalDateTime lastLoginTime;

    /**
     * 登录令牌
     */
    private String token;
    /**
     * 查询条件关键字
     */
    private String keyword;
    /**
     * 旧密码
     */
    private String oldPassword;
    /**
     * 新密码
     */
    private String newPassword;

}
