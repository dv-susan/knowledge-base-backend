package com.knowledge.base.foundation.controller;

import com.knowledge.base.common.result.Result;
import com.knowledge.base.foundation.dto.SettingsDTO;
import com.knowledge.base.foundation.dto.TestEmailDTO;
import com.knowledge.base.foundation.service.SettingsService;
import com.knowledge.base.foundation.vo.SettingsVO;
import com.knowledge.base.foundation.vo.SystemStatusVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 系统设置Controller
 *
 * <p>提供按分组读写系统设置的统一入口，前端SettingsPage通过此接口加载和保存各分组设置。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/config/settings")
@Tag(name = "系统设置", description = "系统设置分组管理接口")
public class SettingsController {

    @Resource
    private SettingsService settingsService;

    /**
     * 获取全部系统设置
     */
    @GetMapping
    @Operation(summary = "获取全部设置", description = "返回按分组组织的设置项和系统状态")
    public Result<SettingsVO> getSettings() {
        SettingsVO settings = settingsService.getSettings();
        return Result.success(settings);
    }

    /**
     * 按分组更新设置
     */
    @PutMapping
    @Operation(summary = "批量更新设置", description = "更新指定分组下的设置项，未传入的字段保持不变")
    public Result<Boolean> updateSettings(@Valid @RequestBody SettingsDTO settingsDTO) {
        Boolean result = settingsService.updateSettings(settingsDTO);
        return Result.success("设置已保存", result);
    }

    /**
     * 获取系统运行状态
     */
    @GetMapping("/status")
    @Operation(summary = "获取系统状态", description = "返回系统版本、数据库连接状态、存储使用情况等运行指标")
    public Result<SystemStatusVO> getSystemStatus() {
        SystemStatusVO status = settingsService.getSystemStatus();
        return Result.success(status);
    }

    /**
     * 清理系统缓存
     */
    @PostMapping("/cache/clear")
    @Operation(summary = "清理缓存", description = "清理Redis等系统缓存以释放存储空间")
    public Result<String> clearCache() {
        String result = settingsService.clearCache();
        return Result.success("缓存清理完成", result);
    }

    /**
     * 创建数据备份
     */
    @PostMapping("/backup")
    @Operation(summary = "创建备份", description = "创建系统数据的完整备份")
    public Result<String> createBackup() {
        String result = settingsService.createBackup();
        return Result.success("备份创建成功", result);
    }
}
