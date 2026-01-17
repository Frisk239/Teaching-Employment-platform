package com.teaching.employment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.TalentPool;

/**
 * 人才库Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-17
 */
public interface TalentPoolService extends IService<TalentPool> {

    /**
     * 分页查询人才库列表
     *
     * @param current   当前页
     * @param size      每页大小
     * @param companyId 企业ID
     * @param category  人才分类
     * @param status    状态
     * @param priority  优先级
     * @param keyword   关键词
     * @return 人才库分页数据
     */
    IPage<TalentPool> getTalentPoolPage(Integer current, Integer size,
                                        Long companyId, String category,
                                        String status, String priority,
                                        String keyword);

    /**
     * 添加候选人到人才库
     *
     * @param talentPool 人才库信息
     * @return 是否成功
     */
    boolean addToTalentPool(TalentPool talentPool);

    /**
     * 从求职申请添加到人才库
     *
     * @param applicationId 申请ID
     * @param companyId     企业ID
     * @param remark        备注
     * @return 是否成功
     */
    boolean addFromApplication(Long applicationId, Long companyId, String remark);

    /**
     * 更新人才库信息
     *
     * @param talentPool 人才库信息
     * @return 是否成功
     */
    boolean updateTalentPool(TalentPool talentPool);

    /**
     * 删除人才库记录
     *
     * @param id 人才库ID
     * @return 是否成功
     */
    boolean deleteFromTalentPool(Long id);

    /**
     * 批量删除人才库记录
     *
     * @param ids 人才库ID数组
     * @return 是否成功
     */
    boolean batchDelete(Long[] ids);

    /**
     * 更新人才标签
     *
     * @param id   人才库ID
     * @param tags 标签（逗号分隔）
     * @return 是否成功
     */
    boolean updateTags(Long id, String tags);

    /**
     * 更新评分
     *
     * @param id     人才库ID
     * @param rating 评分（1-5）
     * @return 是否成功
     */
    boolean updateRating(Long id, Integer rating);

    /**
     * 更新优先级
     *
     * @param id       人才库ID
     * @param priority 优先级
     * @return 是否成功
     */
    boolean updatePriority(Long id, String priority);

    /**
     * 标记为已联系
     *
     * @param id              人才库ID
     * @param contactMethod   联系方式
     * @param nextContactDate 下次联系日期
     * @return 是否成功
     */
    boolean markAsContacted(Long id, String contactMethod, String nextContactDate);

    /**
     * 检查候选人是否已在人才库中
     *
     * @param companyId 企业ID
     * @param studentId 学员ID
     * @return 是否已存在
     */
    boolean existsInTalentPool(Long companyId, Long studentId);

    /**
     * 获取人才库统计信息
     *
     * @param companyId 企业ID
     * @return 统计信息
     */
    java.util.Map<String, Object> getTalentPoolStats(Long companyId);
}
