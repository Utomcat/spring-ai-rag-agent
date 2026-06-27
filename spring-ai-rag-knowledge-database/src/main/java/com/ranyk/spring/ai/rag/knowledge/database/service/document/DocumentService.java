package com.ranyk.spring.ai.rag.knowledge.database.service.document;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ranyk.spring.ai.rag.knowledge.database.domain.document.entity.Document;
import com.ranyk.spring.ai.rag.knowledge.database.repository.document.DocumentRepository;
import org.springframework.stereotype.Service;

/**
 * CLASS_NAME: DocumentService.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 知识库文档业务逻辑处理类
 * @date: 2026-06-27
 */
@Service
public class DocumentService extends ServiceImpl<DocumentRepository, Document> {
}
