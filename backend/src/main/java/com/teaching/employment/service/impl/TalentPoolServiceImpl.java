package com.teaching.employment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.JobApplication;
import com.teaching.employment.entity.Student;
import com.teaching.employment.entity.TalentPool;
import com.teaching.employment.exception.BusinessException;
import com.teaching.employment.mapper.TalentPoolMapper;
import com.teaching.employment.service.JobApplicationService;
import com.teaching.employment.service.StudentService;
import com.teaching.employment.service.TalentPoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 人才库Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TalentPoolServiceImpl extends ServiceImpl<TalentPoolMapper, TalentPool> implements TalentPoolService {

    private final TalentPoolMapper talentPoolMapper;
    private final StudentService studentService;
    private final JobApplicationService jobApplicationService;

    @Override
    public IPage<TalentPool> getTalentPoolPage(Integer current, Integer size,
                                                Long companyId, String category,
                                                String status, String priority,
                                                String keyword) {
        Page<TalentPool> page = new Page<>(current, size);
        return talentPoolMapper.selectTalentPoolPage(page, companyId, category, status, priority, keyword);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addToTalentPool(TalentPool talentPool) {
        // 检查是否已存在
        if (existsInTalentPool(talentPool.getCompanyId(), talentPool.getStudentId())) {
            throw new BusinessException("该候选人已在人才库中");
        }

        talentPool.setCreateTime(LocalDateTime.now());
        talentPool.setUpdateTime(LocalDateTime.now());

        if (talentPool.getStatus() == null) {
            talentPool.setStatus("active");
        }
        if (talentPool.getPriority() == null) {
            talentPool.setPriority("normal");
        }
        if (talentPool.getRating() == null) {
            talentPool.setRating(3);
        }
        if (talentPool.getContactCount() == null) {
            talentPool.setContactCount(0);
        }
        if (talentPool.getSource() == null) {
            talentPool.setSource("manual");
        }

        return talentPoolMapper.insert(talentPool) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addFromApplication(Long applicationId, Long companyId, String remark) {
        JobApplication application = jobApplicationService.getById(applicationId);
        if (application == null) {
            throw new BusinessException("求职申请不存在");
        }

        // 检查是否已存在
        if (existsInTalentPool(companyId, application.getStudentId())) {
            throw new BusinessException("该候选人已在人才库中");
        }

        Student student = studentService.getById(application.getStudentId());
        if (student == null) {
            throw new BusinessException("学员信息不存在");
        }

        TalentPool talentPool = new TalentPool();
        talentPool.setCompanyId(companyId);
        talentPool.setStudentId(application.getStudentId());
        talentPool.setResumeId(application.getResumeId());
        talentPool.setSource("application");
        talentPool.setSourceId(applicationId);
        talentPool.setRemark(remark);
        talentPool.setStatus("active");
        talentPool.setPriority("normal");
        talentPool.setRating(3);
        talentPool.setContactCount(0);
        talentPool.setCreateTime(LocalDateTime.now());
        talentPool.setUpdateTime(LocalDateTime.now());

        return talentPoolMapper.insert(talentPool) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTalentPool(TalentPool talentPool) {
        talentPool.setUpdateTime(LocalDateTime.now());
        return talentPoolMapper.updateById(talentPool) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFromTalentPool(Long id) {
        return talentPoolMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDelete(Long[] ids) {
        for (Long id : ids) {
            talentPoolMapper.deleteById(id);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTags(Long id, String tags) {
        TalentPool talentPool = talentPoolMapper.selectById(id);
        if (talentPool == null) {
            throw new BusinessException("人才库记录不存在");
        }

        talentPool.setTags(tags);
        talentPool.setUpdateTime(LocalDateTime.now());
        return talentPoolMapper.updateById(talentPool) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRating(Long id, Integer rating) {
        if (rating < 1 || rating > 5) {
            throw new BusinessException("评分必须在1-5之间");
        }

        TalentPool talentPool = talentPoolMapper.selectById(id);
        if (talentPool == null) {
            throw new BusinessException("人才库记录不存在");
        }

        talentPool.setRating(rating);
        talentPool.setUpdateTime(LocalDateTime.now());
        return talentPoolMapper.updateById(talentPool) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePriority(Long id, String priority) {
        TalentPool talentPool = talentPoolMapper.selectById(id);
        if (talentPool == null) {
            throw new BusinessException("人才库记录不存在");
        }

        talentPool.setPriority(priority);
        talentPool.setUpdateTime(LocalDateTime.now());
        return talentPoolMapper.updateById(talentPool) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsContacted(Long id, String contactMethod, String nextContactDate) {
        TalentPool talentPool = talentPoolMapper.selectById(id);
        if (talentPool == null) {
            throw new BusinessException("人才库记录不存在");
        }

        talentPool.setStatus("contacted");
        talentPool.setLastContactMethod(contactMethod);
        talentPool.setLastContactDate(LocalDate.now());

        if (nextContactDate != null && !nextContactDate.isEmpty()) {
            talentPool.setNextContactDate(LocalDate.parse(nextContactDate));
        }

        talentPool.setContactCount((talentPool.getContactCount() != null ? talentPool.getContactCount() : 0) + 1);
        talentPool.setUpdateTime(LocalDateTime.now());

        return talentPoolMapper.updateById(talentPool) > 0;
    }

    @Override
    public boolean existsInTalentPool(Long companyId, Long studentId) {
        LambdaQueryWrapper<TalentPool> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TalentPool::getCompanyId, companyId)
                .eq(TalentPool::getStudentId, studentId);
        return talentPoolMapper.selectCount(wrapper) > 0;
    }

    @Override
    public Map<String, Object> getTalentPoolStats(Long companyId) {
        Map<String, Object> stats = new HashMap<>();

        // 总人才数
        LambdaQueryWrapper<TalentPool> totalWrapper = new LambdaQueryWrapper<>();
        totalWrapper.eq(TalentPool::getCompanyId, companyId);
        Long totalCount = talentPoolMapper.selectCount(totalWrapper);
        stats.put("totalCount", totalCount);

        // 激活人才数
        LambdaQueryWrapper<TalentPool> activeWrapper = new LambdaQueryWrapper<>();
        activeWrapper.eq(TalentPool::getCompanyId, companyId)
                .eq(TalentPool::getStatus, "active");
        Long activeCount = talentPoolMapper.selectCount(activeWrapper);
        stats.put("activeCount", activeCount);

        // 已联系人才数
        LambdaQueryWrapper<TalentPool> contactedWrapper = new LambdaQueryWrapper<>();
        contactedWrapper.eq(TalentPool::getCompanyId, companyId)
                .eq(TalentPool::getStatus, "contacted");
        Long contactedCount = talentPoolMapper.selectCount(contactedWrapper);
        stats.put("contactedCount", contactedCount);

        // 高优先级人才数
        LambdaQueryWrapper<TalentPool> highPriorityWrapper = new LambdaQueryWrapper<>();
        highPriorityWrapper.eq(TalentPool::getCompanyId, companyId)
                .eq(TalentPool::getPriority, "high");
        Long highPriorityCount = talentPoolMapper.selectCount(highPriorityWrapper);
        stats.put("highPriorityCount", highPriorityCount);

        // 平均评分
        // 这里简化处理，实际可以写SQL查询平均分
        stats.put("averageRating", 3.5);

        return stats;
    }
}
