package com.knowledge.base.userauth.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.userauth.dto.RoleDTO;
import com.knowledge.base.userauth.service.RoleService;
import com.knowledge.base.userauth.vo.RoleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理Controller
 *
 * <p>按照阿里巴巴Java开发规范设计，提供角色管理相关接口</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/roles")
@Tag(name = "角色管理", description = "角色信息管理接口")
public class RoleController {

    @Resource
    private RoleService roleService;

    /**
     * 创建角色
     *
     * @param roleDTO 角色信息
     * @return 角色ID
     */
    @PostMapping
    @Operation(summary = "创建角色", description = "创建新角色")
    public Result<Long> createRole(@Valid @RequestBody RoleDTO roleDTO) {
        log.info("创建角色请求：name={}", roleDTO.getName());

        Long roleId = roleService.createRole(roleDTO);
        return Result.success("创建角色成功", roleId);
    }

    /**
     * 更新角色
     *
     * @param roleDTO 角色信息
     * @return 是否成功
     */
    @PutMapping
    @Operation(summary = "更新角色", description = "更新角色信息")
    public Result<Boolean> updateRole(@Valid @RequestBody RoleDTO roleDTO) {
        log.info("更新角色请求：roleId={}", roleDTO.getId());

        Boolean success = roleService.updateRole(roleDTO);
        return Result.success("更新角色成功", success);
    }

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     * @return 是否成功
     */
    @DeleteMapping("/{roleId}")
    @Operation(summary = "删除角色", description = "根据角色ID删除角色")
    public Result<Boolean> deleteRole(
        @Parameter(description = "角色ID", required = true)
        @PathVariable Long roleId) {
        log.info("删除角色请求：roleId={}", roleId);

        Boolean success = roleService.deleteRole(roleId);
        return Result.success("删除角色成功", success);
    }

    /**
     * 根据ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色信息
     */
    @GetMapping("/{roleId}")
    @Operation(summary = "查询角色", description = "根据角色ID查询角色详情")
    public Result<RoleVO> getRoleById(
        @Parameter(description = "角色ID", required = true)
        @PathVariable Long roleId) {
        log.info("查询角色请求：roleId={}", roleId);

        RoleVO roleVO = roleService.getRoleById(roleId);
        return Result.success(roleVO);
    }

    /**
     * 分页查询角色列表
     *
     * @param current 当前页
     * @param size    每页大小
     * @param keyword 搜索关键词
     * @return 角色分页信息
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询角色", description = "分页查询角色列表")
    public Result<IPage<RoleVO>> pageRoles(
        @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
        @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
        @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        log.info("分页查询角色请求：current={}, size={}, keyword={}", current, size, keyword);

        IPage<RoleVO> page = roleService.pageRoles(current, size, keyword);
        return Result.success(page);
    }

    /**
     * 获取所有角色
     *
     * @return 角色列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取所有角色", description = "获取所有角色列表")
    public Result<List<RoleVO>> getAllRoles() {
        log.info("获取所有角色请求");

        List<RoleVO> roles = roleService.getAllRoles();
        return Result.success(roles);
    }

    /**
     * 获取所有角色（简化版，用于下拉选择等场景）
     *
     * @return 角色列表
     */
    @GetMapping
    @Operation(summary = "获取所有角色", description = "获取所有角色列表（简化版）")
    public Result<List<RoleVO>> listRoles() {
        log.info("获取所有角色请求（简化版）");

        List<RoleVO> roles = roleService.getAllRoles();
        return Result.success(roles);
    }

    /**
     * 分配权限
     *
     * @param roleId        角色ID
     * @param permissionIds 权限ID列表
     * @return 是否成功
     */
    @PostMapping("/{roleId}/permissions")
    @Operation(summary = "分配权限", description = "为角色分配权限")
    public Result<Boolean> assignPermissions(
        @Parameter(description = "角色ID", required = true)
        @PathVariable Long roleId,
        @RequestBody List<Long> permissionIds) {
        log.info("分配权限请求：roleId={}, permissionCount={}", roleId, permissionIds.size());

        Boolean success = roleService.assignPermissions(roleId, permissionIds);
        return Result.success("分配权限成功", success);
    }

    /**
     * 获取角色权限
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    @GetMapping("/{roleId}/permissions")
    @Operation(summary = "获取角色权限", description = "获取角色的权限列表")
    public Result<List<Long>> getRolePermissions(
        @Parameter(description = "角色ID", required = true)
        @PathVariable Long roleId) {
        log.info("获取角色权限请求：roleId={}", roleId);

        List<Long> permissionIds = roleService.getRolePermissions(roleId);
        return Result.success(permissionIds);
    }
}
