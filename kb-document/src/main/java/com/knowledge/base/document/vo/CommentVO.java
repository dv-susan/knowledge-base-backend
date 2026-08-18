package com.knowledge.base.document.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "评论信息")
public class CommentVO {

    @Schema(description = "评论ID")
    private Long id;

    @Schema(description = "文档ID")
    private Long documentId;

    @Schema(description = "父评论ID")
    private Long parentId;

    @Schema(description = "根评论ID")
    private Long rootId;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "评论人ID")
    private Long commenterId;

    @Schema(description = "评论人姓名")
    private String commenterName;

    @Schema(description = "评论人头像")
    private String commenterAvatar;

    @Schema(description = "回复给谁（用户ID）")
    private Long replyToUserId;

    @Schema(description = "回复给谁（用户姓名）")
    private String replyToUserName;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "回复数")
    private Integer replyCount;

    @Schema(description = "是否已点赞")
    private Boolean isLiked;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "子评论列表")
    private List<CommentVO> replies;
}
