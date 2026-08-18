package com.knowledge.base.document.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.base.common.exception.BusinessException;
import com.knowledge.base.common.result.PageResult;
import com.knowledge.base.common.utils.SnowflakeIdGenerator;
import com.knowledge.base.document.dto.CommentCreateDTO;
import com.knowledge.base.document.dto.CommentQueryDTO;
import com.knowledge.base.document.entity.Comment;
import com.knowledge.base.document.event.StatisticsEventPublisher;
import com.knowledge.base.document.mapper.CommentMapper;
import com.knowledge.base.document.service.CommentService;
import com.knowledge.base.document.client.UserServiceClient;
import com.knowledge.base.document.utils.UserContext;
import com.knowledge.base.document.vo.CommentVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 评论Service实现类
 *
 * <p>按照阿里巴巴Java开发规范设计，实现评论相关业务逻辑</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private UserServiceClient userServiceClient;

    @Resource
    private StatisticsEventPublisher statisticsEventPublisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createComment(CommentCreateDTO dto) {
        log.info("创建评论：documentId={}, parentId={}", dto.getDocumentId(), dto.getParentId());

        // 检查父评论是否存在
        Long rootId = null;
        if (dto.getParentId() != null && dto.getParentId() > 0) {
            Comment parentComment = commentMapper.selectById(dto.getParentId());
            if (parentComment == null) {
                throw new BusinessException("父评论不存在");
            }
            if (!parentComment.getDocumentId().equals(dto.getDocumentId())) {
                throw new BusinessException("父评论不属于该文档");
            }
            rootId = parentComment.getRootId() != null ? parentComment.getRootId() : parentComment.getId();

            // 更新父评论的回复数
            jdbcTemplate.update(
                    "UPDATE tb_comment SET reply_count = reply_count + 1 WHERE id = ?",
                    dto.getParentId()
            );
        }

        // 从上下文获取当前用户信息
        Long userId = UserContext.getCurrentUserId();
        String userName = UserContext.getCurrentUserName();
        String userAvatar = UserContext.getCurrentUserAvatar();
        // 如果JWT中没有头像，从用户服务获取最新头像
        if (userAvatar == null || userAvatar.isEmpty()) {
            userAvatar = userServiceClient.getUserAvatar(userId);
        }

        // 构建评论实体
        Comment comment = new Comment();
        comment.setId(SnowflakeIdGenerator.getInstance().nextId());
        comment.setDocumentId(dto.getDocumentId());
        comment.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        comment.setRootId(rootId);
        comment.setContent(dto.getContent());
        comment.setCommenterId(userId);
        comment.setCommenterName(userName);
        comment.setCommenterAvatar(userAvatar);
        comment.setReplyToUserId(dto.getReplyToUserId());
        comment.setStatus(1);
        comment.setLikeCount(0);
        comment.setReplyCount(0);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        comment.setDeleted(0);

        // 保存评论
        int count = commentMapper.insert(comment);
        if (count <= 0) {
            throw new BusinessException("创建评论失败");
        }

        // 更新文档的评论数
        jdbcTemplate.update(
                "UPDATE kb_document SET comment_count = comment_count + 1 WHERE id = ?",
                dto.getDocumentId()
        );

        // 发布评论统计事件到 RabbitMQ
        String documentTitle;
        try {
            documentTitle = jdbcTemplate.queryForObject(
                    "SELECT title FROM kb_document WHERE id = ?", String.class, dto.getDocumentId());
        } catch (Exception e) {
            documentTitle = null;
        }
        statisticsEventPublisher.publishCommentEvent(userId, userName, dto.getDocumentId(), documentTitle);

        return comment.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteComment(Long commentId) {
        log.info("删除评论：commentId={}", commentId);

        if (commentId == null) {
            throw new BusinessException("评论ID不能为空");
        }

        // 检查评论是否存在
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }

        // 检查权限，只有评论作者可以删除
        Long currentUserId = UserContext.getCurrentUserId();
        if (!currentUserId.equals(comment.getCommenterId())) {
            throw new BusinessException("只能删除自己的评论");
        }

        // 检查是否有子评论
        Long childCount = commentMapper.selectCount(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getParentId, commentId)
        );
        if (childCount > 0) {
            throw new BusinessException("该评论下有回复，不能删除");
        }

        // 删除评论
        int count = commentMapper.deleteById(commentId);

        // 更新父评论的回复数
        if (comment.getParentId() != null && comment.getParentId() > 0) {
            jdbcTemplate.update(
                    "UPDATE tb_comment SET reply_count = reply_count - 1 WHERE id = ?",
                    comment.getParentId()
            );
        }

        // 更新文档的评论数
        if (count > 0) {
            jdbcTemplate.update(
                    "UPDATE kb_document SET comment_count = comment_count - 1 WHERE id = ?",
                    comment.getDocumentId()
            );
        }

        return count > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean likeComment(Long commentId) {
        log.info("点赞评论：commentId={}", commentId);

        if (commentId == null) {
            throw new BusinessException("评论ID不能为空");
        }

        // 检查评论是否存在
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }

        // 从上下文获取当前用户ID
        Long userId = getCurrentUserIdSafely();

        // 检查是否已点赞
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tb_like WHERE target_id = ? AND user_id = ? AND target_type = 2",
                Integer.class,
                commentId, userId
        );

        if (count != null && count > 0) {
            throw new BusinessException("已经点赞过了");
        }

        // 添加点赞记录
        jdbcTemplate.update(
                "INSERT INTO tb_like (id, target_id, user_id, target_type, created_at) VALUES (?, ?, ?, 2, NOW())",
                SnowflakeIdGenerator.getInstance().nextId(), commentId, userId
        );

        // 更新评论点赞数
        jdbcTemplate.update(
                "UPDATE tb_comment SET like_count = like_count + 1 WHERE id = ?",
                commentId
        );

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean unlikeComment(Long commentId) {
        log.info("取消点赞评论：commentId={}", commentId);

        if (commentId == null) {
            throw new BusinessException("评论ID不能为空");
        }

        // 检查评论是否存在
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }

        // 从上下文获取当前用户ID
        Long userId = getCurrentUserIdSafely();

        // 删除点赞记录
        int count = jdbcTemplate.update(
                "DELETE FROM tb_like WHERE target_id = ? AND user_id = ? AND target_type = 2",
                commentId, userId
        );

        // 更新评论点赞数
        if (count > 0) {
            jdbcTemplate.update(
                    "UPDATE tb_comment SET like_count = like_count - 1 WHERE id = ?",
                    commentId
            );
        }

        return count > 0;
    }

    @Override
    public PageResult<CommentVO> pageDocumentComments(Long documentId, CommentQueryDTO dto) {
        // 构建查询条件 - 只查询根评论
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getDocumentId, documentId)
                .eq(Comment::getParentId, 0)
                .eq(Comment::getStatus, 1);

        // 排序
        if (StringUtils.hasText(dto.getSortBy())) {
            if ("like_count".equals(dto.getSortBy())) {
                wrapper.orderByDesc(Comment::getLikeCount);
            } else {
                boolean isAsc = "asc".equals(dto.getSortOrder());
                if (isAsc) {
                    wrapper.orderByAsc(Comment::getCreatedAt);
                } else {
                    wrapper.orderByDesc(Comment::getCreatedAt);
                }
            }
        } else {
            wrapper.orderByDesc(Comment::getCreatedAt);
        }

        // 分页查询
        Page<Comment> page = new Page<>(dto.getCurrent(), dto.getSize());
        IPage<Comment> commentPage = commentMapper.selectPage(page, wrapper);

        // 获取当前用户ID
        Long userId = getCurrentUserIdSafely();

        // 转换为VO并加载子评论
        List<Comment> records = commentPage.getRecords();
        List<CommentVO> voRecords = new ArrayList<>();
        Set<Long> allCommentIds = new HashSet<>();

        for (Comment comment : records) {
            CommentVO vo = convertToVO(comment);
            allCommentIds.add(comment.getId());
            List<CommentVO> replies = getCommentReplies(comment.getId());
            for (CommentVO reply : replies) {
                allCommentIds.add(reply.getId());
            }
            vo.setReplies(replies);
            voRecords.add(vo);
        }

        // 批量查询当前用户是否已点赞
        Set<Long> likedIds = queryLikedCommentIds(userId, allCommentIds);

        // 设置isLiked
        for (CommentVO vo : voRecords) {
            vo.setIsLiked(likedIds.contains(vo.getId()));
            if (vo.getReplies() != null) {
                for (CommentVO reply : vo.getReplies()) {
                    reply.setIsLiked(likedIds.contains(reply.getId()));
                }
            }
        }

        IPage<CommentVO> voPage = new Page<>(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal());
        voPage.setRecords(voRecords);

        return PageResult.<CommentVO>builder()
                .records(voPage.getRecords())
                .total(voPage.getTotal())
                .current(voPage.getCurrent())
                .size(voPage.getSize())
                .build();
    }

    @Override
    public List<CommentVO> getCommentReplies(Long parentCommentId) {
        if (parentCommentId == null || parentCommentId <= 0) {
            return new ArrayList<>();
        }

        List<Comment> comments = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getParentId, parentCommentId)
                        .eq(Comment::getStatus, 1)
                        .orderByAsc(Comment::getCreatedAt)
        );

        Long userId = getCurrentUserIdSafely();
        Set<Long> likedIds = queryLikedCommentIds(userId,
                comments.stream().map(Comment::getId).collect(Collectors.toSet()));

        return comments.stream()
                .map(comment -> {
                    CommentVO vo = convertToVO(comment);
                    vo.setIsLiked(likedIds.contains(comment.getId()));
                    return vo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 安全获取当前用户ID（未登录返回null）
     */
    private Long getCurrentUserIdSafely() {
        try {
            return UserContext.getCurrentUserId();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 批量查询评论点赞状态
     *
     * @param userId     当前用户ID（null表示未登录）
     * @param commentIds 评论ID集合
     * @return 已点赞的评论ID集合
     */
    private Set<Long> queryLikedCommentIds(Long userId, Set<Long> commentIds) {
        if (userId == null || commentIds.isEmpty()) {
            return Collections.emptySet();
        }

        // 构建 IN 查询
        String placeholders = commentIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(","));
        List<Object> params = new ArrayList<>();
        params.add(userId);
        params.addAll(commentIds);

        List<Long> likedIds = jdbcTemplate.queryForList(
                "SELECT target_id FROM tb_like WHERE user_id = ? AND target_type = 2 AND target_id IN (" + placeholders + ")",
                Long.class,
                params.toArray()
        );

        return new HashSet<>(likedIds);
    }

    /**
     * 转换为VO
     *
     * @param comment 评论实体
     * @return 评论VO
     */
    private CommentVO convertToVO(Comment comment) {
        return CommentVO.builder()
                .id(comment.getId())
                .documentId(comment.getDocumentId())
                .parentId(comment.getParentId())
                .rootId(comment.getRootId())
                .content(comment.getContent())
                .commenterId(comment.getCommenterId())
                .commenterName(comment.getCommenterName())
                .commenterAvatar(comment.getCommenterAvatar())
                .replyToUserId(comment.getReplyToUserId())
                .replyToUserName(comment.getReplyToUserName())
                .status(comment.getStatus())
                .likeCount(comment.getLikeCount() != null ? comment.getLikeCount() : 0)
                .replyCount(comment.getReplyCount() != null ? comment.getReplyCount() : 0)
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
