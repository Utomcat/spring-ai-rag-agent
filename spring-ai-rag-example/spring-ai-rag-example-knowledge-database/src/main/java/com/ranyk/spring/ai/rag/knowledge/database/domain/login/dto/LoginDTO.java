package com.ranyk.spring.ai.rag.knowledge.database.domain.login.dto;

import com.ranyk.spring.ai.rag.base.domain.dto.BaseDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.user.dto.AppUserDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;

/**
 * CLASS_NAME: LoginDTO.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 登录信息数据传输 DTO 类
 * @date: 2026-06-27
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LoginDTO extends BaseDTO<LoginDTO> {

    @Serial
    private static final long serialVersionUID = -6509347428965680176L;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 登录成功后, 系统生成的 token 令牌
     */
    private String token;
    /**
     * 登录成功后, 获取到的登录用户简要信息封装 DTO 传输对象 {@link AppUserDTO}
     */
    private AppUserDTO user;

}
