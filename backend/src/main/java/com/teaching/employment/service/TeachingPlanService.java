package com.teaching.employment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.dto.TeachingPlanVO;
import com.teaching.employment.entity.TeachingPlan;

import java.util.List;

/**
 * 教学计划Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-19
 */
public interface TeachingPlanService extends IService<TeachingPlan> {

    /**
     * 根据课程ID获取教学计划列表（按课次排序）
     *
     * @param courseId 课程ID
     * @return 教学计划列表
     */
    List<TeachingPlan> getListByCourseId(Long courseId);

    /**
     * 根据课程ID获取教学计划（包含时间信息）
     *
     * @param courseId 课程ID
     * @return 教学计划VO列表
     */
    List<TeachingPlanVO> getListWithTime(Long courseId);

    /**
     * 批量保存教学计划
     *
     * @param plans 教学计划列表
     * @return 保存成功的计划列表
     */
    List<TeachingPlan> batchSave(List<TeachingPlan> plans);
}
