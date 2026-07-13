package com.ranyk.spring.ai.rag.knowledge.database.domain.login.vo;

import com.ranyk.spring.ai.rag.knowledge.database.domain.user.vo.UserBriefVO;

/**
 * CLASS_NAME: LoginVO.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 系统登录成功返回前端响应 VO 类, 其字段说明如下:
 * <ul>
 *     <li>token: 登录成功后返回的令牌</li>
 *     <li>user: 登录用户简要信息, 参见 {@link UserBriefVO}</li>
 * </ul>
 * @date: 2026-06-27
 */
public record LoginVO(String token, UserBriefVO user) {
}
