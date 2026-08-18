package com.knowledge.base.document.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.base.common.exception.BusinessException;
import com.knowledge.base.common.utils.SnowflakeIdGenerator;
import com.knowledge.base.document.dto.CategoryDTO;
import com.knowledge.base.document.entity.Category;
import com.knowledge.base.document.mapper.CategoryMapper;
import com.knowledge.base.document.service.CategoryService;
import com.knowledge.base.document.vo.CategoryVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类Service实现类
 *
 * <p>按照阿里巴巴Java开发规范设计，实现分类相关业务逻辑</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "sidebar:categories", allEntries = true)
    public Long createCategory(CategoryDTO categoryDTO) {
        log.info("创建分类：categoryName={}", categoryDTO.getName());

        // 检查分类名称是否已存在
        Category existCategory = categoryMapper.selectOne(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getCategoryName, categoryDTO.getName())
        );
        if (existCategory != null) {
            throw new BusinessException("分类名称已存在");
        }

        // 检查父分类是否存在
        Long parentId = categoryDTO.getParentId() != null ? categoryDTO.getParentId() : 0L;
        if (parentId > 0) {
            Category parentCategory = categoryMapper.selectById(parentId);
            if (parentCategory == null) {
                throw new BusinessException("父分类不存在");
            }
        }

        // 生成分类编码
        String categoryCode = StringUtils.hasText(categoryDTO.getName())
                ? generateCategoryCode(categoryDTO.getName())
                : "CATEGORY_" + System.currentTimeMillis();

        // 构建分类实体
        Category category = new Category();
        category.setId(SnowflakeIdGenerator.getInstance().nextId());
        category.setParentId(parentId);
        category.setCategoryName(categoryDTO.getName());
        category.setCategoryCode(categoryCode);
        category.setDescription(categoryDTO.getDescription());
        category.setIcon(categoryDTO.getIcon());
        category.setSort(categoryDTO.getSortOrder() != null ? categoryDTO.getSortOrder() : 0);
        category.setStatus(1);
        category.setDocumentCount(0);

        // 保存分类
        int count = categoryMapper.insert(category);
        if (count <= 0) {
            throw new BusinessException("创建分类失败");
        }

        return category.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "sidebar:categories", allEntries = true)
    public Boolean updateCategory(CategoryDTO categoryDTO) {
        log.info("更新分类：categoryId={}", categoryDTO.getId());

        if (categoryDTO.getId() == null) {
            throw new BusinessException("分类ID不能为空");
        }

        // 检查分类是否存在
        Category existCategory = categoryMapper.selectById(categoryDTO.getId());
        if (existCategory == null) {
            throw new BusinessException("分类不存在");
        }

        // 检查分类名称是否被其他分类使用
        if (StringUtils.hasText(categoryDTO.getName())
                && !categoryDTO.getName().equals(existCategory.getCategoryName())) {
            Category category = categoryMapper.selectOne(
                    new LambdaQueryWrapper<Category>()
                            .eq(Category::getCategoryName, categoryDTO.getName())
            );
            if (category != null && !category.getId().equals(categoryDTO.getId())) {
                throw new BusinessException("分类名称已被使用");
            }
        }

        // 检查父分类是否存在
        if (categoryDTO.getParentId() != null) {
            if (categoryDTO.getParentId().equals(categoryDTO.getId())) {
                throw new BusinessException("父分类不能是自己");
            }
            if (categoryDTO.getParentId() > 0) {
                Category parentCategory = categoryMapper.selectById(categoryDTO.getParentId());
                if (parentCategory == null) {
                    throw new BusinessException("父分类不存在");
                }
            }
        }

        // 构建更新实体
        Category category = new Category();
        category.setId(categoryDTO.getId());
        if (StringUtils.hasText(categoryDTO.getName())) {
            category.setCategoryName(categoryDTO.getName());
        }
        category.setDescription(categoryDTO.getDescription());
        if (categoryDTO.getParentId() != null) {
            category.setParentId(categoryDTO.getParentId());
        }
        if (categoryDTO.getIcon() != null) {
            category.setIcon(categoryDTO.getIcon());
        }
        if (categoryDTO.getSortOrder() != null) {
            category.setSort(categoryDTO.getSortOrder());
        }

        int count = categoryMapper.updateById(category);
        return count > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "sidebar:categories", allEntries = true)
    public Boolean deleteCategory(Long categoryId) {
        log.info("删除分类：categoryId={}", categoryId);

        if (categoryId == null) {
            throw new BusinessException("分类ID不能为空");
        }

        // 检查分类是否存在
        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        // 检查是否有子分类
        Long childCount = categoryMapper.selectCount(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getParentId, categoryId)
        );
        if (childCount > 0) {
            throw new BusinessException("该分类下有子分类，不能删除");
        }

        // TODO: 检查是否有关联文档，如果有则不允许删除或提示用户

        // 删除分类
        int count = categoryMapper.deleteById(categoryId);
        return count > 0;
    }

    @Override
    public CategoryVO getCategoryById(Long categoryId) {
        if (categoryId == null) {
            throw new BusinessException("分类ID不能为空");
        }

        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        return convertToVO(category);
    }

    @Override
    @Cacheable(value = "categoryTree", key = "'tree'", cacheManager = "caffeineCacheManager")
    public List<CategoryVO> getCategoryTree() {
        // 查询所有分类
        List<Category> allCategories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getStatus, 1)
                        .orderByAsc(Category::getSort)
        );

        // 转换为VO
        List<CategoryVO> categoryVOs = allCategories.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 构建树形结构
        return buildCategoryTree(categoryVOs, 0L);
    }

    @Override
    public List<CategoryVO> getChildren(Long parentId) {
        if (parentId == null) {
            parentId = 0L;
        }

        List<Category> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getParentId, parentId)
                        .eq(Category::getStatus, 1)
                        .orderByAsc(Category::getSort)
        );

        return categories.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "sidebar:categories", allEntries = true)
    public Boolean moveCategory(Long categoryId, Long newParentId) {
        log.info("移动分类：categoryId={}, newParentId={}", categoryId, newParentId);

        if (categoryId == null) {
            throw new BusinessException("分类ID不能为空");
        }

        // 检查分类是否存在
        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        // 检查是否移动到自己
        if (categoryId.equals(newParentId)) {
            throw new BusinessException("不能移动到自己");
        }

        // 检查新父分类是否存在
        if (newParentId != null && newParentId > 0) {
            Category parentCategory = categoryMapper.selectById(newParentId);
            if (parentCategory == null) {
                throw new BusinessException("父分类不存在");
            }

            // 检查是否移动到自己的子分类下
            if (isDescendant(categoryId, newParentId)) {
                throw new BusinessException("不能移动到自己的子分类下");
            }
        }

        // 更新父分类ID
        Category updateCategory = new Category();
        updateCategory.setId(categoryId);
        updateCategory.setParentId(newParentId != null ? newParentId : 0L);

        int count = categoryMapper.updateById(updateCategory);
        return count > 0;
    }

    @Override
    public List<CategoryVO> getAllCategories() {
        List<Category> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getStatus, 1)
                        .orderByAsc(Category::getSort)
        );

        return categories.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 转换为VO
     *
     * @param category 分类实体
     * @return 分类VO
     */
    private CategoryVO convertToVO(Category category) {
        return CategoryVO.builder()
                .id(category.getId())
                .name(category.getCategoryName())
                .description(category.getDescription())
                .parentId(category.getParentId())
                .sortOrder(category.getSort())
                .icon(category.getIcon())
                .documentCount(category.getDocumentCount() != null ? category.getDocumentCount().longValue() : 0L)
                .build();
    }

    /**
     * 构建分类树
     *
     * @param categories 分类列表
     * @param parentId   父分类ID
     * @return 分类树
     */
    private List<CategoryVO> buildCategoryTree(List<CategoryVO> categories, Long parentId) {
        List<CategoryVO> tree = new ArrayList<>();

        for (CategoryVO category : categories) {
            if (parentId.equals(category.getParentId())) {
                // 递归查找子分类
                category.setChildren(buildCategoryTree(categories, category.getId()));
                tree.add(category);
            }
        }

        return tree;
    }

    /**
     * 检查是否是后代节点
     *
     * @param ancestorId 祖先节点ID
     * @param descendantId 后代节点ID
     * @return 是否是后代
     */
    private boolean isDescendant(Long ancestorId, Long descendantId) {
        Category category = categoryMapper.selectById(descendantId);
        while (category != null && category.getParentId() != null && category.getParentId() > 0) {
            if (category.getParentId().equals(ancestorId)) {
                return true;
            }
            category = categoryMapper.selectById(category.getParentId());
        }
        return false;
    }

    /**
     * 生成分类编码
     *
     * @param categoryName 分类名称
     * @return 分类编码
     */
    private String generateCategoryCode(String categoryName) {
        // 简单的拼音首字母或缩写生成逻辑
        // 实际项目中可以使用拼音转换库
        return "CAT_" + categoryName.toUpperCase()
                .replaceAll("[^A-Z0-9]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "") + "_" + System.currentTimeMillis();
    }
}
