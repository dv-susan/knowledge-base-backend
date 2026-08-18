package com.knowledge.base.userauth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.base.common.exception.BusinessException;
import com.knowledge.base.common.result.ResultCode;
import com.knowledge.base.common.utils.SnowflakeIdGenerator;
import com.knowledge.base.common.utils.JwtTokenUtil;
import com.knowledge.base.common.utils.UserContextUtil;
import com.knowledge.base.userauth.dto.UserDTO;
import com.knowledge.base.userauth.entity.Role;
import com.knowledge.base.userauth.entity.User;
import com.knowledge.base.userauth.entity.UserRole;
import com.knowledge.base.userauth.mapper.RoleMapper;
import com.knowledge.base.userauth.mapper.UserMapper;
import com.knowledge.base.userauth.mapper.UserRoleMapper;
import com.knowledge.base.userauth.dto.RegisterDTO;
import com.knowledge.base.userauth.service.EmailService;
import com.knowledge.base.userauth.service.SecurityConfigService;
import com.knowledge.base.userauth.service.TeamService;
import com.knowledge.base.userauth.service.UserService;
import com.knowledge.base.userauth.util.VerificationTokenUtil;
import com.knowledge.base.userauth.vo.LoginVO;
import com.knowledge.base.userauth.vo.RegisterVO;
import com.knowledge.base.userauth.vo.TokenValidateVO;
import com.knowledge.base.userauth.vo.UserStatisticsVO;
import com.knowledge.base.userauth.vo.UserVO;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 用户Service实现类
 *
 * <p>按照阿里巴巴Java开发规范设计，实现用户相关业务逻辑</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private SecurityConfigService securityConfigService;

    @Resource
    private EmailService emailService;

    @Resource
    private VerificationTokenUtil verificationTokenUtil;

    @Resource
    private TeamService teamService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public LoginVO login(String username, String password) {
        log.info("用户登录：username={}", username);

        // 检查账号是否被锁定
        if (securityConfigService.isAccountLocked(username)) {
            int maxRetry = securityConfigService.getLoginMaxRetry();
            throw new BusinessException("账号已被锁定，请15分钟后再试（最多允许" + maxRetry + "次失败尝试）");
        }

        // 查询用户
        User user = getByUsername(username);
        if (user == null) {
            recordLoginFailureAndThrow(username, "用户名或密码错误");
            return null; // unreachable
        }

        // 检查用户状态
        checkUserStatus(user);

        // 验证密码
        if (!BCrypt.checkpw(password, user.getPassword())) {
            recordLoginFailureAndThrow(username, "用户名或密码错误");
            return null; // unreachable
        }

        // 登录成功：清除失败记录
        securityConfigService.clearLoginFailure(username);

        // 更新最后登录信息
        updateLastLoginInfo(user.getId());

        // 从安全配置读取会话超时时间
        long sessionTimeout = securityConfigService.getSessionTimeout();

        // 生成JWT Token（使用安全配置的会话超时时间）
        String accessToken = jwtTokenUtil.generateAccessToken(user.getId(), user.getUsername(), user.getAvatar(), sessionTimeout);
        String refreshToken = jwtTokenUtil.generateRefreshToken(user.getId());
        List<String> roles = getUserRoleCodes(user.getId());
        List<String> permissions = getUserPermissions(user.getId());

        // 构建登录响应
        LoginVO.UserInfo userInfo = LoginVO.UserInfo.builder()
            .userId(user.getId())
            .username(user.getUsername())
            .nickname(user.getRealName())
            .email(user.getEmail())
            .phone(user.getPhone())
            .avatar(user.getAvatar())
            .roles(roles)
            .permissions(permissions)
            .build();

        return LoginVO.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(sessionTimeout)
            .userInfo(userInfo)
            .build();
    }

    /**
     * 记录登录失败并抛出带剩余次数的异常
     */
    private void recordLoginFailureAndThrow(String username, String defaultMsg) {
        int remaining = securityConfigService.recordLoginFailure(username);
        if (remaining <= 0) {
            int maxRetry = securityConfigService.getLoginMaxRetry();
            throw new BusinessException("用户名或密码错误，账号已被锁定15分钟（最多允许" + maxRetry + "次失败尝试）");
        }
        throw new BusinessException("用户名或密码错误，还剩" + remaining + "次尝试机会");
    }

    /**
     * 检查用户状态，区分邮箱未激活和管理员禁用
     */
    private void checkUserStatus(User user) {
        if (user.getStatus() != null && user.getStatus() == 0) {
            if ((user.getEmailVerified() == null || user.getEmailVerified() == 0)
                    && StringUtils.hasText(user.getActivationToken())) {
                throw new BusinessException("账户未激活，请先查看邮箱并点击激活链接完成账户激活");
            }
            throw new BusinessException(ResultCode.USER_DISABLED);
        }
    }

    /**
     * 更新用户最后登录信息
     */
    private void updateLastLoginInfo(Long userId) {
        try {
            jdbcTemplate.update(
                "UPDATE kb_user SET last_login_time = NOW() WHERE id = ?",
                userId
            );
        } catch (Exception e) {
            log.warn("更新用户最后登录时间失败：{}", e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logout(String token) {
        log.info("用户退出登录");

        if (!StringUtils.hasText(token)) {
            log.warn("退出登录失败：Token为空");
            return;
        }

        // 去除Bearer前缀
        String rawToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        // 解析Token获取过期时间
        Date expireTime = null;
        try {
            Claims claims = jwtTokenUtil.parseToken(rawToken);
            if (claims != null) {
                expireTime = claims.getExpiration();
            }
        } catch (Exception e) {
            log.warn("解析Token失败，使用默认过期时间：{}", e.getMessage());
        }

        // 如果无法解析Token，使用24小时后的默认过期时间
        if (expireTime == null) {
            expireTime = new Date(System.currentTimeMillis() + 24 * 3600 * 1000L);
        }

        // SHA-256哈希Token用于存储
        String tokenHash = DigestUtil.sha256Hex(rawToken);

        // 将Token加入黑名单（使用INSERT IGNORE避免重复插入导致报错）
        try {
            jdbcTemplate.update(
                "INSERT IGNORE INTO tb_token_blacklist (id, token_hash, expire_time, created_at) VALUES (?, ?, ?, NOW())",
                SnowflakeIdGenerator.getInstance().nextId(), tokenHash, expireTime
            );
            log.info("Token已加入黑名单，过期时间：{}", expireTime);
        } catch (Exception e) {
            log.error("Token黑名单插入失败：{}", e.getMessage());
        }
    }

    /**
     * 定时清理过期的黑名单Token（每小时执行一次）
     */
    @Scheduled(fixedRate = 3600000)
    public void cleanExpiredTokens() {
        try {
            int count = jdbcTemplate.update(
                "DELETE FROM tb_token_blacklist WHERE expire_time < NOW()"
            );
            if (count > 0) {
                log.info("清理过期Token黑名单：{}条", count);
            }
        } catch (Exception e) {
            log.error("清理过期Token黑名单失败：{}", e.getMessage());
        }
    }

    @Override
    public boolean isTokenBlacklisted(String rawToken) {
        if (!StringUtils.hasText(rawToken)) {
            return false;
        }
        String tokenHash = DigestUtil.sha256Hex(rawToken);
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM tb_token_blacklist WHERE token_hash = ? AND expire_time > NOW()",
                    Integer.class, tokenHash);
            return count != null && count > 0;
        } catch (Exception e) {
            log.warn("查询Token黑名单异常：{}", e.getMessage());
            return false;
        }
    }

    @Override
    public User getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(UserDTO userDTO) {
        log.info("创建用户：username={}, realName={}, department={}, position={}",
                userDTO.getUsername(), userDTO.getRealName(), userDTO.getDepartment(), userDTO.getPosition());

        // 检查用户名是否已存在
        User existUser = getByUsername(userDTO.getUsername());
        if (existUser != null) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXIST);
        }

        // 检查邮箱是否已存在
        if (StringUtils.hasText(userDTO.getEmail())) {
            existUser = userMapper.selectByEmail(userDTO.getEmail());
            if (existUser != null) {
                throw new BusinessException("邮箱已被使用");
            }
        }

        // 检查手机号是否已存在
        if (StringUtils.hasText(userDTO.getPhone())) {
            existUser = userMapper.selectByPhone(userDTO.getPhone());
            if (existUser != null) {
                throw new BusinessException("手机号已被使用");
            }
        }

        // 构建用户实体
        User user = new User();
        BeanUtil.copyProperties(userDTO, user);
        log.debug("BeanUtil.copyProperties 后：realName={}, department={}, position={}",
                user.getRealName(), user.getDepartment(), user.getPosition());

        // 生成ID
        user.setId(SnowflakeIdGenerator.getInstance().nextId());

        // 加密密码
        if (StringUtils.hasText(userDTO.getPassword())) {
            user.setPassword(BCrypt.hashpw(userDTO.getPassword()));
        }

        // 设置默认值
        if (user.getStatus() == null) {
            user.setStatus(1);
        }

        // 保存用户
        int count = userMapper.insert(user);
        if (count <= 0) {
            throw new BusinessException("创建用户失败");
        }

        // 分配默认角色（ROLE_USER / 知识成员）
        assignDefaultRole(user.getId());

        log.info("用户创建成功：id={}", user.getId());
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateUser(UserDTO userDTO) {
        log.info("更新用户：userId={}, realName={}, department={}, position={}",
                userDTO.getId(), userDTO.getRealName(), userDTO.getDepartment(), userDTO.getPosition());

        if (userDTO.getId() == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 检查用户是否存在
        User existUser = userMapper.selectById(userDTO.getId());
        if (existUser == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        // 检查用户名是否被其他用户使用
        if (StringUtils.hasText(userDTO.getUsername())
            && !userDTO.getUsername().equals(existUser.getUsername())) {
            User user = getByUsername(userDTO.getUsername());
            if (user != null && !user.getId().equals(userDTO.getId())) {
                throw new BusinessException("用户名已被使用");
            }
        }

        // 构建更新实体
        User user = new User();
        BeanUtil.copyProperties(userDTO, user);
        log.debug("BeanUtil.copyProperties 后：realName={}, department={}, position={}",
                user.getRealName(), user.getDepartment(), user.getPosition());

        // 如果提供了新密码，则加密
        if (StringUtils.hasText(userDTO.getPassword())) {
            user.setPassword(BCrypt.hashpw(userDTO.getPassword()));
        }

        int count = userMapper.updateById(user);
        log.info("更新结果：count={}", count);
        return count > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUser(Long userId) {
        log.info("删除用户：userId={}", userId);

        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        int count = userMapper.deleteById(userId);
        return count > 0;
    }

    @Override
    public UserVO getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        return buildUserVO(user, true);
    }

    @Override
    public IPage<UserVO> pageUsers(Long current, Long size, String keyword, String role, Integer status) {
        // 构建查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(User::getUsername, keyword)
                .or()
                .like(User::getRealName, keyword)
                .or()
                .like(User::getEmail, keyword)
                .or()
                .like(User::getPhone, keyword);
        }
        
        // 状态筛选
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }

        List<Long> filteredUserIds = resolveUserIdsByRoleFilter(role);
        if (filteredUserIds != null) {
            if (filteredUserIds.isEmpty()) {
                return new Page<>(current, size);
            }
            wrapper.in(User::getId, filteredUserIds);
        }

        // 按创建时间倒序排列
        wrapper.orderByDesc(User::getCreatedAt);

        // 分页查询
        Page<User> page = new Page<>(current, size);
        IPage<User> userPage = userMapper.selectPage(page, wrapper);

        // 转换为VO
        return userPage.convert(user -> buildUserVO(user, false));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean resetPassword(Long userId, String newPassword) {
        log.info("重置用户密码：userId={}", userId);

        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        if (!StringUtils.hasText(newPassword)) {
            throw new BusinessException("新密码不能为空");
        }

        User user = new User();
        user.setId(userId);
        user.setPassword(BCrypt.hashpw(newPassword));

        int count = userMapper.updateById(user);
        return count > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean changePassword(String oldPassword, String newPassword) {
        log.info("修改密码");

        // 从上下文中获取当前登录用户
        Long userId = UserContextUtil.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        // 验证旧密码
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw new BusinessException("旧密码不正确");
        }

        // 更新密码
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setPassword(BCrypt.hashpw(newPassword));

        int count = userMapper.updateById(updateUser);
        return count > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RegisterVO register(RegisterDTO registerDTO) {
        log.info("用户注册：username={}", registerDTO.getUsername());

        // 检查是否开放注册
        if (!isRegistrationAllowed()) {
            throw new BusinessException("系统已关闭注册功能");
        }

        // 验证两次密码一致
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        // 验证密码策略
        securityConfigService.validatePassword(registerDTO.getPassword());

        // 检查用户名是否已存在
        User existUser = getByUsername(registerDTO.getUsername());
        if (existUser != null) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXIST);
        }

        // 检查邮箱是否已存在
        if (org.springframework.util.StringUtils.hasText(registerDTO.getEmail())) {
            existUser = userMapper.selectByEmail(registerDTO.getEmail());
            if (existUser != null) {
                throw new BusinessException("该邮箱已被使用");
            }
        }

        // 检查手机号是否已存在
        if (org.springframework.util.StringUtils.hasText(registerDTO.getPhone())) {
            existUser = userMapper.selectByPhone(registerDTO.getPhone());
            if (existUser != null) {
                throw new BusinessException("该手机号已被使用");
            }
        }

        // 创建用户（邮箱必填 → 需验证激活）
        User user = new User();
        user.setId(SnowflakeIdGenerator.getInstance().nextId());
        user.setUsername(registerDTO.getUsername());
        user.setPassword(BCrypt.hashpw(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());
        user.setRealName(registerDTO.getRealName());
        user.setPhone(registerDTO.getPhone());
        user.setStatus(0);
        user.setEmailVerified(0);
        String activationToken = verificationTokenUtil.generateToken();
        user.setActivationToken(activationToken);
        user.setActivationTokenExpiry(verificationTokenUtil.calculateExpiryTime());

        int count = userMapper.insert(user);
        if (count <= 0) {
            throw new BusinessException("注册失败，请稍后再试");
        }

        // 分配默认角色（ROLE_USER）
        assignDefaultRole(user.getId());

        // 发送激活邮件（失败则回滚注册数据）
        try {
            emailService.sendActivationEmail(registerDTO.getEmail(),
                    registerDTO.getUsername(), user.getActivationToken());
            log.info("激活邮件已发送：email={}, userId={}", registerDTO.getEmail(), user.getId());
        } catch (Exception e) {
            log.error("发送激活邮件失败：email={}, error={}", registerDTO.getEmail(), e.getMessage());
            throw new BusinessException("激活邮件发送失败，注册未完成，请稍后再试");
        }

        // 团队分配在注册事务提交后执行，避免内部异常导致事务回滚
        Long userId = user.getId();
        Long teamId = registerDTO.getTeamId();
        if (teamId != null) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        teamService.addTeamMembers(teamId, List.of(userId));
                        log.info("用户已加入团队：userId={}, teamId={}", userId, teamId);
                    } catch (Exception e) {
                        log.warn("用户加入团队失败（不影响注册）：userId={}, teamId={}, error={}",
                                userId, teamId, e.getMessage());
                    }
                }
            });
        }

        return RegisterVO.builder()
                .userId(userId)
                .emailVerificationRequired(true)
                .message("注册成功！激活邮件已发送至 " + registerDTO.getEmail() + "，请在24小时内点击邮件中的链接激活账户")
                .build();
    }

    /**
     * 检查是否开放注册
     */
    private boolean isRegistrationAllowed() {
        String value = securityConfigService.getConfig("user.registration.enabled");
        return value == null || "true".equals(value);
    }

    /**
     * 为新用户分配默认角色（ROLE_USER）
     */
    private void assignDefaultRole(Long userId) {
        try {
            // 查询ROLE_USER角色
            Role role = roleMapper.selectOne(
                    new LambdaQueryWrapper<Role>()
                            .eq(Role::getRoleCode, "ROLE_USER")
                            .eq(Role::getDeleted, 0)
                            .last("LIMIT 1"));
            if (role != null) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(role.getId());
                userRoleMapper.insert(userRole);
                log.info("已为用户分配默认角色：userId={}, roleId={}", userId, role.getId());
            } else {
                log.warn("未找到ROLE_USER角色，跳过默认角色分配");
            }
        } catch (Exception e) {
            log.error("分配默认角色失败：{}", e.getMessage());
        }
    }

    @Override
    public UserVO getCurrentUserInfo() {
        log.info("获取当前登录用户信息");

        // 从上下文中获取当前登录用户ID
        Long userId = UserContextUtil.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        // 检查用户状态（区分邮箱未激活 / 管理员禁用）
        checkUserStatus(user);

        // 转换为VO并返回
        UserVO userVO = buildUserVO(user, true);

        log.info("成功获取用户信息: userId={}, username={}", userVO.getId(), userVO.getUsername());
        return userVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean assignRoles(Long userId, List<Long> roleIds) {
        log.info("分配角色给用户：userId={}, roleIds={}", userId, roleIds);

        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        // 删除原有角色关联
        jdbcTemplate.update("DELETE FROM kb_user_role WHERE user_id = ?", userId);

        // 批量插入角色关联
        if (roleIds != null && !roleIds.isEmpty()) {
            String sql = "INSERT INTO kb_user_role (id, user_id, role_id, created_at) VALUES (?, ?, ?, NOW())";
            for (Long roleId : roleIds) {
                // 检查角色是否存在
                Role role = roleMapper.selectById(roleId);
                if (role == null) {
                    throw new BusinessException("角色不存在: " + roleId);
                }
                jdbcTemplate.update(sql, SnowflakeIdGenerator.getInstance().nextId(), userId, roleId);
            }
        }

        log.info("角色分配成功：userId={}", userId);
        return true;
    }

    @Override
    public List<Long> getUserRoles(Long userId) {
        log.info("获取用户角色：userId={}", userId);

        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        return jdbcTemplate.queryForList(
                "SELECT role_id FROM kb_user_role WHERE user_id = ?",
                Long.class,
                userId
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean assignPermissions(Long userId, List<Long> permissionIds) {
        log.info("分配权限给用户：userId={}, permissionCount={}", userId, permissionIds != null ? permissionIds.size() : 0);

        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        // 删除原有直接分配的权限
        jdbcTemplate.update("DELETE FROM kb_user_permission WHERE user_id = ?", userId);

        // 批量插入权限关联
        if (permissionIds != null && !permissionIds.isEmpty()) {
            String sql = "INSERT INTO kb_user_permission (id, user_id, permission_id, created_at) VALUES (?, ?, ?, NOW())";
            for (Long permissionId : permissionIds) {
                jdbcTemplate.update(sql, SnowflakeIdGenerator.getInstance().nextId(), userId, permissionId);
            }
        }

        log.info("权限分配成功：userId={}", userId);
        return true;
    }

    private static final List<String> ADMIN_OPERATION_PERMISSIONS = List.of(
            "document:list", "document:create", "document:edit", "document:delete",
            "document:review", "document:category", "document:category:query",
            "document:tag", "document:version",
            "system:user", "system:role", "system:permission",
            "system:permission:create", "system:permission:edit", "system:permission:delete",
            "system:team", "system:statistics", "system:settings"
    );

    private static final List<String> ADMIN_ROLES = List.of("ROLE_SUPER_ADMIN", "ROLE_ADMIN");

    public List<String> getUserPermissions(Long userId) {
        log.info("获取用户权限：userId={}", userId);

        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 查询用户直接分配的权限
        List<String> directPermissions = jdbcTemplate.queryForList(
                "SELECT p.permission_code FROM kb_user_permission up " +
                        "JOIN kb_permission p ON up.permission_id = p.id " +
                        "WHERE up.user_id = ? AND p.status = 1",
                String.class,
                userId
        );

        // 查询用户通过角色获得的权限
        List<String> rolePermissions = jdbcTemplate.queryForList(
                "SELECT DISTINCT p.permission_code FROM kb_user_role ur " +
                        "JOIN kb_role_permission rp ON ur.role_id = rp.role_id " +
                        "JOIN kb_permission p ON rp.permission_id = p.id " +
                        "WHERE ur.user_id = ? AND p.status = 1",
                String.class,
                userId
        );

        // 合并权限并去重
        List<String> allPermissions = Stream.concat(
                directPermissions.stream(),
                rolePermissions.stream()
        ).distinct().collect(Collectors.toList());

        // 管理员/超级管理员自动获得全部操作权限
        List<String> userRoles = getUserRoleCodes(userId);
        boolean isAdmin = userRoles.stream().anyMatch(ADMIN_ROLES::contains);
        if (isAdmin) {
            for (String perm : ADMIN_OPERATION_PERMISSIONS) {
                if (!allPermissions.contains(perm)) {
                    allPermissions.add(perm);
                }
            }
        }

        log.info("用户权限获取成功：userId={}, permissionCount={}, isAdmin={}", userId, allPermissions.size(), isAdmin);
        return allPermissions;
    }

    private UserVO buildUserVO(User user, boolean includePermissions) {
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        List<String> roleCodes = getUserRoleCodes(user.getId());
        userVO.setRoles(roleCodes);
        userVO.setRole(resolvePrimaryRole(roleCodes));
        if (includePermissions) {
            userVO.setPermissions(getUserPermissions(user.getId()));
        } else {
            userVO.setPermissions(new ArrayList<>());
        }
        return userVO;
    }

    private List<String> getUserRoleCodes(Long userId) {
        return roleMapper.selectRoleCodesByUserId(userId);
    }

    private String resolvePrimaryRole(List<String> roleCodes) {
        if (roleCodes == null || roleCodes.isEmpty()) {
            return null;
        }
        return mapRoleCodeToFrontendRole(roleCodes.get(0));
    }

    private String mapRoleCodeToFrontendRole(String roleCode) {
        if (!StringUtils.hasText(roleCode)) {
            return roleCode;
        }
        return switch (roleCode) {
            case "ROLE_SUPER_ADMIN" -> "SUPER_ADMIN";
            case "ROLE_ADMIN" -> "KNOWLEDGE_ADMIN";
            case "ROLE_EDITOR" -> "EDITOR";
            case "ROLE_REVIEWER" -> "REVIEWER";
            case "ROLE_USER", "ROLE_GUEST" -> "VIEWER";
            default -> roleCode.startsWith("ROLE_") ? roleCode.substring(5) : roleCode;
        };
    }

    private List<Long> resolveUserIdsByRoleFilter(String role) {
        if (!StringUtils.hasText(role)) {
            return null;
        }
        List<String> roleCodes = new ArrayList<>();
        roleCodes.add(role);
        if (!role.startsWith("ROLE_")) {
            roleCodes.add("ROLE_" + role);
        }
        switch (role) {
            case "SUPER_ADMIN" -> roleCodes.add("ROLE_SUPER_ADMIN");
            case "KNOWLEDGE_ADMIN" -> roleCodes.add("ROLE_ADMIN");
            case "EDITOR" -> roleCodes.add("ROLE_EDITOR");
            case "REVIEWER" -> roleCodes.add("ROLE_REVIEWER");
            case "VIEWER" -> {
                roleCodes.add("ROLE_USER");
                roleCodes.add("ROLE_GUEST");
            }
            default -> {
            }
        }
        String placeholders = roleCodes.stream().map(item -> "?").collect(Collectors.joining(","));
        String sql = "SELECT DISTINCT ur.user_id FROM kb_user_role ur " +
                "INNER JOIN kb_role r ON ur.role_id = r.id " +
                "WHERE r.deleted = 0 AND r.role_code IN (" + placeholders + ")";
        return jdbcTemplate.queryForList(sql, Long.class, roleCodes.toArray());
    }

    @Override
    public LoginVO refreshToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new BusinessException("刷新Token不能为空");
        }

        // 解析刷新Token
        Claims claims = jwtTokenUtil.parseToken(refreshToken);
        if (claims == null) {
            throw new BusinessException("刷新Token无效或已过期，请重新登录");
        }

        Long userId = Long.parseLong(claims.getSubject());

        // 检查刷新Token是否在黑名单中
        if (isTokenBlacklisted(refreshToken)) {
            throw new BusinessException("刷新Token已失效，请重新登录");
        }

        // 验证用户是否存在且状态正常
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (user.getStatus() == 0) {
            checkUserStatus(user);
        }

        // 从安全配置读取会话超时时间
        long sessionTimeout = securityConfigService.getSessionTimeout();

        // 生成新的访问令牌和刷新令牌（Token 轮换）
        String newAccessToken = jwtTokenUtil.generateAccessToken(user.getId(), user.getUsername(), user.getAvatar(), sessionTimeout);
        String newRefreshToken = jwtTokenUtil.generateRefreshToken(user.getId());

        // 获取用户角色和权限
        List<String> roles = getUserRoleCodes(user.getId());
        List<String> permissions = getUserPermissions(user.getId());

        // 构建响应
        LoginVO.UserInfo userInfo = LoginVO.UserInfo.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getRealName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .roles(roles)
                .permissions(permissions)
                .build();

        log.info("Token刷新成功：userId={}, username={}", user.getId(), user.getUsername());

        return LoginVO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(sessionTimeout)
                .userInfo(userInfo)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String verifyEmail(String token) {
        log.info("邮箱验证：token={}", token);

        if (!StringUtils.hasText(token)) {
            throw new BusinessException("激活令牌不能为空");
        }

        // 根据令牌查找用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getActivationToken, token));

        if (user == null) {
            throw new BusinessException("激活链接无效");
        }

        // 检查令牌是否过期
        if (verificationTokenUtil.isTokenExpired(user.getActivationTokenExpiry())) {
            throw new BusinessException("激活链接已过期，请重新注册");
        }

        // 检查邮箱是否已验证
        if (user.getEmailVerified() != null && user.getEmailVerified() == 1) {
            return "账户已激活，请直接登录";
        }

        // 激活账户
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmailVerified(1);
        updateUser.setStatus(1);
        updateUser.setActivationToken(null);
        updateUser.setActivationTokenExpiry(null);

        userMapper.updateById(updateUser);
        log.info("邮箱验证成功：userId={}, email={}", user.getId(), user.getEmail());

        return "账户激活成功，请登录系统";
    }

    // ==================== 密码重置相关方法 ====================

    private static final String RESET_CODE_PREFIX = "password:reset:code:";
    private static final long RESET_CODE_EXPIRE_MINUTES = 10;
    private static final int RESET_CODE_LENGTH = 6;

    @Override
    public void sendResetCode(String email) {
        // 检查邮箱是否已注册
        User user = userMapper.selectByEmail(email);
        if (user == null) {
            throw new BusinessException("该邮箱未注册");
        }

        // 检查是否在冷却期（60秒内不允许重复发送）
        String redisKey = RESET_CODE_PREFIX + email;
        String existingCode = stringRedisTemplate.opsForValue().get(redisKey);
        if (existingCode != null) {
            Long ttl = stringRedisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
            if (ttl != null && ttl > (RESET_CODE_EXPIRE_MINUTES * 60 - 60)) {
                throw new BusinessException("验证码已发送，请" + ttl + "秒后再试");
            }
        }

        // 生成6位数字验证码
        String code = generateResetCode();

        // 存储到Redis（10分钟有效期）
        stringRedisTemplate.opsForValue().set(redisKey, code, RESET_CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);

        // 发送验证码邮件
        try {
            emailService.sendResetCodeEmail(email, code);
            log.info("密码重置验证码已发送：email={}", email);
        } catch (Exception e) {
            // 发送失败则删除Redis中的验证码
            stringRedisTemplate.delete(redisKey);
            log.error("发送密码重置验证码邮件失败：email={}, error={}", email, e.getMessage());
            throw new BusinessException("邮件发送失败，请稍后再试");
        }
    }

    @Override
    public boolean verifyResetCode(String email, String code) {
        String redisKey = RESET_CODE_PREFIX + email;
        String storedCode = stringRedisTemplate.opsForValue().get(redisKey);

        if (storedCode == null) {
            throw new BusinessException("验证码已过期，请重新获取");
        }

        if (!storedCode.equals(code)) {
            throw new BusinessException("验证码错误");
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(String email, String code, String newPassword) {
        // 先验证验证码
        String redisKey = RESET_CODE_PREFIX + email;
        String storedCode = stringRedisTemplate.opsForValue().get(redisKey);

        if (storedCode == null) {
            throw new BusinessException("验证码已过期，请重新获取");
        }

        if (!storedCode.equals(code)) {
            throw new BusinessException("验证码错误");
        }

        // 验证密码策略
        securityConfigService.validatePassword(newPassword);

        // 查找用户
        User user = userMapper.selectByEmail(email);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 更新密码
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setPassword(BCrypt.hashpw(newPassword));
        userMapper.updateById(updateUser);

        // 删除验证码防止重复使用
        stringRedisTemplate.delete(redisKey);

        log.info("密码重置成功：email={}, userId={}", email, user.getId());
    }

    /**
     * 生成6位数字验证码
     */
    private String generateResetCode() {
        int code = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }

    @Override
    public UserStatisticsVO getUserStatistics(Long userId) {
        log.info("获取用户统计数据：userId={}", userId);

        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        Long docCount = userMapper.countDocumentsByAuthorId(userId);
        Long likeCount = userMapper.sumLikesByAuthorId(userId);
        Long viewCount = userMapper.sumViewsByAuthorId(userId);

        return UserStatisticsVO.builder()
                .documentCount(docCount != null ? docCount : 0L)
                .viewCount(viewCount != null ? viewCount : 0L)
                .likeCount(likeCount != null ? likeCount : 0L)
                .commentCount(0L)
                .build();
    }

    @Override
    public TokenValidateVO validateToken(String authorization, String token) {
        // 优先从请求头提取Token，其次使用参数中的Token
        String jwtToken = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            jwtToken = authorization.substring(7);
        } else if (token != null && !token.isEmpty()) {
            jwtToken = token;
        }

        if (jwtToken == null || jwtToken.isEmpty()) {
            return TokenValidateVO.builder().valid(false).build();
        }

        try {
            Long userId = jwtTokenUtil.getUserIdFromToken(jwtToken);
            if (userId == null) {
                return TokenValidateVO.builder().valid(false).build();
            }

            if (isTokenBlacklisted(jwtToken)) {
                log.warn("Token验证失败：Token已登出，userId={}", userId);
                return TokenValidateVO.builder().valid(false).build();
            }

            UserVO user = getUserById(userId);
            if (user == null || user.getStatus() != null && user.getStatus() != 1) {
                return TokenValidateVO.builder().valid(false).build();
            }

            List<String> roles = roleMapper.selectRoleCodesByUserId(userId);

            return TokenValidateVO.builder()
                    .userId(userId)
                    .username(user.getUsername())
                    .nickname(user.getNickname())
                    .avatar(user.getAvatar())
                    .email(user.getEmail())
                    .status(user.getStatus())
                    .roles(roles)
                    .valid(true)
                    .build();
        } catch (Exception e) {
            log.warn("Token验证异常：{}", e.getMessage());
            return TokenValidateVO.builder().valid(false).build();
        }
    }

    @Override
    public List<Long> getUserIdsByRoleCode(String roleCode) {
        try {
            return roleMapper.selectUserIdsByRoleCode(roleCode);
        } catch (Exception e) {
            log.error("查询角色用户失败：roleCode={}, error={}", roleCode, e.getMessage());
            return List.of();
        }
    }
}
