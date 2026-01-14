package com.teaching.employment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.JobApplication;

/**
 * 求职申请Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public interface JobApplicationService extends IService<JobApplication> {

    /**
     * 分页查询求职申请列表
     *
     * @param current     当前页
     * @param size        每页大小
     * @param studentName 学生姓名(精确搜索)
     * @param positionId  职位ID
     * @param studentId   学生ID
     * @param companyId   企业ID
     * @param status      申请状态
     * @param currentStage 当前阶段
     * @return 申请分页数据
     */
    IPage<JobApplication> getJobApplicationPage(Integer current, Integer size, String studentName, Long positionId, Long studentId,
                                                  Long companyId, String status, String currentStage);

    /**
     * 根据学生ID查询申请列表
     *
     * @param current   当前页
     * @param size      每页大小
     * @param studentId 学生ID
     * @return 申请分页数据
     */
    IPage<JobApplication> getApplicationsByStudentId(Integer current, Integer size, Long studentId);

    /**
     * 根据职位ID查询申请列表
     *
     * @param current    当前页
     * @param size       每页大小
     * @param positionId 职位ID
     * @return 申请分页数据
     */
    IPage<JobApplication> getApplicationsByPositionId(Integer current, Integer size, Long positionId);

    /**
     * 根据企业ID查询申请列表
     *
     * @param current   当前页
     * @param size      每页大小
     * @param companyId 企业ID
     * @return 申请分页数据
     */
    IPage<JobApplication> getApplicationsByCompanyId(Integer current, Integer size, Long companyId);

    /**
     * 检查学生是否已申请该职位
     *
     * @param positionId 职位ID
     * @param studentId  学生ID
     * @return 是否已申请
     */
    boolean hasApplied(Long positionId, Long studentId);

    /**
     * 投递简历
     *
     * @param positionId 职位ID
     * @param studentId  学生ID
     * @param resumeId   简历ID
     * @return 是否成功
     */
    boolean submitApplication(Long positionId, Long studentId, Long resumeId);

    /**
     * 筛选简历（通过/不通过）
     *
     * @param applicationId 申请ID
     * @param passed        是否通过
     * @param remark        备注信息
     * @return 是否成功
     */
    boolean screenResume(Long applicationId, boolean passed, String remark);

    /**
     * 更新申请状态
     *
     * @param applicationId 申请ID
     * @param status        新状态
     * @param currentStage  当前阶段
     * @return 是否成功
     */
    boolean updateApplicationStatus(Long applicationId, String status, String currentStage);

    /**
     * 撤销申请
     *
     * @param applicationId 申请ID
     * @param studentId     学生ID
     * @return 是否成功
     */
    boolean withdrawApplication(Long applicationId, Long studentId);
}
