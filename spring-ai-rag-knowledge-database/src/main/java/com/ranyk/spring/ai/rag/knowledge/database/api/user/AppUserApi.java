package com.ranyk.spring.ai.rag.knowledge.database.api.user;

import com.ranyk.spring.ai.rag.knowledge.database.base.domain.po.PageQueryPO;
import com.ranyk.spring.ai.rag.knowledge.database.base.domain.vo.MultiResult;
import com.ranyk.spring.ai.rag.knowledge.database.base.domain.vo.Result;
import com.ranyk.spring.ai.rag.knowledge.database.domain.user.dto.AppUserDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.user.mapstruct.AppUserMapper;
import com.ranyk.spring.ai.rag.knowledge.database.domain.user.po.AppUserQueryPO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.user.po.AppUserSavePO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.user.po.AppUserUpdatePO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.user.po.AppUserUpdatePasswordPO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.user.vo.AppUserVO;
import com.ranyk.spring.ai.rag.knowledge.database.service.user.AppUserService;
import com.ranyk.spring.ai.rag.knowledge.database.utils.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * CLASS_NAME: AppUserRepository.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 数据库表 t_user 的操作接口
 * @date: 2026-06-27
 */
@RestController
@RequestMapping("/api/user")
public class AppUserApi {

    /**
     * 用户数据业务逻辑处理类对象 {@link AppUserService}
     */
    private final AppUserService appUserService;
    /**
     * 用户数据映射处理类对象 {@link AppUserMapper}
     */
    private final AppUserMapper appUserMapper;

    /**
     * 构造函数 - 由 Spring IOC 容器在创建当前 Bean 对象实例时自动注入相关的 Bean 对象
     *
     * @param appUserService 用户数据业务逻辑处理类对象 {@link AppUserService}
     * @param appUserMapper  用户数据映射处理类对象 {@link AppUserMapper}
     */
    @Autowired
    public AppUserApi(AppUserService appUserService,
                      AppUserMapper appUserMapper) {
        this.appUserService = appUserService;
        this.appUserMapper = appUserMapper;
    }

    /**
     * 分页查询用户（管理员）
     *
     * @param pageQueryPO 分页查询参数 {@link PageQueryPO}&lt;{@link AppUserQueryPO}&gt;
     * @return 分页查询结果 {@link MultiResult}&lt;{@link AppUserVO}&gt;
     */
    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public MultiResult<AppUserVO> page(PageQueryPO pageQueryPO, AppUserQueryPO appUserQueryPO) {
        AppUserDTO query = appUserMapper.appUserQueryPOToAppUserDTO(appUserQueryPO);
        query.setPage(pageQueryPO.page());
        query.setSize(pageQueryPO.size());
        AppUserDTO appUserDTO = appUserService.list(query);
        return MultiResult.successMulti(appUserMapper.appUserDTOListToAppUserVOList(appUserDTO.getDataList()),
                appUserDTO.getTotal(),
                appUserDTO.getPage(),
                appUserDTO.getSize());
    }

    /**
     * 新增或更新用户（管理员）
     *
     * @param appUserSavePO 用户保存参数对象 {@link AppUserSavePO}
     * @return 操作结果 {@link Result}
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> save(@Valid @RequestBody AppUserSavePO appUserSavePO) {
        appUserService.saveAppUser(appUserMapper.appUserSavePOToAppUserDTO(appUserSavePO));
        return Result.success();
    }

    /**
     * 删除用户（管理员）
     *
     * @param id 用户 ID
     * @return 操作结果 {@link Result}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        appUserService.deleteAppUser(AppUserDTO.builder().id(id).build());
        return Result.success();
    }

    /**
     * 当前登录用户详情
     *
     * @return 操作结果 {@link Result}&lt;{@link AppUserVO}&gt;
     */
    @GetMapping("/me")
    public Result<AppUserVO> me() {
        AppUserDTO appUserDTO = appUserService.getUserById(AppUserDTO.builder().id(SecurityUtils.requireUser().getUserId()).build());
        return Result.success(appUserMapper.appUserDTOToAppUserVO(appUserDTO));
    }

    /**
     * 更新个人资料
     *
     * @param appUserUpdatePO 用户更新参数对象 {@link AppUserUpdatePO}
     * @return 操作结果 {@link Result}
     */
    @PutMapping("/me/profile")
    public Result<Void> profile(@RequestBody AppUserUpdatePO appUserUpdatePO) {
        AppUserDTO appUserDTO = appUserMapper.appUserUpdatePOToAppUserDTO(appUserUpdatePO);
        appUserDTO.setId(SecurityUtils.requireUser().getUserId());
        appUserService.updateProfile(appUserDTO);
        return Result.success();
    }

    /**
     * 上传头像（管理员与普通用户均可）
     *
     * @param file 头像文件
     * @return 头像图片的相对路径
     */
    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> uploadAvatar(@RequestPart("file") MultipartFile file) {
        String relative = appUserService.updateAvatar(file, AppUserDTO.builder().id(SecurityUtils.requireUser().getUserId()).build());
        return Result.success(relative);
    }

    /**
     * 修改本人密码
     *
     * @param appUserUpdatePasswordPO 用户更新密码参数对象 {@link AppUserUpdatePasswordPO}
     * @return 操作结果 {@link Result}
     */
    @PutMapping("/me/password")
    public Result<Void> password(@RequestBody AppUserUpdatePasswordPO appUserUpdatePasswordPO) {
        AppUserDTO appUserDTO = appUserMapper.appUserUpdatePasswordPOToAppUserDTO(appUserUpdatePasswordPO);
        appUserDTO.setId(SecurityUtils.requireUser().getUserId());
        appUserService.changePassword(appUserDTO);
        return Result.success();
    }

}