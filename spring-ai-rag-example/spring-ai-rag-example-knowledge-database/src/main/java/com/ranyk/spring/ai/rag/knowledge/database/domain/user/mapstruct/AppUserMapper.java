package com.ranyk.spring.ai.rag.knowledge.database.domain.user.mapstruct;

import com.ranyk.spring.ai.rag.knowledge.database.domain.user.dto.AppUserDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.user.entity.AppUser;
import com.ranyk.spring.ai.rag.knowledge.database.domain.user.po.AppUserQueryPO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.user.po.AppUserSavePO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.user.po.AppUserUpdatePO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.user.po.AppUserUpdatePasswordPO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.user.vo.AppUserVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

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
            @Mapping(target = "token", ignore = true),
            @Mapping(target = "total", ignore = true),
            @Mapping(target = "page", ignore = true),
            @Mapping(target = "size", ignore = true),
            @Mapping(target = "dataList", ignore = true),
            @Mapping(target = "keyword", ignore = true),
            @Mapping(target = "oldPassword", ignore = true),
            @Mapping(target = "newPassword", ignore = true),
    })
    AppUserDTO appUserToAppUserDTO(AppUser appUser);

    /**
     * 将 {@link AppUserDTO} 对象转换为 {@link AppUserVO} 对象
     *
     * @param appUserDTO 待转换的 {@link AppUserDTO} 对象
     * @return 转换后的 {@link AppUserVO} 对象
     */
    AppUserVO appUserDTOToAppUserVO(AppUserDTO appUserDTO);

    /**
     * 将 {@link AppUserQueryPO} 对象转换为 {@link AppUserDTO} 对象
     *
     * @param appUserQueryPO 待转换的 {@link AppUserQueryPO} 对象
     * @return 转换后的 {@link AppUserDTO} 对象
     */
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "avatar", ignore = true),
            @Mapping(target = "username", ignore = true),
            @Mapping(target = "realName", ignore = true),
            @Mapping(target = "role", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "lastLoginTime", ignore = true),
            @Mapping(target = "createBy", ignore = true),
            @Mapping(target = "createTime", ignore = true),
            @Mapping(target = "updateBy", ignore = true),
            @Mapping(target = "updateTime", ignore = true),
            @Mapping(target = "token", ignore = true),
            @Mapping(target = "total", ignore = true),
            @Mapping(target = "page", ignore = true),
            @Mapping(target = "size", ignore = true),
            @Mapping(target = "dataList", ignore = true),
            @Mapping(target = "oldPassword", ignore = true),
            @Mapping(target = "newPassword", ignore = true),
            @Mapping(target = "password", ignore = true),
    })
    AppUserDTO appUserQueryPOToAppUserDTO(AppUserQueryPO appUserQueryPO);

    /**
     * 将 {@link AppUserDTO} 对象列表转换为 {@link AppUserVO} 对象列表
     *
     * @param appUserDTOList 待转换的 {@link AppUserDTO} 对象列表
     * @return 转换后的 {@link AppUserVO} 对象列表
     */
    List<AppUserVO> appUserDTOListToAppUserVOList(List<AppUserDTO> appUserDTOList);

    /**
     * 将 {@link AppUser} 对象列表转换为 {@link AppUserDTO} 对象列表
     *
     * @param appUserList 待转换的 {@link AppUser} 对象列表
     * @return 转换后的 {@link AppUserDTO} 对象列表
     */
    List<AppUserDTO> appUserListToAppUserDTOList(List<AppUser> appUserList);

    /**
     * 将 {@link AppUserSavePO} 对象转换为 {@link AppUserDTO} 对象
     *
     * @param appUserSavePO 待转换的 {@link AppUserSavePO} 对象
     * @return 转换后的 {@link AppUserDTO} 对象
     */
    @Mappings({
            @Mapping(target = "token", ignore = true),
            @Mapping(target = "createBy", ignore = true),
            @Mapping(target = "createTime", ignore = true),
            @Mapping(target = "updateBy", ignore = true),
            @Mapping(target = "updateTime", ignore = true),
            @Mapping(target = "total", ignore = true),
            @Mapping(target = "page", ignore = true),
            @Mapping(target = "size", ignore = true),
            @Mapping(target = "dataList", ignore = true),
            @Mapping(target = "keyword", ignore = true),
            @Mapping(target = "lastLoginTime", ignore = true),
            @Mapping(target = "oldPassword", ignore = true),
            @Mapping(target = "newPassword", ignore = true),
    })
    AppUserDTO appUserSavePOToAppUserDTO(AppUserSavePO appUserSavePO);

    /**
     * 将 {@link AppUserDTO} 对象转换为 {@link AppUser} 对象
     *
     * @param appUserDTO 待转换的 {@link AppUserDTO} 对象
     * @return 转换后的 {@link AppUser} 对象
     */
    AppUser appUserDTOToAppUser(AppUserDTO appUserDTO);

    /**
     * 将 {@link AppUserUpdatePO} 对象转换为 {@link AppUserDTO} 对象
     *
     * @param appUserUpdatePO 待转换的 {@link AppUserUpdatePO} 对象
     * @return 转换后的 {@link AppUserDTO} 对象
     */
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "username", ignore = true),
            @Mapping(target = "password", ignore = true),
            @Mapping(target = "role", ignore = true),
            @Mapping(target = "avatar", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "oldPassword", ignore = true),
            @Mapping(target = "newPassword", ignore = true),
            @Mapping(target = "token", ignore = true),
            @Mapping(target = "total", ignore = true),
            @Mapping(target = "page", ignore = true),
            @Mapping(target = "size", ignore = true),
            @Mapping(target = "dataList", ignore = true),
            @Mapping(target = "keyword", ignore = true),
            @Mapping(target = "lastLoginTime", ignore = true),
            @Mapping(target = "createBy", ignore = true),
            @Mapping(target = "createTime", ignore = true),
            @Mapping(target = "updateBy", ignore = true),
            @Mapping(target = "updateTime", ignore = true),
    })
    AppUserDTO appUserUpdatePOToAppUserDTO(AppUserUpdatePO appUserUpdatePO);

    /**
     * 将 {@link AppUserUpdatePasswordPO} 对象转换为 {@link AppUserDTO} 对象
     *
     * @param appUserUpdatePasswordPO 待转换的 {@link AppUserUpdatePasswordPO} 对象
     * @return 转换后的 {@link AppUserDTO} 对象
     */
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "username", ignore = true),
            @Mapping(target = "password", ignore = true),
            @Mapping(target = "realName", ignore = true),
            @Mapping(target = "role", ignore = true),
            @Mapping(target = "avatar", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "token", ignore = true),
            @Mapping(target = "total", ignore = true),
            @Mapping(target = "page", ignore = true),
            @Mapping(target = "size", ignore = true),
            @Mapping(target = "dataList", ignore = true),
            @Mapping(target = "keyword", ignore = true),
            @Mapping(target = "lastLoginTime", ignore = true),
            @Mapping(target = "createBy", ignore = true),
            @Mapping(target = "createTime", ignore = true),
            @Mapping(target = "updateBy", ignore = true),
            @Mapping(target = "updateTime", ignore = true),
    })
    AppUserDTO appUserUpdatePasswordPOToAppUserDTO(AppUserUpdatePasswordPO appUserUpdatePasswordPO);
}
