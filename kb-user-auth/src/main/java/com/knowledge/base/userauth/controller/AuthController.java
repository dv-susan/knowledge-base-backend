package com.knowledge.base.userauth.controller;

import com.knowledge.base.common.exception.BusinessException;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.userauth.dto.LoginDTO;
import com.knowledge.base.userauth.dto.RegisterDTO;
import com.knowledge.base.userauth.dto.ResetPasswordDTO;
import com.knowledge.base.userauth.dto.SendResetCodeDTO;
import com.knowledge.base.userauth.dto.VerifyResetCodeDTO;
import com.knowledge.base.userauth.service.UserService;
import com.knowledge.base.userauth.vo.LoginVO;
import com.knowledge.base.userauth.vo.RegisterVO;
import com.knowledge.base.userauth.vo.TokenValidateVO;
import com.knowledge.base.userauth.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 认证Controller
 *
 * <p>负责 HTTP 请求接收与参数校验，业务逻辑委托给 {@link UserService} 处理。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "认证管理", description = "用户认证相关接口")
public class AuthController {

    @Resource
    private UserService userService;

    /**
     * Token验证（供其他微服务Feign调用）
     *
     * @param authorization Authorization 请求头（Bearer token）
     * @param token         URL参数中的token（备选）
     * @return Token验证结果
     */
    @PostMapping("/validate")
    @Operation(summary = "Token验证", description = "供其他微服务Feign调用，验证JWT Token并返回用户身份和角色信息")
    public Result<TokenValidateVO> validateToken(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(required = false) String token) {
        return Result.success(userService.validateToken(authorization, token));
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 当前用户信息
     */
    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public Result<UserVO> getCurrentUser() {
        UserVO userVO = userService.getCurrentUserInfo();
        return Result.success(userVO);
    }

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求参数
     * @return 登录响应信息
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户使用用户名密码登录系统")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        log.info("用户登录请求：username={}", loginDTO.getUsername());
        LoginVO loginVO = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
        return Result.success(loginVO);
    }

    /**
     * 用户注册
     *
     * @param registerDTO 注册请求参数
     * @return 注册响应（含邮箱验证状态）
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册账号，若提供邮箱则需验证激活")
    public Result<RegisterVO> register(@Valid @RequestBody RegisterDTO registerDTO) {
        log.info("用户注册请求：username={}", registerDTO.getUsername());
        RegisterVO registerVO = userService.register(registerDTO);
        return Result.success(registerVO);
    }

    /**
     * 邮箱验证激活账户
     *
     * @param token 激活令牌
     * @return 激活结果消息
     */
    @GetMapping("/verify-email")
    @Operation(summary = "邮箱验证", description = "通过邮件中的激活链接验证邮箱并激活账户")
    public Result<String> verifyEmail(@RequestParam String token) {
        log.info("邮箱验证请求：token={}", token);
        String message = userService.verifyEmail(token);
        return Result.success(message);
    }

    /**
     * 用户退出
     *
     * @param token 访问令牌
     * @return 响应结果
     */
    @PostMapping("/logout")
    @Operation(summary = "用户退出", description = "用户退出登录")
    public Result<Void> logout(@RequestHeader("Authorization") String token) {
        log.info("用户退出登录");
        userService.logout(token);
        return Result.success();
    }

    /**
     * 刷新Token
     *
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新Token", description = "使用刷新令牌获取新的访问令牌")
    public Result<LoginVO> refresh(@RequestParam String refreshToken) {
        log.info("刷新Token请求");
        LoginVO loginVO = userService.refreshToken(refreshToken);
        return Result.success(loginVO);
    }

    /**
     * 根据角色编码查询用户ID列表（供 kb-foundation 等微服务 Feign 调用）
     *
     * @param roleCode 角色编码，如 ROLE_REVIEWER
     * @return 用户ID列表
     */
    @GetMapping("/users/by-role")
    @Operation(summary = "根据角色查询用户ID", description = "供其他微服务Feign调用，查询拥有指定角色的所有用户ID")
    public Result<List<Long>> getUserIdsByRole(@RequestParam String roleCode) {
        return Result.success(userService.getUserIdsByRoleCode(roleCode));
    }

    /**
     * 查询审核员用户ID列表（无Result包装，专供Feign调用）
     *
     * @return 审核员用户ID列表
     */
    @GetMapping("/reviewer-ids")
    @Operation(summary = "查询审核员ID列表", description = "供kb-foundation微服务Feign调用，返回审核员用户ID列表")
    public List<Long> getReviewerIds() {
        List<Long> userIds = userService.getUserIdsByRoleCode("ROLE_REVIEWER");
        log.info("查询审核员ID：count={}", userIds.size());
        return userIds;
    }

    // ==================== 密码重置接口 ====================

    /**
     * 发送密码重置验证码
     *
     * @param dto 请求（含注册邮箱）
     * @return 发送结果
     */
    @PostMapping("/password/reset/send-code")
    @Operation(summary = "发送重置密码验证码", description = "向注册邮箱发送6位验证码，有效期10分钟")
    public Result<Void> sendResetCode(@Valid @RequestBody SendResetCodeDTO dto) {
        log.info("发送密码重置验证码：email={}", dto.getEmail());
        userService.sendResetCode(dto.getEmail());
        return Result.success();
    }

    /**
     * 验证重置密码验证码
     *
     * @param dto 请求（含邮箱和验证码）
     * @return 验证结果
     */
    @PostMapping("/password/reset/verify-code")
    @Operation(summary = "验证重置密码验证码", description = "校验邮箱对应的验证码是否正确")
    public Result<Void> verifyResetCode(@Valid @RequestBody VerifyResetCodeDTO dto) {
        log.info("验证密码重置验证码：email={}", dto.getEmail());
        userService.verifyResetCode(dto.getEmail(), dto.getCode());
        return Result.success();
    }

    /**
     * 重置密码
     *
     * @param dto 请求（含邮箱、验证码、新密码）
     * @return 重置结果
     */
    @PostMapping("/password/reset")
    @Operation(summary = "重置密码", description = "使用验证码重置用户密码")
    public Result<Void> resetPassword(@Valid @RequestBody ResetPasswordDTO dto) {
        log.info("重置密码：email={}", dto.getEmail());
        userService.resetPassword(dto.getEmail(), dto.getCode(), dto.getNewPassword());
        return Result.success();
    }
}
