package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.School;
import com.teaching.employment.mapper.SchoolMapper;
import com.teaching.employment.service.SchoolService;
import com.teaching.employment.utils.ExcelUtil;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
     * @return 学校分页列表
     */
    public IPage<School> getSchoolPage(Integer current, Integer size, String schoolName) {
        Page<School> page = new Page<>(current, size);
        LambdaQueryWrapper<School> wrapper = new LambdaQueryWrapper<>();

        // 动态条件查询
        if (StrUtil.isNotBlank(schoolName)) {
            wrapper.like(School::getSchoolName, schoolName);
        }

        // 按创建时间倒序排列
        wrapper.orderByDesc(School::getCreateTime);

        return this.page(page, wrapper);
    }

    /**
     * Excel导出学校
     *
     * @param response HTTP响应
     * @throws IOException IO异常
     */
    @Override
    public void exportSchools(HttpServletResponse response) throws IOException {
        // 查询所有学校
        LambdaQueryWrapper<School> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(School::getCreateTime);
        List<School> schools = this.list(wrapper);

        System.out.println("========================================");
        System.out.println("导出学校数据，共 " + schools.size() + " 条记录");

        // 转换为导出模板格式
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<ExcelUtil.SchoolExportTemplate> exportData = new ArrayList<>();

        for (School school : schools) {
            ExcelUtil.SchoolExportTemplate template = new ExcelUtil.SchoolExportTemplate();
            template.setSchoolName(school.getSchoolName());
            template.setSchoolCode(school.getSchoolCode());
            template.setAddress(school.getAddress());
            template.setContactPerson(school.getContactPerson());
            template.setContactPhone(school.getContactPhone());
            template.setDescription(school.getDescription());
            template.setStatus(school.getStatus() == 1 ? "正常" : "停用");
            template.setCreateTime(school.getCreateTime() != null
                ? school.getCreateTime().format(dateFormatter)
                : "");
            exportData.add(template);

            System.out.println("学校: " + school.getSchoolName() + ", 代码: " + school.getSchoolCode());
        }

        System.out.println("准备导出 " + exportData.size() + " 条数据到Excel");
        System.out.println("========================================");

        // 导出Excel
        ExcelUtil.export(response, "学校列表", exportData, ExcelUtil.SchoolExportTemplate.class);
        System.out.println("Excel导出完成");
    }
}
