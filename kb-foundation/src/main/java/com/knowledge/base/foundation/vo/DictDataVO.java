package com.knowledge.base.foundation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 字典数据VO
 *
 * <p>按照阿里巴巴Java开发规范设计，用于返回字典数据信息</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "字典数据响应")
public class DictDataVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典数据ID
     */
    @Schema(description = "字典数据ID")
    private Long id;

    /**
     * 字典ID
     */
    @Schema(description = "字典ID")
    private Long dictId;

    /**
     * 字典编码（冗余）
     */
    @Schema(description = "字典编码")
    private String dictCode;

    /**
     * 字典标签
     */
    @Schema(description = "字典标签")
    private String dictLabel;

    /**
     * 字典值
     */
    @Schema(description = "字典值")
    private String dictValue;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer dictSort;

    /**
     * 样式类名
     */
    @Schema(description = "样式类名")
    private String cssClass;

    /**
     * 列表样式
     */
    @Schema(description = "列表样式")
    private String listClass;

    /**
     * 是否默认：0-否，1-是
     */
    @Schema(description = "是否默认")
    private Integer isDefault;

    /**
     * 状态：0-禁用，1-正常
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
