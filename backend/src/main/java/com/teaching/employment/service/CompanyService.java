package com.teaching.employment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.Company;

import java.util.List;

/**
 * 企业Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public interface CompanyService extends IService<Company> {

    /**
     * 分页查询企业列表
     */
    IPage<Company> getCompanyPage(Integer current, Integer size, String verifyStatus, Object status, String keyword);

    /**
     * 查询所有企业（用于下拉框）
     */
    List<Company> getCompanyList(Object status);

    /**
     * 企业认证审核
     */
    boolean verifyCompany(Long companyId, String verifyStatus, String rejectReason);
}
