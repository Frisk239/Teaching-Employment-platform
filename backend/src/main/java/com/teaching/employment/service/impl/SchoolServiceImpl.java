package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.School;
import com.teaching.employment.mapper.SchoolMapper;
import com.teaching.employment.service.SchoolService;
import org.springframework.stereotype.Service;

/**
 * 学校Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Service
public class SchoolServiceImpl extends ServiceImpl<SchoolMapper, School> implements SchoolService {

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
    public IPage<School> getSchoolPage(Integer current, Integer size, String schoolName, String province, String city) {
        Page<School> page = new Page<>(current, size);
        LambdaQueryWrapper<School> wrapper = new LambdaQueryWrapper<>();

        // 动态条件查询
        if (StrUtil.isNotBlank(schoolName)) {
            wrapper.like(School::getSchoolName, schoolName);
        }
        if (StrUtil.isNotBlank(province)) {
            wrapper.like(School::getProvince, province);
        }
        if (StrUtil.isNotBlank(city)) {
            wrapper.like(School::getCity, city);
        }

        // 按创建时间倒序排列
        wrapper.orderByDesc(School::getCreateTime);

        return this.page(page, wrapper);
    }
}
