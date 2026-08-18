package com.knowledge.base.userauth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.knowledge.base.userauth.dto.RegisterDTO;
import com.knowledge.base.userauth.dto.UserDTO;
import com.knowledge.base.userauth.entity.User;
import com.knowledge.base.userauth.vo.LoginVO;
import com.knowledge.base.userauth.vo.RegisterVO;
import com.knowledge.base.userauth.vo.UserStatisticsVO;
import com.knowledge.base.userauth.vo.UserVO;

import java.util.List;

/**
 * 用户Service接口
 *
 * <p>按照阿里巴巴Java开发规范设计，提供用户业务逻辑操作</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录响应信息
     */
    LoginVO login(String username, String password);

    /**
     * 注册
     *
     * @param registerDTO 注册请求参数
     * @return 注册响应信息（含邮箱验证状态）
     */
    RegisterVO register(RegisterDTO registerDTO);

    /**
     * 验证邮箱激活账户
     *
     * @param token 激活令牌
     * @return 激活结果消息
     */
    String verifyEmail(String token);

    /**
     * 用户退出
     *
     * @param token 令牌
     */
    void logout(String token);

    /**
     * 检查Token是否在黑名单中（已登出）
     *
     * @param rawToken 原始JWT Token（不含Bearer前缀）
     * @return true-已登出/无效，false-有效
     */
    boolean isTokenBlacklisted(String rawToken);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User getByUsername(String username);

    /**
     * 创建用户
     *
     * @param userDTO 用户信息
     * @return 用户ID
     */
    Long createUser(UserDTO userDTO);

    /**
     * 更新用户
     *
     * @param userDTO 用户信息
     * @return 是否成功
     */
    Boolean updateUser(UserDTO userDTO);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    Boolean deleteUser(Long userId);

    /**
     * 根据ID查询用户
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    UserVO getUserById(Long userId);

    /**
     * 分页查询用户列表
     *
     * @param current  当前页
     * @param size     每页大小
     * @param keyword  搜索关键词
     * @param role     角色筛选
     * @param status   状态筛选
     * @return 用户分页信息
     */
    IPage<UserVO> pageUsers(Long current, Long size, String keyword, String role, Integer status);

    /**
     * 重置用户密码
     *
     * @param userId          用户ID
     * @param newPassword 新密码
     * @return 是否成功
     */
    Boolean resetPassword(Long userId, String newPassword);

    /**
     * 修改用户密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    Boolean changePassword(String oldPassword, String newPassword);

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    UserVO getCurrentUserInfo();

    /**
     * 分配角色给用户
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     * @return 是否成功
     */
    Boolean assignRoles(Long userId, List<Long> roleIds);

    /**
     * 获取用户角色列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> getUserRoles(Long userId);

    /**
     * 分配权限给用户（直接分配权限，非通过角色）
     *
     * @param userId        用户ID
     * @param permissionIds 权限ID列表
     * @return 是否成功
     */
    Boolean assignPermissions(Long userId, List<Long> permissionIds);

    /**
     * 获取用户所有权限（包括角色权限和直接分配的权限）
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    List<String> getUserPermissions(Long userId);

    /**
     * 刷新Token
     *
     * @param refreshToken 刷新令牌
     * @return 新的登录响应信息（含新访问令牌和刷新令牌）
     */
    LoginVO refreshToken(String refreshToken);

    /**
     * 获取用户统计数据（文档数、浏览量、点赞数、评论数）
     *
     * @param userId 用户ID
     * @return 用户统计数据
     */
    UserStatisticsVO getUserStatistics(Long userId);

    /**
     * 发送密码重置验证码
     *
     * @param email 注册邮箱
     */
    void sendResetCode(String email);

    /**
     * 验证密码重置验证码
     *
     * @param email 注册邮箱
     * @param code  验证码
     * @return 验证结果
     */
    boolean verifyResetCode(String email, String code);

    /**
     * 重置密码
     *
     * @param email       注册邮箱
     * @param code        验证码
     * @param newPassword 新密码
     */
    void resetPassword(String email, String code, String newPassword);

    /**
     * 验证Token并返回用户身份信息
     *
     * @param authorization Authorization 请求头（Bearer token）
     * @param token         URL参数中的token（备选）
     * @return Token验证结果
     */
    com.knowledge.base.userauth.vo.TokenValidateVO validateToken(String authorization, String token);

    /**
     * 根据角色编码查询用户ID列表
     *
     * @param roleCode 角色编码，如 ROLE_REVIEWER
     * @return 用户ID列表
     */
    List<Long> getUserIdsByRoleCode(String roleCode);
}
