package com.ranyk.spring.ai.rag.knowledge.database.config;

import com.ranyk.spring.ai.rag.knowledge.database.ai.agent.invoker.AgentInvoker;
import com.ranyk.spring.ai.rag.knowledge.database.ai.agent.model.AgentDefinition;
import com.ranyk.spring.ai.rag.knowledge.database.ai.agent.registry.AgentRegistry;
import com.ranyk.spring.ai.rag.knowledge.database.config.properties.AgentProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CLASS_NAME: AgentConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Agents 配置类 - 负责注册 Agents 相关的 Bean
 * @date: 2026-07-06
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "ai.agent", name = "enabled", havingValue = "true")
public class AgentConfiguration {
    
    private final AgentProperties agentProperties;
    
    /**
     * 注册 AgentRegistry
     */
    @Bean
    public AgentRegistry agentRegistry() {
        log.info("初始化 AgentRegistry");
        return new AgentRegistry();
    }
    
    /**
     * 注册 AgentInvoker
     */
    @Bean
    public AgentInvoker agentInvoker(AgentRegistry agentRegistry) {
        log.info("初始化 AgentInvoker");
        AgentInvoker invoker = new AgentInvoker(agentRegistry, agentProperties);
        
        // 从配置文件注册子 Agents
        registerSubAgents(agentRegistry);
        
        return invoker;
    }
    
    /**
     * 从配置文件注册子 Agents
     */
    private void registerSubAgents(AgentRegistry agentRegistry) {
        if (agentProperties.getSubAgents() == null || agentProperties.getSubAgents().isEmpty()) {
            log.info("未配置子 Agents");
            return;
        }
        
        log.info("开始注册子 Agents,共 {} 个", agentProperties.getSubAgents().size());
        
        for (AgentProperties.SubAgent subAgent : agentProperties.getSubAgents()) {
            if (!subAgent.getEnabled()) {
                log.debug("跳过未启用的 Agent: {}", subAgent.getName());
                continue;
            }
            
            AgentDefinition definition = AgentDefinition.builder()
                    .name(subAgent.getName())
                    .type(subAgent.getType())
                    .description(subAgent.getDescription())
                    .tools(subAgent.getTools())
                    .enabled(subAgent.getEnabled())
                    .maxRetries(subAgent.getMaxRetries())
                    .timeoutSeconds(subAgent.getTimeoutSeconds())
                    .build();
            
            agentRegistry.registerAgent(definition);
        }
        
        log.info("子 Agents 注册完成,共注册 {} 个 Agents", agentRegistry.getAgentCount());
    }
}
