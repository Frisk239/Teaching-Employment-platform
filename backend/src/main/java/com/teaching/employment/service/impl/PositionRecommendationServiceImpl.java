package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.Position;
import com.teaching.employment.entity.PositionRecommendation;
import com.teaching.employment.entity.Student;
import com.teaching.employment.entity.Teacher;
import com.teaching.employment.entity.User;
import com.teaching.employment.mapper.PositionRecommendationMapper;
import com.teaching.employment.service.PositionService;
import com.teaching.employment.service.PositionRecommendationService;
import com.teaching.employment.service.StudentService;
import com.teaching.employment.service.TeacherService;
import com.teaching.employment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 职位推荐Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-20
 */
@Service
@RequiredArgsConstructor
public class PositionRecommendationServiceImpl extends ServiceImpl<PositionRecommendationMapper, PositionRecommendation>
        implements PositionRecommendationService {

    private final PositionRecommendationMapper positionRecommendationMapper;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final PositionService positionService;
    private final UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createRecommendation(Long studentId, Long positionId, Long teacherId, String reason, String remark) {
        PositionRecommendation recommendation = new PositionRecommendation();
        recommendation.setStudentId(studentId);
        recommendation.setPositionId(positionId);
        recommendation.setTeacherId(teacherId);
        recommendation.setReason(reason);
        recommendation.setRemark(remark);
        recommendation.setStatus("pending");

        return save(recommendation);
    }

    @Override
    public IPage<PositionRecommendation> getRecommendationsByStudent(Integer current, Integer size, Long studentId) {
        Page<PositionRecommendation> page = new Page<>(current, size);
        LambdaQueryWrapper<PositionRecommendation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PositionRecommendation::getStudentId, studentId);
        wrapper.orderByDesc(PositionRecommendation::getCreateTime);

        IPage<PositionRecommendation> result = positionRecommendationMapper.selectPage(page, wrapper);
        fillRelatedData(result.getRecords());

        return result;
    }

    @Override
    public IPage<PositionRecommendation> getRecommendationsByTeacher(Integer current, Integer size, Long teacherId) {
        Page<PositionRecommendation> page = new Page<>(current, size);
        LambdaQueryWrapper<PositionRecommendation> wrapper = new LambdaQueryWrapper<>();

        // 如果teacherId为null或0，则查询所有记录（学院负责人场景）
        if (teacherId != null && teacherId != 0) {
            wrapper.eq(PositionRecommendation::getTeacherId, teacherId);
        }

        wrapper.orderByDesc(PositionRecommendation::getCreateTime);

        IPage<PositionRecommendation> result = positionRecommendationMapper.selectPage(page, wrapper);
        fillRelatedData(result.getRecords());

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsViewed(Long id) {
        PositionRecommendation recommendation = getById(id);
        if (recommendation == null) {
            return false;
        }

        recommendation.setStatus("viewed");
        recommendation.setViewTime(LocalDateTime.now());

        return updateById(recommendation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, String status) {
        PositionRecommendation recommendation = getById(id);
        if (recommendation == null) {
            return false;
        }

        recommendation.setStatus(status);
        if ("viewed".equals(status) || "applied".equals(status)) {
            recommendation.setViewTime(LocalDateTime.now());
        }

        return updateById(recommendation);
    }

    /**
     * 填充关联数据
     */
    private void fillRelatedData(List<PositionRecommendation> recommendations) {
        if (recommendations == null || recommendations.isEmpty()) {
            return;
        }

        // 收集所有需要查询的ID
        List<Long> studentIds = recommendations.stream()
                .map(PositionRecommendation::getStudentId)
                .distinct()
                .collect(Collectors.toList());

        List<Long> teacherIds = recommendations.stream()
                .map(PositionRecommendation::getTeacherId)
                .distinct()
                .collect(Collectors.toList());

        List<Long> positionIds = recommendations.stream()
                .map(PositionRecommendation::getPositionId)
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

        // 批量查询职位
        final Map<Long, Position> positionMap;
        if (!positionIds.isEmpty()) {
            List<Position> positions = positionService.listByIds(positionIds);
            positionMap = positions.stream().collect(Collectors.toMap(Position::getId, p -> p));
        } else {
            positionMap = Map.of();
        }

        // 填充数据
        recommendations.forEach(rec -> {
            Student student = studentMap.get(rec.getStudentId());
            if (student != null) {
                rec.setStudentName(student.getName());
            }

            Teacher teacher = teacherMap.get(rec.getTeacherId());
            if (teacher != null) {
                rec.setTeacherName(teacher.getName());
            }

            Position position = positionMap.get(rec.getPositionId());
            if (position != null) {
                rec.setPositionName(position.getPositionName());
                rec.setCompanyName(position.getCompanyName());
            }
        });
    }
}
