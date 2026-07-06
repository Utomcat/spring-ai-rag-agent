package com.ranyk.spring.ai.rag.knowledge.database.ai.skill.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * CLASS_NAME: CodeGenerationSkillHandler.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 代码生成 Skill 处理器 - 根据描述生成 Java/Python/SQL 代码模板
 * @date: 2026-07-06
 */
@Slf4j
@Component
public class CodeGenerationSkillHandler implements SkillHandler {
    
    @Override
    public Object execute(Map<String, Object> params) {
        log.info("执行代码生成 Skill, 参数: {}", params);
        
        try {
            // 获取参数
            String description = (String) params.get("description");
            String language = (String) params.getOrDefault("language", "java");
            String framework = (String) params.getOrDefault("framework", "spring-boot");
            
            if (description == null || description.isEmpty()) {
                throw new IllegalArgumentException("description 参数不能为空");
            }
            
            // 生成代码
            String code = generateCode(description, language, framework);
            
            // 构建结果
            Map<String, Object> result = new HashMap<>();
            result.put("code", code);
            result.put("language", language);
            result.put("framework", framework);
            result.put("description", description);
            
            log.info("代码生成完成,语言: {}, 框架: {}", language, framework);
            return result;
            
        } catch (Exception e) {
            log.error("代码生成失败", e);
            throw new RuntimeException("代码生成失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 根据语言和框架生成代码模板
     */
    private String generateCode(String description, String language, String framework) {
        return switch (language.toLowerCase()) {
            case "java" -> generateJavaCode(description, framework);
            case "python" -> generatePythonCode(description, framework);
            case "sql" -> generateSQLCode(description);
            default -> throw new IllegalArgumentException("不支持的编程语言: " + language);
        };
    }
    
    /**
     * 生成 Java 代码
     */
    private String generateJavaCode(String description, String framework) {
        StringBuilder sb = new StringBuilder();
        
        if ("spring-boot".equals(framework)) {
            sb.append("// Spring Boot REST Controller\n");
            sb.append("@RestController\n");
            sb.append("@RequestMapping(\"/api\")\n");
            sb.append("public class GeneratedController {\n\n");
            sb.append("    /**\n");
            sb.append("     * ").append(description).append("\n");
            sb.append("     */\n");
            sb.append("    @GetMapping(\"/endpoint\")\n");
            sb.append("    public ResponseEntity<String> handleRequest() {\n");
            sb.append("        // TODO: 实现业务逻辑\n");
            sb.append("        return ResponseEntity.ok(\"Success\");\n");
            sb.append("    }\n");
            sb.append("}\n");
        } else if ("mybatis".equals(framework)) {
            sb.append("// MyBatis Mapper Interface\n");
            sb.append("@Mapper\n");
            sb.append("public interface GeneratedMapper {\n\n");
            sb.append("    /**\n");
            sb.append("     * ").append(description).append("\n");
            sb.append("     */\n");
            sb.append("    @Select(\"SELECT * FROM table_name WHERE condition = #{param}\")\n");
            sb.append("    List<Map<String, Object>> queryData(@Param(\"param\") String param);\n");
            sb.append("}\n");
        } else {
            sb.append("// Java Class\n");
            sb.append("public class GeneratedClass {\n\n");
            sb.append("    /**\n");
            sb.append("     * ").append(description).append("\n");
            sb.append("     */\n");
            sb.append("    public void execute() {\n");
            sb.append("        // TODO: 实现业务逻辑\n");
            sb.append("    }\n");
            sb.append("}\n");
        }
        
        return sb.toString();
    }
    
    /**
     * 生成 Python 代码
     */
    private String generatePythonCode(String description, String framework) {
        StringBuilder sb = new StringBuilder();
        
        if ("flask".equals(framework)) {
            sb.append("# Flask REST API\n");
            sb.append("from flask import Flask, jsonify\n\n");
            sb.append("app = Flask(__name__)\n\n");
            sb.append("@app.route('/api/endpoint')\n");
            sb.append("def handle_request():\n");
            sb.append("    \"\"\"\n");
            sb.append("    ").append(description).append("\n");
            sb.append("    \"\"\"\n");
            sb.append("    # TODO: 实现业务逻辑\n");
            sb.append("    return jsonify({'status': 'success'})\n\n");
            sb.append("if __name__ == '__main__':\n");
            sb.append("    app.run(debug=True)\n");
        } else if ("django".equals(framework)) {
            sb.append("# Django View\n");
            sb.append("from django.http import JsonResponse\n\n");
            sb.append("def generated_view(request):\n");
            sb.append("    \"\"\"\n");
            sb.append("    ").append(description).append("\n");
            sb.append("    \"\"\"\n");
            sb.append("    # TODO: 实现业务逻辑\n");
            sb.append("    return JsonResponse({'status': 'success'})\n");
        } else {
            sb.append("# Python Function\n");
            sb.append("def generated_function():\n");
            sb.append("    \"\"\"\n");
            sb.append("    ").append(description).append("\n");
            sb.append("    \"\"\"\n");
            sb.append("    # TODO: 实现业务逻辑\n");
            sb.append("    pass\n");
        }
        
        return sb.toString();
    }
    
    /**
     * 生成 SQL 代码
     */
    private String generateSQLCode(String description) {

        return "-- SQL Query\n" +
                "-- " + description + "\n\n" +
                "SELECT \n" +
                "    column1,\n" +
                "    column2,\n" +
                "    COUNT(*) as total\n" +
                "FROM table_name\n" +
                "WHERE condition = 'value'\n" +
                "GROUP BY column1, column2\n" +
                "HAVING total > 0\n" +
                "ORDER BY total DESC\n" +
                "LIMIT 100;\n";
    }
}
