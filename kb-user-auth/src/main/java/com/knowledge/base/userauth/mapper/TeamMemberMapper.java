package com.knowledge.base.userauth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.userauth.entity.TeamMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 团队成员Mapper接口
 *
 * @author 苏三
 * @since 1.0.0
 */
@Mapper
public interface TeamMemberMapper extends BaseMapper<TeamMember> {

    /**
     * 根据团队ID查询所有成员
     */
    List<TeamMember> selectByTeamId(@Param("teamId") Long teamId);

    /**
     * 根据用户ID查询所属团队
     */
    List<TeamMember> selectByUserId(@Param("userId") Long userId);

    /**
     * 批量删除团队成员
     */
    int deleteByTeamIdAndUserIds(@Param("teamId") Long teamId, @Param("userIds") List<Long> userIds);

    /**
     * 统计团队下的成员数量
     */
    Long countByTeamId(@Param("teamId") Long teamId);
}
