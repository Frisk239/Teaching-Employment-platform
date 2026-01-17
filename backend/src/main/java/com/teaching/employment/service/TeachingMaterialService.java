package com.teaching.employment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.TeachingMaterial;

/**
 * 教学资料Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-17
 */
public interface TeachingMaterialService extends IService<TeachingMaterial> {

    /**
     * 分页查询教学资料
     */
    IPage<TeachingMaterial> getTeachingMaterialPage(Integer current, Integer size, Long teacherId, Long courseId,
                                                     String materialType, String category, String keyword);

    /**
     * 增加下载次数
     */
    void incrementDownloadCount(Long id);

    /**
     * 增加查看次数
     */
    void incrementViewCount(Long id);
}
