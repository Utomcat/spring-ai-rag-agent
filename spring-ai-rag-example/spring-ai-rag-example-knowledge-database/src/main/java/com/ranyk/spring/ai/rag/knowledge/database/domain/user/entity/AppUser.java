package com.ranyk.spring.ai.rag.knowledge.database.domain.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ranyk.spring.ai.rag.datasource.domain.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * CLASS_NAME: AppUser.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 数据库表 t_user 映射实体类
 * @date: 2026-06-27
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AppUser extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 2399168121196135565L;
    /**
     * 登录名
     */
    @TableField(value = "username")
    private String username;
    /**
     * 加密密码（BCrypt/MD5）
     */
    @TableField(value = "password")
    private String password;
    /**
     * 真实姓名
     */
    @TableField(value = "real_name")
    private String realName;
    /**
     * 头像相对路径（对应 /files/ 下资源）
     */
    @TableField(value = "avatar")
    private String avatar;
    /**
     * 角色：ADMIN / USER
     */
    @TableField(value = "role")
    private String role;
    /**
     * 状态：1正常 0禁用
     */
    @TableField(value = "status")
    private Integer status;
    /**
     * 最后登录时间
     *
     */
    @TableField(value = "last_login_time")
    private LocalDateTime lastLoginTime;
}