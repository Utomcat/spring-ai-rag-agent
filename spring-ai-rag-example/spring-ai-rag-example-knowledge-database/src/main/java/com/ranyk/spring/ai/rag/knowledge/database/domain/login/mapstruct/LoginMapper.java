package com.ranyk.spring.ai.rag.knowledge.database.domain.login.mapstruct;

import com.ranyk.spring.ai.rag.knowledge.database.domain.login.dto.LoginDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.login.po.LoginPO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.login.vo.LoginVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * CLASS_NAME: LoginMapper.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 登录业务数据 MapStruct 转换 Mapper
 * @date: 2026-06-27
 */
@SuppressWarnings("unused")
@Mapper(componentModel = "spring")
public interface LoginMapper {

    /**
     * 将 LoginPO 对象 转换成 LoginDTO 对象
     *
     * @param loginPO 登录信息数据封装 PO 对象 {@link LoginPO}
     * @return 登录信息数据传输 DTO 对象 {@link LoginDTO}
     */
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "token", ignore = true),
            @Mapping(target = "user", ignore = true),
            @Mapping(target = "createBy", ignore = true),
            @Mapping(target = "createTime", ignore = true),
            @Mapping(target = "updateBy", ignore = true),
            @Mapping(target = "updateTime", ignore = true),
            @Mapping(target = "total", ignore = true),
            @Mapping(target = "page", ignore = true),
            @Mapping(target = "size", ignore = true),
            @Mapping(target = "dataList", ignore = true),
    })
    LoginDTO loginPOToLoginDTO(LoginPO loginPO);

    /**
     * 将 LoginDTO 对象 转换成 LoginVO 对象
     *
     * @param loginDTO 登录信息数据传输 DTO 对象 {@link LoginDTO}
     * @return 登录信息数据封装 VO 对象 {@link LoginVO}
     */
    LoginVO loginDTOToLoginVO(LoginDTO loginDTO);
}
