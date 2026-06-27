package com.ranyk.spring.ai.rag.knowledge.database.service.user;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ranyk.spring.ai.rag.knowledge.database.common.constant.StatusEnum;
import com.ranyk.spring.ai.rag.knowledge.database.common.exception.ServiceException;
import com.ranyk.spring.ai.rag.knowledge.database.config.properties.FileProperties;
import com.ranyk.spring.ai.rag.knowledge.database.domain.user.dto.AppUserDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.user.entity.AppUser;
import com.ranyk.spring.ai.rag.knowledge.database.domain.user.mapstruct.AppUserMapper;
import com.ranyk.spring.ai.rag.knowledge.database.repository.user.AppUserRepository;
import com.ranyk.spring.ai.rag.knowledge.database.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * CLASS_NAME: AppUserRepository.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 数据库表 t_user 的操作接口
 * @date: 2026-06-27
 */
@Slf4j
@Service
public class AppUserService extends ServiceImpl<AppUserRepository, AppUser> {

    /**
     * AppUserRepository 对象
     */
    private final AppUserRepository appUserRepository;
    /**
     * JwtUtils 对象
     */
    private final JwtUtils jwtUtils;
    /**
     * 用户信息数据转换 MapStruct 映射接口对象 {@link AppUserMapper}
     */
    private final AppUserMapper appUserMapper;
    /**
     * 文件属性配置对象
     */
    private final FileProperties fileProperties;

    /**
     * 构造方法 - 通过 Spring IOC 容器向当前 Bean 中自动注入 AppUserRepository 对象
     *
     * @param appUserRepository 数据访问层接口对象
     * @param jwtUtils          JwtUtils 对象
     * @param appUserMapper     用户信息数据转换 MapStruct 映射接口对象 {@link AppUserMapper}
     * @param fileProperties    文件属性配置对象
     */
    @Autowired
    public AppUserService(AppUserRepository appUserRepository, JwtUtils jwtUtils, AppUserMapper appUserMapper, FileProperties fileProperties) {
        this.appUserRepository = appUserRepository;
        this.jwtUtils = jwtUtils;
        this.appUserMapper = appUserMapper;
        this.fileProperties = fileProperties;
    }

    /**
     * 用户登录处理逻辑
     *
     * @param appUserDTO 用户登录信息数据封装 DTO 对象 {@link AppUserDTO}
     * @return 用户登录信息数据封装 DTO 对象 {@link AppUserDTO}
     */
    public AppUserDTO login(AppUserDTO appUserDTO) {
        // 根据用户名查询用户信息
        AppUser appUser = this.appUserRepository.selectUserByUsername(appUserDTO.getUsername()).orElse(AppUser.builder().build());
        // 判断用户信息是否为空
        if (Objects.isNull(appUser.getId())) {
            log.error("当前根据用户的登录账户未查询到用户信息!");
            throw new ServiceException("username.not.exists", new String[]{appUserDTO.getUsername()});
        }
        // 判断用户状态是否正常
        if (Objects.isNull(appUser.getStatus()) || !Objects.equals(StatusEnum.ACCOUNT_NORMAL.getValue(), appUser.getStatus())) {
            log.error("当前用户账户状态异常!");
            StatusEnum statusEnum = StatusEnum.getStatusEnumByTypeAndValue("account", appUser.getStatus());
            throw new ServiceException("account.status.anomalous", new String[]{appUserDTO.getUsername(), statusEnum.getDescCn(), statusEnum.getDescEN()});
        }
        // 判断用户密码是否正确
        if (!DigestUtil.bcryptCheck(appUserDTO.getPassword(), appUser.getPassword())) {
            log.error("当前用户输入的用户密码不正确!");
            throw new ServiceException("user.password.incorrect", new String[]{});
        }
        // 生成 JWT Token
        String token = jwtUtils.createToken(appUser.getId(), appUser.getUsername(), appUser.getRole());
        // 构建用户登录信息数据封装 DTO 对象
        return AppUserDTO.builder()
                .id(appUser.getId())
                .username(appUser.getUsername())
                .realName(appUser.getRealName())
                .role(appUser.getRole())
                // 此处处理用户头像, 如果用户头像为空则设置为默认头像, 通过 FileProperties 对象获取 upload 的 root 属性值 + /avatar/fHe1eSEYZ.png 为准
                .avatar(StrUtil.isBlank(appUser.getAvatar()) ? buildDefaultAvatarUrl() : appUser.getAvatar())
                .token(token)
                .build();
    }

    /**
     * 构建默认头像 URL
     *
     * @return 默认头像的访问 URL
     */
    private String buildDefaultAvatarUrl() {
        String uploadRoot = fileProperties.getUpload().getRoot();
        uploadRoot = uploadRoot.endsWith("/") ? uploadRoot : uploadRoot + "/";
        return uploadRoot + "avatar/fHe1eSEYZ.png";
    }

    /**
     * 根据用户名查询用户信息（供 Spring Security 使用）
     *
     * @param appUserDTO 用户登录信息数据封装 DTO 对象 {@link AppUserDTO}
     * @return 用户登录信息数据封装 DTO 对象 {@link AppUserDTO}
     */
    public AppUserDTO findByUsername(AppUserDTO appUserDTO) {
        AppUser appUser = this.appUserRepository.selectUserByUsername(appUserDTO.getUsername()).orElse(AppUser.builder().build());
        return appUserMapper.appUserToAppUserDTO(appUser);
    }

}