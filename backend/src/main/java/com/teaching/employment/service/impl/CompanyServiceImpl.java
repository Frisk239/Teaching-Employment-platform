package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.Company;
import com.teaching.employment.exception.BusinessException;
import com.teaching.employment.mapper.CompanyMapper;
import com.teaching.employment.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 企业Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Service
@RequiredArgsConstructor
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {

    private final CompanyMapper companyMapper;

    @Override
    public IPage<Company> getCompanyPage(Integer current, Integer size, String verifyStatus, Object status,
                                          String keyword) {
        Page<Company> page = new Page<>(current, size);
        LambdaQueryWrapper<Company> wrapper = new LambdaQueryWrapper<>();

        if (StrUtil.isNotBlank(verifyStatus)) {
            wrapper.eq(Company::getVerifyStatus, verifyStatus);
        }
        if (status != null) {
            wrapper.eq(Company::getStatus, status);
        }
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Company::getCompanyName, keyword)
                    .or().like(Company::getShortName, keyword)
                    .or().like(Company::getIndustry, keyword));
        }

        wrapper.orderByDesc(Company::getCreateTime);

        return companyMapper.selectPage(page, wrapper);
    }

    @Override
    public List<Company> getCompanyList(Object status) {
        LambdaQueryWrapper<Company> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(Company::getStatus, status);
        }

        wrapper.orderByAsc(Company::getCompanyName);

        return companyMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean verifyCompany(Long companyId, String verifyStatus, String rejectReason) {
        Company company = companyMapper.selectById(companyId);
        if (company == null) {
            throw new BusinessException("企业不存在");
        }

        if (!"pending".equals(company.getVerifyStatus())) {
            throw new BusinessException("该企业已审核，无法重复审核");
        }

        if (!"approved".equals(verifyStatus) && !"rejected".equals(verifyStatus)) {
            throw new BusinessException("审核状态无效");
        }

        company.setVerifyStatus(verifyStatus);
        company.setVerifyTime(LocalDateTime.now());

        if ("rejected".equals(verifyStatus)) {
            if (StrUtil.isBlank(rejectReason)) {
                throw new BusinessException("拒绝审核必须填写拒绝原因");
            }
            company.setRejectReason(rejectReason);
            company.setStatus(0);
        } else {
            company.setStatus(1);
        }

        return companyMapper.updateById(company) > 0;
    }
}
