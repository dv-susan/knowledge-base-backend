package com.knowledge.base.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.document.dto.CategoryDocCountDTO;
import com.knowledge.base.document.entity.Document;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.knowledge.base.document.vo.DocumentNeighborVO;
import java.util.List;

/**
 * 文档Mapper接口
 *
 * @author 苏三
 * @since 1.0.0
 */
@Mapper
public interface DocumentMapper extends BaseMapper<Document> {

    int incrementViewCount(@Param("documentId") Long documentId);

    int incrementLikeCount(@Param("documentId") Long documentId);

    int decrementLikeCount(@Param("documentId") Long documentId);

    int incrementFavoriteCount(@Param("documentId") Long documentId);

    int decrementFavoriteCount(@Param("documentId") Long documentId);

    int incrementCommentCount(@Param("documentId") Long documentId);

    /**
     * 按分类统计文档数量
     */
    List<CategoryDocCountDTO> countByCategory();

    /**
     * 查询上一篇文档（默认排序：置顶 DESC, 排序 DESC, 发布时间 DESC）
     */
    DocumentNeighborVO selectPrev(@Param("isTop") Integer isTop,
                                  @Param("sort") Integer sort,
                                  @Param("publishTime") java.time.LocalDateTime publishTime);

    /**
     * 查询下一篇文档（默认排序：置顶 DESC, 排序 DESC, 发布时间 DESC）
     */
    DocumentNeighborVO selectNext(@Param("isTop") Integer isTop,
                                  @Param("sort") Integer sort,
                                  @Param("publishTime") java.time.LocalDateTime publishTime);
}