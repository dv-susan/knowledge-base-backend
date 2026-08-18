package com.knowledge.base.document.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.knowledge.base.common.result.PageResult;
import com.knowledge.base.document.dto.CommentCreateDTO;
import com.knowledge.base.document.dto.CommentQueryDTO;
import com.knowledge.base.document.entity.Comment;
import com.knowledge.base.document.vo.CommentVO;

import java.util.List;

/**
 * 评论Service接口
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface CommentService extends IService<Comment> {

    /**
     * 创建评论
     *
     * @param dto 创建DTO
     * @return 评论ID
     */
    Long createComment(CommentCreateDTO dto);

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @return 是否成功
     */
    Boolean deleteComment(Long commentId);

    /**
     * 点赞评论
     *
     * @param commentId 评论ID
     * @return 是否成功
     */
    Boolean likeComment(Long commentId);

    /**
     * 取消点赞评论
     *
     * @param commentId 评论ID
     * @return 是否成功
     */
    Boolean unlikeComment(Long commentId);

    /**
     * 分页查询文档评论
     *
     * @param documentId 文档ID
     * @param dto 查询DTO
     * @return 分页结果
     */
    PageResult<CommentVO> pageDocumentComments(Long documentId, CommentQueryDTO dto);

    /**
     * 获取评论回复列表
     *
     * @param parentCommentId 父评论ID
     * @return 回复列表
     */
    List<CommentVO> getCommentReplies(Long parentCommentId);
}
