package com.ranyk.spring.ai.rag.knowledge.database.repository.category;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ranyk.spring.ai.rag.knowledge.database.domain.category.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * CLASS_NAME: CategoryRepository.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 知识库分类数据库操作接口
 * @date: 2026-06-27
 */
@Mapper
public interface CategoryRepository extends BaseMapper<Category> {
}
