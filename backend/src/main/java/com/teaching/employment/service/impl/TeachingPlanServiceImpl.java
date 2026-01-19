package com.teaching.employment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.dto.TeachingPlanVO;
import com.teaching.employment.entity.TeachingPlan;
import com.teaching.employment.entity.Timetable;
import com.teaching.employment.mapper.TeachingPlanMapper;
import com.teaching.employment.mapper.TimetableMapper;
import com.teaching.employment.service.TeachingPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 教学计划Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-19
 */
@Service
@RequiredArgsConstructor
public class TeachingPlanServiceImpl extends ServiceImpl<TeachingPlanMapper, TeachingPlan>
        implements TeachingPlanService {

    private final TimetableMapper timetableMapper;

    @Override
    public List<TeachingPlan> getListByCourseId(Long courseId) {
        return lambdaQuery()
                .eq(TeachingPlan::getCourseId, courseId)
                .orderByAsc(TeachingPlan::getLessonNumber)
                .list();
    }

    @Override
    public List<TeachingPlanVO> getListWithTime(Long courseId) {
        // 1. 查询教学计划
        List<TeachingPlan> plans = getListByCourseId(courseId);

        // 2. 查询该课程的课程表
        LambdaQueryWrapper<Timetable> timetableQuery = new LambdaQueryWrapper<>();
        timetableQuery.eq(Timetable::getCourseId, courseId);
        List<Timetable> timetables = timetableMapper.selectList(timetableQuery);

        // 3. 根据weekNumber关联
        return plans.stream().map(plan -> {
            TeachingPlanVO vo = new TeachingPlanVO();
            vo.setId(plan.getId());
            vo.setCourseId(plan.getCourseId());
            vo.setLessonNumber(plan.getLessonNumber());
            vo.setWeekNumber(plan.getWeekNumber());
            vo.setTitle(plan.getTitle());
            vo.setContent(plan.getContent());

            // 查找对应周次的课程表信息
            Timetable timetable = timetables.stream()
                    .filter(tt -> tt.getWeekNumber().equals(plan.getWeekNumber()))
                    .findFirst()
                    .orElse(null);

            vo.setTimetable(timetable);

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<TeachingPlan> batchSave(List<TeachingPlan> plans) {
        plans.forEach(this::saveOrUpdate);
        return plans;
    }
}
