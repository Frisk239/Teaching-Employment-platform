package com.teaching.employment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.TechStackTemplate;

import java.util.List;

public interface TechStackTemplateService extends IService<TechStackTemplate> {

    /**
     * 获取默认模板
     */
    TechStackTemplate getDefaultTemplate();

    /**
     * 根据岗位类型获取模板
     */
    List<TechStackTemplate> getTemplatesByPositionType(String positionType);

    /**
     * 获取所有模板
     */
    List<TechStackTemplate> getAllTemplates();
}
