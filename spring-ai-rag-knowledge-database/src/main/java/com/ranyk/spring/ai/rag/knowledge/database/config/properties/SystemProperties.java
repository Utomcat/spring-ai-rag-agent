package com.ranyk.spring.ai.rag.knowledge.database.config.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * CLASS_NAME: SystemProperties.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 自定义系统配置类
 * @date: 2026-07-02
 */
@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = SystemProperties.CONFIG_PREFIX)
public class SystemProperties {

    /**
     * 配置属性前缀
     */
    public static final String CONFIG_PREFIX = "system";
    /**
     * 系统默认的头像路径 - 该路径是在项目的上传目录下 avatar 文件夹下, 默认值为 /avatar/default.png 该文件需要存在于系统中
     */
    private String defaultAvatar = "/avatar/default.png";
}
