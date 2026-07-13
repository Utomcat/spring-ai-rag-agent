package com.ranyk.spring.ai.rag.datasource.config.properties;

import cn.hutool.core.util.StrUtil;
import com.ranyk.spring.ai.rag.common.constant.DatabaseTypeEnum;
import com.ranyk.spring.ai.rag.common.exception.DataSourceException;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * CLASS_NAME: HikariDataSourceProperties.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Hikari 数据库连接池,配置属性类
 * @date: 2026-06-26
 */
@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = HikariDataSourceProperties.CONFIG_PREFIX)
public class HikariDataSourceProperties {
    /**
     * 自定义 - 文件配置属性前缀
     */
    public static final String CONFIG_PREFIX = "spring.datasource.hikari";
    /**
     * 自定义常量 - URL - 用于存放默认的数据库配置 Map 的 KEY
     */
    private static final String URL = "url";
    /**
     * 自定义常量 - USER_NAME - 用于存放默认的数据库配置 Map 的 KEY
     */
    private static final String USER_NAME = "userName";
    /**
     * 自定义常量 - PASSWORD - 用于存放默认的数据库配置 Map 的 KEY
     */
    private static final String PASSWORD = "password";
    /**
     * 自定义常量 - DRIVER_CLASS_NAME - 用于存放默认的数据库配置 Map 的 KEY
     */
    private static final String DRIVER_CLASS_NAME = "driverClassName";
    /**
     * 默认数据库连接 URL 配置
     */
    private static final Map<DatabaseTypeEnum, Map<String, String>> DEFAULT_URLS = Map.of(
            DatabaseTypeEnum.MYSQL, Map.of(URL, "jdbc:mysql://localhost:3306/mydb?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false",
                    USER_NAME, "root",
                    PASSWORD, "123456",
                    DRIVER_CLASS_NAME, "com.mysql.cj.jdbc.Driver"),
            DatabaseTypeEnum.POSTGRESQL, Map.of(URL, "jdbc:postgresql://localhost:5432/mydb",
                    USER_NAME, "postgres",
                    PASSWORD, "123456",
                    DRIVER_CLASS_NAME, "org.postgresql.Driver"),
            DatabaseTypeEnum.H2, Map.of(URL, "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
                    USER_NAME, "sa",
                    PASSWORD, "",
                    DRIVER_CLASS_NAME, "org.h2.Driver"),
            DatabaseTypeEnum.ORACLE, Map.of(URL, "jdbc:oracle:thin:@localhost:1521:orcl",
                    USER_NAME, "oracle",
                    PASSWORD, "123456",
                    DRIVER_CLASS_NAME, "oracle.jdbc.driver.OracleDriver"),
            DatabaseTypeEnum.SQL_SERVER, Map.of(URL, "jdbc:sqlserver://localhost:1433;databaseName=mydb",
                    USER_NAME, "sa",
                    PASSWORD, "123456",
                    DRIVER_CLASS_NAME, "com.microsoft.sqlserver.jdbc.SQLServerDriver"),
            DatabaseTypeEnum.MARIADB, Map.of(URL, "jdbc:mariadb://localhost:3306/mydb",
                    USER_NAME, "root",
                    PASSWORD, "123456",
                    DRIVER_CLASS_NAME, "org.mariadb.jdbc.Driver"),
            DatabaseTypeEnum.SQLITE, Map.of(URL, "jdbc:sqlite:/path/to/database.db",
                    USER_NAME, "sa",
                    PASSWORD, "",
                    DRIVER_CLASS_NAME, "org.sqlite.JDBC"),
            DatabaseTypeEnum.DB2, Map.of(URL, "jdbc:db2://localhost:50000/mydb",
                    USER_NAME, "db2admin",
                    PASSWORD, "123456",
                    DRIVER_CLASS_NAME, "com.ibm.db2.jcc.DB2Driver"),
            DatabaseTypeEnum.HSQLDB, Map.of(URL, "jdbc:hsqldb:mem:testdb",
                    USER_NAME, "sa",
                    PASSWORD, "",
                    DRIVER_CLASS_NAME, "org.hsqldb.jdbcDriver"),
            DatabaseTypeEnum.DERBY, Map.of(URL, "jdbc:derby:memory:testdb;create=true",
                    USER_NAME, "sa",
                    PASSWORD, "",
                    DRIVER_CLASS_NAME, "org.apache.derby.jdbc.EmbeddedDriver")
    );
    /**
     * 自定义的配置属性 - 数据库连接 URL
     */
    private String url = "";
    /**
     * Spring Boot 官方的配置属性 - 数据库连接 URL
     */
    private String jdbcUrl = "";
    /**
     * 自定义的配置属性 - 数据库类型
     */
    private String databaseType = "";
    /**
     * 自定义的配置属性 - 数据库连接用户名 - 默认值: root
     */
    private String userName = "";
    /**
     * Spring Boot 官方的配置属性 - 数据库连接用户名
     */
    private String username = "";
    /**
     * 数据库连接密码 - 默认值: 123456
     */
    private String password = "";
    /**
     * 自定义的配置属性 - 数据库连接驱动类名
     */
    private String driverClassName = "";
    /**
     * Spring Boot 官方的配置属性 - 数据库连接驱动类名
     */
    private String driverClass = "";
    /**
     * HikariCP 连接池名称配置 - 默认值: DefaultHikariPool
     */
    private String poolName = "DefaultHikariPool";
    /**
     * HikariCP 连接池最大连接数配置 - 默认值: 10
     */
    private int maximumPoolSize = 10;
    /**
     * HikariCP 连接池最小空闲连接数配置 - 默认值: 10
     */
    private int minimumIdle = 10;
    /**
     * HikariCP 连接池连接获取超时时间配置, 单位 毫秒 - 默认值: 30000 毫秒 (30秒)
     */
    private int connectionTimeout = 30000;
    /**
     * HikariCP 连接池连接空闲超时时间配置, 单位 毫秒 - 默认值: 600000 毫秒 (10分钟)
     */
    private int idleTimeout = 600000;
    /**
     * HikariCP 连接池连接最大生命周期配置, 单位 毫秒 - 默认值: 1800000 毫秒 (30分钟)
     */
    private int maxLifetime = 1800000;
    /**
     * HikariCP 连接池连接测试查询语句配置 - 默认值: SELECT 1
     */
    private String connectionTestQuery = "SELECT 1";
    /**
     * HikariCP 连接池连接泄漏检测阈值配置, 单位 毫秒 - 默认值: 60000 毫秒 (1分钟)
     */
    private int leakDetectionThreshold = 60000;
    /**
     * HikariCP 连接池注册 JMX MBean 配置 - 默认值: true
     */
    private boolean registerMbeans = true;

    /**
     * 初始化方法,用于初始化数据库连接池配置
     */
    @PostConstruct
    public void init() {
        normalizeFields();
        boolean hasUrl = StrUtil.isNotBlank(url);
        boolean hasUserName = StrUtil.isNotBlank(userName);
        boolean hasPassword = StrUtil.isNotBlank(password);
        boolean hasDriverClassName = StrUtil.isNotBlank(driverClassName);
        boolean hasDatabaseType = StrUtil.isNotBlank(databaseType);

        if (!hasUrl || !hasUserName || !hasPassword || !hasDriverClassName) {
            if (!hasDatabaseType) {
                throw new DataSourceException("未配置数据源连接信息,请配置数据源连接基础属性 url/jdbc-url、userName/username、password、driverClassName/driver-class 或配置 databaseType 去获取默认配置");
            }

            DatabaseTypeEnum dbType = DatabaseTypeEnum.valueOf(databaseType.toUpperCase());
            Map<String, String> defaultConfig = DEFAULT_URLS.get(dbType);

            if (Objects.isNull(defaultConfig)) {
                throw new DataSourceException("不支持的数据库类型: " + databaseType);
            }

            if (StrUtil.isBlank(url)) {
                this.url = defaultConfig.get(URL);
            }
            if (StrUtil.isBlank(userName)) {
                this.userName = defaultConfig.get(USER_NAME);
            }
            if (StrUtil.isBlank(password)) {
                this.password = defaultConfig.get(PASSWORD);
            }
            if (StrUtil.isBlank(driverClassName)) {
                this.driverClassName = defaultConfig.get(DRIVER_CLASS_NAME);
            }
        }
    }

    /**
     * 规范化字段方法,用于将 jdbcUrl、username、driverClass 字段的值赋给 url、userName、driverClassName 字段, 目的是用于让自定义的数据库配置 和 Spring Boot 官方的数据库配置属性能够统一
     */
    private void normalizeFields() {
        // 如果 url 字段为空且 jdbcUrl 字段不为空,则将 jdbcUrl 的值赋给 url 字段
        if (StrUtil.isBlank(url) && StrUtil.isNotBlank(jdbcUrl)) {
            this.url = jdbcUrl;
        }
        // 如果 userName 字段为空且 username 字段不为空,则将 username 的值赋给 userName 字段
        if (StrUtil.isBlank(userName) && StrUtil.isNotBlank(username)) {
            this.userName = username;
        }
        // 如果 driverClassName 字段为空且 driverClass 字段不为空,则将 driverClass 的值赋给 driverClassName 字段
        if (StrUtil.isBlank(driverClassName) && StrUtil.isNotBlank(driverClass)) {
            this.driverClassName = driverClass;
        }
    }
}
