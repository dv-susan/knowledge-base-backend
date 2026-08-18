package com.knowledge.base.userauth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.knowledge.base.common.result.PageResult;
import com.knowledge.base.userauth.dto.TeamCreateDTO;
import com.knowledge.base.userauth.dto.TeamQueryDTO;
import com.knowledge.base.userauth.dto.TeamUpdateDTO;
import com.knowledge.base.userauth.entity.Team;
import com.knowledge.base.userauth.vo.TeamMemberVO;
import com.knowledge.base.userauth.vo.TeamVO;

import java.util.List;

/**
 * 团队Service接口
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface TeamService extends IService<Team> {

    /**
     * 创建团队
     *
     * @param dto 创建DTO
     * @return 团队ID
     */
    Long createTeam(TeamCreateDTO dto);

    /**
     * 更新团队
     *
     * @param dto 更新DTO
     * @return 是否成功
     */
    Boolean updateTeam(TeamUpdateDTO dto);

    /**
     * 删除团队
     *
     * @param teamId 团队ID
     * @return 是否成功
     */
    Boolean deleteTeam(Long teamId);

    /**
     * 获取团队详情
     *
     * @param teamId 团队ID
     * @return 团队VO
     */
    TeamVO getTeamDetail(Long teamId);

    /**
     * 分页查询团队
     *
     * @param dto 查询DTO
     * @return 分页结果
     */
    PageResult<TeamVO> pageTeams(TeamQueryDTO dto);

    /**
     * 获取团队树
     *
     * @param rootOnly 是否只返回根团队（一级团队）
     * @return 团队树
     */
    List<TeamVO> getTeamTree(boolean rootOnly);

    /**
     * 添加团队成员
     *
     * @param teamId  团队ID
     * @param userIds 用户ID列表
     * @return 是否成功
     */
    Boolean addTeamMembers(Long teamId, List<Long> userIds);

    /**
     * 移除团队成员
     *
     * @param teamId 团队ID
     * @param userIds 用户ID列表
     * @return 是否成功
     */
    Boolean removeTeamMembers(Long teamId, List<Long> userIds);

    /**
     * 获取团队成员
     *
     * @param teamId 团队ID
     * @return 团队成员列表（含用户信息）
     */
    List<TeamMemberVO> getTeamMembers(Long teamId);
}
