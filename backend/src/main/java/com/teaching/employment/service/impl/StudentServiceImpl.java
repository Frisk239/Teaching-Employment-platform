package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.School;
import com.teaching.employment.entity.Student;
import com.teaching.employment.entity.User;
import com.teaching.employment.exception.BusinessException;
import com.teaching.employment.mapper.StudentMapper;
import com.teaching.employment.service.SchoolService;
import com.teaching.employment.service.StudentService;
import com.teaching.employment.service.UserService;
import com.teaching.employment.utils.ExcelUtil;
import com.teaching.employment.utils.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 学员Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Service
@RequiredArgsConstructor
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    private final StudentMapper studentMapper;
    private final UserService userService;
    private final SchoolService schoolService;

    @Override
    public IPage<Student> getStudentPage(Integer current, Integer size, Long schoolId, String keyword) {
        Page<Student> page = new Page<>(current, size);
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();

        if (schoolId != null) {
            wrapper.eq(Student::getSchoolId, schoolId);
        }
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Student::getStudentNo, keyword)
                    .or().like(Student::getPhone, keyword));
        }

        wrapper.orderByDesc(Student::getCreateTime);
        return studentMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String importStudents(MultipartFile file) throws IOException {
        List<ExcelUtil.StudentImportTemplate> dataList = ExcelUtil.importExcel(file, ExcelUtil.StudentImportTemplate.class);

        if (dataList == null || dataList.isEmpty()) {
            throw new BusinessException("Excel文件为空");
        }

        int successCount = 0;
        int failCount = 0;
        List<String> errorMessages = new ArrayList<>();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 0; i < dataList.size(); i++) {
            ExcelUtil.StudentImportTemplate row = dataList.get(i);
            int rowNum = i + 2; // Excel行号（从第2行开始，第1行是表头）

            try {
                // 验证必填字段
                if (StrUtil.isBlank(row.getStudentNo()) ||
                    StrUtil.isBlank(row.getRealName()) ||
                    StrUtil.isBlank(row.getIdCard()) ||
                    StrUtil.isBlank(row.getPhone())) {
                    errorMessages.add("第" + rowNum + "行：必填字段为空");
                    failCount++;
                    continue;
                }

                // 查找学校
                LambdaQueryWrapper<School> schoolWrapper = new LambdaQueryWrapper<>();
                schoolWrapper.eq(School::getSchoolName, row.getSchoolName());
                School school = schoolService.getOne(schoolWrapper);
                if (school == null) {
                    errorMessages.add("第" + rowNum + "行：学校[" + row.getSchoolName() + "]不存在");
                    failCount++;
                    continue;
                }

                // 检查学号是否已存在
                Student existStudent = getStudentByStudentNo(row.getStudentNo());
                if (existStudent != null) {
                    errorMessages.add("第" + rowNum + "行：学号[" + row.getStudentNo() + "]已存在");
                    failCount++;
                    continue;
                }

                // 创建用户账号
                User user = new User();
                user.setUsername(row.getStudentNo()); // 学号作为用户名
                user.setPassword(PasswordUtil.encode("123456")); // 默认密码123456
                user.setRealName(row.getRealName());
                user.setPhone(row.getPhone());
                user.setEmail(row.getEmail());
                user.setRoleId(4L); // 学员角色ID
                user.setSchoolId(school.getId());
                user.setStatus(1);
                boolean userSaved = userService.save(user);
                if (!userSaved) {
                    errorMessages.add("第" + rowNum + "行：创建用户账号失败");
                    failCount++;
                    continue;
                }

                // 创建学员信息
                Student student = new Student();
                student.setUserId(user.getId());
                student.setStudentNo(row.getStudentNo());
                student.setSchoolId(school.getId());
                student.setClassName(row.getClassName());
                student.setMajor(row.getMajor());
                student.setGrade(row.getGrade());
                student.setGender("男".equals(row.getGender()) ? 1 : 2);
                student.setIdCard(row.getIdCard());
                student.setPhone(row.getPhone());
                student.setEmail(row.getEmail());
                student.setAddress(row.getAddress());
                student.setPoliticalStatus(row.getPoliticalStatus());
                student.setNation(row.getNation());
                student.setGuardianName(row.getGuardianName());
                student.setGuardianPhone(row.getGuardianPhone());

                // 解析日期
                if (StrUtil.isNotBlank(row.getEnrollmentDate())) {
                    student.setEnrollmentDate(LocalDate.parse(row.getEnrollmentDate(), dateFormatter));
                }
                if (StrUtil.isNotBlank(row.getGraduationDate())) {
                    student.setGraduationDate(LocalDate.parse(row.getGraduationDate(), dateFormatter));
                }

                boolean studentSaved = this.save(student);
                if (studentSaved) {
                    successCount++;
                } else {
                    failCount++;
                    errorMessages.add("第" + rowNum + "行：保存学员信息失败");
                }

            } catch (Exception e) {
                failCount++;
                errorMessages.add("第" + rowNum + "行：" + e.getMessage());
            }
        }

        // 构建结果消息
        StringBuilder result = new StringBuilder();
        result.append("导入完成！成功：").append(successCount).append("条，失败：").append(failCount).append("条");
        if (!errorMessages.isEmpty()) {
            result.append("\n失败原因：\n");
            errorMessages.forEach(msg -> result.append(msg).append("\n"));
        }

        return result.toString();
    }

    @Override
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil.downloadStudentTemplate(response);
    }

    @Override
    public Student getStudentByStudentNo(String studentNo) {
        return studentMapper.selectStudentWithDetailsByStudentNo(studentNo);
    }
}
