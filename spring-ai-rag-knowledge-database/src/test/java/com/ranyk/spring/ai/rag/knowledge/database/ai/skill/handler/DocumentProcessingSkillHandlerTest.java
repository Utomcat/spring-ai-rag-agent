package com.ranyk.spring.ai.rag.knowledge.database.ai.skill.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CLASS_NAME: DocumentProcessingSkillHandlerTest.java
 *
 * @author ranyk
 * @version V1.0
 * @description: DocumentProcessingSkillHandler 单元测试
 * @date: 2026-07-06
 */
@DisplayName("DocumentProcessingSkillHandler 测试")
class DocumentProcessingSkillHandlerTest {

    private DocumentProcessingSkillHandler handler;
    
    @TempDir
    Path tempDir;
    
    private File testTxtFile;
    private File testMdFile;

    @BeforeEach
    void setUp() throws Exception {
        handler = new DocumentProcessingSkillHandler();
        
        // 创建测试文件
        testTxtFile = tempDir.resolve("test.txt").toFile();
        Files.writeString(testTxtFile.toPath(), "这是测试文本内容\n第二行内容");
        
        testMdFile = tempDir.resolve("test.md").toFile();
        Files.writeString(testMdFile.toPath(), "# 标题\n\n这是Markdown内容");
    }

    @Test
    @DisplayName("测试处理 TXT 文件 - 成功")
    void testExecute_TxtFile_Success() {
        // Given
        Map<String, Object> params = new HashMap<>();
        params.put("file_path", testTxtFile.getAbsolutePath());
        params.put("output_format", "text");

        // When
        Object result = handler.execute(params);

        // Then
        assertNotNull(result);
        assertTrue(result instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> resultMap = (Map<String, Object>) result;
        assertEquals("test.txt", resultMap.get("file_name"));
        assertEquals("Text", resultMap.get("file_type"));
        assertTrue(((String) resultMap.get("content")).contains("这是测试文本内容"));
    }

    @Test
    @DisplayName("测试处理 MD 文件 - 成功")
    void testExecute_MdFile_Success() {
        // Given
        Map<String, Object> params = new HashMap<>();
        params.put("file_path", testMdFile.getAbsolutePath());
        params.put("output_format", "markdown");

        // When
        Object result = handler.execute(params);

        // Then
        assertNotNull(result);
        assertTrue(result instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> resultMap = (Map<String, Object>) result;
        assertEquals("test.md", resultMap.get("file_name"));
        assertEquals("Markdown", resultMap.get("file_type"));
        assertTrue(((String) resultMap.get("content")).contains("# test.md"));
    }

    @Test
    @DisplayName("测试提取元数据")
    void testExecute_WithMetadata() {
        // Given
        Map<String, Object> params = new HashMap<>();
        params.put("file_path", testTxtFile.getAbsolutePath());
        params.put("extract_metadata", true);

        // When
        Object result = handler.execute(params);

        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> resultMap = (Map<String, Object>) result;
        assertTrue(resultMap.containsKey("metadata"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> metadata = (Map<String, Object>) resultMap.get("metadata");
        assertTrue(metadata.containsKey("last_modified"));
        assertTrue(metadata.containsKey("can_read"));
        assertTrue(metadata.containsKey("absolute_path"));
    }

    @Test
    @DisplayName("测试缺少 file_path 参数")
    void testExecute_MissingFilePath() {
        // Given
        Map<String, Object> params = new HashMap<>();

        // When & Then - 实际抛出 RuntimeException (因为内部捕获并重新包装)
        assertThrows(RuntimeException.class, () -> {
            handler.execute(params);
        });
    }

    @Test
    @DisplayName("测试文件不存在")
    void testExecute_FileNotFound() {
        // Given
        Map<String, Object> params = new HashMap<>();
        params.put("file_path", "/non/existent/file.txt");

        // When & Then - 实际抛出 RuntimeException (因为内部捕获并重新包装)
        assertThrows(RuntimeException.class, () -> {
            handler.execute(params);
        });
    }

    @Test
    @DisplayName("测试不支持的文件格式")
    void testExecute_UnsupportedFormat() throws Exception {
        // Given
        File unsupportedFile = tempDir.resolve("test.pdf").toFile();
        Files.writeString(unsupportedFile.toPath(), "PDF content");
        
        Map<String, Object> params = new HashMap<>();
        params.put("file_path", unsupportedFile.getAbsolutePath());

        // When & Then
        // PDF 目前返回占位符,不抛异常
        Object result = handler.execute(params);
        assertNotNull(result);
    }

    @Test
    @DisplayName("测试默认输出格式为 text")
    void testExecute_DefaultOutputFormat() {
        // Given
        Map<String, Object> params = new HashMap<>();
        params.put("file_path", testTxtFile.getAbsolutePath());
        // 不指定 output_format

        // When
        Object result = handler.execute(params);

        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> resultMap = (Map<String, Object>) result;
        assertTrue(((String) resultMap.get("content")).contains("这是测试文本内容"));
    }

    @Test
    @DisplayName("测试获取文件类型")
    void testGetFileType() throws Exception {
        // 通过反射测试私有方法不太合适,这里通过执行结果间接验证
        Map<String, Object> params = new HashMap<>();
        params.put("file_path", testTxtFile.getAbsolutePath());
        
        Object result = handler.execute(params);
        @SuppressWarnings("unchecked")
        Map<String, Object> resultMap = (Map<String, Object>) result;
        
        assertEquals("Text", resultMap.get("file_type"));
    }
}
