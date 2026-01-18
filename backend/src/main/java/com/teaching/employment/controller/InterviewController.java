package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.Interview;
import com.teaching.employment.entity.User;
// import com.teaching.employment.service.EmailService;  // Temporarily disabled - Thymeleaf dependency not added
import com.teaching.employment.service.InterviewService;
import com.teaching.employment.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 面试管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Slf4j
@RestController
@RequestMapping("/interview")
@Api(tags = "面试管理")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;
    // private final EmailService emailService;  // Temporarily disabled - Thymeleaf dependency not added
    private final UserService userService;

    /**
     * 分页查询面试列表
     */
    @GetMapping("/page")
    @ApiOperation("分页查询面试列表")
    public Result<IPage<Interview>> getInterviewPage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("申请ID") @RequestParam(required = false) Long applicationId,
            @ApiParam("职位ID") @RequestParam(required = false) Long positionId,
            @ApiParam("学生ID") @RequestParam(required = false) Long studentId,
            @ApiParam("面试状态") @RequestParam(required = false) String status) {
        IPage<Interview> page = interviewService.getInterviewPage(current, size, applicationId,
                positionId, studentId, status);
        return Result.ok(page);
    }

    /**
     * 根据学生ID查询面试列表
     */
    @GetMapping("/student/{studentId}")
    @ApiOperation("根据学生ID查询面试列表")
    public Result<IPage<Interview>> getInterviewsByStudentId(
            @PathVariable Long studentId,
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        IPage<Interview> page = interviewService.getInterviewsByStudentId(current, size, studentId);
        return Result.ok(page);
    }

    /**
     * 根据申请ID查询面试列表
     */
    @GetMapping("/application/{applicationId}")
    @ApiOperation("根据申请ID查询面试列表")
    public Result<IPage<Interview>> getInterviewsByApplicationId(
            @PathVariable Long applicationId,
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        IPage<Interview> page = interviewService.getInterviewsByApplicationId(current, size, applicationId);
        return Result.ok(page);
    }

    /**
     * 获取学生即将到来的面试
     */
    @GetMapping("/student/{studentId}/upcoming")
    @ApiOperation("获取学生即将到来的面试")
    public Result<List<Interview>> getUpcomingInterviews(@PathVariable Long studentId) {
        List<Interview> interviews = interviewService.getUpcomingInterviews(studentId);
        return Result.ok(interviews);
    }

    /**
     * 根据ID查询面试
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询面试")
    public Result<Interview> getInterviewById(@PathVariable Long id) {
        Interview interview = interviewService.getById(id);
        return Result.ok(interview);
    }

    /**
     * 新增面试
     */
    @PostMapping
    @ApiOperation("新增面试")
    public Result<Void> createInterview(@RequestBody Interview interview) {
        // 默认状态为已安排
        if (interview.getStatus() == null) {
            interview.setStatus("scheduled");
        }
        // 默认结果为待面试
        if (interview.getResult() == null) {
            interview.setResult("pending");
        }
        boolean success = interviewService.save(interview);

        // 发送面试邀请邮件
        if (success && interview.getStudentId() != null) {
            try {
                User student = userService.getById(interview.getStudentId());
                // TODO: Re-enable when EmailService is properly configured
                // if (student != null && student.getEmail() != null) {
                //     emailService.sendInterviewInviteEmail(student.getEmail(), interview, student);
                //     log.info("面试邀请邮件已发送: studentId={}, interviewId={}", interview.getStudentId(), interview.getId());
                // }
            } catch (Exception e) {
                log.error("发送面试邀请邮件失败: {}", e.getMessage(), e);
                // 邮件发送失败不影响面试创建
            }
        }

        return success ? Result.ok("新增成功") : Result.error("新增失败");
    }

    /**
     * 更新面试
     */
    @PutMapping
    @ApiOperation("更新面试")
    public Result<Void> updateInterview(@RequestBody Interview interview) {
        boolean success = interviewService.updateById(interview);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除面试
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除面试")
    public Result<Void> deleteInterview(@PathVariable Long id) {
        boolean success = interviewService.removeById(id);
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }

    /**
     * 安排面试
     */
    @PostMapping("/schedule")
    @ApiOperation("安排面试")
    public Result<Void> scheduleInterview(
            @ApiParam("申请ID") @RequestParam Long applicationId,
            @ApiParam("面试轮次") @RequestParam Integer round,
            @ApiParam("面试类型") @RequestParam String interviewType,
            @ApiParam("面试时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime interviewTime,
            @ApiParam("面试地点") @RequestParam(required = false) String location,
            @ApiParam("会议链接") @RequestParam(required = false) String meetingLink,
            @ApiParam("面试官") @RequestParam String interviewer,
            @ApiParam("面试官联系方式") @RequestParam(required = false) String interviewerContact) {
        boolean success = interviewService.scheduleInterview(applicationId, round, interviewType,
                interviewTime, location, meetingLink, interviewer, interviewerContact);
        return success ? Result.ok("安排成功") : Result.error("安排失败");
    }

    /**
     * 取消面试
     */
    @PostMapping("/{id}/cancel")
    @ApiOperation("取消面试")
    public Result<Void> cancelInterview(@PathVariable Long id) {
        boolean success = interviewService.cancelInterview(id);
        return success ? Result.ok("取消成功") : Result.error("取消失败");
    }

    /**
     * 提交面试结果
     */
    @PostMapping("/{id}/submit-result")
    @ApiOperation("提交面试结果")
    public Result<Void> submitInterviewResult(
            @PathVariable Long id,
            @ApiParam("面试结果") @RequestParam String result,
            @ApiParam("面试评分") @RequestParam(required = false) Integer score,
            @ApiParam("面试评价") @RequestParam(required = false) String comment) {
        // 获取面试信息
        Interview interview = interviewService.getById(id);
        if (interview == null) {
            return Result.error("面试不存在");
        }

        boolean success = interviewService.submitInterviewResult(id, result, score, comment);

        // 发送面试结果邮件
        if (success && interview.getStudentId() != null) {
            try {
                User student = userService.getById(interview.getStudentId());
                // TODO: Re-enable when EmailService is properly configured
                // if (student != null && student.getEmail() != null) {
                //     boolean isPassed = "pass".equals(result);
                //     emailService.sendInterviewResultEmail(student.getEmail(), interview, student, isPassed);
                //     log.info("面试结果邮件已发送: studentId={}, interviewId={}, passed={}", interview.getStudentId(), id, isPassed);
                // }
            } catch (Exception e) {
                log.error("发送面试结果邮件失败: {}", e.getMessage(), e);
                // 邮件发送失败不影响结果提交
            }
        }

        return success ? Result.ok("提交成功") : Result.error("提交失败");
    }

    /**
     * 重新安排面试
     */
    @PostMapping("/{id}/reschedule")
    @ApiOperation("重新安排面试")
    public Result<Void> rescheduleInterview(
            @PathVariable Long id,
            @ApiParam("新的面试时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime interviewTime,
            @ApiParam("新的面试地点") @RequestParam(required = false) String location,
            @ApiParam("新的会议链接") @RequestParam(required = false) String meetingLink) {
        boolean success = interviewService.rescheduleInterview(id, interviewTime, location, meetingLink);
        return success ? Result.ok("重新安排成功") : Result.error("重新安排失败");
    }

    /**
     * 学员接受面试
     */
    @PostMapping("/{id}/accept")
    @ApiOperation("学员接受面试")
    public Result<Void> acceptInterview(@PathVariable Long id) {
        Interview interview = interviewService.getById(id);
        if (interview == null) {
            return Result.error("面试不存在");
        }

        // 更新面试状态为已接受
        interview.setStatus("accepted");
        boolean success = interviewService.updateById(interview);

        // 发送确认邮件给企业
        if (success && interview.getStudentId() != null) {
            try {
                User student = userService.getById(interview.getStudentId());
                // TODO: Re-enable when EmailService is properly configured
                // if (student != null) {
                //     // 获取企业邮箱（这里需要从Company表获取，暂时使用占位符）
                //     String companyEmail = "hr@" + interview.getCompanyName().replaceAll("\\s+", "") + ".com";
                //     emailService.sendInterviewAcceptedEmail(companyEmail, interview, student);
                //     log.info("面试接受确认邮件已发送: companyId={}, interviewId={}", interview.getCompanyId(), id);
                // }
            } catch (Exception e) {
                log.error("发送面试接受确认邮件失败: {}", e.getMessage(), e);
            }
        }

        return success ? Result.ok("已接受面试") : Result.error("操作失败");
    }

    /**
     * 学员拒绝面试
     */
    @PostMapping("/{id}/reject")
    @ApiOperation("学员拒绝面试")
    public Result<Void> rejectInterview(
            @PathVariable Long id,
            @ApiParam("拒绝原因") @RequestParam(required = false) String reason) {
        Interview interview = interviewService.getById(id);
        if (interview == null) {
            return Result.error("面试不存在");
        }

        // 更新面试状态为已拒绝
        interview.setStatus("rejected");
        interview.setComment(reason);  // Use comment field instead of feedback
        boolean success = interviewService.updateById(interview);

        log.info("学员拒绝面试: interviewId={}, reason={}", id, reason);
        return success ? Result.ok("已拒绝面试") : Result.error("操作失败");
    }
}
