package com.knowledge.base.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.document.entity.DocumentShare;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文档分享Mapper接口
 *
 * @author 苏三
 * @since 1.0.0
 */
@Mapper
public interface DocumentShareMapper extends BaseMapper<DocumentShare> {

    /**
     * 根据分享ID查询
     *
     * @param shareId 分享ID
     * @return 分享信息
     */
    @Select("SELECT * FROM kb_document_share WHERE share_id = #{shareId} AND deleted = 0")
    DocumentShare selectByShareId(@Param("shareId") String shareId);

    /**
     * 根据文档ID查询所有有效分享
     *
     * @param documentId 文档ID
     * @return 分享列表
     */
    @Select("SELECT * FROM kb_document_share WHERE document_id = #{documentId} AND status = 0 AND deleted = 0 ORDER BY share_time DESC")
    List<DocumentShare> selectValidSharesByDocumentId(@Param("documentId") Long documentId);

    /**
     * 根据分享人ID查询分享列表
     *
     * @param sharerId 分享人ID
     * @return 分享列表
     */
    @Select("SELECT * FROM kb_document_share WHERE sharer_id = #{sharerId} AND deleted = 0 ORDER BY share_time DESC")
    List<DocumentShare> selectBySharerId(@Param("sharerId") Long sharerId);
}