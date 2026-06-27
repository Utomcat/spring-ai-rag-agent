package com.ranyk.spring.ai.rag.knowledge.database.domain.user.mapstruct;

import com.ranyk.spring.ai.rag.knowledge.database.domain.user.dto.AppUserDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.user.entity.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * CLASS_NAME: AppUserMapper.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 用户信息数据转换 MapStruct 映射接口
 * @date: 2026-06-27
 */
@Mapper(componentModel = "spring")
public interface AppUserMapper {

    /**
     * 将 {@link AppUser} 对象转换为 {@link AppUserDTO} 对象
     *
     * @param appUser 待转换的 {@link AppUser} 对象
     * @return 转换后的 {@link AppUserDTO} 对象
     */
    @Mappings({
            @Mapping(target = "token", ignore = true)
    })
    AppUserDTO appUserToAppUserDTO(AppUser appUser);
}
