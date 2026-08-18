package com.knowledge.base.document.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Caffeine 本地缓存配置
 *
 * <p>为高频读取的分类树等数据提供 JVM 内存级缓存，
 * 避免每次请求都走 Redis 网络调用。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Configuration
public class CaffeineCacheConfig {

    /**
     * Caffeine 本地缓存管理器
     *
     * <p>默认 5 分钟过期，适用于分类树等低频变更场景。
     * 数据变更时通过 @CacheEvict 主动驱逐。</p>
     *
     * @return Caffeine CacheManager
     */
    @Bean
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(20)
                .recordStats());
        return cacheManager;
    }
}
