package com.ranyk.spring.ai.rag.knowledge.database.ai.skill.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CLASS_NAME: DatabaseQuerySkillHandler.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 数据库查询 Skill 处理器 - 执行 SQL 查询并返回格式化结果
 * @date: 2026-07-06
 */
@Slf4j
@Component
public class DatabaseQuerySkillHandler implements SkillHandler {
    
    // TODO: 实际项目中应该从配置注入数据源
    private static final String DEFAULT_JDBC_URL = "jdbc:mysql://localhost:3306/test";
    private static final String DEFAULT_USERNAME = "root";
    private static final String DEFAULT_PASSWORD = "";
    
    @Override
    public Object execute(Map<String, Object> params) {
        log.info("执行数据库查询 Skill, 参数: {}", params);
        
        try {
            // 获取参数
            String sql = (String) params.get("sql");
            String jdbcUrl = (String) params.getOrDefault("jdbc_url", DEFAULT_JDBC_URL);
            String username = (String) params.getOrDefault("username", DEFAULT_USERNAME);
            String password = (String) params.getOrDefault("password", DEFAULT_PASSWORD);
            Integer maxRows = (Integer) params.getOrDefault("max_rows", 100);
            
            if (sql == null || sql.isEmpty()) {
                throw new IllegalArgumentException("sql 参数不能为空");
            }
            
            // 安全检查:只允许 SELECT 查询
            if (!isSelectQuery(sql)) {
                throw new IllegalArgumentException("只允许执行 SELECT 查询");
            }
            
            // 执行查询
            List<Map<String, Object>> results = executeQuery(sql, jdbcUrl, username, password, maxRows);
            
            // 构建结果
            Map<String, Object> result = new HashMap<>();
            result.put("row_count", results.size());
            result.put("data", results);
            result.put("query", sql);
            
            log.info("数据库查询完成,返回 {} 行数据", results.size());
            return result;
            
        } catch (Exception e) {
            log.error("数据库查询失败", e);
            throw new RuntimeException("数据库查询失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 检查是否为 SELECT 查询
     */
    private boolean isSelectQuery(String sql) {
        String trimmed = sql.trim().toUpperCase();
        return trimmed.startsWith("SELECT") || trimmed.startsWith("WITH");
    }
    
    /**
     * 执行 SQL 查询
     */
    private List<Map<String, Object>> executeQuery(String sql, String jdbcUrl, 
                                                    String username, String password, 
                                                    int maxRows) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             Statement stmt = conn.createStatement()) {
            
            // 设置最大行数限制
            stmt.setMaxRows(maxRows);
            
            try (ResultSet rs = stmt.executeQuery(sql)) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnLabel(i);
                        Object value = rs.getObject(i);
                        row.put(columnName, value);
                    }
                    results.add(row);
                }
            }
        }
        
        return results;
    }
}
