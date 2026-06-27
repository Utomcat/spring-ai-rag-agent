package com.ranyk.spring.ai.rag.knowledge.database.repository.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ranyk.spring.ai.rag.knowledge.database.domain.user.entity.AppUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

/**
 * CLASS_NAME: AppUserRepository.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 数据库表 t_user 的操作接口
 * @date: 2026-06-27
 */
@Mapper
public interface AppUserRepository extends BaseMapper<AppUser> {

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名 需要查询的用户名
     * @return 用户信息
     */
    Optional<AppUser> selectUserByUsername(String username);
}