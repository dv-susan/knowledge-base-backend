package com.knowledge.base.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.ai.entity.Message;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息Mapper
 *
 * @author 苏三
 * @since 1.0.0
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}