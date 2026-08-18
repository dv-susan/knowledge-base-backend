package com.knowledge.base.document.filter;

import com.knowledge.base.common.utils.JwtTokenUtil;
import com.knowledge.base.common.utils.UserContextUtil;
import com.knowledge.base.document.client.UserServiceClient;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JWT认证过滤器（文档服务专用）
 *
 * <p>职责说明：</p>
 * <ul>
 *   <li>从请求头中解析JWT Token，验证用户身份</li>
 *   <li>将用户信息设置到Spring Security上下文中</li>
 *   <li>将用户信息设置到ThreadLocal上下文中，供后续业务使用</li>
 *   <li>请求结束后清理上下文，防止内存泄漏</li>
 * </ul>
 *
 * <p>与用户认证服务的区别：</p>
 * <ul>
 *   <li>不依赖UserMapper，直接从Token解析用户信息</li>
 *   <li>适用于微服务架构中的业务服务</li>
 * </ul>
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
    private UserServiceClient userServiceClient;

    @Value("${security.jwt.exclude-paths}")
    private String[] excludePaths;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * JWT Token认证处理
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        String method = request.getMethod();

        log.debug("JwtAuthenticationFilter: URI={}, Method={}", requestUri, method);

        // 从请求中提取token
        String token = extractTokenFromRequest(request);
        log.info("JwtAuthenticationFilter 处理请求: uri={}, method={}, hasToken={}", requestUri, method, token != null);

        boolean authenticated = false;

        if (token != null) {
            try {
                // 解析token获取用户信息
                Long userId = parseToken(token);
                if (userId != null) {
                    log.info("Token解析成功: userId={}, uri={}", userId, requestUri);
                    setSecurityContext(userId, token, request);
                    UserContextUtil.setUserId(userId);
                    UserContextUtil.setToken(token);
                    // 从JWT中提取用户名和头像
                    String username = jwtTokenUtil.getUsernameFromToken(token);
                    if (username != null) {
                        UserContextUtil.setUsername(username);
                    }
                    String avatar = jwtTokenUtil.getAvatarFromToken(token);
                    if (avatar != null) {
                        UserContextUtil.setAvatar(avatar);
                    }
                    authenticated = true;
                } else {
                    log.warn("JWT Token无效或已过期: uri={}, method={}", requestUri, method);
                }
            } catch (Exception e) {
                log.error("JWT认证异常: uri={}, method={}, error={}", requestUri, method, e.getMessage(), e);
            }
        } else {
            log.warn("请求未携带Token: uri={}, method={}", requestUri, method);
        }

        // Fallback: X-User-Id header (set by gateway after JWT validation)
        if (!authenticated) {
            String userIdHeader = request.getHeader("X-User-Id");
            if (userIdHeader != null) {
                try {
                    Long userId = Long.parseLong(userIdHeader);
                    log.info("使用X-User-Id header认证: userId={}, uri={}", userId, requestUri);
                    // 网关兜底认证也必须写入 SecurityContext，
                    // 否则 anyRequest().authenticated() 会把请求判定为未登录。
                    setSecurityContext(userId, token, request);

                    UserContextUtil.setUserId(userId);
                    if (token != null) {
                        UserContextUtil.setToken(token);
                    }
                    authenticated = true;
                } catch (NumberFormatException e) {
                    log.warn("X-User-Id header 格式无效: {}", userIdHeader);
                }
            }
        }

        try {
            // 执行后续过滤器
            filterChain.doFilter(request, response);
        } finally {
            // 请求结束后清理上下文，防止ThreadLocal内存泄漏
            UserContextUtil.clear();
        }
    }

    /**
     * 设置Spring Security认证上下文
     */
    private void setSecurityContext(Long userId, String token, HttpServletRequest request) {
        List<SimpleGrantedAuthority> authorities = getUserAuthorities(userId, token);

        // 创建认证对象
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userId,      // principal：用户ID
                        token,       // credentials：保留token用于追踪
                        authorities  // authorities：权限列表
                );

        // 设置认证详情
        WebAuthenticationDetails details = new WebAuthenticationDetailsSource().buildDetails(request);
        authentication.setDetails(details);

        // 将认证对象设置到Spring Security上下文中
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 权限服务不可达时的降级策略：仅授予基础读权限。
     * <p>非管理员用户不应获得修改/删除等敏感权限。</p>
     */
    private static final List<String> FALLBACK_BASIC_PERMISSIONS = List.of(
            "document:list",
            "document:category:query"
    );

    /**
     * 获取用户权限列表
     *
     * <p>降级策略（两层）：</p>
     * <ol>
     *   <li>权限列表为空（kb-user-auth 不可达/报错）→ 授予全部文档权限</li>
     *   <li>权限列表非空但缺少 {@code document:list}（数据库权限映射不完整）→ 补充基本文档权限</li>
     * </ol>
     */
    private List<SimpleGrantedAuthority> getUserAuthorities(Long userId, String token) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        // 默认添加普通用户权限
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        List<String> permissions = userServiceClient.getUserPermissions(userId, token);

        log.info("用户权限查询结果: userId={}, permissionCount={}, permissions={}", userId, permissions.size(), permissions);

        if (permissions.isEmpty()) {
            log.warn("无法从用户服务获取权限，使用降级策略：仅授予基础读权限, userId={}", userId);
            permissions = FALLBACK_BASIC_PERMISSIONS;
        } else if (!permissions.contains("document:list")) {
            // kb-user-auth 返回了权限，但缺少基础文档查看权限（数据库权限映射不完整）
            log.warn("用户权限中缺少 document:list，自动补充基础权限, userId={}", userId);
            List<String> augmented = new ArrayList<>(permissions);
            augmented.add("document:list");
            augmented.add("document:category:query");
            permissions = augmented;
        }

        for (String permission : permissions) {
            authorities.add(new SimpleGrantedAuthority(permission));
        }
        return authorities;
    }

    /**
     * 解析Token获取用户ID
     *
     * <p>直接解析Token提取userId，跳过过期校验（过期由前端刷新Token处理）。</p>
     */
    private Long parseToken(String token) {
        return jwtTokenUtil.getUserIdFromToken(token);
    }

    /**
     * 从请求头中提取Token
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        // 优先从 Authorization header 读取
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        // 回退：从 cookie 读取（用于 <video>/<audio>/fetch() 等不走 axios 的请求）
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
     * 支持 Ant 风格路径匹配。</p>
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
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
}
