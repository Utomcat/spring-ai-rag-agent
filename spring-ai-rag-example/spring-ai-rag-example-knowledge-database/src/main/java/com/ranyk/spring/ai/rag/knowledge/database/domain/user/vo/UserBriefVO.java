package com.ranyk.spring.ai.rag.knowledge.database.domain.user.vo;

/**
 * CLASS_NAME: UserBriefVO.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 灯光用户简要信息返回前端响应 VO 类, 其具体属性说明如下:
 * <ul>
 *     <li>id: 用户 ID</li>
 *     <li>username: 用户名</li>
 *     <li>realName: 真实姓名</li>
 *     <li>role: 角色名称</li>
 *     <li>avatar: 头像</li>
 * </ul>
 * @date: 2026-06-27
 */
public record UserBriefVO(Long id, String username, String realName, String role, String avatar) {
}
