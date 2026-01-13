package com.teaching.employment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.Interview;

import java.time.LocalDateTime;

/**
 * 面试Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public interface InterviewService extends IService<Interview> {

    /**
     * 分页查询面试列表
     *
     * @param current       当前页
     * @param size          每页大小
     * @param applicationId 申请ID
     * @param positionId    职位ID
     * @param studentId     学生ID
     * @param status        面试状态
     * @return 面试分页数据
     */
    IPage<Interview> getInterviewPage(Integer current, Integer size, Long applicationId, Long positionId,
                                       Long studentId, String status);

    /**
     * 根据学生ID查询面试列表
     *
     * @param current   当前页
     * @param size      每页大小
     * @param studentId 学生ID
     * @return 面试分页数据
     */
    IPage<Interview> getInterviewsByStudentId(Integer current, Integer size, Long studentId);

    /**
     * 根据申请ID查询面试列表
     *
     * @param current       当前页
     * @param size          每页大小
     * @param applicationId 申请ID
     * @return 面试分页数据
     */
    IPage<Interview> getInterviewsByApplicationId(Integer current, Integer size, Long applicationId);

    /**
     * 安排面试
     *
     * @param applicationId  申请ID
     * @param round          面试轮次
     * @param interviewType  面试类型
     * @param interviewTime  面试时间
     * @param location       面试地点（线下面试）
     * @param meetingLink    会议链接（线上面试）
     * @param interviewer    面试官
     * @param interviewerContact 面试官联系方式
     * @return 是否成功
     */
    boolean scheduleInterview(Long applicationId, Integer round, String interviewType,
                              LocalDateTime interviewTime, String location, String meetingLink,
                              String interviewer, String interviewerContact);

    /**
     * 取消面试
     *
     * @param interviewId 面试ID
     * @return 是否成功
     */
    boolean cancelInterview(Long interviewId);

    /**
     * 提交面试结果
     *
     * @param interviewId 面试ID
     * @param result      面试结果
     * @param score       面试评分
     * @param comment     面试评价
     * @return 是否成功
     */
    boolean submitInterviewResult(Long interviewId, String result, Integer score, String comment);

    /**
     * 重新安排面试
     *
     * @param interviewId    面试ID
     * @param interviewTime  新的面试时间
     * @param location       新的面试地点（线下面试）
     * @param meetingLink    新的会议链接（线上面试）
     * @return 是否成功
     */
    boolean rescheduleInterview(Long interviewId, LocalDateTime interviewTime, String location, String meetingLink);

    /**
     * 获取学生即将到来的面试
     *
     * @param studentId 学生ID
     * @return 面试列表
     */
    java.util.List<Interview> getUpcomingInterviews(Long studentId);
}
