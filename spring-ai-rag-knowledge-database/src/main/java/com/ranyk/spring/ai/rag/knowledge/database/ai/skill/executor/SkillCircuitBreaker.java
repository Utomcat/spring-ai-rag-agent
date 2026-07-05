package com.ranyk.spring.ai.rag.knowledge.database.ai.skill.executor;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * CLASS_NAME: SkillCircuitBreaker.java
 *
 * @author ranyk
 * @version V1.0
 * @description: Skill 熔断器 - 防止故障服务雪崩
 * @date: 2026-07-06
 */
@Slf4j
public class SkillCircuitBreaker {
    
    /**
     * 熔断器状态枚举
     */
    public enum State {
        CLOSED,     // 关闭状态(正常)
        OPEN,       // 打开状态(熔断)
        HALF_OPEN   // 半开状态(探测恢复)
    }
    
    private final int failureThreshold;      // 失败阈值
    private final int successThreshold;      // 成功阈值(半开状态下需要连续成功的次数)
    private final long waitDurationMs;       // 等待时长(毫秒)
    private final double failureRateThreshold; // 失败率阈值(0-1)
    private final int slidingWindowSize;     // 滑动窗口大小
    
    /**
     * 每个 Skill 的熔断器状态
     */
    private final Map<String, CircuitState> circuitStates = new ConcurrentHashMap<>();
    
    /**
     * 构造函数
     *
     * @param failureThreshold    失败次数阈值
     * @param successThreshold    成功次数阈值
     * @param waitDurationMs      等待时长(毫秒)
     * @param failureRateThreshold 失败率阈值
     * @param slidingWindowSize   滑动窗口大小
     */
    public SkillCircuitBreaker(int failureThreshold, int successThreshold, 
                               long waitDurationMs, double failureRateThreshold, 
                               int slidingWindowSize) {
        this.failureThreshold = failureThreshold;
        this.successThreshold = successThreshold;
        this.waitDurationMs = waitDurationMs;
        this.failureRateThreshold = failureRateThreshold;
        this.slidingWindowSize = slidingWindowSize;
    }
    
    /**
     * 执行带熔断保护的操作
     *
     * @param skillId   Skill ID
     * @param operation 要执行的操作
     * @param <T>       返回值类型
     * @return 操作结果
     */
    public <T> T execute(String skillId, Supplier<T> operation) {
        CircuitState state = getCircuitState(skillId);
        
        // 检查是否需要转换状态
        checkStateTransition(state);
        
        // 根据状态决定是否执行
        if (state.getState() == State.OPEN) {
            log.warn("Skill [{}] 熔断器已打开,拒绝执行", skillId);
            throw new CircuitBreakerOpenException("Skill [" + skillId + "] 熔断器已打开");
        }
        
        try {
            // 执行操作
            T result = operation.get();
            
            // 记录成功
            recordSuccess(state);
            
            return result;
            
        } catch (Exception e) {
            // 记录失败
            recordFailure(state);
            
            throw e;
        }
    }
    
    /**
     * 获取或创建熔断器状态
     *
     * @param skillId Skill ID
     * @return 熔断器状态
     */
    private CircuitState getCircuitState(String skillId) {
        return circuitStates.computeIfAbsent(skillId, k -> new CircuitState());
    }
    
    /**
     * 检查状态转换
     *
     * @param state 当前状态
     */
    private void checkStateTransition(CircuitState state) {
        synchronized (state) {
            switch (state.getState()) {
                case CLOSED:
                    // 检查是否需要打开熔断器
                    if (shouldOpen(state)) {
                        state.setState(State.OPEN);
                        state.setOpenedAt(LocalDateTime.now());
                        log.warn("熔断器打开: {}", state);
                    }
                    break;
                    
                case OPEN:
                    // 检查是否可以进入半开状态
                    if (canHalfOpen(state)) {
                        state.setState(State.HALF_OPEN);
                        state.setHalfOpenedAt(LocalDateTime.now());
                        state.setConsecutiveSuccesses(0);
                        log.info("熔断器进入半开状态: {}", state);
                    }
                    break;
                    
                case HALF_OPEN:
                    // 半开状态下,如果连续成功达到阈值,则关闭熔断器
                    if (state.getConsecutiveSuccesses() >= successThreshold) {
                        state.setState(State.CLOSED);
                        state.reset();
                        log.info("熔断器关闭: {}", state);
                    }
                    break;
            }
        }
    }
    
    /**
     * 判断是否应该打开熔断器
     *
     * @param state 状态
     * @return 是否应该打开
     */
    private boolean shouldOpen(CircuitState state) {
        // 失败次数超过阈值
        if (state.getFailureCount() >= failureThreshold) {
            return true;
        }
        
        // 失败率超过阈值
        if (state.getTotalCount() >= slidingWindowSize) {
            double failureRate = (double) state.getFailureCount() / state.getTotalCount();
            return failureRate >= failureRateThreshold;
        }
        
        return false;
    }
    
    /**
     * 判断是否可以进入半开状态
     *
     * @param state 状态
     * @return 是否可以进入半开状态
     */
    private boolean canHalfOpen(CircuitState state) {
        if (state.getOpenedAt() == null) {
            return false;
        }
        
        long elapsed = java.time.Duration.between(state.getOpenedAt(), LocalDateTime.now()).toMillis();
        return elapsed >= waitDurationMs;
    }
    
    /**
     * 记录成功
     *
     * @param state 状态
     */
    private void recordSuccess(CircuitState state) {
        synchronized (state) {
            state.incrementSuccess();
            state.incrementTotal();
            
            if (state.getState() == State.HALF_OPEN) {
                state.incrementConsecutiveSuccesses();
            }
            
            // 保持滑动窗口大小
            maintainSlidingWindow(state);
        }
    }
    
    /**
     * 记录失败
     *
     * @param state 状态
     */
    private void recordFailure(CircuitState state) {
        synchronized (state) {
            state.incrementFailure();
            state.incrementTotal();
            
            if (state.getState() == State.HALF_OPEN) {
                // 半开状态下失败,立即回到打开状态
                state.setState(State.OPEN);
                state.setOpenedAt(LocalDateTime.now());
                log.warn("半开状态下失败,熔断器重新打开");
            }
            
            // 保持滑动窗口大小
            maintainSlidingWindow(state);
        }
    }
    
    /**
     * 维护滑动窗口
     *
     * @param state 状态
     */
    private void maintainSlidingWindow(CircuitState state) {
        if (state.getTotalCount() > slidingWindowSize * 2) {
            // 简化处理:当总数超过窗口大小的2倍时,减半计数
            state.setSuccessCount(state.getSuccessCount() / 2);
            state.setFailureCount(state.getFailureCount() / 2);
            state.setTotalCount(state.getTotalCount() / 2);
        }
    }
    
    /**
     * 获取熔断器状态
     *
     * @param skillId Skill ID
     * @return 状态
     */
    public State getState(String skillId) {
        CircuitState state = circuitStates.get(skillId);
        return state != null ? state.getState() : State.CLOSED;
    }
    
    /**
     * 重置熔断器
     *
     * @param skillId Skill ID
     */
    public void reset(String skillId) {
        CircuitState state = circuitStates.get(skillId);
        if (state != null) {
            state.reset();
            log.info("熔断器已重置: {}", skillId);
        }
    }
    
    /**
     * 创建默认的熔断器
     *
     * @return 默认熔断器
     */
    public static SkillCircuitBreaker createDefault() {
        return new SkillCircuitBreaker(
                5,          // 失败5次触发熔断
                3,          // 半开状态下连续成功3次关闭熔断器
                60000,      // 熔断后等待60秒进入半开状态
                0.5,        // 失败率超过50%触发熔断
                20          // 滑动窗口大小20
        );
    }
    
    /**
     * 熔断器打开异常
     */
    public static class CircuitBreakerOpenException extends RuntimeException {
        public CircuitBreakerOpenException(String message) {
            super(message);
        }
    }
    
    /**
     * 熔断器状态内部类
     */
    private static class CircuitState {
        private State state = State.CLOSED;
        private int successCount = 0;
        private int failureCount = 0;
        private int totalCount = 0;
        private int consecutiveSuccesses = 0;
        private LocalDateTime openedAt;
        private LocalDateTime halfOpenedAt;
        
        public State getState() {
            return state;
        }
        
        public void setState(State state) {
            this.state = state;
        }
        
        public int getSuccessCount() {
            return successCount;
        }
        
        public void setSuccessCount(int successCount) {
            this.successCount = successCount;
        }
        
        public int getFailureCount() {
            return failureCount;
        }
        
        public void setFailureCount(int failureCount) {
            this.failureCount = failureCount;
        }
        
        public int getTotalCount() {
            return totalCount;
        }
        
        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }
        
        public int getConsecutiveSuccesses() {
            return consecutiveSuccesses;
        }
        
        public void setConsecutiveSuccesses(int consecutiveSuccesses) {
            this.consecutiveSuccesses = consecutiveSuccesses;
        }
        
        public LocalDateTime getOpenedAt() {
            return openedAt;
        }
        
        public void setOpenedAt(LocalDateTime openedAt) {
            this.openedAt = openedAt;
        }
        
        public LocalDateTime getHalfOpenedAt() {
            return halfOpenedAt;
        }
        
        public void setHalfOpenedAt(LocalDateTime halfOpenedAt) {
            this.halfOpenedAt = halfOpenedAt;
        }
        
        public void incrementSuccess() {
            this.successCount++;
        }
        
        public void incrementFailure() {
            this.failureCount++;
        }
        
        public void incrementTotal() {
            this.totalCount++;
        }
        
        public void incrementConsecutiveSuccesses() {
            this.consecutiveSuccesses++;
        }
        
        public void reset() {
            this.state = State.CLOSED;
            this.successCount = 0;
            this.failureCount = 0;
            this.totalCount = 0;
            this.consecutiveSuccesses = 0;
            this.openedAt = null;
            this.halfOpenedAt = null;
        }
        
        @Override
        public String toString() {
            return String.format("CircuitState{state=%s, success=%d, failure=%d, total=%d}", 
                    state, successCount, failureCount, totalCount);
        }
    }
}
