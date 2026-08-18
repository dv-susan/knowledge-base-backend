package com.knowledge.base.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.document.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 标签Mapper接口
 *
 * @author 苏三
 * @since 1.0.0
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

}