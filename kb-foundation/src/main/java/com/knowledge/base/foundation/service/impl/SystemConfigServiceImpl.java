package com.knowledge.base.foundation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.base.common.config.SystemConfigCache;
import com.knowledge.base.common.exception.BusinessException;
import com.knowledge.base.common.utils.SnowflakeIdGenerator;
import com.knowledge.base.foundation.entity.SystemConfig;
import com.knowledge.base.foundation.mapper.SystemConfigMapper;
import com.knowledge.base.foundation.service.SystemConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig> implements SystemConfigService {

    @Resource
    private SystemConfigMapper systemConfigMapper;

    @Resource
    private SystemConfigCache systemConfigCache;

    /**
     * 服务启动完成后（DB、Redis 等基础设施就绪）将所有配置加载到 Redis 缓存
     */
    @EventListener(ApplicationReadyEvent.class)
    public void loadConfigsToRedis() {
        log.info("开始加载系统配置到 Redis 缓存...");
        try {
            List<SystemConfig> configs = systemConfigMapper.selectList(
                    new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getDeleted, 0));
            Map<String, String> configMap = new HashMap<>();
            for (SystemConfig config : configs) {
                configMap.put(config.getConfigKey(), config.getConfigValue());
            }
            systemConfigCache.loadAll(configMap);
        } catch (Exception e) {
            log.error("加载系统配置到 Redis 缓存失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 写入 Redis 缓存（事务提交后执行，避免回滚覆盖）
     */
    private void syncToRedis(SystemConfig config) {
        try {
            systemConfigCache.setConfig(config.getConfigKey(), config.getConfigValue());
        } catch (Exception e) {
            log.error("同步配置到 Redis 失败：key={}, error={}", config.getConfigKey(), e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public IPage<SystemConfig> pageConfigs(Long current, Long size, String category) {
        log.info("分页查询配置：current={}, size={}, category={}", current, size, category);

        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(category)) {
            wrapper.eq(SystemConfig::getCategory, category);
        }

        wrapper.orderByAsc(SystemConfig::getId);

        Page<SystemConfig> page = new Page<>(current, size);
        return systemConfigMapper.selectPage(page, wrapper);
    }

    /** {@inheritDoc} */
    @Override
    public SystemConfig getConfigByKey(String key) {
        log.info("获取配置：key={}", key);

        if (!StringUtils.hasText(key)) {
            throw new BusinessException("配置键不能为空");
        }

        return systemConfigMapper.selectByConfigKey(key);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean createConfig(SystemConfig config) {
        log.info("创建配置：key={}", config.getConfigKey());

        if (!StringUtils.hasText(config.getConfigKey())) {
            throw new BusinessException("配置键不能为空");
        }

        SystemConfig existConfig = systemConfigMapper.selectByConfigKey(config.getConfigKey());
        if (existConfig != null) {
            throw new BusinessException("配置键已存在");
        }

        config.setId(SnowflakeIdGenerator.getInstance().nextId());
        config.setCreatedAt(LocalDateTime.now());
        config.setUpdatedAt(LocalDateTime.now());

        int count = systemConfigMapper.insert(config);
        if (count > 0) {
            syncToRedis(config);
        }
        return count > 0;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateConfig(String key, SystemConfig config) {
        log.info("更新配置：key={}", key);

        SystemConfig existConfig = systemConfigMapper.selectByConfigKey(key);
        if (existConfig == null) {
            throw new BusinessException("配置不存在");
        }

        existConfig.setConfigValue(config.getConfigValue());
        existConfig.setDescription(config.getDescription());
        existConfig.setCategory(config.getCategory());
        existConfig.setIsPublic(config.getIsPublic());
        existConfig.setUpdatedAt(LocalDateTime.now());

        int count = systemConfigMapper.updateById(existConfig);
        if (count > 0) {
            syncToRedis(existConfig);
        }
        return count > 0;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteConfig(String key) {
        log.info("删除配置：key={}", key);

        SystemConfig existConfig = systemConfigMapper.selectByConfigKey(key);
        if (existConfig == null) {
            throw new BusinessException("配置不存在");
        }

        int count = systemConfigMapper.deleteById(existConfig.getId());
        if (count > 0) {
            systemConfigCache.deleteConfig(key);
        }
        return count > 0;
    }

    /** {@inheritDoc} */
    @Override
    public List<SystemConfig> getConfigsByCategory(String category) {
        log.info("按分类获取配置：category={}", category);

        if (!StringUtils.hasText(category)) {
            throw new BusinessException("配置分类不能为空");
        }

        return systemConfigMapper.selectByCategory(category);
    }

    /** {@inheritDoc} */
    @Override
    public List<SystemConfig> getPublicConfigs() {
        log.info("获取公开配置");

        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getIsPublic, 1);

        return systemConfigMapper.selectList(wrapper);
    }
}