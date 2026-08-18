package com.knowledge.base.document.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 文档相邻篇响应VO
 *
 * <p>用于文档详情页上一篇/下一篇导航</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "文档相邻篇信息")
public class DocumentNeighborVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "上一篇文档ID")
    private Long prevId;

    @Schema(description = "上一篇文档标题")
    private String prevTitle;

    @Schema(description = "下一篇文档ID")
    private Long nextId;

    @Schema(description = "下一篇文档标题")
    private String nextTitle;
}
