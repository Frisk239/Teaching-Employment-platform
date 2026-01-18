package com.teaching.employment.controller;

import com.teaching.employment.common.Result;
import com.teaching.employment.entity.Internship;
import com.teaching.employment.service.InternshipService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 实习经历管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-18
 */
@RestController
@RequestMapping("/internship")
@Api(tags = "实习经历管理")
@RequiredArgsConstructor
public class InternshipController {

    private final InternshipService internshipService;

    /**
     * 根据用户ID查询实习经历列表
     */
    @GetMapping("/user/{userId}")
    @ApiOperation("根据用户ID查询实习经历列表")
    public Result<List<Internship>> getInternshipByUserId(
            @ApiParam("用户ID") @PathVariable Long userId) {
        List<Internship> list = internshipService.getInternshipByUserId(userId);
        return Result.ok(list);
    }

    /**
     * 根据ID查询实习经历详情
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询实习经历详情")
    public Result<Internship> getInternshipById(
            @ApiParam("实习经历ID") @PathVariable Long id) {
        Internship internship = internshipService.getInternshipById(id);
        return Result.ok(internship);
    }

    /**
     * 新增实习经历
     */
    @PostMapping
    @ApiOperation("新增实习经历")
    public Result<Void> createInternship(@RequestBody Internship internship) {
        boolean success = internshipService.createInternship(internship);
        return success ? Result.ok("新增成功") : Result.error("新增失败");
    }

    /**
     * 更新实习经历
     */
    @PutMapping
    @ApiOperation("更新实习经历")
    public Result<Void> updateInternship(@RequestBody Internship internship) {
        boolean success = internshipService.updateInternship(internship);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除实习经历
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除实习经历")
    public Result<Void> deleteInternship(
            @ApiParam("实习经历ID") @PathVariable Long id) {
        boolean success = internshipService.deleteInternship(id);
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }
}
