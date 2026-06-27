package com.ranyk.spring.ai.rag.knowledge.database.repository.document;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ranyk.spring.ai.rag.knowledge.database.domain.document.entity.Document;
import org.apache.ibatis.annotations.Mapper;

/**
 * CLASS_NAME: DocumentRespository.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 知识库文档数据库操作接口
 * @date: 2026-06-27
 */
@Mapper
public interface DocumentRepository extends BaseMapper<Document> {
}
