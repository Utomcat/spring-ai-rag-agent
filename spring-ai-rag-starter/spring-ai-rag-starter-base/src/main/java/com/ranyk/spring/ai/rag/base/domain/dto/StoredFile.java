package com.ranyk.spring.ai.rag.base.domain.dto;

import java.nio.file.Path;

/**
 * CLASS_NAME: StoredFile.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 文件存储数据类, 其字段说明如下:
 * <ul>
 *     <li>fileName: 文件名</li>
 *     <li>relativePath: 文件的相对路径</li>
 *     <li>fileType: 文件类型</li>
 *     <li>fileSize: 文件大小</li>
 *     <li>absolutePath: 文件的绝对路径, {@link Path} 对象</li>
 * </ul>
 * @date: 2026-06-28
 */
public record StoredFile(String fileName, String relativePath, String fileType, Long fileSize, Path absolutePath) {
}
