package com.ranyk.spring.ai.rag.web.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Manager;
import org.apache.catalina.session.StandardManager;
import org.springframework.boot.tomcat.TomcatContextCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CLASS_NAME: TomcatConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Tomcat 配置类，禁用 Session 文件持久化以避免启动时 FileNotFoundException
 * @date: 2026-07-13
 */
@Slf4j
@Configuration
public class TomcatConfiguration {

    /**
     * 禁用 Session 文件持久化以避免启动时 FileNotFoundException
     *
     * @return {@link TomcatContextCustomizer}
     */
    @Bean
    public TomcatContextCustomizer sessionPersistenceDisableCustomizer() {
        log.debug("===========================  配置 TomcatContextCustomizer Bean start  ===============================");
        log.debug("配置 TomcatContextCustomizer Bean 处理中 ...");
        log.debug("===========================  配置 TomcatContextCustomizer Bean end   ================================");
        return context -> {
            Manager manager = context.getManager();
            if (manager instanceof StandardManager standardManager) {
                standardManager.setPathname("");
            }
        };
    }
}
