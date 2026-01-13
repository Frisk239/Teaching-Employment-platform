package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.Company;
import com.teaching.employment.service.CompanyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 企业管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@RestController
@RequestMapping("/company")
@Api(tags = "企业管理")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    /**
     * 分页查询企业列表
     */
    @GetMapping("/page")
    @ApiOperation("分页查询企业列表")
    public Result<IPage<Company>> getCompanyPage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("认证状态") @RequestParam(required = false) String verifyStatus,
            @ApiParam("状态") @RequestParam(required = false) Object status,
            @ApiParam("关键词") @RequestParam(required = false) String keyword) {
        IPage<Company> page = companyService.getCompanyPage(current, size, verifyStatus, status, keyword);
        return Result.ok(page);
    }

    /**
     * 查询所有企业（用于下拉框）
     */
    @GetMapping("/list")
    @ApiOperation("查询所有企业")
    public Result<List<Company>> getCompanyList(
            @ApiParam("状态") @RequestParam(required = false) Object status) {
        List<Company> list = companyService.getCompanyList(status);
        return Result.ok(list);
    }

    /**
     * 根据ID查询企业
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询企业")
    public Result<Company> getCompanyById(@PathVariable Long id) {
        Company company = companyService.getById(id);
        return Result.ok(company);
    }

    /**
     * 新增企业
     */
    @PostMapping
    @ApiOperation("新增企业")
    public Result<Void> createCompany(@RequestBody Company company) {
        // 默认认证状态为待认证
        if (company.getVerifyStatus() == null) {
            company.setVerifyStatus("pending");
        }
        // 默认状态为正常
        if (company.getStatus() == null) {
            company.setStatus(1);
        }
        boolean success = companyService.save(company);
        return success ? Result.ok("新增成功") : Result.error("新增失败");
    }

    /**
     * 更新企业
     */
    @PutMapping
    @ApiOperation("更新企业")
    public Result<Void> updateCompany(@RequestBody Company company) {
        boolean success = companyService.updateById(company);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除企业
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除企业")
    public Result<Void> deleteCompany(@PathVariable Long id) {
        boolean success = companyService.removeById(id);
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }

    /**
     * 企业认证审核
     */
    @PostMapping("/{id}/verify")
    @ApiOperation("企业认证审核")
    public Result<Void> verifyCompany(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String verifyStatus = request.get("verifyStatus");
        String rejectReason = request.get("rejectReason");

        boolean success = companyService.verifyCompany(id, verifyStatus, rejectReason);
        return success ? Result.ok("审核成功") : Result.error("审核失败");
    }
}
