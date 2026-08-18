package com.knowledge.base.foundation.service;

import com.knowledge.base.foundation.dto.SettingsDTO;
import com.knowledge.base.foundation.vo.SettingsVO;
import com.knowledge.base.foundation.vo.SystemStatusVO;

/**
 * 系统设置Service接口
 *
 * <p>提供按分组查询和批量更新系统设置的能力，封装了底层配置表的读写操作。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface SettingsService {

    /**
     * 获取全部系统设置（按分组返回）
     */
    SettingsVO getSettings();

    /**
     * 按分组批量更新设置
     */
    Boolean updateSettings(SettingsDTO settingsDTO);

    /**
     * 获取系统运行状态
     */
    SystemStatusVO getSystemStatus();

    /**
     * 清理系统缓存
     */
    String clearCache();

    /**
     * 创建数据备份
     */
    String createBackup();
}
