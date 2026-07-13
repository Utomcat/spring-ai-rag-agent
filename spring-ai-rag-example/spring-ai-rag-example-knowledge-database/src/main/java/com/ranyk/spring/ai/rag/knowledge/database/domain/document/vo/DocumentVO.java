package com.ranyk.spring.ai.rag.knowledge.database.domain.document.vo;

import java.time.LocalDateTime;

/**
 * CLASS_NAME: DocumentVO.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 知识库文档数据返回前端数据封装 VO 类, 其字段说明如下:
 * <ul>
 *     <li>id: 文档 ID</li>
 *     <li>categoryId: 文档分类 ID</li>
 *     <li>title: 文档标题</li>
 *     <li>fileName: 文档文件名</li>
 *     <li>filePath: 文档文件路径</li>
 *     <li>fileType: 文档文件类型, 文件后缀的小写, 如 txt、pdf、md、doc、docx 等</li>
 *     <li>fileSize: 文档文件大小</li>
 *     <li>status: 文档状态</li>
 *     <li>vectorCount: 文档向量数量</li>
 *     <li>uploadUserId: 上传用户 ID</li>
 *     <li>createTime: 创建时间</li>
 * </ul>
 * @date: 2026-06-28
 */
public record DocumentVO(Long id,
                         Long categoryId,
                         String title,
                         String fileName,
                         String filePath,
                         String fileType,
                         String fileSize,
                         String status,
                         Integer vectorCount,
                         Long uploadUserId,
                         LocalDateTime createTime) {
}
