package com.ranyk.spring.ai.rag.knowledge.database.common.constant;

import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * CLASS_NAME: FileTypeEnum.java
 *
 * @author ranyk
 * @version V2.0
 * @description: 文件类型常量枚举类 - 支持 RAG 知识库文档处理
 * @date: 2026-06-25
 */
@Getter
@SuppressWarnings("unused")
public enum FileTypeEnum {

    // ==================== 办公文档类型 ====================
    /**
     * 文本文件类型
     */
    TEXT("文本文件", FileCategoryEnum.DOCUMENT, List.of("txt", "text")),
    /**
     * Word文件类型
     */
    WORD("Word文件", FileCategoryEnum.DOCUMENT, List.of("doc", "docx")),
    /**
     * Excel文件类型
     */
    EXCEL("Excel文件", FileCategoryEnum.DOCUMENT, List.of("xls", "xlsx", "csv")),
    /**
     * PowerPoint文件类型
     */
    PPT("PowerPoint文件", FileCategoryEnum.DOCUMENT, List.of("ppt", "pptx")),
    /**
     * PDF文件类型
     */
    PDF("PDF文件", FileCategoryEnum.DOCUMENT, List.of("pdf")),
    /**
     * Markdown文件类型
     */
    MARKDOWN("Markdown文件", FileCategoryEnum.DOCUMENT, List.of("md", "markdown")),
    /**
     * RTF富文本文件类型
     */
    RTF("RTF富文本文件", FileCategoryEnum.DOCUMENT, List.of("rtf")),

    // ==================== Web与数据格式 ====================
    /**
     * HTML文件类型
     */
    HTML("HTML文件", FileCategoryEnum.WEB, List.of("html", "htm")),
    /**
     * JSON文件类型
     */
    JSON("JSON文件", FileCategoryEnum.DATA, List.of("json")),
    /**
     * YAML文件类型
     */
    YAML("YAML文件", FileCategoryEnum.DATA, List.of("yaml", "yml")),
    /**
     * XML文件类型
     */
    XML("XML文件", FileCategoryEnum.DATA, List.of("xml")),

    // ==================== 编程语言 ====================
    /**
     * Java代码文件类型
     */
    JAVA("Java代码文件", FileCategoryEnum.CODE, List.of("java")),
    /**
     * Python代码文件类型
     */
    PYTHON("Python代码文件", FileCategoryEnum.CODE, List.of("py", "pyw")),
    /**
     * JavaScript代码文件类型
     */
    JAVASCRIPT("JavaScript代码文件", FileCategoryEnum.CODE, List.of("js", "jsx", "mjs", "cjs")),
    /**
     * TypeScript代码文件类型
     */
    TYPESCRIPT("TypeScript代码文件", FileCategoryEnum.CODE, List.of("ts", "tsx")),
    /**
     * Go代码文件类型
     */
    GO("Go代码文件", FileCategoryEnum.CODE, List.of("go")),
    /**
     * C/C++代码文件类型
     */
    CPP("C/C++代码文件", FileCategoryEnum.CODE, List.of("c", "cpp", "h", "hpp")),
    /**
     * C#代码文件类型
     */
    CSHARP("C#代码文件", FileCategoryEnum.CODE, List.of("cs")),
    /**
     * PHP代码文件类型
     */
    PHP("PHP代码文件", FileCategoryEnum.CODE, List.of("php")),
    /**
     * Ruby代码文件类型
     */
    RUBY("Ruby代码文件", FileCategoryEnum.CODE, List.of("rb", "erb")),
    /**
     * Kotlin代码文件类型
     */
    KOTLIN("Kotlin代码文件", FileCategoryEnum.CODE, List.of("kt", "kts")),
    /**
     * Swift代码文件类型
     */
    SWIFT("Swift代码文件", FileCategoryEnum.CODE, List.of("swift")),
    /**
     * Rust代码文件类型
     */
    RUST("Rust代码文件", FileCategoryEnum.CODE, List.of("rs")),

    // ==================== 脚本语言 ====================
    /**
     * Shell脚本文件类型
     */
    SHELL("Shell脚本文件", FileCategoryEnum.SCRIPT, List.of("sh", "bash", "zsh")),
    /**
     * SQL脚本文件类型
     */
    SQL("SQL脚本文件", FileCategoryEnum.SCRIPT, List.of("sql")),
    /**
     * PowerShell脚本文件类型
     */
    POWERSHELL("PowerShell脚本文件", FileCategoryEnum.SCRIPT, List.of("ps1", "psm1", "psd1")),

    // ==================== 系统与配置文件 ====================
    /**
     * 日志文件类型
     */
    LOG("日志文件", FileCategoryEnum.SYSTEM, List.of("log")),
    /**
     * 配置文件类型
     */
    PROPERTIES("配置文件", FileCategoryEnum.SYSTEM, List.of("properties", "env", "ini", "conf", "cfg", "config")),

    // ==================== 多媒体文件 ====================
    /**
     * 图片文件类型(OCR场景)
     */
    IMAGE("图片文件", FileCategoryEnum.MEDIA, List.of("png", "jpg", "jpeg", "gif", "bmp", "webp", "svg")),

    // ==================== 压缩归档文件 ====================
    /**
     * 压缩文件类型
     */
    ARCHIVE("压缩文件", FileCategoryEnum.ARCHIVE, List.of("zip", "tar", "gz", "rar", "7z")),

    // ==================== 未知类型 ====================
    /**
     * 未知文件类型
     */
    UNKNOWN("未知文件类型", FileCategoryEnum.OTHER, Collections.emptyList());

    /**
     * 文件类型描述
     */
    private final String desc;

    /**
     * 文件类型分类
     */
    private final FileCategoryEnum category;

    /**
     * 文件类型后缀（统一小写，无重复）
     */
    private final List<String> suffix;

    /**
     * 构造方法 - 文件类型枚举对象
     *
     * @param desc     文件类型描述
     * @param category 文件类型分类
     * @param suffix   文件类型后缀
     */
    FileTypeEnum(String desc, FileCategoryEnum category, List<String> suffix) {
        this.desc = desc;
        this.category = category;
        this.suffix = suffix.stream()
                .map(String::toLowerCase)
                .distinct()
                .toList();
    }

    // ==================== 缓存优化 ====================

    /**
     * 后缀到文件类型的映射缓存（线程安全）
     */
    private static final Map<String, FileTypeEnum> SUFFIX_TO_TYPE_CACHE = new ConcurrentHashMap<>();

    /**
     * 分类到文件类型的映射缓存
     */
    private static final Map<FileCategoryEnum, List<FileTypeEnum>> CATEGORY_TO_TYPES_CACHE = new EnumMap<>(FileCategoryEnum.class);

    static {
        // 初始化后缀映射缓存
        for (FileTypeEnum type : values()) {
            for (String suffix : type.getSuffix()) {
                SUFFIX_TO_TYPE_CACHE.put(suffix.toLowerCase(), type);
            }
        }

        // 初始化分类映射缓存
        for (FileCategoryEnum category : FileCategoryEnum.values()) {
            List<FileTypeEnum> types = Arrays.stream(values())
                    .filter(type -> type.getCategory() == category)
                    .toList();
            CATEGORY_TO_TYPES_CACHE.put(category, types);
        }
    }

    /**
     * 通过后缀查找对应的文件类型（O(1) 复杂度，带缓存）
     *
     * @param suffix 文件后缀（不区分大小写，可带点号）
     * @return 匹配的文件类型, 未找到返回 {@link FileTypeEnum#UNKNOWN}
     */
    public static FileTypeEnum findBySuffix(String suffix) {
        if (suffix == null || suffix.isBlank()) {
            return UNKNOWN;
        }

        // 去除点号并转为小写
        String cleanSuffix = suffix.startsWith(".")
                ? suffix.substring(1).toLowerCase()
                : suffix.toLowerCase();

        return SUFFIX_TO_TYPE_CACHE.getOrDefault(cleanSuffix, UNKNOWN);
    }

    /**
     * 根据分类获取文件类型列表
     *
     * @param category 文件类型分类
     * @return 该分类下的所有文件类型
     */
    public static List<FileTypeEnum> getByCategory(FileCategoryEnum category) {
        return CATEGORY_TO_TYPES_CACHE.getOrDefault(category, Collections.emptyList());
    }

    /**
     * 获取指定分类的所有后缀列表
     *
     * @param category 文件类型分类
     * @return 去重后的后缀列表
     */
    public static List<String> getSuffixesByCategory(FileCategoryEnum category) {
        return CATEGORY_TO_TYPES_CACHE.getOrDefault(category, Collections.emptyList())
                .stream()
                .flatMap(type -> type.getSuffix().stream())
                .distinct()
                .toList();
    }

    /**
     * 根据分类获取文件类型列表（排除指定的文件类型）
     *
     * @param category    文件类型分类
     * @param ignoreTypes 要忽略的文件类型
     * @return 该分类下除忽略文件类型外的所有文件类型
     */
    public static List<String> getSuffixesByCategory(FileCategoryEnum category, FileTypeEnum... ignoreTypes) {
        return CATEGORY_TO_TYPES_CACHE.getOrDefault(category, Collections.emptyList())
                .stream()
                .filter(type -> !Arrays.asList(ignoreTypes).contains(type))
                .flatMap(type -> type.getSuffix().stream())
                .distinct()
                .toList();
    }

    /**
     * 获取所有文件类型的后缀列表（已去重）
     *
     * @return 所有后缀（小写）
     */
    public static List<String> getAllSuffixes() {
        return SUFFIX_TO_TYPE_CACHE.keySet().stream()
                .sorted()
                .toList();
    }

    /**
     * 批量通过后缀查找文件类型
     *
     * @param suffixes 文件后缀列表
     * @return 文件类型列表（去重）
     */
    public static Set<FileTypeEnum> findBySuffixes(Collection<String> suffixes) {
        if (suffixes == null || suffixes.isEmpty()) {
            return Collections.emptySet();
        }

        return suffixes.stream()
                .map(FileTypeEnum::findBySuffix)
                .filter(type -> type != UNKNOWN)
                .collect(Collectors.toSet());
    }

    /**
     * 判断后缀是否受支持
     *
     * @param suffix 文件后缀
     * @return true-支持, false-不支持
     */
    public static boolean isSupported(String suffix) {
        return findBySuffix(suffix) != UNKNOWN;
    }

    /**
     * 获取所有支持的文件类型（排除 UNKNOWN）
     *
     * @return 支持的文件类型列表
     */
    public static List<FileTypeEnum> getSupportedTypes() {
        return Arrays.stream(values())
                .filter(type -> type != UNKNOWN)
                .toList();
    }
}
