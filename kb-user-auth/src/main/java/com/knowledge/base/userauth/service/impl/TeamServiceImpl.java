package com.knowledge.base.userauth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.base.common.exception.BusinessException;
import com.knowledge.base.common.result.PageResult;
import com.knowledge.base.common.utils.SnowflakeIdGenerator;
import com.knowledge.base.userauth.dto.TeamCreateDTO;
import com.knowledge.base.userauth.dto.TeamQueryDTO;
import com.knowledge.base.userauth.dto.TeamUpdateDTO;
import com.knowledge.base.userauth.entity.Team;
import com.knowledge.base.userauth.entity.TeamMember;
import com.knowledge.base.userauth.mapper.TeamMapper;
import com.knowledge.base.userauth.mapper.TeamMemberMapper;
import com.knowledge.base.userauth.service.TeamService;
import com.knowledge.base.userauth.vo.TeamMemberVO;
import com.knowledge.base.userauth.vo.TeamVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 团队Service实现
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {

    @Resource
    private TeamMapper teamMapper;

    @Resource
    private TeamMemberMapper teamMemberMapper;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource(name = "caffeineCacheManager")
    private CacheManager caffeineCacheManager;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /** Caffeine 本地缓存名 */
    private static final String CAFFEINE_CACHE_NAME = "sidebar-teams";
    /** Redis 缓存 key 前缀 */
    private static final String REDIS_TEAM_KEY_PREFIX = "sidebar:teams:";

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "sidebar:teams", allEntries = true)
    public Long createTeam(TeamCreateDTO dto) {
        // 检查团队编码是否存在
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Team::getTeamCode, dto.getTeamCode());
        if (baseMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("团队编码已存在");
        }

        // 构建团队实体
        Team team = new Team();
        team.setId(SnowflakeIdGenerator.getInstance().nextId());
        team.setTeamName(dto.getTeamName());
        team.setTeamCode(dto.getTeamCode());
        team.setDescription(dto.getDescription());
        team.setIcon(dto.getIcon());

        // 处理层级关系
        if (dto.getParentId() != null) {
            Team parentTeam = baseMapper.selectById(dto.getParentId());
            if (parentTeam == null) {
                throw new BusinessException("父团队不存在");
            }
            team.setParentId(dto.getParentId());
            team.setLevel(parentTeam.getLevel() + 1);
            team.setPath(parentTeam.getPath() + "/" + team.getId());
        } else {
            team.setLevel(1);
            team.setPath("/" + team.getId());
        }

        team.setLeaderId(dto.getLeaderId());
        team.setStatus(1);
        team.setMemberCount(0);
        team.setDocCount(0);

        baseMapper.insert(team);
        evictTeamCache();
        log.info("创建团队成功，团队ID: {}", team.getId());
        return team.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "sidebar:teams", allEntries = true)
    public Boolean updateTeam(TeamUpdateDTO dto) {
        Team team = baseMapper.selectById(dto.getId());
        if (team == null) {
            throw new BusinessException("团队不存在");
        }

        // 检查团队编码是否被占用
        if (StrUtil.isNotBlank(dto.getTeamCode()) && !dto.getTeamCode().equals(team.getTeamCode())) {
            LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Team::getTeamCode, dto.getTeamCode());
            queryWrapper.ne(Team::getId, dto.getId());
            if (baseMapper.selectCount(queryWrapper) > 0) {
                throw new BusinessException("团队编码已被占用");
            }
        }

        Team updateEntity = new Team();
        updateEntity.setId(dto.getId());
        if (StrUtil.isNotBlank(dto.getTeamName())) {
            updateEntity.setTeamName(dto.getTeamName());
        }
        if (StrUtil.isNotBlank(dto.getTeamCode())) {
            updateEntity.setTeamCode(dto.getTeamCode());
        }
        if (StrUtil.isNotBlank(dto.getDescription())) {
            updateEntity.setDescription(dto.getDescription());
        }
        if (StrUtil.isNotBlank(dto.getIcon())) {
            updateEntity.setIcon(dto.getIcon());
        }
        if (dto.getLeaderId() != null) {
            updateEntity.setLeaderId(dto.getLeaderId());
        }
        if (dto.getStatus() != null) {
            updateEntity.setStatus(dto.getStatus());
        }

        int result = baseMapper.updateById(updateEntity);
        evictTeamCache();
        log.info("更新团队成功，团队ID: {}", dto.getId());
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "sidebar:teams", allEntries = true)
    public Boolean deleteTeam(Long teamId) {
        // 检查是否有子团队
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Team::getParentId, teamId);
        long childCount = baseMapper.selectCount(queryWrapper);
        if (childCount > 0) {
            throw new BusinessException("该团队下有子团队，无法删除");
        }

        // 清理团队成员关联
        LambdaQueryWrapper<TeamMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(TeamMember::getTeamId, teamId);
        teamMemberMapper.delete(memberWrapper);

        // 删除团队
        int result = baseMapper.deleteById(teamId);
        evictTeamCache();
        log.info("删除团队成功，团队ID: {}", teamId);
        return result > 0;
    }

    @Override
    public TeamVO getTeamDetail(Long teamId) {
        Team team = baseMapper.selectById(teamId);
        if (team == null) {
            throw new BusinessException("团队不存在");
        }
        return convertToVO(team);
    }

    @Override
    public PageResult<TeamVO> pageTeams(TeamQueryDTO dto) {
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();

        if (StrUtil.isNotBlank(dto.getTeamName())) {
            queryWrapper.like(Team::getTeamName, dto.getTeamName());
        }
        if (StrUtil.isNotBlank(dto.getTeamCode())) {
            queryWrapper.eq(Team::getTeamCode, dto.getTeamCode());
        }
        if (dto.getStatus() != null) {
            queryWrapper.eq(Team::getStatus, dto.getStatus());
        }

        // 按层级和创建时间排序
        queryWrapper.orderByAsc(Team::getLevel).orderByDesc(Team::getCreatedAt);

        Page<Team> page = new Page<>(dto.getCurrent(), dto.getSize());
        Page<Team> resultPage = baseMapper.selectPage(page, queryWrapper);

        List<TeamVO> voList = resultPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(
                resultPage.getCurrent(),
                resultPage.getSize(),
                resultPage.getTotal(),
                voList
        );
    }

    @Override
    public List<TeamVO> getTeamTree(boolean rootOnly) {
        String cacheKey = rootOnly ? "roots" : "tree";
        String redisKey = REDIS_TEAM_KEY_PREFIX + cacheKey;

        // 1. 检查 Caffeine 本地缓存 (L1)
        Cache caffeineCache = caffeineCacheManager.getCache(CAFFEINE_CACHE_NAME);
        if (caffeineCache != null) {
            Cache.ValueWrapper wrapper = caffeineCache.get(cacheKey);
            if (wrapper != null) {
                @SuppressWarnings("unchecked")
                List<TeamVO> cached = (List<TeamVO>) wrapper.get();
                if (cached != null && !cached.isEmpty()) {
                    log.debug("团队空间树命中 Caffeine 本地缓存: key={}", cacheKey);
                    return cached;
                }
            }
        }

        // 2. 检查 Redis 缓存 (L2)
        try {
            @SuppressWarnings("unchecked")
            List<TeamVO> redisCached = (List<TeamVO>) redisTemplate.opsForValue().get(redisKey);
            if (redisCached != null && !redisCached.isEmpty()) {
                log.debug("团队空间树命中 Redis 缓存: key={}", redisKey);
                // 回写 Caffeine 本地缓存
                if (caffeineCache != null) {
                    caffeineCache.put(cacheKey, redisCached);
                }
                return redisCached;
            }
        } catch (Exception e) {
            log.warn("读取 Redis 团队空间树缓存失败：{}", e.getMessage());
        }

        // 3. 缓存未命中，查询数据库
        log.debug("团队空间树缓存未命中，执行数据库查询");
        List<TeamVO> result = queryTeamTree(rootOnly);

        // 4. 写入双层缓存
        if (caffeineCache != null) {
            caffeineCache.put(cacheKey, result);
        }
        try {
            redisTemplate.opsForValue().set(redisKey, result, 30, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("写入 Redis 团队空间树缓存失败：{}", e.getMessage());
        }

        return result;
    }

    /**
     * 从数据库查询团队空间树（原 {@link #getTeamTree} 的 DB 查询逻辑）
     */
    private List<TeamVO> queryTeamTree(boolean rootOnly) {
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Team::getStatus, 1);
        queryWrapper.orderByAsc(Team::getLevel).orderByAsc(Team::getCreatedAt);

        List<Team> allTeams = baseMapper.selectList(queryWrapper);

        if (rootOnly) {
            return allTeams.stream()
                    .filter(team -> team.getParentId() == null || team.getParentId() == 0L)
                    .map(this::convertToVO)
                    .collect(Collectors.toList());
        }

        return buildTeamTree(allTeams, null);
    }

    /**
     * 清除团队空间树的双层缓存（L1 + L2）
     *
     * <p>在团队创建、更新、删除后调用，确保菜单栏数据实时性。</p>
     */
    private void evictTeamCache() {
        // 清除 Caffeine 本地缓存 (L1)
        Cache caffeineCache = caffeineCacheManager.getCache(CAFFEINE_CACHE_NAME);
        if (caffeineCache != null) {
            caffeineCache.evict("roots");
            caffeineCache.evict("tree");
            log.debug("已清除 Caffeine 团队空间树缓存");
        }
        // 清除 Redis 缓存 (L2)
        try {
            redisTemplate.delete(REDIS_TEAM_KEY_PREFIX + "roots");
            redisTemplate.delete(REDIS_TEAM_KEY_PREFIX + "tree");
            log.debug("已清除 Redis 团队空间树缓存");
        } catch (Exception e) {
            log.warn("删除 Redis 团队空间树缓存失败：{}", e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addTeamMembers(Long teamId, List<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return false;
        }

        Team team = baseMapper.selectById(teamId);
        if (team == null) {
            throw new BusinessException("团队不存在");
        }

        int addedCount = 0;
        for (Long userId : userIds) {
            // 检查是否已是成员
            LambdaQueryWrapper<TeamMember> existWrapper = new LambdaQueryWrapper<>();
            existWrapper.eq(TeamMember::getTeamId, teamId)
                        .eq(TeamMember::getUserId, userId);
            if (teamMemberMapper.selectCount(existWrapper) > 0) {
                continue;
            }

            TeamMember member = new TeamMember();
            member.setId(SnowflakeIdGenerator.getInstance().nextId());
            member.setTeamId(teamId);
            member.setUserId(userId);
            member.setMemberRole("member");
            member.setJoinTime(LocalDateTime.now());
            teamMemberMapper.insert(member);
            addedCount++;
        }

        // 重新计算成员数量
        Long actualCount = teamMemberMapper.countByTeamId(teamId);
        Team updateTeam = new Team();
        updateTeam.setId(teamId);
        updateTeam.setMemberCount(actualCount.intValue());
        baseMapper.updateById(updateTeam);

        log.info("添加团队成员成功，团队ID: {}, 新增成员数: {}, 当前总数: {}", teamId, addedCount, actualCount);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean removeTeamMembers(Long teamId, List<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return false;
        }

        Team team = baseMapper.selectById(teamId);
        if (team == null) {
            throw new BusinessException("团队不存在");
        }

        // 删除成员关联记录
        teamMemberMapper.deleteByTeamIdAndUserIds(teamId, userIds);

        // 重新计算成员数量
        Long actualCount = teamMemberMapper.countByTeamId(teamId);
        Team updateTeam = new Team();
        updateTeam.setId(teamId);
        updateTeam.setMemberCount(actualCount.intValue());
        baseMapper.updateById(updateTeam);

        log.info("移除团队成员成功，团队ID: {}, 移除人数: {}, 当前总数: {}", teamId, userIds.size(), actualCount);
        return true;
    }

    @Override
    public List<TeamMemberVO> getTeamMembers(Long teamId) {
        Team team = baseMapper.selectById(teamId);
        if (team == null) {
            throw new BusinessException("团队不存在");
        }

        List<TeamMember> members = teamMemberMapper.selectByTeamId(teamId);
        if (CollUtil.isEmpty(members)) {
            return new ArrayList<>();
        }

        // 批量查询用户信息
        List<Long> userIds = members.stream()
                .map(TeamMember::getUserId)
                .collect(Collectors.toList());

        String userSql = "SELECT id, username, real_name, avatar FROM kb_user WHERE id IN ("
                + userIds.stream().map(String::valueOf).collect(Collectors.joining(","))
                + ") AND deleted = 0";
        List<Map<String, Object>> userRows = jdbcTemplate.queryForList(userSql);

        Map<Long, Map<String, Object>> userMap = userRows.stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row.get("id")).longValue(),
                        row -> row
                ));

        return members.stream().map(member -> {
            Map<String, Object> userInfo = userMap.get(member.getUserId());
            String username = userInfo != null ? (String) userInfo.get("username") : "未知用户";
            String realName = userInfo != null ? (String) userInfo.get("real_name") : null;
            String avatar = userInfo != null ? (String) userInfo.get("avatar") : null;

            return TeamMemberVO.builder()
                    .userId(member.getUserId())
                    .username(username)
                    .realName(realName)
                    .avatar(avatar)
                    .role(member.getMemberRole())
                    .joinedAt(member.getJoinTime())
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 构建团队树
     */
    private List<TeamVO> buildTeamTree(List<Team> allTeams, Long parentId) {
        List<TeamVO> tree = new ArrayList<>();

        for (Team team : allTeams) {
            if ((parentId == null && team.getParentId() == null) ||
                    (parentId != null && parentId.equals(team.getParentId()))) {
                TeamVO vo = convertToVO(team);
                vo.setChildren(buildTeamTree(allTeams, team.getId()));
                tree.add(vo);
            }
        }

        return tree;
    }

    /**
     * 转换为VO
     */
    private TeamVO convertToVO(Team team) {
        TeamVO vo = new TeamVO();
        BeanUtil.copyProperties(team, vo);
        vo.setCreatedAt(team.getCreatedAt());

        // 设置父团队名称
        if (team.getParentId() != null) {
            Team parentTeam = baseMapper.selectById(team.getParentId());
            if (parentTeam != null) {
                vo.setParentName(parentTeam.getTeamName());
            }
        }

        // 设置负责人名称
        if (team.getLeaderId() != null) {
            try {
                List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT username FROM kb_user WHERE id = ? AND deleted = 0", team.getLeaderId()
                );
                if (CollUtil.isNotEmpty(rows)) {
                    vo.setLeaderName((String) rows.get(0).get("username"));
                }
            } catch (Exception e) {
                log.warn("获取团队负责人信息失败: leaderId={}", team.getLeaderId(), e);
            }
        }

        return vo;
    }
}
