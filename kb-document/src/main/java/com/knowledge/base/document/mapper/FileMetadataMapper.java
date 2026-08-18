package com.knowledge.base.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.document.entity.FileMetadata;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文件元数据Mapper接口
 *
 * @author 苏三
 * @since 1.0.0
 */
@Mapper
public interface FileMetadataMapper extends BaseMapper<FileMetadata> {

    /**
     * 查询用户的文件列表
     *
     * @param userId 用户ID
     * @return 文件列表
     */
    @Select("SELECT * FROM kb_file_metadata WHERE uploader_id = #{userId} AND deleted = 0 ORDER BY created_at DESC")
    List<FileMetadata> findByUploaderId(@Param("userId") Long userId);

    /**
     * 按文件分类查询文件列表
     *
     * @param userId       用户ID
     * @param fileCategory 文件分类
     * @return 文件列表
     */
    @Select("SELECT * FROM kb_file_metadata WHERE uploader_id = #{userId} AND file_category = #{fileCategory} AND deleted = 0 ORDER BY created_at DESC")
    List<FileMetadata> findByUploaderIdAndCategory(@Param("userId") Long userId, @Param("fileCategory") String fileCategory);

    /**
     * 统计用户的文件数量
     *
     * @param userId 用户ID
     * @return 文件数量
     */
    @Select("SELECT COUNT(*) FROM kb_file_metadata WHERE uploader_id = #{userId} AND deleted = 0")
    Integer countByUploaderId(@Param("userId") Long userId);

    /**
     * 统计用户的文件总大小
     *
     * @param userId 用户ID
     * @return 文件总大小
     */
    @Select("SELECT COALESCE(SUM(file_size), 0) FROM kb_file_metadata WHERE uploader_id = #{userId} AND deleted = 0")
    Long sumFileSizeByUploaderId(@Param("userId") Long userId);
}
