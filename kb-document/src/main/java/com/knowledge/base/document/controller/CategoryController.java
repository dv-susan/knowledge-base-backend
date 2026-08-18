package com.knowledge.base.document.controller;

import com.knowledge.base.common.result.Result;
import com.knowledge.base.document.dto.CategoryDTO;
import com.knowledge.base.document.service.CategoryService;
import com.knowledge.base.document.vo.CategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理Controller
 *
 * <p>按照阿里巴巴Java开发规范设计，提供文档分类管理相关接口</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/categories")
@Tag(name = "分类管理", description = "文档分类管理接口")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 创建分类
     *
     * @param categoryDTO 分类信息
     * @return 分类ID
     */
    @PostMapping
    @Operation(summary = "创建分类", description = "创建新分类")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_CATEGORY)")
    public Result<Long> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        log.info("创建分类请求：name={}", categoryDTO.getName());

        Long categoryId = categoryService.createCategory(categoryDTO);
        return Result.success("创建分类成功", categoryId);
    }

    /**
     * 更新分类
     *
     * @param categoryDTO 分类信息
     * @return 是否成功
     */
    @PutMapping
    @Operation(summary = "更新分类", description = "更新分类信息")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_CATEGORY)")
    public Result<Boolean> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        log.info("更新分类请求：categoryId={}", categoryDTO.getId());

        Boolean success = categoryService.updateCategory(categoryDTO);
        return Result.success("更新分类成功", success);
    }

    /**
     * 删除分类
     *
     * @param categoryId 分类ID
     * @return 是否成功
     */
    @DeleteMapping("/{categoryId}")
    @Operation(summary = "删除分类", description = "根据分类ID删除分类")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_CATEGORY)")
    public Result<Boolean> deleteCategory(
        @Parameter(description = "分类ID", required = true)
        @PathVariable Long categoryId) {
        log.info("删除分类请求：categoryId={}", categoryId);

        Boolean success = categoryService.deleteCategory(categoryId);
        return Result.success("删除分类成功", success);
    }

    /**
     * 根据ID查询分类
     *
     * @param categoryId 分类ID
     * @return 分类信息
     */
    @GetMapping("/{categoryId}")
    @Operation(summary = "查询分类", description = "根据分类ID查询分类详情")
    public Result<CategoryVO> getCategoryById(
        @Parameter(description = "分类ID", required = true)
        @PathVariable Long categoryId) {
        log.info("查询分类请求：categoryId={}", categoryId);

        CategoryVO categoryVO = categoryService.getCategoryById(categoryId);
        return Result.success(categoryVO);
    }

    /**
     * 获取分类树
     *
     * @return 分类树
     */
    @GetMapping("/tree")
    @Operation(summary = "获取分类树", description = "获取完整的分类树结构")
    public Result<List<CategoryVO>> getCategoryTree() {
        log.info("获取分类树请求");

        List<CategoryVO> tree = categoryService.getCategoryTree();
        return Result.success(tree);
    }

    /**
     * 获取子分类
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    @GetMapping("/children/{parentId}")
    @Operation(summary = "获取子分类", description = "获取指定父分类的子分类列表")
    public Result<List<CategoryVO>> getChildren(
        @Parameter(description = "父分类ID", required = true)
        @PathVariable Long parentId) {
        log.info("获取子分类请求：parentId={}", parentId);

        List<CategoryVO> children = categoryService.getChildren(parentId);
        return Result.success(children);
    }

    /**
     * 移动分类
     *
     * @param categoryId    分类ID
     * @param newParentId 新父分类ID
     * @return 是否成功
     */
    @PutMapping("/{categoryId}/move")
    @Operation(summary = "移动分类", description = "移动分类到新的父分类下")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_CATEGORY)")
    public Result<Boolean> moveCategory(
        @Parameter(description = "分类ID", required = true)
        @PathVariable Long categoryId,
        @Parameter(description = "新父分类ID", required = true)
        @RequestParam Long newParentId) {
        log.info("移动分类请求：categoryId={}, newParentId={}", categoryId, newParentId);

        Boolean success = categoryService.moveCategory(categoryId, newParentId);
        return Result.success("移动分类成功", success);
    }

    /**
     * 获取所有分类（平铺）
     *
     * @return 分类列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取所有分类", description = "获取所有分类列表（平铺）")
    public Result<List<CategoryVO>> getAllCategories() {
        log.info("获取所有分类请求");

        List<CategoryVO> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }
}
