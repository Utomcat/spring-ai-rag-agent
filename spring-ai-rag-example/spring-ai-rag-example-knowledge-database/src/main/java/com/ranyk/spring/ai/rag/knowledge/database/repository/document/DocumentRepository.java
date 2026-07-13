package com.ranyk.spring.ai.rag.knowledge.database.repository.document;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ranyk.spring.ai.rag.knowledge.database.domain.document.entity.Document;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

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

    /**
     * 根据分类统计文档数量
     *
     * @return 文档数量统计结果
     */
    List<Map<String, Long>> countGroupByCategory();
}
