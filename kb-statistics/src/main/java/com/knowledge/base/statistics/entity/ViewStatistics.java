package com.knowledge.base.statistics.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 浏览统计实体
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@TableName("kb_view_history")
public class ViewStatistics {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 文档ID
     */
    private Long documentId;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
