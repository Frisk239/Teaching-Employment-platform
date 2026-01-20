package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.JobApplication;
import com.teaching.employment.entity.Offer;
import com.teaching.employment.entity.Position;
import com.teaching.employment.entity.Student;
import com.teaching.employment.entity.Company;
import com.teaching.employment.entity.User;
import com.teaching.employment.exception.BusinessException;
import com.teaching.employment.mapper.OfferMapper;
import com.teaching.employment.service.CompanyService;
import com.teaching.employment.service.JobApplicationService;
import com.teaching.employment.service.OfferService;
import com.teaching.employment.service.PositionService;
import com.teaching.employment.service.StudentService;
import com.teaching.employment.service.UserService;
import com.teaching.employment.util.EmailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * OfferService实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OfferServiceImpl extends ServiceImpl<OfferMapper, Offer> implements OfferService {

    private final OfferMapper offerMapper;
    private final JobApplicationService jobApplicationService;
    private final PositionService positionService;
    private final StudentService studentService;
    private final CompanyService companyService;
    private final UserService userService;
    private final EmailUtil emailUtil;

    @Override
    public IPage<Offer> getOfferPage(Integer current, Integer size, Long applicationId, Long positionId,
                                     Long studentId, Long companyId, String status) {
        Page<Offer> page = new Page<>(current, size);
        LambdaQueryWrapper<Offer> wrapper = new LambdaQueryWrapper<>();

        if (applicationId != null) {
            wrapper.eq(Offer::getApplicationId, applicationId);
        }
        if (positionId != null) {
            wrapper.eq(Offer::getPositionId, positionId);
        }
        if (studentId != null) {
            wrapper.eq(Offer::getStudentId, studentId);
        }
        if (companyId != null) {
            wrapper.eq(Offer::getCompanyId, companyId);
        }
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(Offer::getStatus, status);
        }

        wrapper.orderByDesc(Offer::getCreateTime);

        IPage<Offer> resultPage = offerMapper.selectPage(page, wrapper);

        // 填充关联信息
        resultPage.getRecords().forEach(this::fillRelatedInfo);

        return resultPage;
    }

    /**
     * 填充Offer的关联信息
     */
    private void fillRelatedInfo(Offer offer) {
        // 填充企业信息
        if (offer.getCompanyId() != null) {
            Company company = companyService.getById(offer.getCompanyId());
            if (company != null) {
                offer.setCompanyName(company.getCompanyName());
                offer.setCompany(company);
            }
        }

        // 填充学生信息
        if (offer.getStudentId() != null) {
            Student student = studentService.getById(offer.getStudentId());
            if (student != null) {
                // 填充学生邮箱
                offer.setStudentEmail(student.getEmail());

                // 从User表填充学生真实姓名
                if (student.getUserId() != null) {
                    User user = userService.getById(student.getUserId());
                    if (user != null && user.getRealName() != null) {
                        offer.setStudentName(user.getRealName());
                    } else {
                        offer.setStudentName("学生" + student.getId());
                    }
                } else {
                    offer.setStudentName("学生" + student.getId());
                }
            }
        }
    }

    @Override
    public IPage<Offer> getOffersByStudentId(Integer current, Integer size, Long studentId) {
        return getOfferPage(current, size, null, null, studentId, null, null);
    }

    @Override
    public IPage<Offer> getOffersByCompanyId(Integer current, Integer size, Long companyId) {
        return getOfferPage(current, size, null, null, null, companyId, null);
    }

    @Override
    public Offer getOfferByApplicationId(Long applicationId) {
        LambdaQueryWrapper<Offer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Offer::getApplicationId, applicationId);

        return offerMapper.selectOne(wrapper);
    }

    @Override
    public Offer getOfferWithDetails(Long id) {
        Offer offer = offerMapper.selectById(id);
        if (offer != null) {
            fillRelatedInfo(offer);
        }
        return offer;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createAndSendOffer(Long applicationId, String positionName, String city,
                                      BigDecimal salary, String salaryUnit,
                                      LocalDate onboardDate, LocalDate acceptDeadline,
                                      String attachmentUrl, String remark) {
        // 检查申请是否存在且状态正确
        JobApplication application = jobApplicationService.getById(applicationId);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }
        if (!"interview_passed".equals(application.getStatus())) {
            throw new BusinessException("只有面试通过的申请才能发送Offer");
        }

        // 检查是否已发送Offer
        Offer existingOffer = getOfferByApplicationId(applicationId);
        if (existingOffer != null && !"cancelled".equals(existingOffer.getStatus())
                && !"rejected".equals(existingOffer.getStatus())) {
            throw new BusinessException("该申请已发送Offer");
        }

        // 获取职位和学生信息
        Position position = positionService.getById(application.getPositionId());
        Student student = studentService.getById(application.getStudentId());

        if (position == null || student == null) {
            throw new BusinessException("职位或学生信息不存在");
        }

        // 生成Offer编号
        String offerNo = "OFFER-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 创建Offer
        Offer offer = new Offer();
        offer.setApplicationId(applicationId);
        offer.setPositionId(application.getPositionId());
        offer.setStudentId(application.getStudentId());
        offer.setCompanyId(position.getCompanyId());
        offer.setOfferNo(offerNo);
        offer.setPositionName(positionName);
        offer.setCity(city);
        offer.setSalary(salary);
        offer.setSalaryUnit(salaryUnit);
        offer.setOnboardDate(onboardDate);
        offer.setAcceptDeadline(acceptDeadline);
        offer.setAttachmentUrl(attachmentUrl);
        offer.setRemark(remark);
        offer.setStatus("pending");
        offer.setEmailStatus("pending");

        boolean result = offerMapper.insert(offer) > 0;

        if (result) {
            // 更新申请状态
            jobApplicationService.updateApplicationStatus(applicationId, "offered", "offer");
            // 增加职位已招人数
            positionService.incrementHiredCount(application.getPositionId());

            // 发送Offer邮件
            try {
                Company company = companyService.getById(position.getCompanyId());
                String companyName = company != null ? company.getCompanyName() : "公司";

                // 从User表获取学生真实姓名
                String studentRealName = student.getEmail(); // 默认使用邮箱
                if (student.getUserId() != null) {
                    User user = userService.getById(student.getUserId());
                    if (user != null && user.getRealName() != null) {
                        studentRealName = user.getRealName();
                    } else {
                        studentRealName = "学生" + student.getId();
                    }
                }

                boolean emailSent = emailUtil.sendOfferEmail(
                        student.getEmail(),
                        studentRealName,
                        companyName,
                        positionName,
                        salary != null ? salary.doubleValue() : null,
                        onboardDate != null ? onboardDate.toString() : null
                );

                if (emailSent) {
                    updateEmailStatus(offer.getId(), "sent");
                    log.info("Offer邮件发送成功: studentId={}, offerId={}", student.getId(), offer.getId());
                } else {
                    updateEmailStatus(offer.getId(), "failed");
                    log.error("Offer邮件发送失败: studentId={}, offerId={}", student.getId(), offer.getId());
                }
            } catch (Exception e) {
                log.error("发送Offer邮件异常: studentId={}, offerId={}, error={}",
                        student.getId(), offer.getId(), e.getMessage(), e);
                updateEmailStatus(offer.getId(), "failed");
            }
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean acceptOffer(Long offerId) {
        Offer offer = offerMapper.selectById(offerId);
        if (offer == null) {
            throw new BusinessException("Offer不存在");
        }

        if (!"pending".equals(offer.getStatus()) && !"expired".equals(offer.getStatus())) {
            throw new BusinessException("只有待接受或已过期的Offer才能接受");
        }

        // 检查是否超过截止日期
        if (offer.getAcceptDeadline() != null && offer.getAcceptDeadline().isBefore(LocalDate.now())) {
            throw new BusinessException("Offer已超过接受截止日期");
        }

        offer.setStatus("accepted");
        offer.setAcceptTime(LocalDateTime.now());

        boolean result = offerMapper.updateById(offer) > 0;

        if (result) {
            // 更新申请状态为已入职（如果有关联的申请）
            if (offer.getApplicationId() != null) {
                jobApplicationService.updateApplicationStatus(offer.getApplicationId(), "hired", "hired");
            }
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean rejectOffer(Long offerId) {
        Offer offer = offerMapper.selectById(offerId);
        if (offer == null) {
            throw new BusinessException("Offer不存在");
        }

        if (!"pending".equals(offer.getStatus())) {
            throw new BusinessException("只有待接受的Offer才能拒绝");
        }

        offer.setStatus("rejected");

        boolean result = offerMapper.updateById(offer) > 0;

        if (result) {
            // 更新申请状态（如果有关联的申请）
            if (offer.getApplicationId() != null) {
                jobApplicationService.updateApplicationStatus(offer.getApplicationId(), "declined", null);
            }
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOffer(Long offerId) {
        Offer offer = offerMapper.selectById(offerId);
        if (offer == null) {
            throw new BusinessException("Offer不存在");
        }

        if ("accepted".equals(offer.getStatus())) {
            throw new BusinessException("已接受的Offer不能取消");
        }

        if ("cancelled".equals(offer.getStatus())) {
            throw new BusinessException("Offer已取消");
        }

        offer.setStatus("cancelled");

        boolean result = offerMapper.updateById(offer) > 0;

        if (result) {
            // 更新申请状态（如果有关联的申请）
            if (offer.getApplicationId() != null) {
                jobApplicationService.updateApplicationStatus(offer.getApplicationId(), "rejected", null);
            }
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsExpired(Long offerId) {
        Offer offer = offerMapper.selectById(offerId);
        if (offer == null) {
            throw new BusinessException("Offer不存在");
        }

        if (!"pending".equals(offer.getStatus())) {
            throw new BusinessException("只有待接受的Offer才能标记为过期");
        }

        offer.setStatus("expired");

        return offerMapper.updateById(offer) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateEmailStatus(Long offerId, String emailStatus) {
        Offer offer = offerMapper.selectById(offerId);
        if (offer == null) {
            throw new BusinessException("Offer不存在");
        }

        offer.setEmailStatus(emailStatus);
        if ("sent".equals(emailStatus)) {
            offer.setEmailSendTime(LocalDateTime.now());
        }

        return offerMapper.updateById(offer) > 0;
    }

    @Override
    public Map<String, Object> getOfferStats(Long companyId) {
        LambdaQueryWrapper<Offer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Offer::getCompanyId, companyId);

        long totalOffers = offerMapper.selectCount(wrapper);

        long pendingOffers = offerMapper.selectCount(new LambdaQueryWrapper<Offer>()
                .eq(Offer::getCompanyId, companyId)
                .eq(Offer::getStatus, "pending"));

        long acceptedOffers = offerMapper.selectCount(new LambdaQueryWrapper<Offer>()
                .eq(Offer::getCompanyId, companyId)
                .eq(Offer::getStatus, "accepted"));

        long rejectedOffers = offerMapper.selectCount(new LambdaQueryWrapper<Offer>()
                .eq(Offer::getCompanyId, companyId)
                .eq(Offer::getStatus, "rejected"));

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOffers", totalOffers);
        stats.put("pendingOffers", pendingOffers);
        stats.put("acceptedOffers", acceptedOffers);
        stats.put("rejectedOffers", rejectedOffers);
        stats.put("acceptRate", totalOffers > 0 ? (acceptedOffers * 100.0 / totalOffers) : 0);

        return stats;
    }
}
