package com.knowledge.base.userauth.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Caffeine 本地缓存配置（L1 缓存层）
 *
 * <p>将频繁读取的数据（如团队空间树）缓存在应用内存中，
 * 避免每次菜单渲染都走 Redis 网络请求。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Configuration
public class CaffeineCacheConfig {

    /**
     * Caffeine 本地缓存管理器（L1）
     *
     * <p>过期时间设为 35 分钟，数据变更时主动驱逐。
     * 团队结构变更频次极低，无需短 TTL。</p>
     *
     * @return Caffeine CacheManager 实例
     */
    @Bean
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(35, TimeUnit.MINUTES)
                .maximumSize(10)
                .recordStats());
        return cacheManager;
    }
}
