package com.teaching.employment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.School;

/**
 * 学校Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public interface SchoolService extends IService<School> {

    /**
     * 分页查询学校（支持动态条件）
     *
     * @param current     当前页
     * @param size        每页大小
     * @param schoolName  学校名称（可选）
     * @param province    省份（可选）
     * @param city        城市（可选）
     * @return 学校分页列表
     */
    IPage<School> getSchoolPage(Integer current, Integer size, String schoolName, String province, String city);
}
