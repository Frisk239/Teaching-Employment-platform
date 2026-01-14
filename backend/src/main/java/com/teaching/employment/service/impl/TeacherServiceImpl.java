package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.Course;
import com.teaching.employment.entity.School;
import com.teaching.employment.entity.Teacher;
import com.teaching.employment.entity.User;
import com.teaching.employment.mapper.CourseMapper;
import com.teaching.employment.mapper.TeacherMapper;
import com.teaching.employment.mapper.UserMapper;
import com.teaching.employment.service.SchoolService;
import com.teaching.employment.service.TeacherService;
import com.teaching.employment.utils.ExcelUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 教师Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Service
@RequiredArgsConstructor
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    private final TeacherMapper teacherMapper;
    private final CourseMapper courseMapper;
    private final UserMapper userMapper;
    private final SchoolService schoolService;

    @Override
    public List<Teacher> listByIds(Collection<? extends Serializable> idList) {
        List<Teacher> teachers = super.listByIds(idList);

        if (teachers == null || teachers.isEmpty()) {
            return teachers;
        }

        // 填充学校名称和用户姓名
        fillRelatedData(teachers);

        return teachers;
    }

    @Override
    public IPage<Teacher> getTeacherPage(Integer current, Integer size, Long schoolId, String keyword, String department) {
        Page<Teacher> page = new Page<>(current, size);
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();

        if (schoolId != null) {
            wrapper.eq(Teacher::getSchoolId, schoolId);
        }
        if (StrUtil.isNotBlank(keyword)) {
            // 关键词搜索: 仅支持姓名搜索
            // 1. 先查询User表中匹配姓名的用户IDs
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.like(User::getRealName, keyword)
                    .select(User::getId);
            List<User> matchingUsers = userMapper.selectList(userWrapper);
            List<Long> userIds = matchingUsers.stream()
                    .map(User::getId)
                    .collect(Collectors.toList());

            // 2. 查询userId在匹配列表中的教师
            if (!userIds.isEmpty()) {
                wrapper.in(Teacher::getUserId, userIds);
            } else {
                // 如果没有匹配的用户,返回空结果
                wrapper.eq(Teacher::getId, -1L);
            }
        }
        if (StrUtil.isNotBlank(department)) {
            wrapper.like(Teacher::getDepartment, department);
        }

        wrapper.orderByDesc(Teacher::getCreateTime);
        IPage<Teacher> result = teacherMapper.selectPage(page, wrapper);

        // 填充学校名称和用户姓名
        fillRelatedData(result.getRecords());

        return result;
    }

    @Override
    public Map<String, Object> getTeacherStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 查询所有教师
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Teacher::getCreateTime);
        List<Teacher> teachers = teacherMapper.selectList(wrapper);

        // 教师总数
        stats.put("total", teachers.size());

        // 覆盖学校数(去重)
        long schoolCount = teachers.stream()
                .map(Teacher::getSchoolId)
                .distinct()
                .count();
        stats.put("schools", schoolCount);

        // 涉及部门数(去重,排除null和空字符串)
        long departmentCount = teachers.stream()
                .map(Teacher::getDepartment)
                .filter(dept -> dept != null && !dept.trim().isEmpty())
                .distinct()
                .count();
        stats.put("departments", departmentCount);

        // 开设课程数(通过CourseMapper查询)
        LambdaQueryWrapper<Course> courseWrapper = new LambdaQueryWrapper<>();
        List<Course> courses = courseMapper.selectList(courseWrapper);
        stats.put("courses", courses.size());

        return stats;
    }

    @Override
    public void fillRelatedData(List<Teacher> teachers) {
        if (teachers == null || teachers.isEmpty()) {
            return;
        }

        // 收集所有需要查询的学校ID
        List<Long> schoolIds = teachers.stream()
                .map(Teacher::getSchoolId)
                .distinct()
                .collect(Collectors.toList());

        // 收集所有需要查询的用户ID
        List<Long> userIds = teachers.stream()
                .map(Teacher::getUserId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询学校信息
        final Map<Long, String> schoolMap;
        if (!schoolIds.isEmpty()) {
            List<School> schools = schoolService.listByIds(schoolIds);
            schoolMap = schools.stream()
                    .collect(Collectors.toMap(School::getId, School::getSchoolName));
        } else {
            schoolMap = Map.of();
        }

        // 批量查询用户信息
        final Map<Long, User> userMap;
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds);
            userMap = users.stream()
                    .collect(Collectors.toMap(User::getId, u -> u));
        } else {
            userMap = Map.of();
        }

        // 填充数据到教师对象
        teachers.forEach(teacher -> {
            teacher.setSchoolName(schoolMap.get(teacher.getSchoolId()));
            if (teacher.getUserId() != null) {
                User user = userMap.get(teacher.getUserId());
                if (user != null) {
                    teacher.setRealName(user.getRealName());
                    teacher.setPhone(user.getPhone());
                    teacher.setEmail(user.getEmail());
                }
            }
        });
    }

    @Override
    public List<Teacher> getTeacherList(Long schoolId) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        if (schoolId != null) {
            wrapper.eq(Teacher::getSchoolId, schoolId);
        }
        wrapper.orderByDesc(Teacher::getCreateTime);
        List<Teacher> list = teacherMapper.selectList(wrapper);

        // 填充学校名称和用户姓名
        fillRelatedData(list);

        return list;
    }

    @Override
    public Teacher getTeacherByUserId(Long userId) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teacher::getUserId, userId);
        return teacherMapper.selectOne(wrapper);
    }

    @Override
    public Map<String, Object> getTeacherWorkload(Long teacherId) {
        Map<String, Object> workload = new HashMap<>();

        // 使用CourseMapper直接查询教师的所有课程
        LambdaQueryWrapper<Course> courseWrapper = new LambdaQueryWrapper<>();
        courseWrapper.eq(Course::getTeacherId, teacherId);
        List<Course> courses = courseMapper.selectList(courseWrapper);

        // 统计课程数量
        int totalCourses = courses.size();

        // 统计总课时
        int totalHours = courses.stream()
                .mapToInt(course -> course.getTotalHours() != null ? course.getTotalHours() : 0)
                .sum();

        // 统计不同状态的课程数量
        long ongoingCourses = courses.stream()
                .filter(course -> "ongoing".equals(course.getStatus()))
                .count();

        long completedCourses = courses.stream()
                .filter(course -> "completed".equals(course.getStatus()))
                .count();

        long pendingCourses = courses.stream()
                .filter(course -> "pending".equals(course.getStatus()))
                .count();

        // 统计总学员数
        int totalStudents = courses.stream()
                .mapToInt(course -> course.getCurrentStudents() != null ? course.getCurrentStudents() : 0)
                .sum();

        workload.put("teacherId", teacherId);
        workload.put("totalCourses", totalCourses);
        workload.put("totalHours", totalHours);
        workload.put("ongoingCourses", ongoingCourses);
        workload.put("completedCourses", completedCourses);
        workload.put("pendingCourses", pendingCourses);
        workload.put("totalStudents", totalStudents);

        return workload;
    }

    @Override
    public List<Course> getTeacherCourses(Long teacherId) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getTeacherId, teacherId);
        wrapper.orderByDesc(Course::getCreateTime);
        return courseMapper.selectList(wrapper);
    }

    @Override
    public void exportTeachers(HttpServletResponse response) throws IOException {
        // 查询所有教师
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Teacher::getCreateTime);
        List<Teacher> teachers = teacherMapper.selectList(wrapper);

        // 填充关联数据(学校名称、用户姓名、手机号、邮箱)
        fillRelatedData(teachers);

        // 转换为导出模板格式
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<ExcelUtil.TeacherExportTemplate> exportData = new ArrayList<>();

        for (Teacher teacher : teachers) {
            ExcelUtil.TeacherExportTemplate template = new ExcelUtil.TeacherExportTemplate();
            template.setTeacherNo(teacher.getTeacherNo());
            template.setRealName(teacher.getRealName());
            template.setGender(teacher.getGender() == 1 ? "男" : teacher.getGender() == 2 ? "女" : "");
            template.setBirthDate(teacher.getBirthDate() != null ? teacher.getBirthDate().format(dateFormatter) : "");
            template.setIdCard(teacher.getIdCard());
            template.setPhone(teacher.getPhone());
            template.setEmail(teacher.getEmail());
            template.setSchoolName(teacher.getSchoolName());
            template.setDepartment(teacher.getDepartment());
            template.setTitle(teacher.getTitle());
            template.setEducation(teacher.getEducation());
            template.setSpecialty(teacher.getSpecialty());
            template.setEntryDate(teacher.getEntryDate() != null ? teacher.getEntryDate().format(dateFormatter) : "");
            template.setAddress(teacher.getAddress());
            template.setDescription(teacher.getDescription());
            exportData.add(template);
        }

        // 导出到Excel
        ExcelUtil.export(response, "教师数据", exportData, ExcelUtil.TeacherExportTemplate.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String importTeachers(MultipartFile file) throws IOException {
        List<ExcelUtil.TeacherImportTemplate> dataList = ExcelUtil.importExcel(file, ExcelUtil.TeacherImportTemplate.class);

        if (dataList == null || dataList.isEmpty()) {
            return "导入失败！文件中没有数据";
        }

        int successCount = 0;
        int failCount = 0;
        StringBuilder errorMessages = new StringBuilder();

        // 批量查询所有学校,建立schoolName -> schoolId的映射
        List<School> allSchools = schoolService.list();
        Map<String, Long> schoolNameMap = allSchools.stream()
                .collect(Collectors.toMap(School::getSchoolName, School::getId));

        // 批量查询所有工号,用于重复性检查
        List<Teacher> existingTeachers = teacherMapper.selectList(new LambdaQueryWrapper<>());
        Map<String, Teacher> teacherNoMap = existingTeachers.stream()
                .collect(Collectors.toMap(Teacher::getTeacherNo, t -> t));

        List<Teacher> teachersToInsert = new ArrayList<>();
        List<User> usersToInsert = new ArrayList<>();

        for (int i = 0; i < dataList.size(); i++) {
            ExcelUtil.TeacherImportTemplate data = dataList.get(i);
            int rowNum = i + 2; // Excel行号从2开始(第1行是表头)

            try {
                // 验证必填字段
                if (StrUtil.isBlank(data.getTeacherNo())) {
                    errorMessages.append(String.format("第%d行：工号不能为空\n", rowNum));
                    failCount++;
                    continue;
                }
                if (StrUtil.isBlank(data.getRealName())) {
                    errorMessages.append(String.format("第%d行：姓名不能为空\n", rowNum));
                    failCount++;
                    continue;
                }
                if (StrUtil.isBlank(data.getSchoolName())) {
                    errorMessages.append(String.format("第%d行：所属学校不能为空\n", rowNum));
                    failCount++;
                    continue;
                }

                // 检查工号是否已存在
                if (teacherNoMap.containsKey(data.getTeacherNo())) {
                    errorMessages.append(String.format("第%d行：工号[%s]已存在\n", rowNum, data.getTeacherNo()));
                    failCount++;
                    continue;
                }

                // 查找学校ID
                Long schoolId = schoolNameMap.get(data.getSchoolName());
                if (schoolId == null) {
                    errorMessages.append(String.format("第%d行：学校[%s]不存在\n", rowNum, data.getSchoolName()));
                    failCount++;
                    continue;
                }

                // 创建用户账号
                User user = new User();
                user.setUsername(data.getTeacherNo()); // 使用工号作为用户名
                user.setPassword("123456"); // 默认密码
                user.setRealName(data.getRealName());
                user.setPhone(data.getPhone());
                user.setEmail(data.getEmail());
                user.setSchoolId(schoolId);
                user.setRoleId(3L); // 3-教师角色
                user.setStatus(1);
                usersToInsert.add(user);

                // 创建教师记录
                Teacher teacher = new Teacher();
                teacher.setTeacherNo(data.getTeacherNo());
                teacher.setSchoolId(schoolId);
                teacher.setDepartment(data.getDepartment());
                teacher.setTitle(data.getTitle());
                teacher.setEducation(data.getEducation());
                teacher.setSpecialty(data.getSpecialty());
                teacher.setEntryDate(StrUtil.isNotBlank(data.getEntryDate()) ? LocalDate.parse(data.getEntryDate()) : null);
                teacher.setIdCard(data.getIdCard());
                teacher.setGender("男".equals(data.getGender()) ? 1 : "女".equals(data.getGender()) ? 2 : null);
                teacher.setBirthDate(StrUtil.isNotBlank(data.getBirthDate()) ? LocalDate.parse(data.getBirthDate()) : null);
                teacher.setAddress(data.getAddress());
                teacher.setDescription(data.getDescription());

                teachersToInsert.add(teacher);
                successCount++;

            } catch (Exception e) {
                errorMessages.append(String.format("第%d行：%s\n", rowNum, e.getMessage()));
                failCount++;
            }
        }

        // 批量插入用户
        for (int i = 0; i < usersToInsert.size(); i++) {
            User user = usersToInsert.get(i);
            userMapper.insert(user);
            // 设置userId到教师记录
            teachersToInsert.get(i).setUserId(user.getId());
        }

        // 批量插入教师
        for (Teacher teacher : teachersToInsert) {
            teacherMapper.insert(teacher);
        }

        // 构建结果消息
        StringBuilder result = new StringBuilder();
        result.append(String.format("导入完成！成功：%d条，失败：%d条\n", successCount, failCount));
        if (errorMessages.length() > 0) {
            result.append("失败原因：\n").append(errorMessages);
        }

        return result.toString();
    }

    @Override
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        // 创建一个空模板,包含一行示例数据
        List<ExcelUtil.TeacherImportTemplate> data = new ArrayList<>();

        // 获取第一个真实存在的学校
        List<School> schools = schoolService.list();
        String exampleSchoolName = schools.isEmpty() ? "请替换为真实学校名称" : schools.get(0).getSchoolName();

        ExcelUtil.TeacherImportTemplate template = new ExcelUtil.TeacherImportTemplate();
        template.setTeacherNo("T2025001");
        template.setRealName("张三");
        template.setGender("男");
        template.setBirthDate("1990-01-01");
        template.setIdCard("110101199001011234");
        template.setPhone("13800138000");
        template.setEmail("zhangsan@example.com");
        template.setSchoolName(exampleSchoolName);
        template.setDepartment("计算机学院");
        template.setTitle("教授");
        template.setEducation("博士");
        template.setSpecialty("人工智能");
        template.setEntryDate("2020-09-01");
        template.setAddress("北京市朝阳区");
        template.setDescription("教授,主要研究方向为人工智能");

        data.add(template);

        ExcelUtil.export(response, "教师导入模板", data, ExcelUtil.TeacherImportTemplate.class);
    }
}
