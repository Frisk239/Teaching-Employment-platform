package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.Company;
import com.teaching.employment.entity.Position;
import com.teaching.employment.exception.BusinessException;
import com.teaching.employment.mapper.CompanyMapper;
import com.teaching.employment.mapper.PositionMapper;
import com.teaching.employment.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 招聘职位Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Service
@RequiredArgsConstructor
public class PositionServiceImpl extends ServiceImpl<PositionMapper, Position> implements PositionService {

    private final PositionMapper positionMapper;
    private final CompanyMapper companyMapper;

    @Override
    public IPage<Position> getPositionPage(Integer current, Integer size, Long companyId, String positionType,
                                            String city, String education, String status, String keyword,
                                            BigDecimal salaryMin, BigDecimal salaryMax) {
        Page<Position> page = new Page<>(current, size);
        LambdaQueryWrapper<Position> wrapper = new LambdaQueryWrapper<>();

        if (companyId != null) {
            wrapper.eq(Position::getCompanyId, companyId);
        }
        if (StrUtil.isNotBlank(positionType)) {
            wrapper.eq(Position::getPositionType, positionType);
        }
        if (StrUtil.isNotBlank(city)) {
            wrapper.eq(Position::getCity, city);
        }
        if (StrUtil.isNotBlank(education)) {
            wrapper.eq(Position::getEducation, education);
        }
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(Position::getStatus, status);
        }
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(Position::getPositionName, keyword);
        }
        if (salaryMin != null) {
            wrapper.ge(Position::getSalaryMin, salaryMin);
        }
        if (salaryMax != null) {
            wrapper.le(Position::getSalaryMax, salaryMax);
        }

        wrapper.orderByDesc(Position::getPublishTime);

        IPage<Position> resultPage = positionMapper.selectPage(page, wrapper);

        // 填充企业名称
        fillCompanyNames(resultPage.getRecords());

        return resultPage;
    }

    /**
     * 填充职位列表中的企业名称
     */
    private void fillCompanyNames(List<Position> positions) {
        if (positions == null || positions.isEmpty()) {
            return;
        }

        // 获取所有企业ID
        List<Long> companyIds = positions.stream()
                .map(Position::getCompanyId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        if (companyIds.isEmpty()) {
            return;
        }

        // 批量查询企业信息
        List<Company> companies = companyMapper.selectBatchIds(companyIds);
        Map<Long, String> companyMap = companies.stream()
                .collect(Collectors.toMap(Company::getId, Company::getCompanyName));

        // 填充企业名称
        positions.forEach(position -> {
            if (position.getCompanyId() != null) {
                position.setCompanyName(companyMap.get(position.getCompanyId()));
            }
        });
    }

    @Override
    public IPage<Position> getPositionsByCompanyId(Integer current, Integer size, Long companyId) {
        return getPositionPage(current, size, companyId, null, null, null, null, null, null, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishPosition(Long positionId) {
        Position position = positionMapper.selectById(positionId);
        if (position == null) {
            throw new BusinessException("职位不存在");
        }

        if (!"draft".equals(position.getStatus()) && !"paused".equals(position.getStatus())) {
            throw new BusinessException("只有草稿或暂停状态的职位才能发布");
        }

        position.setStatus("active");
        position.setPublishTime(LocalDateTime.now());

        return positionMapper.updateById(position) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean pausePosition(Long positionId) {
        Position position = positionMapper.selectById(positionId);
        if (position == null) {
            throw new BusinessException("职位不存在");
        }

        if (!"active".equals(position.getStatus())) {
            throw new BusinessException("只有招聘中的职位才能暂停");
        }

        position.setStatus("paused");

        return positionMapper.updateById(position) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean closePosition(Long positionId) {
        Position position = positionMapper.selectById(positionId);
        if (position == null) {
            throw new BusinessException("职位不存在");
        }

        position.setStatus("closed");

        return positionMapper.updateById(position) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean incrementApplicationCount(Long positionId) {
        Position position = positionMapper.selectById(positionId);
        if (position == null) {
            throw new BusinessException("职位不存在");
        }

        int currentCount = position.getApplicationCount() != null ? position.getApplicationCount() : 0;
        position.setApplicationCount(currentCount + 1);

        return positionMapper.updateById(position) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean decrementApplicationCount(Long positionId) {
        Position position = positionMapper.selectById(positionId);
        if (position == null) {
            throw new BusinessException("职位不存在");
        }

        int currentCount = position.getApplicationCount() != null ? position.getApplicationCount() : 0;
        if (currentCount > 0) {
            position.setApplicationCount(currentCount - 1);
            return positionMapper.updateById(position) > 0;
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean incrementHiredCount(Long positionId) {
        Position position = positionMapper.selectById(positionId);
        if (position == null) {
            throw new BusinessException("职位不存在");
        }

        int currentCount = position.getHiredCount() != null ? position.getHiredCount() : 0;
        position.setHiredCount(currentCount + 1);

        return positionMapper.updateById(position) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean decrementHiredCount(Long positionId) {
        Position position = positionMapper.selectById(positionId);
        if (position == null) {
            throw new BusinessException("职位不存在");
        }

        int currentCount = position.getHiredCount() != null ? position.getHiredCount() : 0;
        if (currentCount > 0) {
            position.setHiredCount(currentCount - 1);
            return positionMapper.updateById(position) > 0;
        }

        return true;
    }

    @Override
    public Map<String, Object> getPositionStats(Long positionId) {
        Position position = positionMapper.selectById(positionId);
        if (position == null) {
            throw new BusinessException("职位不存在");
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("applicationCount", position.getApplicationCount() != null ? position.getApplicationCount() : 0);
        stats.put("hiredCount", position.getHiredCount() != null ? position.getHiredCount() : 0);
        stats.put("recruitCount", position.getRecruitCount() != null ? position.getRecruitCount() : 0);
        stats.put("pendingCount", (position.getRecruitCount() != null ? position.getRecruitCount() : 0) -
                (position.getHiredCount() != null ? position.getHiredCount() : 0));

        return stats;
    }
}
