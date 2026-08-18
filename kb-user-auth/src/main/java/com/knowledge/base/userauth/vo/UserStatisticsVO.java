package com.knowledge.base.userauth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户统计响应VO
 *
 * <p>返回用户在知识库中的统计数据（文档数、浏览量、点赞数、评论数）</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户统计数据")
public class UserStatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "文档数量", example = "12")
    private Long documentCount;

    @Schema(description = "总浏览量", example = "345")
    private Long viewCount;

    @Schema(description = "总获赞数", example = "89")
    private Long likeCount;

    @Schema(description = "总评论数", example = "23")
    private Long commentCount;
}
