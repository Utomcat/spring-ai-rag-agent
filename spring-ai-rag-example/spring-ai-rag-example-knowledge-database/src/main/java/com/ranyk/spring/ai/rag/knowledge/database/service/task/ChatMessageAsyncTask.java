package com.ranyk.spring.ai.rag.knowledge.database.service.task;

import cn.hutool.core.util.StrUtil;
import com.ranyk.spring.ai.rag.knowledge.database.service.chat.message.ChatMessageService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * CLASS_NAME: ChatMessageAsyncTask.java
 *
 * @param sessionId          会话ID
 * @param userId             用户ID
 * @param question           用户问题
 * @param answer             助手回答
 * @param referencesJson     引用文档JSON
 * @param now                当前时间
 * @param chatMessageService 聊天消息业务逻辑处理类
 * @param transactionManager 事务管理器
 * @author ranyk
 * @version V1.0
 * @description: 聊天消息保存异步任务, 实现 Runnable 接口, 用于异步保存聊天信息, 并更新会话时间
 * @date: 2026-07-14
 */
@Slf4j
@Builder
public record ChatMessageAsyncTask(Long sessionId,
                                   Long userId,
                                   String question,
                                   String answer,
                                   String referencesJson,
                                   LocalDateTime now,
                                   ChatMessageService chatMessageService,
                                   PlatformTransactionManager transactionManager) implements Runnable {

    /**
     * Runs this operation.
     */
    @Override
    public void run() {
        TransactionStatus status = null;
        try {
            // 使用编程式事务确保两个数据库操作的原子性
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
            def.setTimeout(30);
            // 开始事务
            status = transactionManager.getTransaction(def);

            // 保存聊天消息
            chatMessageService.saveChatMessages(sessionId, userId, question, answer, referencesJson, now);
            log.debug("异步保存聊天消息完成 sessionId => {}, userId => {}", sessionId, userId);

            // 更新会话时间
            chatMessageService.updateSessionUpdateTime(sessionId, userId, now);
            log.debug("异步更新会话时间完成 sessionId => {}, userId => {}", sessionId, userId);
            // 提交事务
            transactionManager.commit(status);
            log.info("异步保存聊天消息成功 sessionId => {}, userId => {}, questionLength => {}, answerLength => {}", sessionId, userId, StrUtil.isNotBlank(question) ? question.length() : 0, StrUtil.isNotBlank(answer) ? answer.length() : 0);
        } catch (Exception e) {
            // 回滚事务
            if (Objects.nonNull(status) && !status.isCompleted()) {
                try {
                    transactionManager.rollback(status);
                    log.warn("异步保存聊天消息失败，已回滚事务, 当前的会话和会话消息信息为 sessionId => {}, userId => {}, question => {}, answer => {} ", sessionId, userId, question, answer);
                    log.warn("异步保存聊天消息失败，已回滚事务 errorMessage => {}", e.getMessage());
                    log.warn("异步保存聊天消息失败，已回滚事务, 当前的异常栈为: \n", e);
                } catch (Exception rollbackEx) {
                    log.warn("异步保存聊天消息失败，回滚事务也异常, 当前的会话和会话消息信息为 sessionId => {}, userId => {}, question => {}, answer => {} ", sessionId, userId, question, answer);
                    log.warn("异步保存聊天消息失败，回滚事务也异常 errorMessage => {}", e.getMessage());
                    log.warn("异步保存聊天消息失败，回滚事务也异常, 当前的异常栈为: \n", e);
                }
            } else {
                log.error("异步保存聊天消息失败，事务状态已完成无法回滚 sessionId => {}, userId => {}, question => {}, answer => {} ", sessionId, userId, question, answer);
                log.error("异步保存聊天消息失败，事务状态已完成无法回滚 errorMessage => {}", e.getMessage());
                log.error("异步保存聊天消息失败，事务状态已完成无法回滚 当前的异常栈为: \n", e);
            }
        }
    }
}
