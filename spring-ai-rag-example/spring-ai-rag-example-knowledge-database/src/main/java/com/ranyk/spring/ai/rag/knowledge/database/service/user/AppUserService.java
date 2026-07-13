package com.ranyk.spring.ai.rag.knowledge.database.service.user;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ranyk.spring.ai.rag.base.config.properties.SystemProperties;
import com.ranyk.spring.ai.rag.common.constant.StatusEnum;
import com.ranyk.spring.ai.rag.common.exception.ServiceException;
import com.ranyk.spring.ai.rag.datasource.domain.dto.PageBaseDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.user.dto.AppUserDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.user.entity.AppUser;
import com.ranyk.spring.ai.rag.knowledge.database.domain.user.mapstruct.AppUserMapper;
import com.ranyk.spring.ai.rag.knowledge.database.repository.user.AppUserRepository;
import com.ranyk.spring.ai.rag.knowledge.database.service.file.FileStorageService;
import com.ranyk.spring.ai.rag.security.config.properties.JwtProperties;
import com.ranyk.spring.ai.rag.security.utils.JwtUtils;
import com.ranyk.spring.ai.rag.web.config.properties.FileProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

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
@SuppressWarnings("all")
public class AppUserService extends ServiceImpl<AppUserRepository, AppUser> {

    /**
     * AppUserRepository 对象
     */
    private final AppUserRepository appUserRepository;
    /**
     * 用户信息数据转换 MapStruct 映射接口对象 {@link AppUserMapper}
     */
    private final AppUserMapper appUserMapper;
    /**
     * 文件属性配置对象
     */
    private final FileProperties fileProperties;
    /**
     * 文件存储服务对象
     */
    private final FileStorageService fileStorageService;
    /**
     * 系统属性配置对象
     */
    private final SystemProperties systemProperties;
    /**
     * JWT 相关属性
     */
    private final JwtProperties jwtProperties;
    /**
     * 当前系统支持的头像文件扩展名集合
     */
    private static final Set<String> AVATAR_EXT = Set.of("jpg", "jpeg", "png", "gif", "webp");

    /**
     * 构造方法 - 通过 Spring IOC 容器向当前 Bean 中自动注入 AppUserRepository 对象
     *
     * @param appUserRepository  数据访问层接口对象
     * @param appUserMapper      用户信息数据转换 MapStruct 映射接口对象 {@link AppUserMapper}
     * @param fileProperties     文件属性配置对象
     * @param fileStorageService 文件存储服务对象
     * @param systemProperties   系统属性配置对象
     * @param jwtProperties      JWT 相关属性
     */
    @Autowired
    public AppUserService(AppUserRepository appUserRepository,
                          AppUserMapper appUserMapper,
                          FileProperties fileProperties,
                          FileStorageService fileStorageService,
                          SystemProperties systemProperties,
                          JwtProperties jwtProperties) {
        this.appUserRepository = appUserRepository;
        this.appUserMapper = appUserMapper;
        this.fileProperties = fileProperties;
        this.fileStorageService = fileStorageService;
        this.systemProperties = systemProperties;
        this.jwtProperties = jwtProperties;
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
        String token = JwtUtils.createToken(appUser.getId(), appUser.getUsername(), appUser.getRole(), jwtProperties);
        // 构建用户登录信息数据封装 DTO 对象
        return AppUserDTO.builder()
                .id(appUser.getId())
                .username(appUser.getUsername())
                .realName(appUser.getRealName())
                .role(appUser.getRole())
                // 此处处理用户头像, 如果用户头像为空则设置为默认头像, 通过 FileProperties 对象获取 upload 的 root 属性值 + /avatar/default.png 为准
                .avatar(StrUtil.isBlank(appUser.getAvatar()) ? systemProperties.getDefaultAvatar() : appUser.getAvatar())
                .token(token)
                .build();
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

    /**
     * 查询最近7天用户注册数量
     *
     * @return 最近7天用户注册数量 {@link List}&lt;{@link Map}&lt;{@link String}, {@link Long}&gt;&gt;
     */
    public List<Map<String, Long>> countUserRegByDayLast7() {
        return appUserRepository.countUserRegByDayLast7();
    }

    /**
     * 分页查询用户信息
     *
     * @param appUserDTO 用户信息数据封装 DTO 对象 {@link AppUserDTO}
     * @return 用户信息数据封装 DTO 对象 {@link AppUserDTO}
     */
    public AppUserDTO list(AppUserDTO appUserDTO) {
        Page<AppUser> page = PageBaseDTO.buildPage(appUserDTO);
        LambdaQueryWrapper<AppUser> queryWrapper = new LambdaQueryWrapper<>();
        // 关键字模糊查询 - 用户名或真实姓名
        queryWrapper.and(StrUtil.isNotBlank(appUserDTO.getKeyword()), wrapper ->
                wrapper.like(AppUser::getUsername, appUserDTO.getKeyword())
                        .or()
                        .like(AppUser::getRealName, appUserDTO.getKeyword())
        );
        // 按 ID 降序排序
        queryWrapper.orderByDesc(AppUser::getId);
        Page<AppUser> userPage = this.page(page, queryWrapper);
        return AppUserDTO.builder()
                .dataList(appUserMapper.appUserListToAppUserDTOList(userPage.getRecords()))
                .total(userPage.getTotal())
                .page(Long.valueOf(userPage.getPages()).intValue())
                .size(Long.valueOf(userPage.getSize()).intValue())
                .build();
    }

    /**
     * 保存用户信息
     *
     * @param appUserDTO 用户信息数据封装 DTO 对象 {@link AppUserDTO}
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveAppUser(AppUserDTO appUserDTO) {
        if (Objects.isNull(appUserDTO.getId())) {
            LambdaQueryWrapper<AppUser> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AppUser::getUsername, appUserDTO.getUsername());
            long count = this.count(queryWrapper);
            if (count > 1) {
                log.error("新增用户时, 用户名存在判定, 用户名已存在!");
                throw new ServiceException("username.already.exists", new String[]{"用户名已存在!"});
            }
            AppUser appUser = appUserMapper.appUserDTOToAppUser(appUserDTO);
            appUser.setPassword(Objects.isNull(appUser.getPassword()) ? DigestUtil.bcrypt("12345678") : DigestUtil.bcrypt(appUser.getPassword()));
            this.saveOrUpdate(appUser);
        } else {
            AppUser appUser = this.getById(appUserDTO.getId());
            if (Objects.isNull(appUser)) {
                log.error("修改用户时, 用户不存在!");
                throw new ServiceException("user.not.exists", new String[]{});
            }
            if (!Objects.equals(appUser.getUsername(), appUserDTO.getUsername())) {
                long count = this.count(Wrappers.<AppUser>lambdaQuery().eq(AppUser::getUsername, appUserDTO.getUsername()));
                if (count > 0) {
                    log.error("修改用户时, 用户名存在判定, 用户名已存在!");
                    throw new ServiceException("username.already.exists", new String[]{"用户名已存在!"});
                }
            }
            AppUser newAppUser = appUserMapper.appUserDTOToAppUser(appUserDTO);
            if (Objects.nonNull(appUserDTO.getPassword()) && StrUtil.isNotBlank(appUserDTO.getPassword())) {
                newAppUser.setPassword(DigestUtil.bcrypt(appUserDTO.getPassword()));
            }
            this.saveOrUpdate(newAppUser);
        }
    }

    /**
     * 删除用户信息
     *
     * @param appUserDTO 用户信息数据封装 DTO 对象 {@link AppUserDTO}
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteAppUser(AppUserDTO appUserDTO) {
        AppUser appUser = this.getById(appUserDTO.getId());
        if (Objects.isNull(appUser) || Objects.isNull(appUser.getId())) {
            log.error("删除用户时, 用户不存在!");
            return;
        }
        if (StrUtil.equalsIgnoreCase(appUser.getRole(), "ADMIN") && StrUtil.equalsIgnoreCase(appUserDTO.getUsername(), "admin")) {
            throw new ServiceException("not.delete.administrator", new String[]{});
        }
        this.removeById(appUserDTO.getId());
    }

    /**
     * 根据用户 ID 查询用户信息
     *
     * @param appUserDTO 用户信息数据封装 DTO 对象 {@link AppUserDTO}
     * @return 用户信息数据封装 DTO 对象 {@link AppUserDTO}
     */
    public AppUserDTO getUserById(AppUserDTO appUserDTO) {
        AppUser appUser = this.getById(appUserDTO.getId());
        // 头像处理 - 如果头像为空, 则使用默认头像
        if (StrUtil.isBlank(appUser.getAvatar())) {
            appUser.setAvatar(systemProperties.getDefaultAvatar());
        }
        return appUserMapper.appUserToAppUserDTO(appUser);
    }

    /**
     * 更新个人资料
     *
     * @param appUserDTO 用户信息数据封装 DTO 对象 {@link AppUserDTO}
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(AppUserDTO appUserDTO) {
        UpdateWrapper<AppUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", appUserDTO.getId());
        updateWrapper.set("real_name", appUserDTO.getRealName());
        updateWrapper.set("update_by", appUserDTO.getId());
        updateWrapper.set("update_time", LocalDateTime.now());
        this.update(updateWrapper);
    }

    /**
     * 更新用户头像
     *
     * @param file       上传的头像文件
     * @param appUserDTO 用户信息数据封装 DTO 对象 {@link AppUserDTO}
     * @return 头像图片的相对路径
     */
    @Transactional(rollbackFor = Exception.class)
    public String updateAvatar(MultipartFile file, AppUserDTO appUserDTO) {
        if (file == null || file.isEmpty()) {
            log.error("上传头像时, 请选择图片文件");
            throw new ServiceException("avatar.upload.error", new String[]{"上传头像时, 没有上传头像文件, 请选择图片文件"});
        }
        String name = file.getOriginalFilename() != null ? file.getOriginalFilename() : "";
        int dot = name.lastIndexOf('.');
        String ext = dot > 0 ? name.substring(dot + 1).toLowerCase(Locale.ROOT) : "";
        if (!AVATAR_EXT.contains(ext)) {
            log.error("上传头像时, 文件类型不支持");
            throw new ServiceException("avatar.upload.error", new String[]{"仅支持 jpg、jpeg、png、gif、webp 图片"});
        }

        AppUser appUser = this.getById(appUserDTO.getId());
        if (Objects.isNull(appUser)) {
            log.error("更新用户头像时, 需更新的用户不存在");
            throw new ServiceException("avatar.upload.error", new String[]{"更新用户头像时, 需更新的用户不存在"});
        }
        String relative;
        try {
            relative = fileStorageService.upload(file).relativePath();
        } catch (Exception e) {
            throw new ServiceException("头像保存失败");
        }
        UpdateWrapper<AppUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", appUserDTO.getId());
        updateWrapper.set("avatar", relative);
        updateWrapper.set("update_by", appUserDTO.getId());
        updateWrapper.set("update_time", LocalDateTime.now());
        this.update(updateWrapper);
        return relative;
    }

    /**
     * 修改密码
     *
     * @param appUserDTO 用户信息数据封装 DTO 对象 {@link AppUserDTO}
     */
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(AppUserDTO appUserDTO) {
        if (Objects.isNull(appUserDTO.getNewPassword()) || appUserDTO.getNewPassword().length() < 8) {
            log.error("新密码至少 8 位");
            throw new ServiceException("password.min.length.error", new String[]{"8"});
        }
        AppUser appUser = this.getById(appUserDTO.getId());
        if (Objects.isNull(appUser) || !DigestUtil.bcryptCheck(appUserDTO.getOldPassword(), appUser.getPassword())) {
            log.error("修改密码时, 旧密码不正确");
            throw new ServiceException("change.password.error", new String[]{"旧密码不正确"});
        }
        UpdateWrapper<AppUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", appUserDTO.getId());
        updateWrapper.set("password", DigestUtil.bcrypt(appUserDTO.getNewPassword()));
        updateWrapper.set("update_by", appUserDTO.getId());
        updateWrapper.set("update_time", LocalDateTime.now());
        this.update(updateWrapper);
    }
}