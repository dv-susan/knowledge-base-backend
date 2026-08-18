package com.knowledge.base.userauth.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.common.utils.UserContextUtil;
import com.knowledge.base.userauth.dto.UserDTO;
import com.knowledge.base.userauth.service.UserService;
import com.knowledge.base.userauth.vo.UserStatisticsVO;
import com.knowledge.base.userauth.vo.UserVO;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户Controller
 *
 * <p>按照阿里巴巴Java开发规范设计，提供用户管理相关接口</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/users")
@Tag(name = "用户管理", description = "用户信息管理接口")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 创建用户
     *
     * @param userDTO 用户信息
     * @return 用户ID
     */
    @PostMapping
    @Operation(summary = "创建用户", description = "创建新用户")
    public Result<Long> createUser(@Valid @RequestBody UserDTO userDTO) {
        log.info("创建用户请求：username={}", userDTO.getUsername());

        Long userId = userService.createUser(userDTO);
        return Result.success("创建用户成功", userId);
    }

    /**
     * 更新用户
     *
     * @param userDTO 用户信息
     * @return 是否成功
     */
    @PutMapping
    @Operation(summary = "更新用户", description = "更新用户信息")
    public Result<Boolean> updateUser(@Valid @RequestBody UserDTO userDTO) {
        log.info("更新用户请求：userId={}", userDTO.getId());

        Boolean success = userService.updateUser(userDTO);
        return Result.success("更新用户成功", success);
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "删除用户", description = "根据用户ID删除用户")
    public Result<Boolean> deleteUser(
        @Parameter(description = "用户ID", required = true)
        @PathVariable Long userId) {
        log.info("删除用户请求：userId={}", userId);

        Boolean success = userService.deleteUser(userId);
        return Result.success("删除用户成功", success);
    }

    /**
     * 根据ID查询用户
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/{userId}")
    @Operation(summary = "查询用户", description = "根据用户ID查询用户信息")
    public Result<UserVO> getUserById(
        @Parameter(description = "用户ID", required = true)
        @PathVariable Long userId) {
        log.info("查询用户请求：userId={}", userId);

        UserVO userVO = userService.getUserById(userId);
        return Result.success(userVO);
    }

    /**
     * 分页查询用户列表
     *
     * @param current 当前页
     * @param size    每页大小
     * @param keyword 搜索关键词
     * @param role    角色筛选
     * @param status  状态筛选
     * @return 用户分页信息
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询用户", description = "分页查询用户列表")
    public Result<IPage<UserVO>> pageUsers(
        @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
        @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
        @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
        @Parameter(description = "角色筛选") @RequestParam(required = false) String role,
        @Parameter(description = "状态筛选") @RequestParam(required = false) Integer status) {
        log.info("分页查询用户请求：current={}, size={}, keyword={}, role={}, status={}", current, size, keyword, role, status);

        IPage<UserVO> page = userService.pageUsers(current, size, keyword, role, status);
        return Result.success(page);
    }

    /**
     * 重置用户密码
     *
     * @param userId      用户ID
     * @param newPassword 新密码
     * @return 是否成功
     */
    @PutMapping("/{userId}/password/reset")
    @Operation(summary = "重置密码", description = "管理员重置用户密码")
    public Result<Boolean> resetPassword(
        @Parameter(description = "用户ID", required = true)
        @PathVariable Long userId,
        @Parameter(description = "新密码", required = true)
        @RequestParam String newPassword) {
        log.info("重置用户密码请求：userId={}", userId);

        Boolean success = userService.resetPassword(userId, newPassword);
        return Result.success("重置密码成功", success);
    }

    /**
     * 修改当前用户密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    @PutMapping("/password/change")
    @Operation(summary = "修改密码", description = "用户修改自己的密码")
    public Result<Boolean> changePassword(
        @Parameter(description = "旧密码", required = true)
        @RequestParam String oldPassword,
        @Parameter(description = "新密码", required = true)
        @RequestParam String newPassword) {
        log.info("修改密码请求");

        Boolean success = userService.changePassword(oldPassword, newPassword);
        return Result.success("修改密码成功", success);
    }

    /**
     * 分配角色给用户
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     * @return 是否成功
     */
    @PostMapping("/{userId}/roles")
    @Operation(summary = "分配角色", description = "为用户分配角色")
    public Result<Boolean> assignRoles(
        @Parameter(description = "用户ID", required = true)
        @PathVariable Long userId,
        @Parameter(description = "角色ID列表", required = true)
        @RequestBody List<Long> roleIds) {
        log.info("分配角色请求：userId={}, roleIds={}", userId, roleIds);

        Boolean success = userService.assignRoles(userId, roleIds);
        return Result.success("角色分配成功", success);
    }

    /**
     * 获取用户角色列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    @GetMapping("/{userId}/roles")
    @Operation(summary = "获取用户角色", description = "获取用户已分配的角色列表")
    public Result<List<Long>> getUserRoles(
        @Parameter(description = "用户ID", required = true)
        @PathVariable Long userId) {
        log.info("获取用户角色请求：userId={}", userId);

        List<Long> roleIds = userService.getUserRoles(userId);
        return Result.success(roleIds);
    }

    /**
     * 分配权限给用户
     *
     * @param userId        用户ID
     * @param permissionIds 权限ID列表
     * @return 是否成功
     */
    @PostMapping("/{userId}/permissions")
    @Operation(summary = "分配权限", description = "直接为用户分配权限（非通过角色）")
    public Result<Boolean> assignPermissions(
        @Parameter(description = "用户ID", required = true)
        @PathVariable Long userId,
        @Parameter(description = "权限ID列表", required = true)
        @RequestBody List<Long> permissionIds) {
        log.info("分配权限请求：userId={}, permissionCount={}", userId, permissionIds.size());

        Boolean success = userService.assignPermissions(userId, permissionIds);
        return Result.success("权限分配成功", success);
    }

    /**
     * 获取用户所有权限
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    @GetMapping("/{userId}/permissions")
    @Operation(summary = "获取用户权限", description = "获取用户所有权限（包括角色权限和直接分配的权限）")
    public Result<List<String>> getUserPermissions(
        @Parameter(description = "用户ID", required = true)
        @PathVariable Long userId) {
        log.info("获取用户权限请求：userId={}", userId);

        List<String> permissions = userService.getUserPermissions(userId);
        return Result.success(permissions);
    }

    /**
     * 获取当前登录用户的统计数据（文档数、浏览量、点赞数、评论数）
     *
     * @return 用户统计数据
     */
    @GetMapping("/me/stats")
    @Operation(summary = "获取当前用户统计数据", description = "获取当前登录用户的文档数、浏览量、点赞数、评论数")
    public Result<UserStatisticsVO> getMyStatistics() {
        Long userId = UserContextUtil.getUserId();
        log.info("获取当前用户统计数据：userId={}", userId);

        UserStatisticsVO stats = userService.getUserStatistics(userId);
        return Result.success(stats);
    }
}
