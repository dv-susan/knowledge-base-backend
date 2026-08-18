package com.knowledge.base.foundation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.knowledge.base.common.config.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 字典数据实体类
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("kb_dict_data")
@Schema(description = "字典数据实体")
public class DictData extends BaseEntity {

    @Schema(description = "字典ID")
    @TableField("dict_id")
    private Long dictId;

    @Schema(description = "字典编码（冗余）")
    @TableField("dict_code")
    private String dictCode;

    @Schema(description = "字典标签")
    @TableField("dict_label")
    private String dictLabel;

    @Schema(description = "字典值")
    @TableField("dict_value")
    private String dictValue;

    @Schema(description = "排序")
    @TableField("dict_sort")
    private Integer dictSort;

    @Schema(description = "样式类名")
    @TableField("css_class")
    private String cssClass;

    @Schema(description = "列表样式")
    @TableField("list_class")
    private String listClass;

    @Schema(description = "是否默认：0-否，1-是")
    @TableField("is_default")
    private Integer isDefault;

    @Schema(description = "状态：0-禁用，1-正常")
    @TableField("status")
    private Integer status;

}
