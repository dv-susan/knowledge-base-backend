package com.knowledge.base.file.vo;

import com.knowledge.base.file.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件分类VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "文件分类响应")
public class CategoryVO {

    /**
     * 分类ID
     */
    @Schema(description = "分类ID")
    private Long id;

    /**
     * 分类名称
     */
    @Schema(description = "分类名称")
    private String name;

    /**
     * 父分类ID
     */
    @Schema(description = "父分类ID")
    private Long parentId;

    /**
     * 分类层级
     */
    @Schema(description = "分类层级")
    private Integer level;

    /**
     * 排序序号
     */
    @Schema(description = "排序序号")
    private Integer sortOrder;

    /**
     * 分类图标
     */
    @Schema(description = "分类图标")
    private String icon;

    /**
     * 分类描述
     */
    @Schema(description = "分类描述")
    private String description;

    /**
     * 状态：0-禁用，1-启用
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 子分类列表
     */
    @Schema(description = "子分类列表")
    private List<CategoryVO> children = new ArrayList<>();

    /**
     * 从实体转换为VO
     *
     * @param entity 分类实体
     * @return 分类VO
     */
    public static CategoryVO fromEntity(Category entity) {
        if (entity == null) {
            return null;
        }
        CategoryVO vo = new CategoryVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setParentId(entity.getParentId());
        vo.setLevel(entity.getLevel());
        vo.setSortOrder(entity.getSortOrder());
        vo.setIcon(entity.getIcon());
        vo.setDescription(entity.getDescription());
        vo.setStatus(entity.getStatus());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setChildren(new ArrayList<>());
        return vo;
    }
}
