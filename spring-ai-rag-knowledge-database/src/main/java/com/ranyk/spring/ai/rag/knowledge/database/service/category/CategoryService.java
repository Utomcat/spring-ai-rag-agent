package com.ranyk.spring.ai.rag.knowledge.database.service.category;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ranyk.spring.ai.rag.knowledge.database.base.domain.dto.BaseDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.category.dto.CategoryDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.category.entity.Category;
import com.ranyk.spring.ai.rag.knowledge.database.domain.category.mapstruct.CategoryMapper;
import com.ranyk.spring.ai.rag.knowledge.database.repository.category.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * CLASS_NAME: CategoryService.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 知识库分类信息业务逻辑处理类
 * @date: 2026-06-27
 */
@Slf4j
@Service
public class CategoryService extends ServiceImpl<CategoryRepository, Category> {

    /**
     * 知识库分类 数据转换 MapStruct 对象
     */
    private final CategoryMapper categoryMapper;

    /**
     * 构造函数 - 由 Spring IOC 容器创建当前 Bean 时自动注入 {@link CategoryMapper} 对象
     *
     * @param categoryMapper 知识库分类 数据转换 MapStruct 对象 {@link CategoryMapper}
     */
    @Autowired
    public CategoryService(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    /**
     * 依据数据查询条件分页查询知识库分类信息数据
     *
     * @param categoryDTO 知识库分类信息数据查询条件
     * @return 知识库分类信息数据 {@link CategoryDTO}
     */
    public CategoryDTO listAll(CategoryDTO categoryDTO) {
        // 构建分页查询参数
        Page<Category> page = BaseDTO.buildPage(categoryDTO);
        // 构建查询条件
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // 添加知识库名称查询条件 - 模糊匹配
        queryWrapper.like(StrUtil.isNotBlank(categoryDTO.getName()), Category::getName, categoryDTO.getName());
        // 添加知识库描述查询条件 - 模糊匹配
        queryWrapper.like(StrUtil.isNotBlank(categoryDTO.getDescription()), Category::getDescription, categoryDTO.getDescription());
        // 添加知识库图标查询条件 - 模糊匹配
        queryWrapper.like(StrUtil.isNotBlank(categoryDTO.getIcon()), Category::getIcon, categoryDTO.getIcon());
        // 添加知识库排序顺序查询条件 - 精确匹配
        queryWrapper.eq(Objects.nonNull(categoryDTO.getSortOrder()), Category::getSortOrder, categoryDTO.getSortOrder());
        // 构建分页查询对象并执行查询
        Page<Category> categoryPage = page(page, queryWrapper);
        // 构建知识库分类信息数据查询结果对象
        return CategoryDTO.builder()
                .dataList(categoryMapper.categoryListToCategoryDTOList(categoryPage.getRecords()))
                .total(categoryPage.getTotal())
                .page(Long.valueOf(categoryPage.getCurrent()).intValue())
                .size(Long.valueOf(categoryPage.getSize()).intValue())
                .build();
    }

    /**
     * 保存知识库分类信息数据
     *
     * @param categoryDTO 知识库分类信息数据 {@link CategoryDTO}
     */
    @Transactional(rollbackFor = Exception.class)
    public void save(CategoryDTO categoryDTO) {
        Category category = categoryMapper.categoryDTOToCategory(categoryDTO);
        if (Objects.nonNull(category.getId())) {
            category.setCreateBy(null);
            category.setCreateTime(null);
        }
        saveOrUpdate(category);
    }

    /**
     * 删除知识库分类信息数据
     *
     * @param id 知识库分类信息数据ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        this.removeById(id);
    }
}
