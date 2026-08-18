package com.knowledge.base.foundation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.knowledge.base.foundation.entity.SystemConfig;

import java.util.List;

/**
 * 系统配置Service接口
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface SystemConfigService {

    /**
     * 分页查询系统配置
     *
     * @param current  当前页
     * @param size     每页大小
     * @param category 分类（可选）
     * @return 分页结果
     */
    IPage<SystemConfig> pageConfigs(Long current, Long size, String category);

    /**
     * 根据配置键获取配置
     *
     * @param key 配置键
     * @return 配置信息
     */
    SystemConfig getConfigByKey(String key);

    /**
     * 创建系统配置
     *
     * @param config 配置信息
     * @return 是否成功
     */
    Boolean createConfig(SystemConfig config);

    /**
     * 更新系统配置
     *
     * @param key    配置键
     * @param config 新配置信息
     * @return 是否成功
     */
    Boolean updateConfig(String key, SystemConfig config);

    /**
     * 删除系统配置
     *
     * @param key 配置键
     * @return 是否成功
     */
    Boolean deleteConfig(String key);

    /**
     * 根据分类获取配置列表
     *
     * @param category 分类
     * @return 配置列表
     */
    List<SystemConfig> getConfigsByCategory(String category);

    /**
     * 获取可公开访问的配置列表
     *
     * @return 公开配置列表
     */
    List<SystemConfig> getPublicConfigs();
}
