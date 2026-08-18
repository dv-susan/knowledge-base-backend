package com.knowledge.base.foundation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 系统状态VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "系统状态")
public class SystemStatusVO implements Serializable {

    @Schema(description = "系统版本")
    private String version;

    @Schema(description = "运行状态：running/stopped/maintenance")
    private String runStatus;

    @Schema(description = "数据库连接状态：connected/disconnected")
    private String dbStatus;

    @Schema(description = "上次备份时间")
    private String lastBackupTime;

    @Schema(description = "总存储空间（字节）")
    private Long totalStorage;

    @Schema(description = "已用存储空间（字节）")
    private Long usedStorage;

    @Schema(description = "文档总数")
    private Long documentCount;

    @Schema(description = "用户总数")
    private Long userCount;

    @Schema(description = "系统启动时间")
    private String startTime;
}
