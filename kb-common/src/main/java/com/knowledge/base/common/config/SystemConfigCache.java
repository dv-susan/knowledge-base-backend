package com.knowledge.base.common.config;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统配置 Redis 缓存
 *
 * <p>所有系统配置（kb_system_config）统一存储在 Redis Hash 中，
 * 各微服务从缓存读取，避免跨库直连数据库。</p>
 *
 * <ul>
 *   <li>Redis Key: {@code kb:system:config}</li>
 *   <li>Hash Field: config_key</li>
 *   <li>Hash Value: config_value</li>
 * </ul>
 *
 * @author 苏三
 * @since 1.1.0
 */
@Slf4j
@Component
public class SystemConfigCache {

    /** Redis Hash 键名 */
    public static final String REDIS_HASH_KEY = "kb:system:config";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取单个配置值
     *
     * @param configKey 配置键
     * @return 配置值，不存在时返回 null
     */
    public String getConfig(String configKey) {
        Object value = stringRedisTemplate.opsForHash().get(REDIS_HASH_KEY, configKey);
        return value != null ? value.toString() : null;
    }

    /**
     * 获取单个配置值（带默认值）
     *
     * @param configKey    配置键
     * @param defaultValue 默认值
     * @return 配置值，不存在时返回默认值
     */
    public String getConfig(String configKey, String defaultValue) {
        String value = getConfig(configKey);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取所有配置（键值对）
     *
     * @return 所有配置的 Map
     */
    public Map<String, String> getAllConfigs() {
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(REDIS_HASH_KEY);
        Map<String, String> result = new HashMap<>();
        entries.forEach((k, v) -> result.put(k.toString(), v.toString()));
        return result;
    }

    /**
     * 写入单个配置到缓存
     *
     * @param configKey   配置键
     * @param configValue 配置值
     */
    public void setConfig(String configKey, String configValue) {
        stringRedisTemplate.opsForHash().put(REDIS_HASH_KEY, configKey, configValue);
        log.debug("配置已写入缓存：{} = {}", configKey, configValue);
    }

    /**
     * 从缓存中删除单个配置
     *
     * @param configKey 配置键
     */
    public void deleteConfig(String configKey) {
        stringRedisTemplate.opsForHash().delete(REDIS_HASH_KEY, configKey);
        log.debug("配置已从缓存删除：{}", configKey);
    }

    /**
     * 批量加载配置到缓存（由 kb-foundation 在启动或刷新时调用）
     *
     * @param configs 配置键值对
     */
    public void loadAll(Map<String, String> configs) {
        if (configs == null || configs.isEmpty()) {
            log.warn("批量加载配置为空，跳过");
            return;
        }
        // 先清空旧缓存再批量写入
        stringRedisTemplate.delete(REDIS_HASH_KEY);
        stringRedisTemplate.opsForHash().putAll(REDIS_HASH_KEY, configs);
        log.info("已批量加载 {} 条配置到 Redis 缓存", configs.size());
    }

    /**
     * 刷新单条配置（仅在缓存中存在时更新，用于防并发写覆盖）
     *
     * @param configKey   配置键
     * @param configValue 配置值
     */
    public void refreshConfig(String configKey, String configValue) {
        Boolean exists = stringRedisTemplate.opsForHash().hasKey(REDIS_HASH_KEY, configKey);
        if (Boolean.TRUE.equals(exists)) {
            setConfig(configKey, configValue);
        }
    }
}
