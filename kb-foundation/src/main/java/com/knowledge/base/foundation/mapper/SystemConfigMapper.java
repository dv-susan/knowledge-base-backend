package com.knowledge.base.foundation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.foundation.entity.SystemConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统配置Mapper接口
 *
 * @author 苏三
 * @since 1.0.0
 */
@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfig> {

    @Select("SELECT * FROM kb_system_config WHERE config_key = #{configKey}")
    SystemConfig selectByConfigKey(@Param("configKey") String configKey);

    @Select("SELECT * FROM kb_system_config WHERE category = #{category} ORDER BY id")
    List<SystemConfig> selectByCategory(@Param("category") String category);

    @Select("SELECT * FROM kb_system_config WHERE is_public = 1 ORDER BY category, id")
    List<SystemConfig> selectPublicConfigs();
}