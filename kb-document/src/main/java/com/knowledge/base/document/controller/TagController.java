package com.knowledge.base.document.controller;

import com.knowledge.base.common.annotation.OperationLog;
import com.knowledge.base.common.result.PageResult;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.document.dto.TagCreateDTO;
import com.knowledge.base.document.dto.TagQueryDTO;
import com.knowledge.base.document.dto.TagUpdateDTO;
import com.knowledge.base.document.service.TagService;
import com.knowledge.base.document.vo.TagVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签管理Controller
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@Tag(name = "标签管理", description = "标签管理相关接口")
public class TagController {

    private final TagService tagService;

    /**
     * 创建标签
     */
    @PostMapping
    @Operation(summary = "创建标签", description = "创建新标签")
    @OperationLog(module = "标签管理", operation = "创建标签", description = "创建新标签")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_TAG)")
    public Result<Long> createTag(@Valid @RequestBody TagCreateDTO dto) {
        Long tagId = tagService.createTag(dto);
        return Result.success(tagId);
    }

    /**
     * 更新标签
     */
    @PutMapping
    @Operation(summary = "更新标签", description = "更新标签信息")
    @OperationLog(module = "标签管理", operation = "更新标签", description = "更新标签信息")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_TAG)")
    public Result<Boolean> updateTag(@Valid @RequestBody TagUpdateDTO dto) {
        Boolean result = tagService.updateTag(dto);
        return Result.success(result);
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/{tagId}")
    @Operation(summary = "删除标签", description = "删除指定标签")
    @OperationLog(module = "标签管理", operation = "删除标签", description = "删除标签")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_TAG)")
    public Result<Boolean> deleteTag(@PathVariable Long tagId) {
        Boolean result = tagService.deleteTag(tagId);
        return Result.success(result);
    }

    /**
     * 获取标签详情
     */
    @GetMapping("/{tagId}")
    @Operation(summary = "获取标签详情", description = "根据ID获取标签详情")
    public Result<TagVO> getTagDetail(@PathVariable Long tagId) {
        TagVO tagVO = tagService.getTagDetail(tagId);
        return Result.success(tagVO);
    }

    /**
     * 分页查询标签
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询标签", description = "分页查询标签列表")
    public Result<PageResult<TagVO>> pageTags(@RequestBody TagQueryDTO dto) {
        PageResult<TagVO> pageResult = tagService.pageTags(dto);
        return Result.success(pageResult);
    }

    /**
     * 获取热门标签
     */
    @GetMapping("/hot")
    @Operation(summary = "获取热门标签", description = "获取使用最多的标签")
    public Result<List<TagVO>> getHotTags(
            @RequestParam(defaultValue = "10") Integer limit) {
        List<TagVO> hotTags = tagService.getHotTags(limit);
        return Result.success(hotTags);
    }

    /**
     * 根据分类获取标签
     */
    @GetMapping("/category/{categoryId}")
    @Operation(summary = "根据分类获取标签", description = "获取指定分类下的标签")
    public Result<List<TagVO>> getTagsByCategory(@PathVariable Long categoryId) {
        List<TagVO> tags = tagService.getTagsByCategory(categoryId);
        return Result.success(tags);
    }
}
