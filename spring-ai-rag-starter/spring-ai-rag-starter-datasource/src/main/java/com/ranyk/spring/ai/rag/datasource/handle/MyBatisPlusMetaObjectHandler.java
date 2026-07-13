package com.ranyk.spring.ai.rag.datasource.handle;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.ranyk.spring.ai.rag.datasource.config.properties.RdbProperties;
import com.ranyk.spring.ai.rag.security.utils.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * CLASS_NAME: MyBatisPlusMetaObjectHandler.java
 *
 * @author ranyk
 * @version V1.0
 * @description: MyBatis Plus 自动填充监听处理器
 * @date: 2026-06-28
 */
@Component
public class MyBatisPlusMetaObjectHandler implements MetaObjectHandler {

    /**
     * RDB (关系型数据库)配置属性
     */
    private final RdbProperties rdbProperties;

    /**
     * 构造方法 - 由 Spring IOC 容器创建当前 Bean 实例对象时, 自动注入相关 Bean 依赖对象
     *
     * @param rdbProperties RDB 配置属性
     */
    @Autowired
    public MyBatisPlusMetaObjectHandler(RdbProperties rdbProperties) {
        this.rdbProperties = rdbProperties;
    }

    /**
     * 插入元对象字段填充(用于插入时对公共字段的填充)
     *
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 检查是否需要跳过自动填充
        if (shouldSkipAutoFill(metaObject)) {
            return;
        }
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        Long userId = SecurityUtils.currentUser().getUserId();
        this.strictInsertFill(metaObject, "createBy", Long.class, userId);
        this.strictInsertFill(metaObject, "updateBy", Long.class, userId);
    }

    /**
     * 更新元对象字段填充(用于更新时对公共字段的填充)
     *
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 检查是否需要跳过自动填充
        if (shouldSkipAutoFill(metaObject)) {
            return;
        }

        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        Long userId = SecurityUtils.currentUser().getUserId();
        this.strictUpdateFill(metaObject, "updateBy", Long.class, userId);
    }

    /**
     * 判断是否应该跳过自动填充
     *
     * @param metaObject 元对象
     * @return true-跳过自动填充, false-执行自动填充
     */
    private boolean shouldSkipAutoFill(MetaObject metaObject) {
        try {
            Object originalObject = metaObject.getOriginalObject();
            if (originalObject == null) {
                return false;
            }

            Class<?> entityClass = originalObject.getClass();
            TableName tableNameAnnotation = entityClass.getAnnotation(TableName.class);

            // 获取表名
            String tableName = Objects.nonNull(tableNameAnnotation) ? tableNameAnnotation.value() : entityClass.getSimpleName().toLowerCase();

            // 检查是否在跳过列表中
            return rdbProperties.getOrm().getSkipAutoFillTables().contains(tableName);
        } catch (Exception e) {
            // 发生异常时,默认执行自动填充
            return false;
        }
    }
}
