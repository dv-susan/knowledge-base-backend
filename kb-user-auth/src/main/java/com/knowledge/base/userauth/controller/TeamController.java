package com.knowledge.base.userauth.controller;

import com.knowledge.base.common.annotation.OperationLog;
import com.knowledge.base.common.result.PageResult;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.userauth.dto.TeamCreateDTO;
import com.knowledge.base.userauth.dto.TeamQueryDTO;
import com.knowledge.base.userauth.dto.TeamUpdateDTO;
import com.knowledge.base.userauth.service.TeamService;
import com.knowledge.base.userauth.vo.TeamMemberVO;
import com.knowledge.base.userauth.vo.TeamVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 团队管理Controller
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
@Tag(name = "团队管理", description = "团队管理相关接口")
public class TeamController {

    private final TeamService teamService;

    /**
     * 创建团队
     */
    @PostMapping
    @Operation(summary = "创建团队", description = "创建新的团队")
    @OperationLog(module = "团队管理", operation = "创建团队", description = "创建新团队")
    public Result<Long> createTeam(@Valid @RequestBody TeamCreateDTO dto) {
        Long teamId = teamService.createTeam(dto);
        return Result.success(teamId);
    }

    /**
     * 更新团队
     */
    @PutMapping
    @Operation(summary = "更新团队", description = "更新团队信息")
    @OperationLog(module = "团队管理", operation = "更新团队", description = "更新团队信息")
    public Result<Boolean> updateTeam(@Valid @RequestBody TeamUpdateDTO dto) {
        Boolean result = teamService.updateTeam(dto);
        return Result.success(result);
    }

    /**
     * 删除团队
     */
    @DeleteMapping("/{teamId}")
    @Operation(summary = "删除团队", description = "删除指定团队")
    @OperationLog(module = "团队管理", operation = "删除团队", description = "删除团队")
    public Result<Boolean> deleteTeam(@PathVariable Long teamId) {
        Boolean result = teamService.deleteTeam(teamId);
        return Result.success(result);
    }

    /**
     * 获取团队详情
     */
    @GetMapping("/{teamId}")
    @Operation(summary = "获取团队详情", description = "根据ID获取团队详情")
    public Result<TeamVO> getTeamDetail(@PathVariable Long teamId) {
        TeamVO teamVO = teamService.getTeamDetail(teamId);
        return Result.success(teamVO);
    }

    /**
     * 分页查询团队
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询团队", description = "分页查询团队列表")
    public Result<PageResult<TeamVO>> pageTeams(@RequestBody TeamQueryDTO dto) {
        PageResult<TeamVO> pageResult = teamService.pageTeams(dto);
        return Result.success(pageResult);
    }

    /**
     * 获取团队树
     */
    @GetMapping("/tree")
    @Operation(summary = "获取团队树", description = "获取完整的团队树结构，rootOnly=true时只返回一级团队")
    public Result<List<TeamVO>> getTeamTree(@RequestParam(required = false, defaultValue = "false") boolean rootOnly) {
        List<TeamVO> teamTree = teamService.getTeamTree(rootOnly);
        return Result.success(teamTree);
    }

    /**
     * 添加团队成员
     */
    @PostMapping("/{teamId}/members")
    @Operation(summary = "添加团队成员", description = "批量添加团队成员")
    @OperationLog(module = "团队管理", operation = "添加成员", description = "添加团队成员")
    public Result<Boolean> addTeamMembers(
            @PathVariable Long teamId,
            @RequestBody List<Long> userIds) {
        Boolean result = teamService.addTeamMembers(teamId, userIds);
        return Result.success(result);
    }

    /**
     * 移除团队成员
     */
    @DeleteMapping("/{teamId}/members")
    @Operation(summary = "移除团队成员", description = "批量移除团队成员")
    @OperationLog(module = "团队管理", operation = "移除成员", description = "移除团队成员")
    public Result<Boolean> removeTeamMembers(
            @PathVariable Long teamId,
            @RequestBody List<Long> userIds) {
        Boolean result = teamService.removeTeamMembers(teamId, userIds);
        return Result.success(result);
    }

    /**
     * 获取团队成员
     */
    @GetMapping("/{teamId}/members")
    @Operation(summary = "获取团队成员", description = "获取团队成员列表")
    public Result<List<TeamMemberVO>> getTeamMembers(@PathVariable Long teamId) {
        List<TeamMemberVO> members = teamService.getTeamMembers(teamId);
        return Result.success(members);
    }
}
