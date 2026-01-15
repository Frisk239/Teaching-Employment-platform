package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.JobApplication;
import com.teaching.employment.service.JobApplicationService;
import com.teaching.employment.service.PositionService;
import com.teaching.employment.service.StudentService;
import com.teaching.employment.service.CompanyService;
import com.teaching.employment.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 求职申请管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@RestController
@RequestMapping("/job-application")
@Api(tags = "求职申请管理")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;
    private final PositionService positionService;
    private final StudentService studentService;
    private final CompanyService companyService;
    private final UserService userService;

    /**
     * 分页查询求职申请列表
     */
    @GetMapping("/page")
    @ApiOperation("分页查询求职申请列表")
    public Result<IPage<JobApplication>> getJobApplicationPage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("学生姓名") @RequestParam(required = false) String studentName,
            @ApiParam("职位ID") @RequestParam(required = false) Long positionId,
            @ApiParam("学生ID") @RequestParam(required = false) Long studentId,
            @ApiParam("企业ID") @RequestParam(required = false) Long companyId,
            @ApiParam("申请状态") @RequestParam(required = false) String status,
            @ApiParam("当前阶段") @RequestParam(required = false) String currentStage) {
        IPage<JobApplication> page = jobApplicationService.getJobApplicationPage(current, size, studentName, positionId,
                studentId, companyId, status, currentStage);
        return Result.ok(page);
    }

    /**
     * 根据学生ID查询申请列表
     */
    @GetMapping("/student/{studentId}")
    @ApiOperation("根据学生ID查询申请列表")
    public Result<IPage<JobApplication>> getApplicationsByStudentId(
            @PathVariable Long studentId,
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        IPage<JobApplication> page = jobApplicationService.getApplicationsByStudentId(current, size, studentId);
        return Result.ok(page);
    }

    /**
     * 根据职位ID查询申请列表
     */
    @GetMapping("/position/{positionId}")
    @ApiOperation("根据职位ID查询申请列表")
    public Result<IPage<JobApplication>> getApplicationsByPositionId(
            @PathVariable Long positionId,
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        IPage<JobApplication> page = jobApplicationService.getApplicationsByPositionId(current, size, positionId);
        return Result.ok(page);
    }

    /**
     * 根据企业ID查询申请列表
     */
    @GetMapping("/company/{companyId}")
    @ApiOperation("根据企业ID查询申请列表")
    public Result<IPage<JobApplication>> getApplicationsByCompanyId(
            @PathVariable Long companyId,
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        IPage<JobApplication> page = jobApplicationService.getApplicationsByCompanyId(current, size, companyId);
        return Result.ok(page);
    }

    /**
     * 根据ID查询申请
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询申请")
    public Result<JobApplication> getJobApplicationById(@PathVariable Long id) {
        JobApplication application = jobApplicationService.getById(id);
        if (application == null) {
            return Result.error("申请不存在");
        }

        // 手动填充关联数据
        application = fillApplicationDetails(application);

        return Result.ok(application);
    }

    /**
     * 填充申请的关联数据
     */
    private JobApplication fillApplicationDetails(JobApplication application) {
        if (application == null) {
            return null;
        }

        // 填充职位名称
        if (application.getPositionId() != null) {
            com.teaching.employment.entity.Position position = positionService.getById(application.getPositionId());
            if (position != null) {
                application.setPositionName(position.getPositionName());
                // 填充企业名称
                if (position.getCompanyId() != null) {
                    com.teaching.employment.entity.Company company = companyService.getById(position.getCompanyId());
                    if (company != null) {
                        application.setCompanyName(company.getCompanyName());
                    }
                }
            }
        }

        // 填充学生姓名和电话
        if (application.getStudentId() != null) {
            com.teaching.employment.entity.Student student = studentService.getById(application.getStudentId());
            if (student != null) {
                application.setStudentPhone(student.getPhone());

                // 填充学生真实姓名
                if (student.getUserId() != null) {
                    com.teaching.employment.entity.User user = userService.getById(student.getUserId());
                    if (user != null && user.getRealName() != null) {
                        application.setStudentName(user.getRealName());
                    } else {
                        application.setStudentName("学生" + student.getId());
                    }
                } else {
                    application.setStudentName("学生" + student.getId());
                }
            }
        }

        return application;
    }

    /**
     * 检查学生是否已申请该职位
     */
    @GetMapping("/check")
    @ApiOperation("检查学生是否已申请该职位")
    public Result<Boolean> hasApplied(
            @ApiParam("职位ID") @RequestParam Long positionId,
            @ApiParam("学生ID") @RequestParam Long studentId) {
        boolean hasApplied = jobApplicationService.hasApplied(positionId, studentId);
        return Result.ok(hasApplied);
    }

    /**
     * 投递简历
     */
    @PostMapping("/submit")
    @ApiOperation("投递简历")
    public Result<Void> submitApplication(
            @ApiParam("职位ID") @RequestParam Long positionId,
            @ApiParam("学生ID") @RequestParam Long studentId,
            @ApiParam("简历ID") @RequestParam Long resumeId) {
        boolean success = jobApplicationService.submitApplication(positionId, studentId, resumeId);
        return success ? Result.ok("投递成功") : Result.error("投递失败");
    }

    /**
     * 新增申请
     */
    @PostMapping
    @ApiOperation("新增申请")
    public Result<Void> createJobApplication(@RequestBody JobApplication application) {
        // 默认状态为待处理
        if (application.getStatus() == null) {
            application.setStatus("pending");
        }
        // 默认阶段为简历筛选
        if (application.getCurrentStage() == null) {
            application.setCurrentStage("resume");
        }
        boolean success = jobApplicationService.save(application);
        return success ? Result.ok("新增成功") : Result.error("新增失败");
    }

    /**
     * 更新申请
     */
    @PutMapping
    @ApiOperation("更新申请")
    public Result<Void> updateJobApplication(@RequestBody JobApplication application) {
        boolean success = jobApplicationService.updateById(application);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除申请（物理删除）
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除申请")
    public Result<Void> deleteJobApplication(@PathVariable Long id) {
        // 使用物理删除而不是逻辑删除
        int result = jobApplicationService.getBaseMapper().deleteById(id);
        return result > 0 ? Result.ok("删除成功") : Result.error("删除失败");
    }

    /**
     * 批量删除申请（物理删除）
     */
    @DeleteMapping("/batch")
    @ApiOperation("批量删除申请")
    public Result<Void> batchDeleteJobApplication(@RequestBody java.util.List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的记录");
        }
        // 使用物理删除而不是逻辑删除
        int result = jobApplicationService.getBaseMapper().deleteBatchIds(ids);
        return result > 0 ? Result.ok("批量删除成功") : Result.error("批量删除失败");
    }

    /**
     * 筛选简历（通过/不通过）
     */
    @PostMapping("/{id}/screen")
    @ApiOperation("筛选简历")
    public Result<Void> screenResume(
            @PathVariable Long id,
            @ApiParam("是否通过") @RequestParam Boolean passed,
            @ApiParam("备注信息") @RequestParam(required = false) String remark) {
        boolean success = jobApplicationService.screenResume(id, passed, remark);
        return success ? Result.ok("筛选成功") : Result.error("筛选失败");
    }

    /**
     * 更新申请状态
     */
    @PostMapping("/{id}/update-status")
    @ApiOperation("更新申请状态")
    public Result<Void> updateApplicationStatus(
            @PathVariable Long id,
            @ApiParam("新状态") @RequestParam String status,
            @ApiParam("当前阶段") @RequestParam String currentStage) {
        boolean success = jobApplicationService.updateApplicationStatus(id, status, currentStage);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 撤销申请
     */
    @PostMapping("/{id}/withdraw")
    @ApiOperation("撤销申请")
    public Result<Void> withdrawApplication(
            @PathVariable Long id,
            @ApiParam("学生ID") @RequestParam Long studentId) {
        boolean success = jobApplicationService.withdrawApplication(id, studentId);
        return success ? Result.ok("撤销成功") : Result.error("撤销失败");
    }
}
