package com.teaching.employment.excel;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.teaching.employment.entity.Student;
import com.teaching.employment.entity.User;
import com.teaching.employment.service.StudentService;
import com.teaching.employment.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 学生Excel导入监听器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Slf4j
@RequiredArgsConstructor
public class StudentImportListener extends AnalysisEventListener<StudentExcel> {

    private final StudentService studentService;
    private final UserService userService;

    /**
     * 每隔500条存储数据库,实际使用中可以3000条,然后清理list,方便内存回收
     */
    private static final int BATCH_COUNT = 500;

    /**
     * 缓存的数据
     */
    private final List<Student> list = new ArrayList<>();

    /**
     * 导入成功数量
     */
    private int successCount = 0;

    /**
     * 导入失败数量
     */
    private int failCount = 0;

    /**
     * 错误信息
     */
    private final List<String> errorMessages = new ArrayList<>();

    @Override
    public void invoke(StudentExcel data, AnalysisContext context) {
        if (data == null) {
            return;
        }

        try {
            // 验证必填字段
            if (StrUtil.isBlank(data.getStudentNo())) {
                errorMessages.add("第" + (context.readRowHolder().getRowIndex() + 1) + "行:学号不能为空");
                failCount++;
                return;
            }

            if (StrUtil.isBlank(data.getRealName())) {
                errorMessages.add("第" + (context.readRowHolder().getRowIndex() + 1) + "行:姓名不能为空");
                failCount++;
                return;
            }

            // 检查学号是否已存在
            Student existingStudent = studentService.lambdaQuery()
                    .eq(Student::getStudentNo, data.getStudentNo())
                    .one();

            if (existingStudent != null) {
                errorMessages.add("第" + (context.readRowHolder().getRowIndex() + 1) + "行:学号" + data.getStudentNo() + "已存在");
                failCount++;
                return;
            }

            // 创建用户
            User user = new User();
            user.setUsername(data.getStudentNo()); // 学号作为用户名
            user.setPassword("123456"); // 默认密码
            user.setRealName(data.getRealName());
            user.setPhone(data.getPhone());
            user.setEmail(data.getEmail());
            user.setRoleId(4L); // 学生角色

            // 创建学生
            Student student = new Student();
            student.setStudentNo(data.getStudentNo());
            student.setClassName(data.getClassName());
            student.setMajor(data.getMajor());
            student.setGrade(data.getGrade());
            student.setPhone(data.getPhone());
            student.setEmail(data.getEmail());
            student.setIdCard(data.getIdCard());
            student.setAddress(data.getAddress());
            student.setPoliticalStatus(data.getPoliticalStatus());
            student.setNation(data.getNation());
            student.setGuardianName(data.getGuardianName());
            student.setGuardianPhone(data.getGuardianPhone());

            // 性别转换
            if (StrUtil.isNotBlank(data.getGender())) {
                if ("男".equals(data.getGender()) || "1".equals(data.getGender())) {
                    student.setGender(1);
                } else if ("女".equals(data.getGender()) || "2".equals(data.getGender())) {
                    student.setGender(2);
                }
            }

            // 日期转换
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            if (StrUtil.isNotBlank(data.getBirthDate())) {
                try {
                    student.setBirthDate(LocalDate.parse(data.getBirthDate(), formatter));
                } catch (Exception e) {
                    log.warn("出生日期格式错误: {}", data.getBirthDate());
                }
            }

            if (StrUtil.isNotBlank(data.getEnrollmentDate())) {
                try {
                    student.setEnrollmentDate(LocalDate.parse(data.getEnrollmentDate(), formatter));
                } catch (Exception e) {
                    log.warn("入学日期格式错误: {}", data.getEnrollmentDate());
                }
            }

            if (StrUtil.isNotBlank(data.getGraduationDate())) {
                try {
                    student.setGraduationDate(LocalDate.parse(data.getGraduationDate(), formatter));
                } catch (Exception e) {
                    log.warn("毕业日期格式错误: {}", data.getGraduationDate());
                }
            }

            // 如果有学校名称,需要根据学校名称查询学校ID(这里简化处理)
            // 实际使用中需要根据schoolName查询schoolId

            list.add(student);

            // 达到BATCH_COUNT了,需要去存储一次数据库,防止数据几万条数据在内存,容易OOM
            if (list.size() >= BATCH_COUNT) {
                saveData();
            }

            successCount++;
        } catch (Exception e) {
            errorMessages.add("第" + (context.readRowHolder().getRowIndex() + 1) + "行:" + e.getMessage());
            failCount++;
            log.error("导入学生数据失败", e);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
        log.info("所有数据解析完成!成功:{},失败:{}", successCount, failCount);
    }

    /**
     * 加上存储数据库
     */
    private void saveData() {
        if (list.isEmpty()) {
            return;
        }

        try {
            // 批量保存
            for (Student student : list) {
                studentService.save(student);
            }
            list.clear();
        } catch (Exception e) {
            log.error("批量保存学生数据失败", e);
        }
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }
}
