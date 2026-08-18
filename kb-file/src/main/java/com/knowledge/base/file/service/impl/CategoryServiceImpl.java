package com.knowledge.base.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.base.common.exception.BusinessException;
import com.knowledge.base.file.dto.CategoryDTO;
import com.knowledge.base.file.entity.Category;
import com.knowledge.base.file.mapper.CategoryMapper;
import com.knowledge.base.file.service.CategoryService;
import com.knowledge.base.file.vo.CategoryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * 文件分类服务实现
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CategoryVO create(CategoryDTO dto) {
        log.info("创建分类: {}", dto.getName());

        // 检查分类名称是否已存在
        checkNameExists(null, dto.getName());

        // 计算分类层级
        int level = 1;
        if (dto.getParentId() != null && dto.getParentId() > 0) {
            Category parent = super.getById(dto.getParentId());
            if (parent == null) {
                throw new BusinessException("父分类不存在");
            }
            level = parent.getLevel() + 1;
            if (level > 5) {
                throw new BusinessException("分类层级不能超过5层");
            }
        }

        // 创建分类实体
        Category category = new Category();
        category.setName(dto.getName());
        category.setParentId(dto.getParentId() == null ? 0L : dto.getParentId());
        category.setLevel(level);
        category.setSortOrder(dto.getSortOrder() == null ? 0 : dto.getSortOrder());
        category.setIcon(dto.getIcon());
        category.setDescription(dto.getDescription());
        category.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        save(category);
        log.info("分类创建成功: id={}, name={}", category.getId(), category.getName());

        return CategoryVO.fromEntity(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CategoryVO update(CategoryDTO dto) {
        log.info("更新分类: id={}, name={}", dto.getId(), dto.getName());

        if (dto.getId() == null) {
            throw new BusinessException("分类ID不能为空");
        }

        Category category = super.getById(dto.getId());
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        // 检查分类名称是否已存在
        checkNameExists(dto.getId(), dto.getName());

        // 更新分类信息
        category.setName(dto.getName());
        category.setSortOrder(dto.getSortOrder() == null ? category.getSortOrder() : dto.getSortOrder());
        category.setIcon(dto.getIcon());
        category.setDescription(dto.getDescription());
        if (dto.getStatus() != null) {
            category.setStatus(dto.getStatus());
        }
        category.setUpdatedAt(LocalDateTime.now());

        updateById(category);
        log.info("分类更新成功: id={}", category.getId());

        return CategoryVO.fromEntity(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        log.info("删除分类: id={}", id);

        Category category = super.getById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        // 检查是否有子分类
        List<Category> children = list(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, id)
                .eq(Category::getDeleted, 0));
        if (!CollectionUtils.isEmpty(children)) {
            throw new BusinessException("该分类下存在子分类，无法删除");
        }

        removeById(id);
        log.info("分类删除成功: id={}", id);
    }

    @Override
    public CategoryVO getById(Long id) {
        Category category = super.getById(id);
        return CategoryVO.fromEntity(category);
    }

    @Override
    public List<CategoryVO> listAll() {
        List<Category> categories = list(new LambdaQueryWrapper<Category>()
                .eq(Category::getDeleted, 0)
                .orderByAsc(Category::getParentId)
                .orderByAsc(Category::getSortOrder));
        return categories.stream()
                .map(CategoryVO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryVO> getTree() {
        List<Category> categories = list(new LambdaQueryWrapper<Category>()
                .eq(Category::getDeleted, 0)
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSortOrder));

        return buildTree(categories);
    }

    @Override
    public List<CategoryVO> listByParentId(Long parentId) {
        List<Category> categories = list(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, parentId == null ? 0 : parentId)
                .eq(Category::getDeleted, 0)
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSortOrder));
        return categories.stream()
                .map(CategoryVO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CategoryVO updateStatus(Long id, Integer status) {
        log.info("更新分类状态: id={}, status={}", id, status);

        Category category = super.getById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        category.setStatus(status);
        category.setUpdatedAt(LocalDateTime.now());
        updateById(category);

        log.info("分类状态更新成功: id={}, status={}", id, status);
        return CategoryVO.fromEntity(category);
    }

    /**
     * 检查分类名称是否已存在
     */
    private void checkNameExists(Long excludeId, String name) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<Category>()
                .eq(Category::getName, name)
                .eq(Category::getDeleted, 0);
        if (excludeId != null) {
            queryWrapper.ne(Category::getId, excludeId);
        }
        if (count(queryWrapper) > 0) {
            throw new BusinessException("分类名称已存在");
        }
    }

    /**
     * 构建分类树
     */
    private List<CategoryVO> buildTree(List<Category> categories) {
        if (CollectionUtils.isEmpty(categories)) {
            return Collections.emptyList();
        }

        // 按父ID分组
        Map<Long, List<Category>> groupedByParentId = categories.stream()
                .collect(Collectors.groupingBy(c -> c.getParentId() == null ? 0L : c.getParentId()));

        // 递归构建树
        return buildChildren(0L, groupedByParentId);
    }

    /**
     * 递归构建子节点
     */
    private List<CategoryVO> buildChildren(Long parentId, Map<Long, List<Category>> groupedByParentId) {
        List<Category> children = groupedByParentId.get(parentId);
        if (CollectionUtils.isEmpty(children)) {
            return Collections.emptyList();
        }

        return children.stream()
                .map(category -> {
                    CategoryVO vo = CategoryVO.fromEntity(category);
                    vo.setChildren(buildChildren(category.getId(), groupedByParentId));
                    return vo;
                })
                .collect(Collectors.toList());
    }
}
