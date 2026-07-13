package com.ranyk.spring.ai.rag.datasource.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.ranyk.spring.ai.rag.datasource.config.properties.HikariDataSourceProperties;
import com.ranyk.spring.ai.rag.datasource.config.properties.RdbProperties;
import com.ranyk.spring.ai.rag.datasource.handle.MyBatisPlusMetaObjectHandler;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * CLASS_NAME: MyBatisPlusConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: MyBatis-Plus 配置类
 * @date: 2026-06-26
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(value = {
        HikariDataSourceProperties.class,
        RdbProperties.class
})
public class MyBatisPlusConfiguration {

    /**
     * HikariCP 数据源属性
     */
    private final HikariDataSourceProperties hikariDataSourceProperties;

    /**
     * 构造方法 - 让 Spring IOC 创建当前类实例的 Bean 时,
     * 自动注入 HikariCP 数据源属性 hikariDataSourceProperties {@link HikariDataSourceProperties} 对象
     *
     * @param hikariDataSourceProperties HikariCP 数据源属性对象, {@link HikariDataSourceProperties}
     */
    @Autowired
    public MyBatisPlusConfiguration(HikariDataSourceProperties hikariDataSourceProperties) {
        this.hikariDataSourceProperties = hikariDataSourceProperties;
    }

    /**
     * 配置数据源 - HikariCP 连接池数据源
     *
     * @return 数据源 {@link DataSource}
     */
    @Primary
    @Bean(name = "dataSource")
    public DataSource dataSource() {
        log.debug("================================ 配置数据源 DataSource start  ========================================");
        log.debug("配置数据源 DataSource Bean 中 ... ");
        HikariDataSource dataSource = new HikariDataSource();
        // === 1. 基础连接信息 ===
        dataSource.setJdbcUrl(hikariDataSourceProperties.getUrl());
        dataSource.setUsername(hikariDataSourceProperties.getUserName());
        dataSource.setPassword(hikariDataSourceProperties.getPassword());
        dataSource.setDriverClassName(hikariDataSourceProperties.getDriverClassName());
        // === 2. 连接池核心参数 ===
        // 连接池名称，便于监控
        dataSource.setPoolName(hikariDataSourceProperties.getPoolName());
        // 最大连接数，默认10
        dataSource.setMaximumPoolSize(hikariDataSourceProperties.getMaximumPoolSize());
        // 最小空闲连接数，默认10
        dataSource.setMinimumIdle(hikariDataSourceProperties.getMinimumIdle());
        // 连接超时时间（毫秒），默认30000 (30秒)
        dataSource.setConnectionTimeout(hikariDataSourceProperties.getConnectionTimeout());
        // 空闲连接最大存活时间（毫秒），默认600000 (10分钟)
        dataSource.setIdleTimeout(hikariDataSourceProperties.getIdleTimeout());
        // 连接最大生命周期（毫秒），默认1800000 (30分钟)
        dataSource.setMaxLifetime(hikariDataSourceProperties.getMaxLifetime());
        // 连接测试查询语句
        dataSource.setConnectionTestQuery(hikariDataSourceProperties.getConnectionTestQuery());
        // === 3. 其他常用配置（可选） ===
        // 连接泄漏检测阈值（毫秒），超过该时长则记录日志
        dataSource.setLeakDetectionThreshold(hikariDataSourceProperties.getLeakDetectionThreshold());
        // 注册 JMX MBean，便于通过 JConsole 等工具监控[reference:0][reference:1]
        dataSource.setRegisterMbeans(hikariDataSourceProperties.isRegisterMbeans());
        log.debug("================================ 配置数据源 DataSource end    ========================================");
        return dataSource;
    }

    /**
     * 向 Spring Bean 容器中注入 Mybatis Plus 拦截器对象, 当下注册了(执行顺序如下): - 该操作是对 MyBatis Plus 的配置
     * <p>
     *     <ol>
     *         <li>多租户: TenantLineInnerInterceptor</li>
     *         <li>防全表更新与删除插件</li>
     *         <li>分页插件</li>
     *         <li>乐观锁插件</li>
     *     </ol>
     * </p>
     * 对于 MyBatis Plus 的内部拦截器,推荐的配置顺序如下:
     * <p>
     *     <ol>
     *         <li>动态表名: DynamicTableNameInnerInterceptor</li>
     *         <li>多租户: TenantLineInnerInterceptor</li>
     *         <li>数据权限（自定义拦截器类）: MyDataPermissionInterceptor</li>
     *         <li>防止全表更新/删除: BlockAttackInnerInterceptor</li>
     *         <li>分页: PaginationInnerInterceptor</li>
     *         <li>乐观锁: OptimisticLockerInnerInterceptor</li>
     *         <li>SQL 性能规范: IllegalSQLInnerInterceptor</li>
     *     </ol>
     * </p>
     *
     * @return 返回 Mybatis Plus 拦截器对象 {@link MybatisPlusInterceptor}
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        log.debug("======================= 配置 MyBatis Plus MybatisPlusInterceptor start  =============================");
        log.debug("配置 MyBatis Plus MybatisPlusInterceptor Bean 中 ... ");
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //防全表更新与删除插件 - 插件 BlockAttackInnerInterceptor 类在插件依赖 mybatis-plus-jsqlparser 下
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        //分页插件 - 插件 PaginationInnerInterceptor 类在插件依赖 mybatis-plus-jsqlparser 下
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        //乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // SQL 性能规范插件/ 非法 SQL 拦截插件 - 插件 IllegalSQLInnerInterceptor 在 3.5.10 版本开始移除
        log.debug("======================= 配置 MyBatis Plus MybatisPlusInterceptor end    =============================");
        return interceptor;
    }

    /**
     * 配置 MyBatis Plus 的元对象处理器, 用于处理 MyBatis Plus 的元对象操作，如插入、更新、删除等
     *
     * @return MyBatis Plus 的元对象处理器 {@link MyBatisPlusMetaObjectHandler}
     */
    @Bean
    public MyBatisPlusMetaObjectHandler myBatisPlusMetaObjectHandler(RdbProperties rdbProperties) {
        log.debug("======================= 配置 MyBatis Plus MyBatisPlusMetaObjectHandler start  =============================");
        log.debug("配置 MyBatis Plus MyBatisPlusMetaObjectHandler Bean 中 ... ");
        log.debug("======================= 配置 MyBatis Plus MyBatisPlusMetaObjectHandler end    =============================");
        return new MyBatisPlusMetaObjectHandler(rdbProperties);
    }
}
