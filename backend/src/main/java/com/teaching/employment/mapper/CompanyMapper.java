package com.teaching.employment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.employment.entity.Company;
import org.apache.ibatis.annotations.Mapper;

/**
 * 企业Mapper接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Mapper
public interface CompanyMapper extends BaseMapper<Company> {
}
