package com.teaching.employment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.Education;

import java.util.List;

/**
 * 教育经历Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-18
 */
public interface EducationService extends IService<Education> {

    /**
     * 根据用户ID查询教育经历列表
     *
     * @param userId 用户ID
     * @return 教育经历列表
     */
    List<Education> getEducationByUserId(Long userId);

    /**
     * 根据ID查询教育经历详情
     *
     * @param id 教育经历ID
     * @return 教育经历详情
     */
    Education getEducationById(Long id);

    /**
     * 创建教育经历
     *
     * @param education 教育经历信息
     * @return 是否创建成功
     */
    boolean createEducation(Education education);

    /**
     * 更新教育经历
     *
     * @param education 教育经历信息
     * @return 是否更新成功
     */
    boolean updateEducation(Education education);

    /**
     * 删除教育经历
     *
     * @param id 教育经历ID
     * @return 是否删除成功
     */
    boolean deleteEducation(Long id);

    /**
     * 验证教育经历是否属于指定用户
     *
     * @param id     教育经历ID
     * @param userId 用户ID
     * @return 是否属于该用户
     */
    boolean validateEducationOwnership(Long id, Long userId);
}
