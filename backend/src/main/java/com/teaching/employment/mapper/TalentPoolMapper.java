package com.teaching.employment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teaching.employment.entity.TalentPool;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 人才库Mapper接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-17
 */
@Mapper
public interface TalentPoolMapper extends BaseMapper<TalentPool> {

    /**
     * 分页查询人才库列表（关联查询学员和企业信息）
     *
     * @param page       分页对象
     * @param companyId  企业ID
     * @param category   人才分类
     * @param status     状态
     * @param priority   优先级
     * @param keyword    关键词（搜索姓名、标签、职位等）
     * @return 人才库分页数据
     */
    IPage<TalentPool> selectTalentPoolPage(
            Page<TalentPool> page,
            @Param("companyId") Long companyId,
            @Param("category") String category,
            @Param("status") String status,
            @Param("priority") String priority,
            @Param("keyword") String keyword
    );
}
