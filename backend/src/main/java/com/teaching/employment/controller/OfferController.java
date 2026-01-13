package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.Offer;
import com.teaching.employment.service.OfferService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * Offer管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@RestController
@RequestMapping("/offer")
@Api(tags = "Offer管理")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;

    /**
     * 分页查询Offer列表
     */
    @GetMapping("/page")
    @ApiOperation("分页查询Offer列表")
    public Result<IPage<Offer>> getOfferPage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("申请ID") @RequestParam(required = false) Long applicationId,
            @ApiParam("职位ID") @RequestParam(required = false) Long positionId,
            @ApiParam("学生ID") @RequestParam(required = false) Long studentId,
            @ApiParam("企业ID") @RequestParam(required = false) Long companyId,
            @ApiParam("Offer状态") @RequestParam(required = false) String status) {
        IPage<Offer> page = offerService.getOfferPage(current, size, applicationId, positionId,
                studentId, companyId, status);
        return Result.ok(page);
    }

    /**
     * 根据学生ID查询Offer列表
     */
    @GetMapping("/student/{studentId}")
    @ApiOperation("根据学生ID查询Offer列表")
    public Result<IPage<Offer>> getOffersByStudentId(
            @PathVariable Long studentId,
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        IPage<Offer> page = offerService.getOffersByStudentId(current, size, studentId);
        return Result.ok(page);
    }

    /**
     * 根据企业ID查询Offer列表
     */
    @GetMapping("/company/{companyId}")
    @ApiOperation("根据企业ID查询Offer列表")
    public Result<IPage<Offer>> getOffersByCompanyId(
            @PathVariable Long companyId,
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        IPage<Offer> page = offerService.getOffersByCompanyId(current, size, companyId);
        return Result.ok(page);
    }

    /**
     * 根据申请ID查询Offer
     */
    @GetMapping("/application/{applicationId}")
    @ApiOperation("根据申请ID查询Offer")
    public Result<Offer> getOfferByApplicationId(@PathVariable Long applicationId) {
        Offer offer = offerService.getOfferByApplicationId(applicationId);
        return Result.ok(offer);
    }

    /**
     * 根据ID查询Offer
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询Offer")
    public Result<Offer> getOfferById(@PathVariable Long id) {
        Offer offer = offerService.getById(id);
        return Result.ok(offer);
    }

    /**
     * 新增Offer
     */
    @PostMapping
    @ApiOperation("新增Offer")
    public Result<Void> createOffer(@RequestBody Offer offer) {
        // 默认状态为待接受
        if (offer.getStatus() == null) {
            offer.setStatus("pending");
        }
        // 默认邮件状态为待发送
        if (offer.getEmailStatus() == null) {
            offer.setEmailStatus("pending");
        }
        boolean success = offerService.save(offer);
        return success ? Result.ok("新增成功") : Result.error("新增失败");
    }

    /**
     * 更新Offer
     */
    @PutMapping
    @ApiOperation("更新Offer")
    public Result<Void> updateOffer(@RequestBody Offer offer) {
        boolean success = offerService.updateById(offer);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除Offer
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除Offer")
    public Result<Void> deleteOffer(@PathVariable Long id) {
        boolean success = offerService.removeById(id);
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }

    /**
     * 创建并发送Offer
     */
    @PostMapping("/create-and-send")
    @ApiOperation("创建并发送Offer")
    public Result<Void> createAndSendOffer(
            @ApiParam("申请ID") @RequestParam Long applicationId,
            @ApiParam("职位名称") @RequestParam String positionName,
            @ApiParam("入职城市") @RequestParam String city,
            @ApiParam("薪资") @RequestParam BigDecimal salary,
            @ApiParam("薪资单位") @RequestParam String salaryUnit,
            @ApiParam("入职日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate onboardDate,
            @ApiParam("接受截止日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate acceptDeadline,
            @ApiParam("附件URL") @RequestParam(required = false) String attachmentUrl,
            @ApiParam("备注") @RequestParam(required = false) String remark) {
        boolean success = offerService.createAndSendOffer(applicationId, positionName, city,
                salary, salaryUnit, onboardDate, acceptDeadline, attachmentUrl, remark);
        return success ? Result.ok("创建并发送成功") : Result.error("创建并发送失败");
    }

    /**
     * 学生接受Offer
     */
    @PostMapping("/{id}/accept")
    @ApiOperation("学生接受Offer")
    public Result<Void> acceptOffer(@PathVariable Long id) {
        boolean success = offerService.acceptOffer(id);
        return success ? Result.ok("接受成功") : Result.error("接受失败");
    }

    /**
     * 学生拒绝Offer
     */
    @PostMapping("/{id}/reject")
    @ApiOperation("学生拒绝Offer")
    public Result<Void> rejectOffer(@PathVariable Long id) {
        boolean success = offerService.rejectOffer(id);
        return success ? Result.ok("拒绝成功") : Result.error("拒绝失败");
    }

    /**
     * 企业取消Offer
     */
    @PostMapping("/{id}/cancel")
    @ApiOperation("企业取消Offer")
    public Result<Void> cancelOffer(@PathVariable Long id) {
        boolean success = offerService.cancelOffer(id);
        return success ? Result.ok("取消成功") : Result.error("取消失败");
    }

    /**
     * 标记Offer为已过期
     */
    @PostMapping("/{id}/mark-expired")
    @ApiOperation("标记Offer为已过期")
    public Result<Void> markAsExpired(@PathVariable Long id) {
        boolean success = offerService.markAsExpired(id);
        return success ? Result.ok("标记成功") : Result.error("标记失败");
    }

    /**
     * 更新邮件发送状态
     */
    @PostMapping("/{id}/update-email-status")
    @ApiOperation("更新邮件发送状态")
    public Result<Void> updateEmailStatus(
            @PathVariable Long id,
            @ApiParam("邮件状态") @RequestParam String emailStatus) {
        boolean success = offerService.updateEmailStatus(id, emailStatus);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 获取Offer统计信息
     */
    @GetMapping("/stats/company/{companyId}")
    @ApiOperation("获取Offer统计信息")
    public Result<Map<String, Object>> getOfferStats(@PathVariable Long companyId) {
        Map<String, Object> stats = offerService.getOfferStats(companyId);
        return Result.ok(stats);
    }
}
