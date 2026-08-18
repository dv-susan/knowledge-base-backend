package com.knowledge.base.foundation.feign;

import com.knowledge.base.common.result.Result;
import com.knowledge.base.foundation.dto.TokenValidateVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * UserAuthFeignClient 降级工厂
 *
 * <p>当 kb-user-auth 服务不可用时，返回 valid=false 的默认响应，
 * 确保基础服务不会因为用户服务故障而完全不可用。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Component
public class UserAuthFeignFallbackFactory implements FallbackFactory<UserAuthFeignClient> {

    @Override
    public UserAuthFeignClient create(Throwable cause) {
        log.error("UserAuthFeignClient 调用失败，启用降级逻辑：{}", cause.getMessage());

        return new UserAuthFeignClient() {
            @Override
            public Result<TokenValidateVO> validateToken(String authorization, String token) {
                log.warn("Token验证降级：kb-user-auth 服务不可用，返回 Token 无效");
                return Result.success(
                        TokenValidateVO.builder().valid(false).build());
            }

            @Override
            public Result<List<Long>> getUserIdsByRole(String roleCode) {
                log.warn("角色用户查询降级：kb-user-auth 服务不可用，返回空列表");
                return Result.success(List.of());
            }
        };
    }
}
