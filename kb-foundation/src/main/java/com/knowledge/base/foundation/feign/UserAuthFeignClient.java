package com.knowledge.base.foundation.feign;

import com.knowledge.base.common.result.Result;
import com.knowledge.base.foundation.dto.TokenValidateVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 用户认证Feign客户端
 *
 * <p>通过 Feign 调用 kb-user-auth 服务的认证接口，复用用户服务的登录和认证逻辑。
 * 避免在各微服务中重复实现 JWT 解析、黑名单检查、角色查询等逻辑。</p>
 *
 * <p>调用的接口：</p>
 * <ul>
 *   <li>POST /auth/validate — 验证JWT Token，返回用户身份和角色信息</li>
 * </ul>
 *
 * @author 苏三
 * @since 1.0.0
 */
@FeignClient(
        name = "kb-user-auth",
        url = "${kb-user-auth.url:#{null}}",
        path = "/auth",
        fallbackFactory = UserAuthFeignFallbackFactory.class
)
public interface UserAuthFeignClient {

    /**
     * 验证JWT Token
     *
     * <p>将Token发送到 kb-user-auth 进行验证，kb-user-auth 负责：</p>
     * <ol>
     *   <li>解析JWT Token</li>
     *   <li>检查Token黑名单（是否已登出）</li>
     *   <li>查询用户信息</li>
     *   <li>查询用户角色列表</li>
     * </ol>
     *
     * @param authorization Authorization 请求头（Bearer <token>）
     * @param token Token参数（备选传递方式）
     * @return Token验证结果，包含 valid、userId、username、roles 等信息
     */
    @PostMapping("/validate")
    Result<TokenValidateVO> validateToken(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(value = "token", required = false) String token);

    /**
     * 根据角色编码查询用户ID列表
     *
     * <p>调用 kb-user-auth 的 /auth/users/by-role 接口（基于 MyBatis），
     * 查询拥有指定角色的所有用户ID。</p>
     *
     * @param roleCode 角色编码，如 ROLE_REVIEWER
     * @return 用户ID列表
     */
    @GetMapping("/users/by-role")
    Result<List<Long>> getUserIdsByRole(@RequestParam("roleCode") String roleCode);
}
