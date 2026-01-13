package com.teaching.employment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.employment.entity.Offer;
import org.apache.ibatis.annotations.Mapper;

/**
 * Offer Mapper接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Mapper
public interface OfferMapper extends BaseMapper<Offer> {
}
