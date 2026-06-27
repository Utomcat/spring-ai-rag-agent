package com.ranyk.spring.ai.rag.knowledge.database.service.category;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ranyk.spring.ai.rag.knowledge.database.domain.category.entity.Category;
import com.ranyk.spring.ai.rag.knowledge.database.repository.category.CategoryRepository;
import org.springframework.stereotype.Service;

/**
 * CLASS_NAME: CategoryService.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 知识库分类信息业务逻辑处理类
 * @date: 2026-06-27
 */
@Service
public class CategoryService extends ServiceImpl<CategoryRepository, Category> {
}
