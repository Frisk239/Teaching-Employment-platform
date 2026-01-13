package com.teaching.employment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.Resume;

/**
 * 简历Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public interface ResumeService extends IService<Resume> {

    /**
     * 分页查询简历列表
     *
     * @param current     当前页
     * @param size        每页大小
     * @param studentId   学生ID
     * @param status      简历状态
     * @param keyword     关键词（姓名、求职意向）
     * @param city        期望城市
     * @return 简历分页数据
     */
    IPage<Resume> getResumePage(Integer current, Integer size, Long studentId, String status, String keyword, String city);

    /**
     * 根据学生ID查询简历
     *
     * @param studentId 学生ID
     * @return 简历信息
     */
    Resume getResumeByStudentId(Long studentId);

    /**
     * 公开简历
     *
     * @param resumeId 简历ID
     * @return 是否成功
     */
    boolean publishResume(Long resumeId);

    /**
     * 隐藏简历
     *
     * @param resumeId 简历ID
     * @return 是否成功
     */
    boolean hideResume(Long resumeId);

    /**
     * 计算简历完整度
     *
     * @param resumeId 简历ID
     * @return 完整度百分比
     */
    Integer calculateCompleteness(Long resumeId);

    /**
     * 更新简历完整度
     *
     * @param resumeId 简历ID
     * @return 是否成功
     */
    boolean updateCompleteness(Long resumeId);
}
