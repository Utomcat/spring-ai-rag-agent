package com.ranyk.spring.ai.rag.knowledge.database.base.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * CLASS_NAME: BaseEntity.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 数据库映射实体通用公共类
 * @date: 2026-06-27
 */
@Data
@SuperBuilder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = -2327903139570497112L;

    /**
     * 数据主键, 自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 数据创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;
    /**
     * 数据创建时间
     */
    @Builder.Default
    @TableField(fill = FieldFill.INSERT)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime = LocalDateTime.now();
    /**
     * 数据更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;
    /**
     * 数据更新时间
     */
    @Builder.Default
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime = LocalDateTime.now();
}
