package com.knowledge.base.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.document.entity.DocumentReview;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文档审核Mapper接口
 *
 * @author 苏三
 * @since 1.0.0
 */
@Mapper
public interface DocumentReviewMapper extends BaseMapper<DocumentReview> {

    /**
     * 查询文档最新一条审核记录。
     *
     * @param documentId 文档ID
     * @return 最新审核记录
     */
    DocumentReview selectLatestByDocumentId(@Param("documentId") Long documentId);
}
