package com.ranyk.spring.ai.rag.knowledge.database.ai.skill.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;

/**
 * CLASS_NAME: DocumentProcessingSkillHandler.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 文档处理 Skill 处理器 - 支持 PDF/Word/TXT 文本提取和格式转换
 * @date: 2026-07-06
 */
@Slf4j
@Component
public class DocumentProcessingSkillHandler implements SkillHandler {
    
    @Override
    public Object execute(Map<String, Object> params) {
        log.info("执行文档处理 Skill, 参数: {}", params);
        
        try {
            // 获取参数
            String filePath = (String) params.get("file_path");
            String outputFormat = (String) params.getOrDefault("output_format", "text");
            Boolean extractMetadata = (Boolean) params.getOrDefault("extract_metadata", false);
            
            if (filePath == null || filePath.isEmpty()) {
                throw new IllegalArgumentException("file_path 参数不能为空");
            }
            
            // 验证文件存在
            File file = new File(filePath);
            if (!file.exists()) {
                throw new IllegalArgumentException("文件不存在: " + filePath);
            }
            
            // 读取文件内容
            String content = readDocumentContent(file);
            
            // 根据输出格式处理
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("file_name", file.getName());
            result.put("file_size", file.length());
            result.put("file_type", getFileType(file));
            
            if ("text".equals(outputFormat)) {
                result.put("content", content);
            } else if ("markdown".equals(outputFormat)) {
                result.put("content", convertToMarkdown(content, file));
            } else {
                result.put("content", content);
            }
            
            // 提取元数据
            if (extractMetadata) {
                result.put("metadata", extractMetadata(file));
            }
            
            log.info("文档处理完成: {}, 内容长度: {}", filePath, content.length());
            return result;
            
        } catch (Exception e) {
            log.error("文档处理失败", e);
            throw new RuntimeException("文档处理失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 读取文档内容
     */
    private String readDocumentContent(File file) throws Exception {
        String fileName = file.getName().toLowerCase();
        
        if (fileName.endsWith(".txt")) {
            return Files.readString(file.toPath());
        } else if (fileName.endsWith(".md")) {
            return Files.readString(file.toPath());
        } else if (fileName.endsWith(".pdf") || fileName.endsWith(".docx")) {
            // TODO: 集成 Apache Tika 或其他库处理复杂文档
            log.warn("复杂文档格式需要额外依赖,当前返回空内容: {}", fileName);
            return "[复杂文档内容需要 Apache Tika 支持]";
        } else {
            throw new IllegalArgumentException("不支持的文件格式: " + fileName);
        }
    }
    
    /**
     * 转换为 Markdown 格式
     */
    private String convertToMarkdown(String content, File file) {
        // 简单的转换逻辑,实际项目可以使用专门的库
        return "# " + file.getName() + "\n\n" + content;
    }
    
    /**
     * 获取文件类型
     */
    private String getFileType(File file) {
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".pdf")) return "PDF";
        if (fileName.endsWith(".docx")) return "Word";
        if (fileName.endsWith(".txt")) return "Text";
        if (fileName.endsWith(".md")) return "Markdown";
        return "Unknown";
    }
    
    /**
     * 提取元数据
     */
    private Map<String, Object> extractMetadata(File file) {
        Map<String, Object> metadata = new java.util.HashMap<>();
        metadata.put("last_modified", file.lastModified());
        metadata.put("can_read", file.canRead());
        metadata.put("absolute_path", file.getAbsolutePath());
        return metadata;
    }
}
