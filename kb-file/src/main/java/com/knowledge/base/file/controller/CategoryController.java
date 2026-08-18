package com.knowledge.base.file.controller;

import com.knowledge.base.common.result.Result;
import com.knowledge.base.file.dto.CategoryDTO;
import com.knowledge.base.file.service.CategoryService;
import com.knowledge.base.file.vo.CategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文件分类控制器
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/file/categories")
@RequiredArgsConstructor
@Tag(name = "文件分类管理", description = "文件分类的增删改查接口")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 创建分类
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "创建分类", description = "创建新的文件分类")
    public Result<CategoryVO> create(@Valid @RequestBody CategoryDTO dto) {
        log.info("创建分类请求: {}", dto.getName());
        CategoryVO result = categoryService.create(dto);
        return Result.success(result);
    }

    /**
     * 更新分类
     */
    @PutMapping
    @Operation(summary = "更新分类", description = "更新指定分类的信息")
    public Result<CategoryVO> update(@Valid @RequestBody CategoryDTO dto) {
        log.info("更新分类请求: id={}", dto.getId());
        CategoryVO result = categoryService.update(dto);
        return Result.success(result);
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除分类", description = "删除指定的分类（需要先删除子分类）")
    public Result<Void> delete(@Parameter(description = "分类ID") @PathVariable Long id) {
        log.info("删除分类请求: id={}", id);
        categoryService.delete(id);
        return Result.success();
    }

    /**
     * 根据ID获取分类详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取分类详情", description = "根据ID获取分类的详细信息")
    public Result<CategoryVO> getById(@Parameter(description = "分类ID") @PathVariable Long id) {
        CategoryVO result = categoryService.getById(id);
        return Result.success(result);
    }

    /**
     * 获取所有分类（平铺列表）
     */
    @GetMapping
    @Operation(summary = "获取所有分类", description = "获取所有分类的平铺列表")
    public Result<List<CategoryVO>> listAll() {
        List<CategoryVO> result = categoryService.listAll();
        return Result.success(result);
    }

    /**
     * 获取分类树结构
     */
    @GetMapping("/tree")
    @Operation(summary = "获取分类树", description = "获取分类的树形结构")
    public Result<List<CategoryVO>> getTree() {
        List<CategoryVO> result = categoryService.getTree();
        return Result.success(result);
    }

    /**
     * 根据父分类ID获取子分类列表
     */
    @GetMapping("/children/{parentId}")
    @Operation(summary = "获取子分类", description = "根据父分类ID获取子分类列表")
    public Result<List<CategoryVO>> listByParentId(
            @Parameter(description = "父分类ID，0表示顶级分类") @PathVariable Long parentId) {
        List<CategoryVO> result = categoryService.listByParentId(parentId);
        return Result.success(result);
    }

    /**
     * 启用分类
     */
    @PutMapping("/{id}/enable")
    @Operation(summary = "启用分类", description = "将指定分类设置为启用状态")
    public Result<CategoryVO> enable(@Parameter(description = "分类ID") @PathVariable Long id) {
        log.info("启用分类请求: id={}", id);
        CategoryVO result = categoryService.updateStatus(id, 1);
        return Result.success(result);
    }

    /**
     * 禁用分类
     */
    @PutMapping("/{id}/disable")
    @Operation(summary = "禁用分类", description = "将指定分类设置为禁用状态")
    public Result<CategoryVO> disable(@Parameter(description = "分类ID") @PathVariable Long id) {
        log.info("禁用分类请求: id={}", id);
        CategoryVO result = categoryService.updateStatus(id, 0);
        return Result.success(result);
    }
}
