package com.njust.ecommerce.converter;

import com.njust.ecommerce.constant.GoodsStatus;

import javax.persistence.AttributeConverter;

/**
 * 商品枚举状态属性转换器
 * */
public class GoodsStatusConverter implements AttributeConverter<GoodsStatus, Integer> {

    /**
     * 转换成可以存入数据表的基本类型
     * */
    @Override
    public Integer convertToDatabaseColumn(GoodsStatus goodsStatus) {
        return goodsStatus.getStatus();
    }

    /**
     * 还原数据表中的字段值到 java 枚举类型
     * */
    @Override
    public GoodsStatus convertToEntityAttribute(Integer status) {
        return GoodsStatus.of(status);
    }
}
