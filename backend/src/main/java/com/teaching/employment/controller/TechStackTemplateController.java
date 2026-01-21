package com.teaching.employment.controller;

import com.teaching.employment.common.Result;
import com.teaching.employment.entity.TechStackTemplate;
import com.teaching.employment.service.TechStackTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 技术栈模板控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-20
 */
@RestController
@RequestMapping("/tech-stack-template")
@Api(tags = "技术栈模板管理")
@RequiredArgsConstructor
public class TechStackTemplateController {

    private final TechStackTemplateService techStackTemplateService;

    /**
     * 查询所有技术栈模板
     */
    @GetMapping("/list")
    @ApiOperation("查询所有技术栈模板")
    public Result<List<TechStackTemplate>> getTechStackTemplateList() {
        List<TechStackTemplate> list = techStackTemplateService.list();
        return Result.ok(list);
    }

    /**
     * 根据岗位类型查询技术栈模板
     */
    @GetMapping("/position-type/{positionType}")
    @ApiOperation("根据岗位类型查询技术栈模板")
    public Result<List<TechStackTemplate>> getTechStackTemplatesByPositionType(
            @PathVariable String positionType) {
        List<TechStackTemplate> list = techStackTemplateService.getTemplatesByPositionType(positionType);
        return Result.ok(list);
    }

    /**
     * 根据ID查询技术栈模板
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询技术栈模板")
    public Result<TechStackTemplate> getTechStackTemplateById(@PathVariable Long id) {
        TechStackTemplate template = techStackTemplateService.getById(id);
        return Result.ok(template);
    }

    /**
     * 新增技术栈模板
     */
    @PostMapping
    @ApiOperation("新增技术栈模板")
    public Result<Void> createTechStackTemplate(@RequestBody TechStackTemplate template) {
        boolean success = techStackTemplateService.save(template);
        return success ? Result.ok("新增成功") : Result.error("新增失败");
    }

    /**
     * 更新技术栈模板
     */
    @PutMapping
    @ApiOperation("更新技术栈模板")
    public Result<Void> updateTechStackTemplate(@RequestBody TechStackTemplate template) {
        boolean success = techStackTemplateService.updateById(template);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除技术栈模板
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除技术栈模板")
    public Result<Void> deleteTechStackTemplate(@PathVariable Long id) {
        boolean success = techStackTemplateService.removeById(id);
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }
}
