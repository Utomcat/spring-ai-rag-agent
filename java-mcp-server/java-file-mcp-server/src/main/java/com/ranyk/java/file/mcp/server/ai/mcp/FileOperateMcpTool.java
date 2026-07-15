package com.ranyk.java.file.mcp.server.ai.mcp;

import cn.hutool.core.util.StrUtil;
import com.ranyk.spring.ai.rag.base.config.properties.SystemProperties;
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
     * 系统属性对象
     */
    private final SystemProperties systemProperties;

    /**
     * 构造方法
     *
     * @param systemProperties 系统属性对象
     */
    @Autowired
    public FileOperateMcpTool(SystemProperties systemProperties) {
        this.systemProperties = systemProperties;
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
        // 声明 文件保存文件夹 Path 对象
        Path dir;
        // 判断文件保存文件夹路径是否为空, 为空则使用默认的文件保存文件夹路径
        if (StrUtil.isBlank(dirPath)) {
            log.info("需要写入的文件路径为空, 需要使用默认的文件路径 {}", systemProperties.getAgentFileOperateDefaultDir());
            dir = Path.of(systemProperties.getAgentFileOperateDefaultDir(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd", Locale.ROOT)));
        } else {
            // 去除文件保存文件夹路径参数中的反斜杠和斜杠
            String originDir = dirPath.replace("\\", "").replace("/", "");
            // 去除默认文件保存文件夹路径中的反斜杠和斜杠
            String defaultDir = systemProperties.getAgentFileOperateDefaultDir().replace("\\", "").replace("/", "");
            log.info("需要写入的文件路径为 {}, 默认文件路径为 {}", originDir, defaultDir);
            dir = StrUtil.equalsIgnoreCase(originDir, defaultDir) ? Path.of(systemProperties.getAgentFileOperateDefaultDir(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd", Locale.ROOT))) : Path.of(dirPath);
        }
        // 判断文件后缀是否传入, 未传入则使用默认的文件后缀 .md
        String actualSuffix = StrUtil.isBlank(suffix) ? ".md" : suffix;
        // 判断文件名是否传入, 未传入则使用默认的文件名, 格式为 20260715123456
        if (StrUtil.isBlank(fileName)) {
            fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss", Locale.ROOT));
        }
        // 构建文件名, 并添加文件后缀
        fileName = fileName + actualSuffix;
        // 构建文件的 Path 对象
        Path filePath = Path.of(dir.toString(), fileName);
        try {
            // 创建文件保存文件夹
            Files.createDirectories(dir);
            // 构造文件内容
            String content = String.join("", contexts);
            // 将文件内容写入到文件中
            Files.writeString(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            log.info("文件 {} 写入成功, 内容长度: {}", filePath, content.length());
            return "文件写入成功, 保存路径: " + filePath;
        } catch (Exception e) {
            log.error("文件 {} 写入失败, 异常信息为: {}", filePath, e.getMessage());
            return "文件写入失败, 异常信息为: " + e.getMessage();
        }
    }
}
