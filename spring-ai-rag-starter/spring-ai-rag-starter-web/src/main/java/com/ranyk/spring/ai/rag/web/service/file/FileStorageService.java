package com.ranyk.spring.ai.rag.web.service.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.ranyk.spring.ai.rag.base.domain.dto.StoredFile;
import com.ranyk.spring.ai.rag.common.exception.FileException;
import com.ranyk.spring.ai.rag.web.config.properties.FileProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * CLASS_NAME: FileStorageService.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 文件存储业务逻辑类
 * @date: 2026-06-28
 */
@Slf4j
@Service
public class FileStorageService {

    /**
     * 文件属性配置类对象
     */
    private final FileProperties fileProperties;
    /**
     * 日期格式化对象 - 用于格式化日期为 yyyyMMdd 格式
     */
    private static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.ROOT);

    /**
     * 构造方法 - 由 Spring IOC 容器在创建当前 Bean 对象实例时,自动注入 {@link FileProperties} 对象
     *
     * @param fileProperties 文件属性配置类对象
     */
    @Autowired
    public FileStorageService(FileProperties fileProperties) {
        this.fileProperties = fileProperties;
    }

    /**
     * 上传文件 - 上传单个文件
     *
     * @param file 文件对象
     * @return 文件存储信息
     */
    public StoredFile upload(MultipartFile file) {
        try {
            // 获取当前日期的 yyyyMMdd 格式字符串
            String yyyyMmDd = LocalDate.now().format(YYYY_MM_DD);
            // 获取文件的原始名称
            String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
            // 获取文件的扩展名
            int dot = original.lastIndexOf('.');
            String ext = dot > 0 ? original.substring(dot) : "";
            // 生成新的文件名
            String name = IdUtil.simpleUUID() + ext.toLowerCase(Locale.ROOT);
            // 获取文件上传的目录
            Path dir = Paths.get(fileProperties.getUpload().getRoot(), yyyyMmDd);
            // 创建目录
            Files.createDirectories(dir);
            // 获取文件的完整路径
            Path target = dir.resolve(name);
            // 将文件保存到目标路径
            file.transferTo(target);
            // 获取文件的相对路径
            String relative = yyyyMmDd + "/" + name;
            // 返回文件的相对路径和完整路径
            return new StoredFile(name, relative, ext, file.getSize(), target);
        } catch (Exception e) {
            throw new FileException("file.upload.error", new String[]{file.getOriginalFilename(), e.getMessage()});
        }
    }

    /**
     * 上传文件 - 上传多个文件
     *
     * @param files 文件对象列表
     * @return 文件存储信息列表
     */
    public List<StoredFile> batchUpload(List<MultipartFile> files) {
        return files.stream().map(file -> {
            try {
                return upload(file);
            } catch (Exception e) {
                throw new FileException("file.upload.error", new String[]{file.getOriginalFilename(), e.getMessage()});
            }
        }).toList();
    }

    /**
     * 删除文件 - 根据文件路径删除文件, 删除文件或目录（递归删除, 不判断是否为空）, 这个方法相当于Linux的delete命令
     *
     * @param path 文件路径
     * @return 删除结果
     */
    public Boolean delete(String path) {
        return FileUtil.del(Paths.get(path));
    }

    /**
     * 批量删除文件 - 根据文件路径列表批量删除文件
     * 注意：此方法为非原子性操作，若中间某个文件删除失败，之前已删除的文件无法恢复
     *
     * @param paths 文件路径列表
     * @return 全部删除成功返回 true，任一文件删除失败返回 false
     */
    public Boolean batchDelete(List<String> paths) {
        return paths.stream().allMatch(this::delete);
    }

    /**
     * 写入文件内容 - 将文件内容写入到文件中
     *
     * @param dirPath  文件保存文件夹路径
     * @param fileName 文件名
     * @param suffix   文件后缀
     * @param contexts 文件内容列表
     * @return 文件写入结果, 如果写入成功则返回文件路径, 否则返回异常信息字符串
     */
    public String writeFileContext(String dirPath, String fileName, String suffix, List<String> contexts) {
        // 声明 文件保存文件夹 Path 对象
        Path dir;
        // 判断文件保存文件夹路径是否为空, 为空则使用默认的文件保存文件夹路径
        if (StrUtil.isBlank(dirPath)) {
            log.info("需要写入的文件路径为空, 需要使用默认的文件路径 {}", fileProperties.getUpload().getRoot());
            dir = Path.of(fileProperties.getUpload().getRoot(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd", Locale.ROOT)));
        } else {
            // 去除文件保存文件夹路径参数中的反斜杠和斜杠
            String originDir = dirPath.replace("\\", "").replace("/", "");
            // 去除默认文件保存文件夹路径中的反斜杠和斜杠
            String defaultDir = fileProperties.getUpload().getRoot().replace("\\", "").replace("/", "");
            log.info("需要写入的文件路径为 {}, 默认文件路径为 {}", originDir, defaultDir);
            dir = StrUtil.equalsIgnoreCase(originDir, defaultDir) ? Path.of(fileProperties.getUpload().getRoot(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd", Locale.ROOT))) : Path.of(dirPath);
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
            log.error("文件 {} 写入失败, 异常信息为: {}", filePath, e.getMessage(), e);
            return "文件写入失败, 异常信息为: " + e.getMessage();
        }
    }
}
