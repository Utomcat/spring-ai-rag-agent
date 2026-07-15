package com.ranyk.spring.ai.rag.base.config.properties;

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
    /**
     * 系统默认的提示词
     */
    private String systemPrompt = """
            
            你是个有用的助手.
            
            你可以回答问题, 提供信息, 并协助各种主题.
            
            你还可以根据收到的输入生成文本.
            """;
    /**
     * 系统默认的 Agent 名称
     */
    private String agentName = "default-agent";
    /**
     * 系统默认的 Agent 文件操作默认保存路径, 末尾带 /
     */
    private String agentFileOperateDefaultDir = "E:/FTP/upload/";
}
