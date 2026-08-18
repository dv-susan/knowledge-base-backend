package com.knowledge.base.userauth.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.userauth.dto.PermissionDTO;
import com.knowledge.base.userauth.service.PermissionService;
import com.knowledge.base.userauth.vo.PermissionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理Controller
 *
 * <p>按照阿里巴巴Java开发规范设计，提供权限管理相关接口</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/permissions")
@Tag(name = "权限管理", description = "权限信息管理接口")
public class PermissionController {

    @Resource
    private PermissionService permissionService;

    /**
     * 创建权限
     *
     * @param permissionDTO 权限信息
     * @return 权限ID
     */
    @PostMapping
    @Operation(summary = "创建权限", description = "创建新权限")
    public Result<Long> createPermission(@Valid @RequestBody PermissionDTO permissionDTO) {
        log.info("创建权限请求：code={}", permissionDTO.getCode());

        Long permissionId = permissionService.createPermission(permissionDTO);
        return Result.success("创建权限成功", permissionId);
    }

    /**
     * 更新权限
     *
     * @param permissionDTO 权限信息
     * @return 是否成功
     */
    @PutMapping
    @Operation(summary = "更新权限", description = "更新权限信息")
    public Result<Boolean> updatePermission(@Valid @RequestBody PermissionDTO permissionDTO) {
        log.info("更新权限请求：permissionId={}", permissionDTO.getId());

        Boolean success = permissionService.updatePermission(permissionDTO);
        return Result.success("更新权限成功", success);
    }

    /**
     * 删除权限
     *
     * @param permissionId 权限ID
     * @return 是否成功
     */
    @DeleteMapping("/{permissionId}")
    @Operation(summary = "删除权限", description = "根据权限ID删除权限")
    public Result<Boolean> deletePermission(
        @Parameter(description = "权限ID", required = true)
        @PathVariable Long permissionId) {
        log.info("删除权限请求：permissionId={}", permissionId);

        Boolean success = permissionService.deletePermission(permissionId);
        return Result.success("删除权限成功", success);
    }

    /**
     * 根据ID查询权限
     *
     * @param permissionId 权限ID
     * @return 权限信息
     */
    @GetMapping("/{permissionId}")
    @Operation(summary = "查询权限", description = "根据权限ID查询权限详情")
    public Result<PermissionVO> getPermissionById(
        @Parameter(description = "权限ID", required = true)
        @PathVariable Long permissionId) {
        log.info("查询权限请求：permissionId={}", permissionId);

        PermissionVO permissionVO = permissionService.getPermissionById(permissionId);
        return Result.success(permissionVO);
    }

    /**
     * 根据父权限ID查询直属子权限
     *
     * @param permissionId 父权限ID
     * @return 子权限列表
     */
    @GetMapping("/{permissionId}/children")
    @Operation(summary = "查询下级资源", description = "根据父权限ID查询直属子权限")
    public Result<List<PermissionVO>> getPermissionsByParentId(
        @Parameter(description = "父权限ID", required = true)
        @PathVariable Long permissionId) {
        log.info("查询下级资源请求：permissionId={}", permissionId);

        List<PermissionVO> permissions = permissionService.getPermissionsByParentId(permissionId);
        return Result.success(permissions);
    }

    /**
     * 分页查询权限列表
     *
     * @param current 当前页
     * @param size    每页大小
     * @param keyword 搜索关键词
     * @return 权限分页信息
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询权限", description = "分页查询权限列表")
    public Result<IPage<PermissionVO>> pagePermissions(
        @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
        @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
        @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        log.info("分页查询权限请求：current={}, size={}, keyword={}", current, size, keyword);

        IPage<PermissionVO> page = permissionService.pagePermissions(current, size, keyword);
        return Result.success(page);
    }

    /**
     * 获取权限树
     *
     * @return 权限树
     */
    @GetMapping("/tree")
    @Operation(summary = "获取权限树", description = "获取完整的权限树结构")
    public Result<List<PermissionVO>> getPermissionTree() {
        log.info("获取权限树请求");

        List<PermissionVO> tree = permissionService.getPermissionTree();
        return Result.success(tree);
    }

    /**
     * 获取所有权限
     *
     * @return 权限列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取所有权限", description = "获取所有权限列表")
    public Result<List<PermissionVO>> getAllPermissions() {
        log.info("获取所有权限请求");

        List<PermissionVO> permissions = permissionService.getAllPermissions();
        return Result.success(permissions);
    }

    /**
     * 获取所有权限（简化版，用于下拉选择等场景）
     *
     * @return 权限列表
     */
    @GetMapping
    @Operation(summary = "获取所有权限", description = "获取所有权限列表（简化版）")
    public Result<List<PermissionVO>> listPermissions() {
        log.info("获取所有权限请求（简化版）");

        List<PermissionVO> permissions = permissionService.getAllPermissions();
        return Result.success(permissions);
    }
}
