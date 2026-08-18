package com.knowledge.base.search.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.knowledge.base.common.utils.SnowflakeIdGenerator;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 搜索历史实体
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@TableName("kb_search_history")
public class SearchHistory {

    /**
     * 主键ID（雪花算法生成）
     */
    @TableId(type = IdType.INPUT)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 搜索次数
     */
    private Integer searchCount;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 插入前自动填充雪花ID
     */
    public void preInsert() {
        if (this.id == null) {
            this.id = SnowflakeIdGenerator.getInstance().nextId();
        }
    }
}
