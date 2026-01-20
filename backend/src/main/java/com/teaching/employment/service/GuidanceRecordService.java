package com.teaching.employment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.GuidanceRecord;

import java.util.List;

/**
 * 指导记录Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-20
 */
public interface GuidanceRecordService extends IService<GuidanceRecord> {

    /**
     * 创建指导记录
     *
     * @param studentId     学员ID
     * @param teacherId     教师ID
     * @param guidanceType  指导类型
     * @param content       指导内容
     * @param nextAction    后续跟进计划
     * @param guidanceDate  指导时间
     * @param location      指导地点
     * @return 是否成功
     */
    boolean createGuidance(Long studentId, Long teacherId, String guidanceType, String content,
                          String nextAction, java.time.LocalDateTime guidanceDate, String location);

    /**
     * 获取学员的指导记录列表
     *
     * @param current   当前页
     * @param size      每页大小
     * @param studentId 学员ID
     * @return 指导记录分页数据
     */
    IPage<GuidanceRecord> getGuidancesByStudent(Integer current, Integer size, Long studentId);

    /**
     * 获取教师创建的指导记录列表
     *
     * @param current   当前页
     * @param size      每页大小
     * @param teacherId 教师ID
     * @return 指导记录分页数据
     */
    IPage<GuidanceRecord> getGuidancesByTeacher(Integer current, Integer size, Long teacherId);

    /**
     * 获取学员的指导记录列表（不分页）
     *
     * @param studentId 学员ID
     * @return 指导记录列表
     */
    List<GuidanceRecord> getGuidanceListByStudent(Long studentId);

    /**
     * 获取指导记录详情（包含关联信息）
     *
     * @param id 记录ID
     * @return 指导记录详情
     */
    GuidanceRecord getGuidanceWithDetails(Long id);
}
