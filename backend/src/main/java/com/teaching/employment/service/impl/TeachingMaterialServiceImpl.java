package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.Course;
import com.teaching.employment.entity.Teacher;
import com.teaching.employment.entity.TeachingMaterial;
import com.teaching.employment.mapper.TeachingMaterialMapper;
import com.teaching.employment.service.CourseService;
import com.teaching.employment.service.TeacherService;
import com.teaching.employment.service.TeachingMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 教学资料Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-17
 */
@Service
@RequiredArgsConstructor
public class TeachingMaterialServiceImpl extends ServiceImpl<TeachingMaterialMapper, TeachingMaterial>
        implements TeachingMaterialService {

    private final TeachingMaterialMapper teachingMaterialMapper;
    private final CourseService courseService;
    private final TeacherService teacherService;

    @Override
    public IPage<TeachingMaterial> getTeachingMaterialPage(Integer current, Integer size, Long teacherId, Long courseId,
                                                            String materialType, String category, String keyword) {
        Page<TeachingMaterial> page = new Page<>(current, size);
        LambdaQueryWrapper<TeachingMaterial> wrapper = new LambdaQueryWrapper<>();

        if (teacherId != null) {
            wrapper.eq(TeachingMaterial::getTeacherId, teacherId);
        }

        if (courseId != null) {
            wrapper.eq(TeachingMaterial::getCourseId, courseId);
        }

        if (StrUtil.isNotBlank(materialType)) {
            wrapper.eq(TeachingMaterial::getMaterialType, materialType);
        }

        if (StrUtil.isNotBlank(category)) {
            wrapper.eq(TeachingMaterial::getCategory, category);
        }

        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(TeachingMaterial::getTitle, keyword)
                    .or()
                    .like(TeachingMaterial::getDescription, keyword)
                    .or()
                    .like(TeachingMaterial::getTags, keyword));
        }

        wrapper.orderByDesc(TeachingMaterial::getCreateTime);

        IPage<TeachingMaterial> resultPage = teachingMaterialMapper.selectPage(page, wrapper);

        // 填充关联字段信息
        resultPage.getRecords().forEach(material -> {
            // 填充课程名称
            if (material.getCourseId() != null) {
                Course course = courseService.getById(material.getCourseId());
                if (course != null) {
                    material.setCourseName(course.getCourseName());
                }
            }

            // 填充教师姓名
            if (material.getTeacherId() != null) {
                Teacher teacher = teacherService.getById(material.getTeacherId());
                if (teacher != null && teacher.getUserId() != null) {
                    // 假设有UserService可以获取用户真实姓名
                    // 这里先设置教师ID作为临时方案
                    material.setTeacherName("教师" + teacher.getId());
                }
            }

            // 填充文件扩展名
            if (StrUtil.isNotBlank(material.getFileUrl())) {
                int lastDotIndex = material.getFileUrl().lastIndexOf('.');
                if (lastDotIndex > 0 && lastDotIndex < material.getFileUrl().length() - 1) {
                    material.setFileExtension(material.getFileUrl().substring(lastDotIndex + 1).toUpperCase());
                }
            }
        });

        return resultPage;
    }

    @Override
    public void incrementDownloadCount(Long id) {
        TeachingMaterial material = teachingMaterialMapper.selectById(id);
        if (material != null) {
            int newCount = material.getDownloadCount() == null ? 1 : material.getDownloadCount() + 1;
            material.setDownloadCount(newCount);
            teachingMaterialMapper.updateById(material);
        }
    }

    @Override
    public void incrementViewCount(Long id) {
        TeachingMaterial material = teachingMaterialMapper.selectById(id);
        if (material != null) {
            int newCount = material.getViewCount() == null ? 1 : material.getViewCount() + 1;
            material.setViewCount(newCount);
            teachingMaterialMapper.updateById(material);
        }
    }
}
