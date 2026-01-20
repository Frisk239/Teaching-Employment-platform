package com.teaching.employment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.PositionRecommendation;

/**
 * 职位推荐Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-20
 */
public interface PositionRecommendationService extends IService<PositionRecommendation> {

    /**
     * 创建职位推荐
     *
     * @param studentId  学员ID
     * @param positionId 职位ID
     * @param teacherId  教师ID
     * @param reason     推荐理由
     * @param remark     备注
     * @return 是否成功
     */
    boolean createRecommendation(Long studentId, Long positionId, Long teacherId, String reason, String remark);

    /**
     * 获取学员的职位推荐列表
     *
     * @param current   当前页
     * @param size      每页大小
     * @param studentId 学员ID
     * @return 职位推荐分页数据
     */
    IPage<PositionRecommendation> getRecommendationsByStudent(Integer current, Integer size, Long studentId);

    /**
     * 获取教师创建的职位推荐列表
     *
     * @param current   当前页
     * @param size      每页大小
     * @param teacherId 教师ID
     * @return 职位推荐分页数据
     */
    IPage<PositionRecommendation> getRecommendationsByTeacher(Integer current, Integer size, Long teacherId);

    /**
     * 标记推荐为已查看
     *
     * @param id 推荐ID
     * @return 是否成功
     */
    boolean markAsViewed(Long id);

    /**
     * 更新推荐状态
     *
     * @param id     推荐ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Long id, String status);
}
