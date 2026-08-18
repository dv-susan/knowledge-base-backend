package com.knowledge.base.foundation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.foundation.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 操作日志Mapper接口
 *
 * @author 苏三
 * @since 1.0.0
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {

    @Select("SELECT module, operation_type, COUNT(*) as count FROM kb_operation_log " +
            "GROUP BY module, operation_type ORDER BY count DESC")
    List<Map<String, Object>> countByModuleAndType();

    @Select("SELECT COUNT(*) FROM kb_operation_log WHERE user_id = #{userId}")
    Long countByUserId(@Param("userId") Long userId);

    @Select("DELETE FROM kb_operation_log WHERE created_at < #{beforeDate}")
    int deleteBeforeDate(@Param("beforeDate") String beforeDate);
}