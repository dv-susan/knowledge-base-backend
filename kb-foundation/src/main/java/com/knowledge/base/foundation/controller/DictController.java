package com.knowledge.base.foundation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.foundation.entity.Dict;
import com.knowledge.base.foundation.entity.DictData;
import com.knowledge.base.foundation.service.DictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典Controller
 *
 * <p>按照阿里巴巴Java开发规范设计，提供字典管理相关接口</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/dicts")
@Tag(name = "字典管理", description = "字典数据管理接口")
public class DictController {

    @Resource
    private DictService dictService;

    /**
     * 分页查询字典类型列表
     *
     * @param current 当前页
     * @param size    每页大小
     * @param keyword 搜索关键词
     * @return 字典分页信息
     */
    @GetMapping
    @Operation(summary = "分页查询字典", description = "分页查询字典类型列表")
    public Result<IPage<Dict>> pageDicts(
        @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
        @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
        @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        log.info("分页查询字典请求：current={}, size={}, keyword={}", current, size, keyword);

        IPage<Dict> page = dictService.pageDicts(current, size, keyword);
        return Result.success(page);
    }

    /**
     * 根据字典编码查询字典详情
     *
     * @param code 字典编码
     * @return 字典详情
     */
    @GetMapping("/{code}")
    @Operation(summary = "查询字典详情", description = "根据字典编码查询字典详情")
    public Result<Dict> getDictByCode(
        @Parameter(description = "字典编码", required = true)
        @PathVariable String code) {
        log.info("查询字典详情请求：code={}", code);

        Dict dict = dictService.getDictByCode(code);
        return Result.success(dict);
    }

    /**
     * 创建字典
     *
     * @param dict 字典信息
     * @return 是否成功
     */
    @PostMapping
    @Operation(summary = "创建字典", description = "创建新的字典类型")
    public Result<Boolean> createDict(@Valid @RequestBody Dict dict) {
        log.info("创建字典请求：code={}, name={}", dict.getDictCode(), dict.getDictName());

        Boolean success = dictService.createDict(dict);
        return Result.success("创建字典成功", success);
    }

    /**
     * 更新字典
     *
     * @param code 字典编码
     * @param dict 字典信息
     * @return 是否成功
     */
    @PutMapping("/{code}")
    @Operation(summary = "更新字典", description = "更新字典类型信息")
    public Result<Boolean> updateDict(
        @Parameter(description = "字典编码", required = true)
        @PathVariable String code,
        @Valid @RequestBody Dict dict) {
        log.info("更新字典请求：code={}", code);

        Boolean success = dictService.updateDict(code, dict);
        return Result.success("更新字典成功", success);
    }

    /**
     * 删除字典
     *
     * @param code 字典编码
     * @return 是否成功
     */
    @DeleteMapping("/{code}")
    @Operation(summary = "删除字典", description = "根据字典编码删除字典")
    public Result<Boolean> deleteDict(
        @Parameter(description = "字典编码", required = true)
        @PathVariable String code) {
        log.info("删除字典请求：code={}", code);

        Boolean success = dictService.deleteDict(code);
        return Result.success("删除字典成功", success);
    }

    /**
     * 获取字典数据列表
     *
     * @param code 字典编码
     * @return 字典数据列表
     */
    @GetMapping("/{code}/data")
    @Operation(summary = "获取字典数据", description = "根据字典编码获取字典数据列表")
    public Result<List<DictData>> getDictData(
        @Parameter(description = "字典编码", required = true)
        @PathVariable String code) {
        log.info("获取字典数据请求：code={}", code);

        List<DictData> dataList = dictService.getDictData(code);
        return Result.success(dataList);
    }

    /**
     * 添加字典数据
     *
     * @param code     字典编码
     * @param dictData 字典数据
     * @return 是否成功
     */
    @PostMapping("/{code}/data")
    @Operation(summary = "添加字典数据", description = "为指定字典添加数据项")
    public Result<Boolean> addDictData(
        @Parameter(description = "字典编码", required = true)
        @PathVariable String code,
        @Valid @RequestBody DictData dictData) {
        log.info("添加字典数据请求：code={}, label={}", code, dictData.getDictLabel());

        Boolean success = dictService.addDictData(code, dictData);
        return Result.success("添加字典数据成功", success);
    }

    /**
     * 更新字典数据
     *
     * @param code     字典编码
     * @param dictData 字典数据
     * @return 是否成功
     */
    @PutMapping("/{code}/data")
    @Operation(summary = "更新字典数据", description = "更新字典数据项")
    public Result<Boolean> updateDictData(
        @Parameter(description = "字典编码", required = true)
        @PathVariable String code,
        @Valid @RequestBody DictData dictData) {
        log.info("更新字典数据请求：code={}, id={}", code, dictData.getId());

        Boolean success = dictService.updateDictData(code, dictData);
        return Result.success("更新字典数据成功", success);
    }

    /**
     * 删除字典数据
     *
     * @param code 字典编码
     * @param id   数据ID
     * @return 是否成功
     */
    @DeleteMapping("/{code}/data/{id}")
    @Operation(summary = "删除字典数据", description = "删除指定的字典数据项")
    public Result<Boolean> deleteDictData(
        @Parameter(description = "字典编码", required = true)
        @PathVariable String code,
        @Parameter(description = "数据ID", required = true)
        @PathVariable Long id) {
        log.info("删除字典数据请求：code={}, id={}", code, id);

        Boolean success = dictService.deleteDictData(code, id);
        return Result.success("删除字典数据成功", success);
    }
}
