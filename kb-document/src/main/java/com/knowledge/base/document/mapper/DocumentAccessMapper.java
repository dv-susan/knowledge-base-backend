package com.knowledge.base.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.document.entity.DocumentAccess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文档访问记录Mapper接口
 *
 * @author 苏三
 * @since 1.0.0
 */
@Mapper
public interface DocumentAccessMapper extends BaseMapper<DocumentAccess> {

    /**
     * 根据用户ID查询最近访问记录，按访问时间倒序排列
     *
     * @param userId 用户ID
     * @param limit  查询数量限制
     * @return 访问记录列表
     */
    @Select("SELECT da.*, d.summary, d.category_id, c.category_name, d.author_name, d.status " +
            "FROM kb_document_access da " +
            "LEFT JOIN kb_document d ON da.document_id = d.id " +
            "LEFT JOIN kb_category c ON d.category_id = c.id " +
            "WHERE da.user_id = #{userId} AND d.deleted = 0 " +
            "ORDER BY da.access_time DESC " +
            "LIMIT #{limit}")
    List<DocumentAccess> selectRecentAccessByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 删除用户的访问记录
     *
     * @param userId 用户ID
     * @param documentId 文档ID
     * @return 删除数量
     */
    int deleteByUserIdAndDocumentId(@Param("userId") Long userId, @Param("documentId") Long documentId);

    /**
     * 清空用户的所有访问记录
     *
     * @param userId 用户ID
     * @return 删除数量
     */
    int deleteAllByUserId(@Param("userId") Long userId);
}
