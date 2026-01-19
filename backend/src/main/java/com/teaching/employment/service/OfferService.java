package com.teaching.employment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.Offer;

import java.time.LocalDate;
import java.util.Map;

/**
 * OfferService接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public interface OfferService extends IService<Offer> {

    /**
     * 分页查询Offer列表
     *
     * @param current       当前页
     * @param size          每页大小
     * @param applicationId 申请ID
     * @param positionId    职位ID
     * @param studentId     学生ID
     * @param companyId     企业ID
     * @param status        Offer状态
     * @return Offer分页数据
     */
    IPage<Offer> getOfferPage(Integer current, Integer size, Long applicationId, Long positionId,
                              Long studentId, Long companyId, String status);

    /**
     * 根据学生ID查询Offer列表
     *
     * @param current   当前页
     * @param size      每页大小
     * @param studentId 学生ID
     * @return Offer分页数据
     */
    IPage<Offer> getOffersByStudentId(Integer current, Integer size, Long studentId);

    /**
     * 根据企业ID查询Offer列表
     *
     * @param current   当前页
     * @param size      每页大小
     * @param companyId 企业ID
     * @return Offer分页数据
     */
    IPage<Offer> getOffersByCompanyId(Integer current, Integer size, Long companyId);

    /**
     * 根据申请ID查询Offer
     *
     * @param applicationId 申请ID
     * @return Offer信息
     */
    Offer getOfferByApplicationId(Long applicationId);

    /**
     * 根据ID查询Offer（包含关联信息）
     *
     * @param id Offer ID
     * @return Offer信息（包含企业名称等关联字段）
     */
    Offer getOfferWithDetails(Long id);

    /**
     * 创建并发送Offer
     *
     * @param applicationId    申请ID
     * @param positionName     职位名称
     * @param city             入职城市
     * @param salary           薪资
     * @param salaryUnit       薪资单位
     * @param onboardDate      入职日期
     * @param acceptDeadline   接受截止日期
     * @param attachmentUrl    附件URL
     * @param remark           备注
     * @return 是否成功
     */
    boolean createAndSendOffer(Long applicationId, String positionName, String city,
                               java.math.BigDecimal salary, String salaryUnit,
                               LocalDate onboardDate, LocalDate acceptDeadline,
                               String attachmentUrl, String remark);

    /**
     * 学生接受Offer
     *
     * @param offerId Offer ID
     * @return 是否成功
     */
    boolean acceptOffer(Long offerId);

    /**
     * 学生拒绝Offer
     *
     * @param offerId Offer ID
     * @return 是否成功
     */
    boolean rejectOffer(Long offerId);

    /**
     * 企业取消Offer
     *
     * @param offerId Offer ID
     * @return 是否成功
     */
    boolean cancelOffer(Long offerId);

    /**
     * 标记Offer为已过期
     *
     * @param offerId Offer ID
     * @return 是否成功
     */
    boolean markAsExpired(Long offerId);

    /**
     * 更新邮件发送状态
     *
     * @param offerId    Offer ID
     * @param emailStatus 邮件状态
     * @return 是否成功
     */
    boolean updateEmailStatus(Long offerId, String emailStatus);

    /**
     * 获取Offer统计信息
     *
     * @param companyId 企业ID
     * @return 统计信息
     */
    Map<String, Object> getOfferStats(Long companyId);
}
