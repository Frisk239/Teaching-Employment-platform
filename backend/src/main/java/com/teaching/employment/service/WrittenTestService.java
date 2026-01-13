package com.teaching.employment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.WrittenTest;

/**
 * 笔试Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public interface WrittenTestService extends IService<WrittenTest> {

    /**
     * 分页查询笔试列表
     *
     * @param current       当前页
     * @param size          每页大小
     * @param applicationId 申请ID
     * @param positionId    职位ID
     * @param studentId     学生ID
     * @param status        考试状态
     * @return 笔试分页数据
     */
    IPage<WrittenTest> getWrittenTestPage(Integer current, Integer size, Long applicationId, Long positionId,
                                           Long studentId, String status);

    /**
     * 根据学生ID查询笔试列表
     *
     * @param current   当前页
     * @param size      每页大小
     * @param studentId 学生ID
     * @return 笔试分页数据
     */
    IPage<WrittenTest> getWrittenTestsByStudentId(Integer current, Integer size, Long studentId);

    /**
     * 根据申请ID获取笔试信息
     *
     * @param applicationId 申请ID
     * @return 笔试信息
     */
    WrittenTest getWrittenTestByApplicationId(Long applicationId);

    /**
     * 安排笔试
     *
     * @param applicationId 申请ID
     * @param testUrl       考试链接
     * @param duration      考试时长（分钟）
     * @param startTime     开始时间
     * @param endTime       结束时间
     * @return 是否成功
     */
    boolean scheduleTest(Long applicationId, String testUrl, Integer duration,
                         java.time.LocalDateTime startTime, java.time.LocalDateTime endTime);

    /**
     * 学生开始考试
     *
     * @param testId 笔试ID
     * @return 是否成功
     */
    boolean startTest(Long testId);

    /**
     * 提交笔试成绩
     *
     * @param testId    笔试ID
     * @param score     得分
     * @param totalScore 总分
     * @param comment   评语
     * @return 是否成功
     */
    boolean submitScore(Long testId, Integer score, Integer totalScore, String comment);

    /**
     * 标记为缺席
     *
     * @param testId 笔试ID
     * @return 是否成功
     */
    boolean markAsMissed(Long testId);
}
