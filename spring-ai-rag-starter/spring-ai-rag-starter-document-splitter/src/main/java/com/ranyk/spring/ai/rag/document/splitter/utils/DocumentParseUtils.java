package com.ranyk.spring.ai.rag.document.splitter.utils;

import com.ranyk.spring.ai.rag.common.constant.FileCategoryEnum;
import com.ranyk.spring.ai.rag.common.constant.FileTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * CLASS_NAME: DocumentParseUtils.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 文档解析工具类
 * @date: 2026-06-25
 */
@Slf4j
@SuppressWarnings("unused")
public class DocumentParseUtils {

    /**
     * TikaDocumentReader 支持的文件后缀集合
     */
    private static final Set<String> TIKA_SUPPORTED_SUFFIXES = Set.copyOf(
            FileTypeEnum.getSuffixesByCategory(FileCategoryEnum.DOCUMENT, FileTypeEnum.MARKDOWN)
    );

    /**
     * MarkdownDocumentReader 支持的文件后缀集合
     */
    private static final Set<String> MARKDOWN_SUPPORTED_SUFFIXES = Set.copyOf(
            FileTypeEnum.MARKDOWN.getSuffix()
    );

    /**
     * 文件解析方法 - 单个文件解析
     *
     * @param filePath 文件路径
     * @return 解析后的文档列表
     */
    public static List<Document> parse(String filePath) {
        // 创建文件对象
        File file = new File(filePath);
        // 获取文件后缀
        String suffix = filePath.substring(filePath.lastIndexOf('.') + 1).toLowerCase();
        // 根据文件后缀使用匹配的文档解析器, 解析文档成 DocumentReader 对象
        DocumentReader reader = selectReader(suffix, file);
        // 返回解析后的 Document 对象 List 集合
        return reader.get();
    }

    /**
     * 文件解析方法 - 多个文件解析
     *
     * @param filePaths 文件路径数组
     * @return 解析后的文档列表
     */
    public static List<Document> parse(String[] filePaths) {
        // 创建文件对象列表
        List<File> files = Stream.of(filePaths).map(File::new).toList();
        // 创建文档解析器列表
        List<DocumentReader> readers = files.stream().map(file -> {
            // 获取文件后缀
            String suffix = file.getName().substring(file.getName().lastIndexOf('.') + 1).toLowerCase();
            // 根据文件后缀使用匹配的文档解析器, 解析文档成 DocumentReader 对象
            return selectReader(suffix, file);
        }).toList();
        // 返回解析后的 Document 对象 List 集合
        return readers.stream().flatMap(reader -> reader.get().stream()).toList();
    }

    /**
     * 根据文件后缀选择文档解析器
     *
     * @param suffix 文件后缀
     * @param file   文件对象
     * @return 文档解析器
     */
    private static DocumentReader selectReader(String suffix, File file) {
        return switch (suffix) {
            case String s when TIKA_SUPPORTED_SUFFIXES.contains(s) ->
                    new TikaDocumentReader(new FileSystemResource(file));
            case String s when MARKDOWN_SUPPORTED_SUFFIXES.contains(s) ->
                    new MarkdownDocumentReader(file.toURI().toString());
            default -> {
                log.info("文件格式 {} , 暂未开发解析处理, 敬请期待!", suffix);
                throw new IllegalArgumentException("文件格式 %s , 暂未开发解析处理, 敬请期待!".formatted(suffix));
            }
        };
    }


}
