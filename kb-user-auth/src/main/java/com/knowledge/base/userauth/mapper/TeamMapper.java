package com.knowledge.base.userauth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.userauth.entity.Team;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 团队Mapper接口
 *
 * <p>按照阿里巴巴Java开发规范设计，提供团队数据访问操作</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Mapper
public interface TeamMapper extends BaseMapper<Team> {

    /**
     * 根据父团队ID查询子团队列表
     */
    List<Team> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 根据团队编码查询团队
     */
    Team selectByTeamCode(@Param("teamCode") String teamCode);

    /**
     * 根据状态查询团队列表
     */
    List<Team> selectByStatus(@Param("status") Integer status);

    /**
     * 根据层级查询团队列表
     */
    List<Team> selectByLevel(@Param("level") Integer level);

    /**
     * 查询所有根团队
     */
    List<Team> selectRootTeams();

    /**
     * 根据负责人ID查询团队列表
     */
    List<Team> selectByLeaderId(@Param("leaderId") Long leaderId);

    /**
     * 根据路径前缀查询团队列表（查询所有子团队）
     */
    List<Team> selectByPathPrefix(@Param("path") String path);

    /**
     * 查询团队树
     */
    List<Team> selectTeamTree();

    /**
     * 更新成员数量
     */
    int updateMemberCount(@Param("teamId") Long teamId, @Param("count") Integer count);

    /**
     * 增加成员数量
     */
    int incrementMemberCount(@Param("teamId") Long teamId);

    /**
     * 减少成员数量
     */
    int decrementMemberCount(@Param("teamId") Long teamId);

    /**
     * 更新文档数量
     */
    int updateDocumentCount(@Param("teamId") Long teamId, @Param("count") Integer count);

    /**
     * 增加文档数量
     */
    int incrementDocumentCount(@Param("teamId") Long teamId);

    /**
     * 减少文档数量
     */
    int decrementDocumentCount(@Param("teamId") Long teamId);

    /**
     * 检查团队编码是否存在
     */
    Boolean checkTeamCodeExists(@Param("teamCode") String teamCode, @Param("id") Long id);

    /**
     * 统计团队数量
     */
    Long countAll();

    /**
     * 统计启用团队数量
     */
    Long countEnabled();
}
