package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.School;
import com.teaching.employment.service.SchoolService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 学校管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@RestController
@RequestMapping("/school")
@Api(tags = "学校管理")
@RequiredArgsConstructor
public class SchoolController {

    private final SchoolService schoolService;

    /**
     * 分页查询学校列表
     */
    @GetMapping("/page")
    @ApiOperation("分页查询学校列表")
    public Result<IPage<School>> getSchoolPage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("学校名称") @RequestParam(required = false) String schoolName,
            @ApiParam("省份") @RequestParam(required = false) String province,
            @ApiParam("城市") @RequestParam(required = false) String city) {
        IPage<School> result = schoolService.getSchoolPage(current, size, schoolName, province, city);
        return Result.ok(result);
    }

    /**
     * 查询所有学校
     */
    @GetMapping("/list")
    @ApiOperation("查询所有学校")
    public Result<java.util.List<School>> getSchoolList() {
        java.util.List<School> list = schoolService.list();
        return Result.ok(list);
    }

    /**
     * 根据ID查询学校
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询学校")
    public Result<School> getSchoolById(@PathVariable Long id) {
        School school = schoolService.getById(id);
        return Result.ok(school);
    }

    /**
     * 新增学校
     */
    @PostMapping
    @ApiOperation("新增学校")
    public Result<Void> createSchool(@RequestBody School school) {
        boolean success = schoolService.save(school);
        return success ? Result.ok("新增成功") : Result.error("新增失败");
    }

    /**
     * 更新学校
     */
    @PutMapping
    @ApiOperation("更新学校")
    public Result<Void> updateSchool(@RequestBody School school) {
        boolean success = schoolService.updateById(school);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除学校
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除学校")
    public Result<Void> deleteSchool(@PathVariable Long id) {
        boolean success = schoolService.removeById(id);
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }
}
