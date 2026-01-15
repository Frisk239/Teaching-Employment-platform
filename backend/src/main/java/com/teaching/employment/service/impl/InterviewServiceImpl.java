package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.Interview;
import com.teaching.employment.entity.JobApplication;
import com.teaching.employment.entity.Position;
import com.teaching.employment.entity.Student;
import com.teaching.employment.entity.Company;
import com.teaching.employment.entity.User;
import com.teaching.employment.exception.BusinessException;
import com.teaching.employment.mapper.InterviewMapper;
import com.teaching.employment.service.CompanyService;
import com.teaching.employment.service.InterviewService;
import com.teaching.employment.service.JobApplicationService;
import com.teaching.employment.service.PositionService;
import com.teaching.employment.service.StudentService;
import com.teaching.employment.service.UserService;
import com.teaching.employment.util.EmailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 面试Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewServiceImpl extends ServiceImpl<InterviewMapper, Interview> implements InterviewService {

    private final InterviewMapper interviewMapper;
    private final JobApplicationService jobApplicationService;
    private final PositionService positionService;
    private final StudentService studentService;
    private final CompanyService companyService;
    private final UserService userService;
    private final EmailUtil emailUtil;

    @Override
    public IPage<Interview> getInterviewPage(Integer current, Integer size, Long applicationId, Long positionId,
                                              Long studentId, String status) {
        Page<Interview> page = new Page<>(current, size);
        LambdaQueryWrapper<Interview> wrapper = new LambdaQueryWrapper<>();

        if (applicationId != null) {
            wrapper.eq(Interview::getApplicationId, applicationId);
        }
        if (positionId != null) {
            wrapper.eq(Interview::getPositionId, positionId);
        }
        if (studentId != null) {
            wrapper.eq(Interview::getStudentId, studentId);
        }
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(Interview::getStatus, status);
        }

        wrapper.orderByDesc(Interview::getCreateTime);

        IPage<Interview> resultPage = interviewMapper.selectPage(page, wrapper);

        // 填充关联字段
        resultPage.getRecords().forEach(this::fillInterviewDetails);

        return resultPage;
    }

    /**
     * 填充面试的关联字段(学生姓名、职位名称、公司名称)
     */
    private Interview fillInterviewDetails(Interview interview) {
        if (interview == null) return null;

        // 填充学生姓名
        if (interview.getStudentId() != null) {
            Student student = studentService.getById(interview.getStudentId());
            if (student != null && student.getUserId() != null) {
                User user = userService.getById(student.getUserId());
                if (user != null && user.getRealName() != null) {
                    interview.setStudentName(user.getRealName());
                }
            }
        }

        // 填充职位名称和公司名称
        if (interview.getPositionId() != null) {
            Position position = positionService.getById(interview.getPositionId());
            if (position != null) {
                interview.setPositionName(position.getPositionName());

                // 填充公司名称
                if (position.getCompanyId() != null) {
                    Company company = companyService.getById(position.getCompanyId());
                    if (company != null) {
                        interview.setCompanyName(company.getCompanyName());
                    }
                }
            }
        }

        return interview;
    }

    @Override
    public IPage<Interview> getInterviewsByStudentId(Integer current, Integer size, Long studentId) {
        return getInterviewPage(current, size, null, null, studentId, null);
    }

    @Override
    public IPage<Interview> getInterviewsByApplicationId(Integer current, Integer size, Long applicationId) {
        return getInterviewPage(current, size, applicationId, null, null, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean scheduleInterview(Long applicationId, Integer round, String interviewType,
                                     LocalDateTime interviewTime, String location, String meetingLink,
                                     String interviewer, String interviewerContact) {
        // 检查申请是否存在
        JobApplication application = jobApplicationService.getById(applicationId);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }

        // 检查申请状态，只有笔试通过或直接进入面试的申请才能安排面试
        if (!"test_passed".equals(application.getStatus()) && !"screened".equals(application.getStatus())) {
            throw new BusinessException("只有笔试通过或已筛选的申请才能安排面试");
        }

        // 检查面试类型
        if ("online".equals(interviewType) && StrUtil.isBlank(meetingLink)) {
            throw new BusinessException("线上面试必须提供会议链接");
        }
        if ("offline".equals(interviewType) && StrUtil.isBlank(location)) {
            throw new BusinessException("线下面试必须提供面试地点");
        }

        // 检查该轮次是否已安排
        LambdaQueryWrapper<Interview> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(Interview::getApplicationId, applicationId);
        checkWrapper.eq(Interview::getRound, round);
        checkWrapper.ne(Interview::getStatus, "cancelled");
        long existingCount = interviewMapper.selectCount(checkWrapper);
        if (existingCount > 0) {
            throw new BusinessException("该轮次面试已安排");
        }

        // 创建面试
        Interview interview = new Interview();
        interview.setApplicationId(applicationId);
        interview.setPositionId(application.getPositionId());
        interview.setStudentId(application.getStudentId());
        interview.setRound(round);
        interview.setInterviewType(interviewType);
        interview.setInterviewTime(interviewTime);
        interview.setLocation(location);
        interview.setMeetingLink(meetingLink);
        interview.setInterviewer(interviewer);
        interview.setInterviewerContact(interviewerContact);
        interview.setResult("pending");
        interview.setStatus("scheduled");

        boolean result = interviewMapper.insert(interview) > 0;

        if (result) {
            // 更新申请阶段为面试
            jobApplicationService.updateApplicationStatus(applicationId, null, "interview");

            // 发送面试通知邮件
            try {
                Position position = positionService.getById(application.getPositionId());
                Student student = studentService.getById(application.getStudentId());
                Company company = companyService.getById(position.getCompanyId());

                if (student != null && position != null && company != null) {
                    String interviewDate = interviewTime.toLocalDate().toString();
                    String interviewTimeStr = interviewTime.toLocalTime().toString();
                    String address = "online".equals(interviewType)
                            ? "线上面试(会议链接: " + meetingLink + ")"
                            : location;

                    boolean emailSent = emailUtil.sendInterviewEmail(
                            student.getEmail(),
                            student.getRealName(),
                            company.getCompanyName(),
                            position.getPositionName(),
                            interviewDate,
                            interviewTimeStr,
                            address
                    );

                    if (emailSent) {
                        log.info("面试通知邮件发送成功: studentId={}, interviewId={}",
                                student.getId(), interview.getId());
                    } else {
                        log.error("面试通知邮件发送失败: studentId={}, interviewId={}",
                                student.getId(), interview.getId());
                    }
                }
            } catch (Exception e) {
                log.error("发送面试通知邮件异常: interviewId={}, error={}",
                        interview.getId(), e.getMessage(), e);
            }
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelInterview(Long interviewId) {
        Interview interview = interviewMapper.selectById(interviewId);
        if (interview == null) {
            throw new BusinessException("面试不存在");
        }

        if ("cancelled".equals(interview.getStatus())) {
            throw new BusinessException("面试已取消");
        }

        if ("completed".equals(interview.getStatus())) {
            throw new BusinessException("已完成的面试不能取消");
        }

        interview.setStatus("cancelled");

        return interviewMapper.updateById(interview) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitInterviewResult(Long interviewId, String result, Integer score, String comment) {
        Interview interview = interviewMapper.selectById(interviewId);
        if (interview == null) {
            throw new BusinessException("面试不存在");
        }

        if (!"scheduled".equals(interview.getStatus())) {
            throw new BusinessException("只有已安排的面试才能提交结果");
        }

        if (StrUtil.isBlank(result)) {
            throw new BusinessException("面试结果不能为空");
        }

        if (!"passed".equals(result) && !"failed".equals(result)) {
            throw new BusinessException("面试结果必须是passed或failed");
        }

        if (score != null && (score < 1 || score > 100)) {
            throw new BusinessException("面试评分必须在1-100分之间");
        }

        interview.setResult(result);
        interview.setScore(score);
        interview.setComment(comment);
        interview.setStatus("completed");

        boolean success = interviewMapper.updateById(interview) > 0;

        if (success) {
            // 更新申请状态
            JobApplication application = jobApplicationService.getById(interview.getApplicationId());
            if (application != null) {
                if ("passed".equals(result)) {
                    // 检查是否为终试
                    if (interview.getRound() == 3) {
                        // 终试通过，更新为面试通过
                        jobApplicationService.updateApplicationStatus(interview.getApplicationId(),
                                "interview_passed", "offer");
                    } else {
                        // 非终试通过，等待下一轮
                        jobApplicationService.updateApplicationStatus(interview.getApplicationId(),
                                "interview_passed", "interview");
                    }
                } else {
                    // 面试失败
                    jobApplicationService.updateApplicationStatus(interview.getApplicationId(),
                            "interview_failed", null);
                }
            }
        }

        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean rescheduleInterview(Long interviewId, LocalDateTime interviewTime, String location, String meetingLink) {
        Interview interview = interviewMapper.selectById(interviewId);
        if (interview == null) {
            throw new BusinessException("面试不存在");
        }

        if ("cancelled".equals(interview.getStatus())) {
            throw new BusinessException("已取消的面试不能重新安排");
        }

        if ("completed".equals(interview.getStatus())) {
            throw new BusinessException("已完成的面试不能重新安排");
        }

        interview.setInterviewTime(interviewTime);

        if (StrUtil.isNotBlank(location)) {
            interview.setLocation(location);
        }
        if (StrUtil.isNotBlank(meetingLink)) {
            interview.setMeetingLink(meetingLink);
        }

        return interviewMapper.updateById(interview) > 0;
    }

    @Override
    public List<Interview> getUpcomingInterviews(Long studentId) {
        LambdaQueryWrapper<Interview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Interview::getStudentId, studentId);
        wrapper.eq(Interview::getStatus, "scheduled");
        wrapper.ge(Interview::getInterviewTime, LocalDateTime.now());
        wrapper.orderByAsc(Interview::getInterviewTime);

        return interviewMapper.selectList(wrapper);
    }
}
