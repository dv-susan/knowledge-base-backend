package com.knowledge.base.ai.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 本地缓存配置
 *
 * <p>使用Caffeine作为本地缓存实现，为频繁访问的只读数据提供高性能缓存。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 配置Caffeine CacheManager
     *
     * <p>写作模板等只读数据缓存24小时，避免每次请求都重建列表。</p>
     *
     * @return CacheManager实例
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .maximumSize(100)
                .recordStats());
        return cacheManager;
    }
}
