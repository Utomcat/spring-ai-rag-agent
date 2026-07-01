package com.ranyk.spring.ai.rag.knowledge.database.api.category;

import com.ranyk.spring.ai.rag.knowledge.database.base.domain.po.PageQueryPO;
import com.ranyk.spring.ai.rag.knowledge.database.base.domain.vo.MultiResult;
import com.ranyk.spring.ai.rag.knowledge.database.base.domain.vo.Result;
import com.ranyk.spring.ai.rag.knowledge.database.domain.category.dto.CategoryDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.category.mapstruct.CategoryMapper;
import com.ranyk.spring.ai.rag.knowledge.database.domain.category.po.CategoryQueryPO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.category.po.CategorySavePO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.category.vo.CategoryVO;
import com.ranyk.spring.ai.rag.knowledge.database.service.category.CategoryService;
import com.ranyk.spring.ai.rag.knowledge.database.utils.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * CLASS_NAME: CategoryApi.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 知识库分类 API 接口类
 * @date: 2026-06-27
 */
@RestController
@RequestMapping("/api/category")
public class CategoryApi {

    /**
     * 知识库分类 业务逻辑处理类对象
     */
    private final CategoryService categoryService;
    /**
     * 知识库分类 数据转换 MapStruct 对象
     */
    private final CategoryMapper categoryMapper;

    /**
     * 构造函数 - 由 Spring IOC 容器在创建该类的 Bean 实例时, 自动注入 {@link CategoryService} 对象
     *
     * @param categoryService 知识库分类 业务逻辑处理类对象 {@link CategoryService}
     * @param categoryMapper  知识库分类 数据转换 MapStruct 对象 {@link CategoryMapper}
     */
    @Autowired
    public CategoryApi(CategoryService categoryService, CategoryMapper categoryMapper) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
    }

    /**
     * 查询请求 - 依据条件查询知识库分类信息
     */
    @GetMapping
    public MultiResult<CategoryVO> list(PageQueryPO pageQueryPO, CategoryQueryPO categoryQueryPO) {
        CategoryDTO query = categoryMapper.categoryQueryPOToCategoryDTO(categoryQueryPO);
        query.setPage(pageQueryPO.page());
        query.setSize(pageQueryPO.size());
        CategoryDTO categoryDTO = categoryService.listAll(query);
        return MultiResult.successMulti(categoryMapper.categoryDTOListToCategoryVOList(categoryDTO.getDataList()), categoryDTO.getTotal(), categoryDTO.getPage(), categoryDTO.getSize());
    }

    /**
     * 新增知识库分类方法 - 需要管理员权限
     *
     * @param categorySavePO 知识库分类保存参数对象 {@link CategorySavePO}
     * @return 操作结果 {@link Result}
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> save(@RequestBody @Valid CategorySavePO categorySavePO) {
        CategoryDTO categoryDTO = categoryMapper.categorySavePOToCategoryDTO(categorySavePO);
        Long userId = SecurityUtils.currentUser().getUserId();
        LocalDateTime now = LocalDateTime.now();
        if (Objects.isNull(categoryDTO.getId())) {
            categoryDTO.setCreateBy(userId);
            categoryDTO.setCreateTime(now);
        }
        categoryDTO.setUpdateBy(userId);
        categoryDTO.setUpdateTime(now);
        categoryService.save(categoryDTO);
        return Result.success();
    }

    /**
     * 依据数据 ID 删除知识库分类方法 - 需要管理员权限
     *
     * @param id 需要删除的知识库分类数据 ID
     * @return 操作结果 {@link Result}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success();
    }

}
