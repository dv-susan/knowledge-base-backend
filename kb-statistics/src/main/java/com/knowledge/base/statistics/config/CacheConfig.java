package com.knowledge.base.statistics.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Caffeine 本地缓存配置
 *
 * <p>作为 L1 本地缓存层，配合 Redis L2 缓存使用。
 * 热门文档等高频读取数据优先从 Caffeine 读取，减少 Redis 网络开销。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Configuration
public class CacheConfig {

    /**
     * Caffeine 本地缓存管理器（L1）
     *
     * <p>过期时间设为35分钟，略大于30分钟的刷新周期，
     * 确保定时任务刷新前缓存不会过期。</p>
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
