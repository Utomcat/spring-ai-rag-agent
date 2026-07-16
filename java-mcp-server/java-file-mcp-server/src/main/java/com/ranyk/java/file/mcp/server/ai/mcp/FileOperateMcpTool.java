package com.ranyk.java.file.mcp.server.ai.mcp;

import cn.hutool.core.util.StrUtil;
import com.ranyk.spring.ai.rag.base.config.properties.SystemProperties;
import com.ranyk.spring.ai.rag.web.service.file.FileStorageService;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * CLASS_NAME: FileOperateMcpTool.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 文件操作 MCP 服务工具类
 * @date: 2026-07-15
 */
@Slf4j
@Component
@SuppressWarnings("unused")
public class FileOperateMcpTool {
    /**
     * 文件存储服务对象
     */
    private final FileStorageService fileStorageService;

    /**
     * 构造方法
     *
     * @param fileStorageService 文件存储服务对象
     */
    @Autowired
    public FileOperateMcpTool(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    /**
     * 将传入的文件内容写入到指定路径的文件中, 并返回对应的写入结果, 如果写入失败则, 返回错误信息
     *
     * @param dirPath  文件保存文件夹路径, 字符串类型, 如 E:\\FTP\\upload\\
     * @param fileName 文件名, 字符串类型, 不带后缀, 如 文档一、文档二
     * @param suffix   文件后缀, 字符串类型, 如 .md、.txt、.csv、.json
     * @param contexts 需要向文件中写入的内容, 为避免文件写入内容过多, 导致 String 类型无法存入, 故使用 List 将内容进行拆分
     * @return 文件写入结果, 字符串类型
     */
    @McpTool(
            name = "writeFileContext",
            description = "将传入的文件内容写入到指定路径的文件中, 并返回对应的写入结果, 如果写入失败则, 返回错误信息",
            generateOutputSchema = true
    )
    public String writeFileContext(@Nullable @McpToolParam(description = "写入文件的保存文件夹路径, 字符串类型, 如果用户未传入指定路径, 则使用默认的文件路径, 如: E:\\FTP\\upload\\ ") String dirPath,
                                   @Nullable @McpToolParam(description = "写入的文件名, 字符串类型, 不带后缀, 如 文档一、文档二 ") String fileName,
                                   @Nullable @McpToolParam(description = "写入的文件后缀, 字符串类型, 如 .md、.txt、.csv、.json") String suffix,
                                   @McpToolParam(description = "需要向文件中写入的内容, 为避免文件写入内容过多, 导致 String 类型无法存入, 故使用 List 将内容进行拆分") List<String> contexts) {
        log.info("调用文件操作 MCP 服务的 writeFileContext 方法, 入参: dirPath => {}, fileName => {}, suffix => {}, context 一共有 => {} 块", dirPath, fileName, suffix, contexts.size());
        return fileStorageService.writeFileContext(dirPath, fileName, suffix, contexts);
    }
}
