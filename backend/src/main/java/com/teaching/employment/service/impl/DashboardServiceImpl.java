package com.teaching.employment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teaching.employment.mapper.DailyReportMapper;
import com.teaching.employment.mapper.OfferMapper;
import com.teaching.employment.mapper.StudentMapper;
import com.teaching.employment.mapper.TeacherMapper;
import com.teaching.employment.mapper.CourseMapper;
import com.teaching.employment.mapper.JobApplicationMapper;
import com.teaching.employment.mapper.UserMapper;
import com.teaching.employment.entity.DailyReport;
import com.teaching.employment.entity.Student;
import com.teaching.employment.entity.User;
import com.teaching.employment.entity.Offer;
import com.teaching.employment.model.dto.DailyReportDTO;
import com.teaching.employment.model.dto.DashboardStatsDTO;
import com.teaching.employment.model.dto.StudentAttentionDTO;
import com.teaching.employment.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Dashboard Service实现
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final TeacherMapper teacherMapper;
    private final StudentMapper studentMapper;
    private final CourseMapper courseMapper;
    private final DailyReportMapper dailyReportMapper;
    private final OfferMapper offerMapper;
    private final JobApplicationMapper jobApplicationMapper;
    private final UserMapper userMapper;

    @Override
    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        // 获取教师数量
        stats.setTeacherCount(teacherMapper.selectCount(null).intValue());

        // 获取学生数量
        stats.setStudentCount(studentMapper.selectCount(null).intValue());

        // 获取课程数量
        stats.setCourseCount(courseMapper.selectCount(null).intValue());

        // 计算就业率（基于已录取的学员数量 - graduationDate不为空视为已毕业/就业）
        Long graduatedCount = studentMapper.selectCount(
            new LambdaQueryWrapper<Student>().isNotNull(Student::getGraduationDate)
        );
        Long totalStudents = studentMapper.selectCount(null);
        double employmentRate = totalStudents > 0
            ? (graduatedCount.doubleValue() / totalStudents) * 100
            : 0.0;
        stats.setEmploymentRate(Math.round(employmentRate * 10.0) / 10.0);

        // 待审阅日报数量（状态为submitted）
        stats.setPendingReports(dailyReportMapper.selectCount(
            new LambdaQueryWrapper<DailyReport>().eq(DailyReport::getStatus, "submitted")
        ).intValue());

        // 正在求职的学员数量（有预计毕业日期的视为正在求职/准备求职）
        LocalDate now = LocalDate.now();
        stats.setSeekingStudents(studentMapper.selectCount(
            new LambdaQueryWrapper<Student>()
                .isNotNull(Student::getGraduationDate)
                .ge(Student::getGraduationDate, now)
        ).intValue());

        // 待确认Offer数量（状态为pending）
        stats.setPendingOffers(offerMapper.selectCount(
            new LambdaQueryWrapper<Offer>()
                .eq(Offer::getStatus, "pending")
        ).intValue());

        // 新学员数量（入学日期在30天内）
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        stats.setNewStudents(studentMapper.selectCount(
            new LambdaQueryWrapper<Student>()
                .ge(Student::getEnrollmentDate, thirtyDaysAgo)
        ).intValue());

        return stats;
    }

    @Override
    public List<DailyReportDTO> getRecentDailyReports(Integer limit) {
        List<DailyReport> reports = dailyReportMapper.selectList(
            new LambdaQueryWrapper<DailyReport>()
                .orderByDesc(DailyReport::getReportDate)
                .last("LIMIT " + limit)
        );

        // 收集所有学生ID
        List<Long> studentIds = reports.stream()
            .map(DailyReport::getStudentId)
            .distinct()
            .collect(Collectors.toList());

        // 批量查询学生信息
        Map<Long, String> studentNameMap = studentIds.isEmpty() ? Map.of() :
            studentMapper.selectBatchIds(studentIds).stream()
                .collect(Collectors.toMap(
                    Student::getId,
                    s -> {
                        // 如果student有realName字段（来自关联查询），使用它
                        if (s.getRealName() != null) {
                            return s.getRealName();
                        }
                        // 否则通过userId查询用户表获取姓名
                        if (s.getUserId() != null) {
                            User user = userMapper.selectById(s.getUserId());
                            return user != null ? user.getRealName() : "未知";
                        }
                        return "未知";
                    }
                ));

        return reports.stream().map(report -> {
            DailyReportDTO dto = new DailyReportDTO();
            dto.setId(report.getId());
            dto.setStudentId(report.getStudentId());
            dto.setStudentName(studentNameMap.getOrDefault(report.getStudentId(), "未知"));
            dto.setReportDate(report.getReportDate());
            // 使用todayContent而不是content
            dto.setContent(report.getTodayContent());
            dto.setStudyHours(report.getStudyHours());
            dto.setStatus(report.getStatus());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<StudentAttentionDTO> getAttentionStudents(Integer limit) {
        // 获取需要关注的学员（有预计毕业日期的学员）
        LocalDate now = LocalDate.now();
        List<Student> students = studentMapper.selectList(
            new LambdaQueryWrapper<Student>()
                .isNotNull(Student::getGraduationDate)
                .ge(Student::getGraduationDate, now)
                .orderByAsc(Student::getEnrollmentDate)
                .last("LIMIT " + limit)
        );

        return students.stream().map(student -> {
            StudentAttentionDTO dto = new StudentAttentionDTO();
            dto.setId(student.getId());

            // 获取学生姓名
            String realName = student.getRealName();
            if (realName == null && student.getUserId() != null) {
                User user = userMapper.selectById(student.getUserId());
                realName = user != null ? user.getRealName() : "未知";
            }
            dto.setRealName(realName != null ? realName : "未知");

            dto.setMajor(student.getMajor());
            dto.setGrade(student.getGrade());

            // 设置求职状态（基于毕业日期判断）
            if (student.getGraduationDate() != null) {
                if (student.getGraduationDate().isBefore(now)) {
                    dto.setSeekingStatus("employed"); // 已毕业/已就业
                } else if (student.getGraduationDate().minusMonths(3).isBefore(now)) {
                    dto.setSeekingStatus("actively_seeking"); // 临近毕业，正在求职
                } else {
                    dto.setSeekingStatus("seeking"); // 准备求职
                }
            } else {
                dto.setSeekingStatus("admitted"); // 已录取
            }

            dto.setClassName(student.getClassName());
            return dto;
        }).collect(Collectors.toList());
    }
}
