package com.knowledge.base.document.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.knowledge.base.common.config.BaseEntity;
import com.knowledge.base.document.enums.TagTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 标签实体
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_tag")
@Schema(description = "标签实体")
public class Tag extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 标签ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "标签ID")
    private Long id;

    /**
     * 标签名称
     */
    @Schema(description = "标签名称")
    private String tagName;

    /**
     * 标签编码
     */
    @Schema(description = "标签编码")
    private String tagCode;

    /**
     * 所属分类ID
     */
    @Schema(description = "所属分类ID")
    private Long categoryId;

    /**
     * 标签类型：0-SYSTEM，1-USER
     */
    @Schema(description = "标签类型")
    private Integer tagType;

    /**
     * 颜色
     */
    @Schema(description = "颜色")
    private String color;

    /**
     * 图标
     */
    @Schema(description = "图标")
    private String icon;

    /**
     * 文档数量
     */
    @Schema(description = "文档数量")
    private Integer docCount;

    /**
     * 状态：0-禁用，1-正常
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 删除标记
     */
    @TableLogic
    @Schema(description = "删除标记")
    private Integer deleted;

    /**
     * 获取标签类型枚举
     *
     * @return 标签类型枚举
     */
    public TagTypeEnum getTagTypeEnum() {
        return TagTypeEnum.of(this.tagType);
    }

    /**
     * 设置标签类型（枚举）
     *
     * @param tagTypeEnum 标签类型枚举
     */
    public void setTagTypeEnum(TagTypeEnum tagTypeEnum) {
        if (tagTypeEnum != null) {
            this.tagType = tagTypeEnum.getCode();
        }
    }
}
