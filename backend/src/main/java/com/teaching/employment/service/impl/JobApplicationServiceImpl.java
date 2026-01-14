package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.JobApplication;
import com.teaching.employment.entity.Position;
import com.teaching.employment.entity.Resume;
import com.teaching.employment.entity.Student;
import com.teaching.employment.entity.Company;
import com.teaching.employment.entity.User;
import com.teaching.employment.exception.BusinessException;
import com.teaching.employment.mapper.JobApplicationMapper;
import com.teaching.employment.service.JobApplicationService;
import com.teaching.employment.service.PositionService;
import com.teaching.employment.service.ResumeService;
import com.teaching.employment.service.StudentService;
import com.teaching.employment.service.CompanyService;
import com.teaching.employment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 求职申请Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Service
@RequiredArgsConstructor
public class JobApplicationServiceImpl extends ServiceImpl<JobApplicationMapper, JobApplication>
        implements JobApplicationService {

    private final JobApplicationMapper jobApplicationMapper;
    private final PositionService positionService;
    private final ResumeService resumeService;
    private final StudentService studentService;
    private final CompanyService companyService;
    private final UserService userService;

    @Override
    public IPage<JobApplication> getJobApplicationPage(Integer current, Integer size, String studentName, Long positionId, Long studentId,
                                                         Long companyId, String status, String currentStage) {
        Page<JobApplication> page = new Page<>(current, size);
        LambdaQueryWrapper<JobApplication> wrapper = new LambdaQueryWrapper<>();

        // 学生姓名精确搜索 - 先通过学生姓名查询学生ID,再用ID过滤
        if (StrUtil.isNotBlank(studentName)) {
            // 查询用户表中符合姓名的用户ID列表
            LambdaQueryWrapper<com.teaching.employment.entity.User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.select(com.teaching.employment.entity.User::getId)
                       .eq(com.teaching.employment.entity.User::getUsername, studentName);
            java.util.List<Long> userIds = userService.list(userWrapper)
                    .stream()
                    .map(com.teaching.employment.entity.User::getId)
                    .collect(java.util.stream.Collectors.toList());

            // 通过用户ID查询学生ID列表
            if (!userIds.isEmpty()) {
                LambdaQueryWrapper<Student> studentWrapper = new LambdaQueryWrapper<>();
                studentWrapper.select(Student::getId)
                           .in(Student::getUserId, userIds);
                java.util.List<Long> studentIds = studentService.list(studentWrapper)
                        .stream()
                        .map(Student::getId)
                        .collect(java.util.stream.Collectors.toList());

                if (!studentIds.isEmpty()) {
                    wrapper.in(JobApplication::getStudentId, studentIds);
                } else {
                    // 没有找到匹配的学生,返回空结果
                    wrapper.eq(JobApplication::getId, -1L);
                }
            } else {
                // 没有找到匹配的用户,返回空结果
                wrapper.eq(JobApplication::getId, -1L);
            }
        }

        if (positionId != null) {
            wrapper.eq(JobApplication::getPositionId, positionId);
        }
        if (studentId != null) {
            wrapper.eq(JobApplication::getStudentId, studentId);
        }
        if (companyId != null) {
            // 需要通过positionId关联查询
            wrapper.in(JobApplication::getPositionId,
                    positionService.list(new LambdaQueryWrapper<Position>()
                            .select(Position::getId)
                            .eq(Position::getCompanyId, companyId))
                            .stream()
                            .map(Position::getId)
                            .toArray(Long[]::new));
        }
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(JobApplication::getStatus, status);
        }
        if (StrUtil.isNotBlank(currentStage)) {
            wrapper.eq(JobApplication::getCurrentStage, currentStage);
        }

        wrapper.orderByDesc(JobApplication::getApplicationTime);

        IPage<JobApplication> resultPage = jobApplicationMapper.selectPage(page, wrapper);

        // 填充关联字段信息
        resultPage.getRecords().forEach(application -> {
            // 填充职位名称
            if (application.getPositionId() != null) {
                Position position = positionService.getById(application.getPositionId());
                if (position != null) {
                    application.setPositionName(position.getPositionName());
                    // 填充企业名称
                    if (position.getCompanyId() != null) {
                        Company company = companyService.getById(position.getCompanyId());
                        if (company != null) {
                            application.setCompanyName(company.getCompanyName());
                        }
                    }
                }
            }

            // 填充学生姓名
            if (application.getStudentId() != null) {
                Student student = studentService.getById(application.getStudentId());
                if (student != null && student.getUserId() != null) {
                    User user = userService.getById(student.getUserId());
                    if (user != null) {
                        application.setStudentName(user.getUsername());
                    } else {
                        application.setStudentName("学生" + student.getId());
                    }
                }
            }
        });

        return resultPage;
    }

    @Override
    public IPage<JobApplication> getApplicationsByStudentId(Integer current, Integer size, Long studentId) {
        return getJobApplicationPage(current, size, null, null, studentId, null, null, null);
    }

    @Override
    public IPage<JobApplication> getApplicationsByPositionId(Integer current, Integer size, Long positionId) {
        return getJobApplicationPage(current, size, null, positionId, null, null, null, null);
    }

    @Override
    public IPage<JobApplication> getApplicationsByCompanyId(Integer current, Integer size, Long companyId) {
        return getJobApplicationPage(current, size, null, null, null, companyId, null, null);
    }

    @Override
    public boolean hasApplied(Long positionId, Long studentId) {
        LambdaQueryWrapper<JobApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JobApplication::getPositionId, positionId);
        wrapper.eq(JobApplication::getStudentId, studentId);
        wrapper.ne(JobApplication::getStatus, "declined"); // 排除已拒绝的

        return jobApplicationMapper.selectCount(wrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitApplication(Long positionId, Long studentId, Long resumeId) {
        // 检查是否已申请
        if (hasApplied(positionId, studentId)) {
            throw new BusinessException("您已经申请过该职位");
        }

        // 检查职位是否存在且在招聘中
        Position position = positionService.getById(positionId);
        if (position == null) {
            throw new BusinessException("职位不存在");
        }
        if (!"active".equals(position.getStatus())) {
            throw new BusinessException("该职位不在招聘中");
        }

        // 检查截止日期
        if (position.getDeadline() != null && position.getDeadline().isBefore(java.time.LocalDate.now())) {
            throw new BusinessException("该职位已过申请截止日期");
        }

        // 检查简历是否存在且已公开
        Resume resume = resumeService.getById(resumeId);
        if (resume == null) {
            throw new BusinessException("简历不存在");
        }
        if (!"active".equals(resume.getStatus())) {
            throw new BusinessException("简历未公开，请先公开简历");
        }
        if (!resume.getStudentId().equals(studentId)) {
            throw new BusinessException("简历与申请人不匹配");
        }

        // 创建申请
        JobApplication application = new JobApplication();
        application.setPositionId(positionId);
        application.setStudentId(studentId);
        application.setResumeId(resumeId);
        application.setStatus("pending");
        application.setCurrentStage("resume");
        application.setApplicationTime(LocalDateTime.now());

        boolean result = jobApplicationMapper.insert(application) > 0;

        if (result) {
            // 增加职位投递数
            positionService.incrementApplicationCount(positionId);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean screenResume(Long applicationId, boolean passed, String remark) {
        JobApplication application = jobApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }

        if (!"pending".equals(application.getStatus())) {
            throw new BusinessException("只有待处理的申请才能筛选");
        }

        application.setHrRemark(remark);

        if (passed) {
            application.setStatus("screened");
        } else {
            application.setStatus("rejected");
        }

        return jobApplicationMapper.updateById(application) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateApplicationStatus(Long applicationId, String status, String currentStage) {
        JobApplication application = jobApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }

        String oldStatus = application.getStatus();

        if (StrUtil.isNotBlank(status)) {
            application.setStatus(status);
        }
        if (StrUtil.isNotBlank(currentStage)) {
            application.setCurrentStage(currentStage);
        }

        boolean result = jobApplicationMapper.updateById(application) > 0;

        // 同步更新职位的已招人数
        if (result && StrUtil.isNotBlank(status)) {
            // 状态变为 hired（已入职）时，增加已招人数
            if ("hired".equals(status) && !"hired".equals(oldStatus)) {
                positionService.incrementHiredCount(application.getPositionId());
            }
            // 状态从 hired 变为其他状态时，减少已招人数
            else if (!"hired".equals(status) && "hired".equals(oldStatus)) {
                positionService.decrementHiredCount(application.getPositionId());
            }
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean withdrawApplication(Long applicationId, Long studentId) {
        JobApplication application = jobApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }

        if (!application.getStudentId().equals(studentId)) {
            throw new BusinessException("无权操作此申请");
        }

        // 只有特定状态才能撤销
        String status = application.getStatus();
        if ("hired".equals(status) || "offered".equals(status) || "declined".equals(status)) {
            throw new BusinessException("当前状态不允许撤销申请");
        }

        application.setStatus("declined");
        boolean result = jobApplicationMapper.updateById(application) > 0;

        if (result) {
            // 同步减少职位的投递数量
            positionService.decrementApplicationCount(application.getPositionId());
        }

        return result;
    }
}
