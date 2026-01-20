package com.teaching.employment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.GuidanceRecord;
import com.teaching.employment.entity.Student;
import com.teaching.employment.entity.Teacher;
import com.teaching.employment.entity.User;
import com.teaching.employment.mapper.GuidanceRecordMapper;
import com.teaching.employment.service.StudentService;
import com.teaching.employment.service.TeacherService;
import com.teaching.employment.service.UserService;
import com.teaching.employment.service.GuidanceRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 指导记录Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-20
 */
@Service
@RequiredArgsConstructor
public class GuidanceRecordServiceImpl extends ServiceImpl<GuidanceRecordMapper, GuidanceRecord>
        implements GuidanceRecordService {

    private final GuidanceRecordMapper guidanceRecordMapper;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createGuidance(Long studentId, Long teacherId, String guidanceType, String content,
                                  String nextAction, java.time.LocalDateTime guidanceDate, String location) {
        GuidanceRecord guidance = new GuidanceRecord();
        guidance.setStudentId(studentId);
        guidance.setTeacherId(teacherId);
        guidance.setGuidanceType(guidanceType);
        guidance.setContent(content);
        guidance.setNextAction(nextAction);
        guidance.setGuidanceDate(guidanceDate);
        guidance.setLocation(location);

        return save(guidance);
    }

    @Override
    public IPage<GuidanceRecord> getGuidancesByStudent(Integer current, Integer size, Long studentId) {
        Page<GuidanceRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<GuidanceRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GuidanceRecord::getStudentId, studentId);
        wrapper.orderByDesc(GuidanceRecord::getGuidanceDate);

        IPage<GuidanceRecord> result = guidanceRecordMapper.selectPage(page, wrapper);
        fillRelatedData(result.getRecords());

        return result;
    }

    @Override
    public IPage<GuidanceRecord> getGuidancesByTeacher(Integer current, Integer size, Long teacherId) {
        Page<GuidanceRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<GuidanceRecord> wrapper = new LambdaQueryWrapper<>();

        // 如果teacherId为null或0，则查询所有记录（学院负责人场景）
        if (teacherId != null && teacherId != 0) {
            wrapper.eq(GuidanceRecord::getTeacherId, teacherId);
        }

        wrapper.orderByDesc(GuidanceRecord::getGuidanceDate);

        IPage<GuidanceRecord> result = guidanceRecordMapper.selectPage(page, wrapper);
        fillRelatedData(result.getRecords());

        return result;
    }

    @Override
    public List<GuidanceRecord> getGuidanceListByStudent(Long studentId) {
        LambdaQueryWrapper<GuidanceRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GuidanceRecord::getStudentId, studentId);
        wrapper.orderByDesc(GuidanceRecord::getGuidanceDate);

        List<GuidanceRecord> records = guidanceRecordMapper.selectList(wrapper);
        fillRelatedData(records);

        return records;
    }

    @Override
    public GuidanceRecord getGuidanceWithDetails(Long id) {
        GuidanceRecord guidance = getById(id);
        if (guidance != null) {
            fillRelatedData(java.util.Collections.singletonList(guidance));
        }
        return guidance;
    }

    /**
     * 填充关联数据
     */
    private void fillRelatedData(List<GuidanceRecord> guidances) {
        if (guidances == null || guidances.isEmpty()) {
            return;
        }

        // 收集所有需要查询的ID
        List<Long> studentIds = guidances.stream()
                .map(GuidanceRecord::getStudentId)
                .distinct()
                .collect(Collectors.toList());

        List<Long> teacherIds = guidances.stream()
                .map(GuidanceRecord::getTeacherId)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询学生和用户
        final Map<Long, Student> studentMap;
        if (!studentIds.isEmpty()) {
            List<Student> students = studentService.listByIds(studentIds);
            // 收集用户ID
            List<Long> userIds = students.stream()
                    .map(Student::getUserId)
                    .distinct()
                    .collect(Collectors.toList());

            // 批量查询用户信息
            final Map<Long, User> userMap;
            if (!userIds.isEmpty()) {
                List<User> users = userService.listByIds(userIds);
                userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));
            } else {
                userMap = Map.of();
            }

            // 填充学生姓名
            studentMap = students.stream().collect(Collectors.toMap(Student::getId, s -> {
                User user = userMap.get(s.getUserId());
                if (user != null) {
                    s.setName(user.getRealName());
                }
                return s;
            }));
        } else {
            studentMap = Map.of();
        }

        // 批量查询教师
        final Map<Long, Teacher> teacherMap;
        if (!teacherIds.isEmpty()) {
            List<Teacher> teachers = teacherService.listByIds(teacherIds);
            teacherMap = teachers.stream().collect(Collectors.toMap(Teacher::getId, t -> t));
        } else {
            teacherMap = Map.of();
        }

        // 填充数据
        final java.util.Map<String, String> guidanceTypeMap = getGuidanceTypeMap();

        guidances.forEach(guidance -> {
            Student student = studentMap.get(guidance.getStudentId());
            if (student != null) {
                guidance.setStudentName(student.getName());
            }

            Teacher teacher = teacherMap.get(guidance.getTeacherId());
            if (teacher != null) {
                guidance.setTeacherName(teacher.getName());
            }

            guidance.setGuidanceTypeName(guidanceTypeMap.getOrDefault(guidance.getGuidanceType(), guidance.getGuidanceType()));
        });
    }

    /**
     * 获取指导类型映射
     */
    private java.util.Map<String, String> getGuidanceTypeMap() {
        return java.util.Map.of(
                "career_planning", "职业规划",
                "resume_guidance", "简历指导",
                "interview_guidance", "面试指导",
                "skill_improvement", "技能提升",
                "psychological_counseling", "心理辅导",
                "other", "其他"
        );
    }
}
