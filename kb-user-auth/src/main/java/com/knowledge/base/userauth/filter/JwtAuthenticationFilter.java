package com.knowledge.base.userauth.filter;

import cn.hutool.crypto.digest.DigestUtil;
import com.knowledge.base.common.utils.JwtTokenUtil;
import com.knowledge.base.common.utils.UserContextUtil;
import com.knowledge.base.userauth.entity.User;
import com.knowledge.base.userauth.mapper.RoleMapper;
import com.knowledge.base.userauth.mapper.UserMapper;
import com.knowledge.base.userauth.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JWT认证过滤器
 *
 * <p>职责说明：</p>
 * <ul>
 *   <li>从请求头中解析JWT Token，验证用户身份</li>
 *   <li>将用户信息设置到Spring Security上下文中</li>
 *   <li>将用户信息设置到ThreadLocal上下文中，供后续业务使用</li>
 *   <li>支持真实JWT Token和Mock Token（开发测试用）</li>
 *   <li>请求结束后清理上下文，防止内存泄漏</li>
 * </ul>
 *
 * <p>执行顺序：在Spring Security过滤器之前执行</p>
 * <p>异常处理：认证失败不阻断请求，由业务层判断是否需要认证</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private UserService userService;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Value("${security.jwt.exclude-paths}")
    private String[] excludePaths;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * JWT Token认证处理
     *
     * <p>处理流程：</p>
     * <ol>
     *   <li>从请求头提取Token</li>
     *   <li>验证Token有效性</li>
     *   <li>解析用户信息并设置到Spring Security上下文</li>
     *   <li>解析用户信息并设置到ThreadLocal上下文</li>
     *   <li>执行后续过滤器</li>
     *   <li>清理上下文</li>
     * </ol>
     *
     * @param request  HTTP请求
     * @param response HTTP响应
     * @param filterChain 过滤器链
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        String method = request.getMethod();

        log.info("========== JwtAuthenticationFilter 开始 ==========");
        log.info("请求URI: {}, Method: {}", requestUri, method);

        // 从请求中提取token
        String token = extractTokenFromRequest(request);
        log.info("提取的Token: {}", token != null ? token.substring(0, Math.min(30, token.length())) + "..." : "null");

        if (token != null) {
            try {
                // 检查Token是否在黑名单中（已退出登录）
                if (isTokenBlacklisted(token)) {
                    log.warn("Token已被拉黑（已退出登录）: uri={}", requestUri);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":401,\"message\":\"Token已失效，请重新登录\"}");
                    return;
                }

                // 解析token获取用户信息
                UserInfo userInfo = parseToken(token);
                if (userInfo != null) {
                    log.info("Token解析成功: userId={}, username={}", userInfo.getUserId(), userInfo.getUsername());

                    // 设置Spring Security认证上下文
                    setSecurityContext(userInfo, token, request);

                    // 设置自定义ThreadLocal上下文
                    UserContextUtil.setUserId(userInfo.getUserId());
                    UserContextUtil.setUsername(userInfo.getUsername());
                    UserContextUtil.setToken(token);

                    log.info("JWT认证成功，已设置SecurityContext");
                    log.info("SecurityContext中的认证信息: {}", SecurityContextHolder.getContext().getAuthentication());
                } else {
                    log.warn("JWT Token无效或已过期: uri={}, method={}", requestUri, method);
                }
            } catch (Exception e) {
                log.error("JWT认证异常: uri={}, method={}, error={}", requestUri, method, e.getMessage(), e);
                // 认证失败不阻断请求，由业务层判断是否需要认证
            }
        } else {
            log.warn("请求未携带Token: uri={}, method={}", requestUri, method);
        }

        try {
            log.info("执行后续过滤器链...");
            // 执行后续过滤器
            filterChain.doFilter(request, response);
            log.info("后续过滤器链执行完毕，响应状态: {}", response.getStatus());
        } finally {
            // 请求结束后清理上下文，防止ThreadLocal内存泄漏
            UserContextUtil.clear();
            log.info("========== JwtAuthenticationFilter 结束 ==========");
        }
    }

    /**
     * 检查Token是否在黑名单中（已退出登录）
     *
     * @param token 原始Token
     * @return true-已拉黑，false-正常
     */
    private boolean isTokenBlacklisted(String token) {
        try {
            String tokenHash = DigestUtil.sha256Hex(token);
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tb_token_blacklist WHERE token_hash = ? AND expire_time > NOW()",
                Integer.class,
                tokenHash
            );
            return count != null && count > 0;
        } catch (Exception e) {
            log.error("检查Token黑名单失败：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 设置Spring Security认证上下文
     *
     * <p>这是解决403错误的关键步骤</p>
     * <p>Spring Security通过SecurityContextHolder判断用户是否已认证</p>
     *
     * @param userInfo 用户信息
     * @param token JWT Token
     * @param request HTTP请求
     */
    private void setSecurityContext(UserInfo userInfo, String token, HttpServletRequest request) {
        log.info("开始设置SecurityContext...");

        List<SimpleGrantedAuthority> authorities = getUserAuthorities(userInfo);
        log.info("用户权限列表: {}", authorities);

        // 创建认证对象
        // 使用三个参数的构造函数：principal, credentials, authorities
        // 这样创建的对象会自动标记为已认证（authenticated=true）
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userInfo.getUserId(),  // principal：用户ID
                        token,                // credentials：保留token用于追踪
                        authorities          // authorities：权限列表
                );

        log.info("创建认证对象: principal={}, credentials长度={}, authorities={}",
                userInfo.getUserId(), token.length(), authorities);

        // 设置认证详情
        WebAuthenticationDetails details = new WebAuthenticationDetailsSource().buildDetails(request);
        authentication.setDetails(details);
        log.info("设置认证详情: remoteAddress={}, sessionId={}",
                details.getRemoteAddress(), details.getSessionId());

        // 将认证对象设置到Spring Security上下文中
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("已将认证对象设置到SecurityContextHolder");

        // 验证设置是否成功
        var auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("验证SecurityContext: is null={}, isAuthenticated={}, principal={}, authorities={}, class={}",
                auth == null,
                auth != null && auth.isAuthenticated(),
                auth != null ? auth.getPrincipal() : null,
                auth != null ? auth.getAuthorities() : null,
                auth != null ? auth.getClass().getSimpleName() : null);
    }

    /**
     * 获取用户完整权限列表
     *
     * <p>从数据库加载用户的角色编码和权限编码，构建 Spring Security Authority 列表。</p>
     * <ol>
     *   <li>角色编码转为 ROLE_ 前缀的 authority（如 ROLE_USER、ROLE_REVIEWER）</li>
     *   <li>权限编码直接作为 authority（如 document:list、system:user）</li>
     *   <li>权限列表为空时兜底授予 ROLE_USER</li>
     * </ol>
     *
     * @param userInfo 用户信息
     * @return 权限列表
     */
    private List<SimpleGrantedAuthority> getUserAuthorities(UserInfo userInfo) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        try {
            // 1. 从数据库加载用户角色编码
            List<String> roleCodes = roleMapper.selectRoleCodesByUserId(userInfo.getUserId());
            if (roleCodes != null) {
                for (String roleCode : roleCodes) {
                    // 确保角色以 ROLE_ 前缀
                    String roleWithPrefix = roleCode.startsWith("ROLE_") ? roleCode : "ROLE_" + roleCode;
                    authorities.add(new SimpleGrantedAuthority(roleWithPrefix));
                }
            }

            // 2. 从数据库加载用户权限编码（直接权限 + 角色继承权限）
            List<String> permissions = userService.getUserPermissions(userInfo.getUserId());
            if (permissions != null) {
                for (String permission : permissions) {
                    authorities.add(new SimpleGrantedAuthority(permission));
                }
            }
        } catch (Exception e) {
            log.error("加载用户权限失败：userId={}, error={}", userInfo.getUserId(), e.getMessage());
        }

        // 3. 兜底：确保至少有一个角色
        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return authorities;
    }

    /**
     * 解析Token获取用户信息
     *
     * <p>支持两种Token格式：</p>
     * <ol>
     *   <li>真实JWT Token：使用JwtTokenUtil解析</li>
     *   <li>Mock Token：开发测试使用，格式为 mock-access-token-{userId}</li>
     * </ol>
     *
     * @param token JWT Token
     * @return 用户信息，解析失败返回null
     */
    private UserInfo parseToken(String token) {
        // 优先尝试解析真实的JWT Token
        if (jwtTokenUtil.validateToken(token)) {
            Long userId = jwtTokenUtil.getUserIdFromToken(token);
            if (userId != null) {
                return loadUserInfo(userId);
            }
        }

        // 开发环境：支持Mock Token
        if (token.startsWith("mock-access-token-")) {
            return parseMockToken(token);
        }

        return null;
    }

    /**
     * 加载用户信息
     *
     * <p>从数据库查询用户详细信息，避免每次请求都查询数据库</p>
     * <p>后续可优化为缓存实现</p>
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    private UserInfo loadUserInfo(Long userId) {
        try {
            User user = userMapper.selectById(userId);
            if (user != null && user.getStatus() == 1) {
                return UserInfo.builder()
                        .userId(user.getId())
                        .username(user.getUsername())
                        .build();
            }
        } catch (Exception e) {
            log.error("查询用户信息失败: userId={}", userId, e);
        }
        return null;
    }

    /**
     * 解析Mock Token（开发测试用）
     *
     * @param token Mock Token
     * @return 用户信息
     */
    private UserInfo parseMockToken(String token) {
        try {
            String userIdStr = token.substring("mock-access-token-".length());
            Long userId = Long.parseLong(userIdStr);
            return loadUserInfo(userId);
        } catch (NumberFormatException e) {
            log.error("解析Mock Token失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 从请求头中提取Token
     *
     * <p>支持两种格式：</p>
     * <ol>
     *   <li>Authorization: Bearer {token}</li>
     *   <li>Authorization: {token}</li>
     * </ol>
     *
     * @param request HTTP请求
     * @return Token字符串，无Token返回null
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        // 优先从 Authorization header 读取
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        // 回退：从 cookie 读取（用于不走 axios 拦截器的浏览器原生请求）
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName())) {
                    String cookieToken = cookie.getValue();
                    if (cookieToken != null && !cookieToken.isBlank()) {
                        return cookieToken;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 判断是否需要跳过此过滤器
     *
     * <p>通过 {@code security.jwt.exclude-paths} 配置排除路径，
     * 支持 Ant 风格路径匹配（如 {@code /public/**}）。
     * OPTIONS 预检请求始终跳过。</p>
     *
     * @param request HTTP请求
     * @return true-跳过过滤，false-执行过滤
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // OPTIONS 预检请求始终跳过
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();
        for (String pattern : excludePaths) {
            if (pathMatcher.match(pattern.trim(), path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 用户信息内部类
     */
    @lombok.Data
    @lombok.Builder
    private static class UserInfo {
        private Long userId;
        private String username;
    }
}
