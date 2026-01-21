package com.teaching.employment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.TechStackTemplate;
import com.teaching.employment.mapper.TechStackTemplateMapper;
import com.teaching.employment.service.TechStackTemplateService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TechStackTemplateServiceImpl extends ServiceImpl<TechStackTemplateMapper, TechStackTemplate>
        implements TechStackTemplateService {

    @Override
    public TechStackTemplate getDefaultTemplate() {
        LambdaQueryWrapper<TechStackTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TechStackTemplate::getIsDefault, 1);
        wrapper.last("LIMIT 1");
        return this.getOne(wrapper);
    }

    @Override
    public List<TechStackTemplate> getTemplatesByPositionType(String positionType) {
        LambdaQueryWrapper<TechStackTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TechStackTemplate::getPositionType, positionType);
        return this.list(wrapper);
    }

    @Override
    public List<TechStackTemplate> getAllTemplates() {
        return this.list();
    }
}
