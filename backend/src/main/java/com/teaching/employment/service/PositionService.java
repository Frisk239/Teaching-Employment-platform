package com.teaching.employment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.Position;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 招聘职位Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public interface PositionService extends IService<Position> {

    /**
     * 分页查询职位列表
     *
     * @param current    当前页
     * @param size       每页大小
     * @param companyId  企业ID
     * @param positionType 职位类型
     * @param city       工作城市
     * @param education  学历要求
     * @param status     职位状态
     * @param keyword    关键词（职位名称）
     * @param salaryMin  最低薪资
     * @param salaryMax  最高薪资
     * @return 职位分页数据
     */
    IPage<Position> getPositionPage(Integer current, Integer size, Long companyId, String positionType,
                                     String city, String education, String status, String keyword,
                                     BigDecimal salaryMin, BigDecimal salaryMax);

    /**
     * 根据企业ID查询职位列表
     *
     * @param current   当前页
     * @param size      每页大小
     * @param companyId 企业ID
     * @return 职位分页数据
     */
    IPage<Position> getPositionsByCompanyId(Integer current, Integer size, Long companyId);

    /**
     * 发布职位
     *
     * @param positionId 职位ID
     * @return 是否成功
     */
    boolean publishPosition(Long positionId);

    /**
     * 暂停职位招聘
     *
     * @param positionId 职位ID
     * @return 是否成功
     */
    boolean pausePosition(Long positionId);

    /**
     * 关闭职位
     *
     * @param positionId 职位ID
     * @return 是否成功
     */
    boolean closePosition(Long positionId);

    /**
     * 增加投递数量
     *
     * @param positionId 职位ID
     * @return 是否成功
     */
    boolean incrementApplicationCount(Long positionId);

    /**
     * 减少投递数量
     *
     * @param positionId 职位ID
     * @return 是否成功
     */
    boolean decrementApplicationCount(Long positionId);

    /**
     * 增加已招人数
     *
     * @param positionId 职位ID
     * @return 是否成功
     */
    boolean incrementHiredCount(Long positionId);

    /**
     * 减少已招人数
     *
     * @param positionId 职位ID
     * @return 是否成功
     */
    boolean decrementHiredCount(Long positionId);

    /**
     * 获取职位统计信息
     *
     * @param positionId 职位ID
     * @return 统计信息（投递数、已招数等）
     */
    Map<String, Object> getPositionStats(Long positionId);
}
