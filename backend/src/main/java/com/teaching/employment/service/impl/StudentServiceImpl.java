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
        IPage<Student> result = studentMapper.selectPage(page, wrapper);

        System.out.println("========== DEBUG: 查询到 " + result.getRecords().size() + " 条学员记录 ==========");

        // 填充学校名称和用户姓名
        fillRelatedData(result.getRecords());

        // 打印第一条数据看看realName是否填充成功
        if (!result.getRecords().isEmpty()) {
            Student first = result.getRecords().get(0);
            System.out.println("========== DEBUG: 第一条数据: studentNo=" + first.getStudentNo() +
                             ", realName=" + first.getRealName() +
                             ", schoolName=" + first.getSchoolName() + " ==========");
        }

        return result;
    }

    /**
     * 填充学员的关联数据(学校名称、用户姓名)
     */
    private void fillRelatedData(List<Student> students) {
        if (students == null || students.isEmpty()) {
            System.out.println("========== DEBUG: fillRelatedData - students为空 ==========");
            return;
        }

        System.out.println("========== DEBUG: fillRelatedData - 开始处理 " + students.size() + " 条记录 ==========");

        // 收集所有需要查询的学校ID
        List<Long> schoolIds = students.stream()
                .map(Student::getSchoolId)
                .distinct()
                .collect(java.util.stream.Collectors.toList());

        // 收集所有需要查询的用户ID
        List<Long> userIds = students.stream()
                .map(Student::getUserId)
                .distinct()
                .collect(java.util.stream.Collectors.toList());

        System.out.println("========== DEBUG: schoolIds=" + schoolIds + ", userIds=" + userIds + " ==========");

        // 批量查询学校信息
        final java.util.Map<Long, String> schoolMap;
        if (!schoolIds.isEmpty()) {
            List<School> schools = schoolService.listByIds(schoolIds);
            System.out.println("========== DEBUG: 查询到 " + schools.size() + " 条学校记录 ==========");
            schoolMap = schools.stream()
                    .collect(java.util.stream.Collectors.toMap(School::getId, School::getSchoolName));
        } else {
            schoolMap = java.util.Map.of();
        }

        // 批量查询用户信息
        final java.util.Map<Long, String> userMap;
        if (!userIds.isEmpty()) {
            List<User> users = userService.listByIds(userIds);
            System.out.println("========== DEBUG: 查询到 " + users.size() + " 条用户记录 ==========");
            userMap = users.stream()
                    .collect(java.util.stream.Collectors.toMap(User::getId, User::getRealName));
        } else {
            userMap = java.util.Map.of();
        }

        System.out.println("========== DEBUG: schoolMap=" + schoolMap + ", userMap=" + userMap + " ==========");

        // 填充数据到学员对象
        students.forEach(student -> {
            student.setSchoolName(schoolMap.get(student.getSchoolId()));
            student.setRealName(userMap.get(student.getUserId()));
        });

        System.out.println("========== DEBUG: 填充完成 ==========");
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

    @Override
    public Student getStudentWithDetails(Long id) {
        Student student = studentMapper.selectById(id);
        if (student != null) {
            fillRelatedData(java.util.Collections.singletonList(student));
        }
        return student;
    }

    @Override
    public void exportStudents(HttpServletResponse response) throws IOException {
        // 查询所有学员
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Student::getCreateTime);
        List<Student> students = studentMapper.selectList(wrapper);

        // 填充关联数据(学校名称和真实姓名)
        fillRelatedData(students);

        // 转换为导出模板格式
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<ExcelUtil.StudentExportTemplate> exportData = new ArrayList<>();

        for (Student student : students) {
            ExcelUtil.StudentExportTemplate template = new ExcelUtil.StudentExportTemplate();
            template.setStudentNo(student.getStudentNo());
            template.setRealName(student.getRealName());
            template.setGender(student.getGender() == 1 ? "男" : student.getGender() == 2 ? "女" : "");
            template.setBirthDate(student.getBirthDate() != null ? student.getBirthDate().format(dateFormatter) : "");
            template.setIdCard(student.getIdCard());
            template.setPhone(student.getPhone());
            template.setEmail(student.getEmail());
            template.setSchoolName(student.getSchoolName());
            template.setClassName(student.getClassName());
            template.setMajor(student.getMajor());
            template.setGrade(student.getGrade());
            template.setAddress(student.getAddress());
            template.setEnrollmentDate(student.getEnrollmentDate() != null ? student.getEnrollmentDate().format(dateFormatter) : "");
            template.setGraduationDate(student.getGraduationDate() != null ? student.getGraduationDate().format(dateFormatter) : "");
            template.setPoliticalStatus(student.getPoliticalStatus());
            template.setNation(student.getNation());
            template.setGuardianName(student.getGuardianName());
            template.setGuardianPhone(student.getGuardianPhone());
            exportData.add(template);
        }

        // 导出到Excel
        ExcelUtil.export(response, "学员数据", exportData, ExcelUtil.StudentExportTemplate.class);
    }
}
