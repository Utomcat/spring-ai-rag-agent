package com.ranyk.spring.ai.rag.knowledge.database.service.stats;

import com.ranyk.spring.ai.rag.knowledge.database.repository.stats.StatsRepository;
import com.ranyk.spring.ai.rag.knowledge.database.service.chat.message.ChatMessageService;
import com.ranyk.spring.ai.rag.knowledge.database.service.document.DocumentService;
import com.ranyk.spring.ai.rag.knowledge.database.service.user.AppUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

/**
 * CLASS_NAME: StatsService.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 汇总统计业务逻辑类
 * @date: 2026-06-28
 */
@Slf4j
@Service
@SuppressWarnings({"unused"})
public class StatsService {

    /**
     * 系统用户业务逻辑类
     */
    private final AppUserService appUserService;
    /**
     * 文档业务逻辑类
     */
    private final DocumentService documentService;
    /**
     * 聊天消息业务逻辑类
     */
    private final ChatMessageService chatMessageService;
    /**
     * 统计数据仓库
     */
    private final StatsRepository statsRepository;

    /**
     * 统计服务类的构造方法 - 由 Spring IOC 容器在创建当前 Bean 实例对象时,自动依赖注入相关的 Bean 实例
     *
     * @param appUserService     系统用户业务逻辑类
     * @param documentService    文档业务逻辑类
     * @param chatMessageService 聊天消息业务逻辑类
     * @param statsRepository    统计数据仓库
     */
    @Autowired
    public StatsService(AppUserService appUserService,
                        DocumentService documentService,
                        ChatMessageService chatMessageService,
                        StatsRepository statsRepository) {
        this.appUserService = appUserService;
        this.documentService = documentService;
        this.chatMessageService = chatMessageService;
        this.statsRepository = statsRepository;
    }

    /**
     * 获取汇总统计数据
     *
     * @return 汇总统计数据
     */
    public Map<String, Object> overview() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("userTotal", appUserService.count());
        m.put("documentTotal", documentService.count());
        Integer allVectorChunks = documentService.countAllVectorChunks();
        m.put("vectorTotal", Objects.nonNull(allVectorChunks) ? allVectorChunks : 0);
        m.put("qaToday", chatMessageService.countTodayAssistantMessages());
        m.put("qaByDay", fillLast7Days(chatMessageService.countAssistantByDayLast7()));
        m.put("categoryDocShare", documentService.countGroupByCategory());
        m.put("userRegByDay", fillLast7Days(appUserService.countUserRegByDayLast7()));
        return m;
    }

    /**
     * 将近 7 日统计补全为连续日期（无数据则为 0）
     *
     * @param rows 近 7 日统计数据
     * @return 补全后的统计数据
     */
    private static List<Map<String, String>> fillLast7Days(List<Map<String, Long>> rows) {
        Map<String, Long> byDay = new LinkedHashMap<>();
        if (rows != null) {
            for (Map<String, Long> r : rows) {
                Object k = r.get("dayKey");
                Number c = r.get("cnt");
                if (k != null && c != null) {
                    byDay.put(k.toString(), c.longValue());
                }
            }
        }
        List<Map<String, String>> out = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            String key = today.minusDays(i).toString();
            Map<String, String> one = new LinkedHashMap<>();
            one.put("date", key);
            one.put("count", String.valueOf(byDay.getOrDefault(key, 0L)));
            out.add(one);
        }
        return out;
    }
}
