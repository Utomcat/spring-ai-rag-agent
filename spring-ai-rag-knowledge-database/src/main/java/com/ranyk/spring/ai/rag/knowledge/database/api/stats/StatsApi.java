package com.ranyk.spring.ai.rag.knowledge.database.api.stats;

import com.ranyk.spring.ai.rag.knowledge.database.base.domain.vo.Result;
import com.ranyk.spring.ai.rag.knowledge.database.service.stats.StatsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * CLASS_NAME: StatsApi.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 管理后台仪表盘统计数据 API 接口类
 * @date: 2026-06-28
 */
@RestController
@RequestMapping("/api/stats")
public class StatsApi {

    /**
     * 统计服务业务逻辑对象
     */
    private final StatsService statsService;

    /**
     * 构造函数 - 由 Spring IOC 容器在创建当前 Bean 对象实例时,自动依赖注入相关的 Bean 对象
     *
     * @param statsService 统计服务业务逻辑对象
     */
    public StatsApi(StatsService statsService) {
        this.statsService = statsService;
    }

    /**
     * 获取仪表盘 overview 统计数据
     *
     * @return 统计数据
     */
    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, Object>> overview() {
        return Result.success(statsService.overview());
    }
}
