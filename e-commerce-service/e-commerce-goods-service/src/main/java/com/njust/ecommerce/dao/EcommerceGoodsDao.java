package com.njust.ecommerce.dao;

import com.njust.ecommerce.constant.BrandCategory;
import com.njust.ecommerce.constant.GoodsCategory;
import com.njust.ecommerce.entity.EcommerceGoods;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

/**
 * EcommerceGoodsDao接口定义
 * */
public interface EcommerceGoodsDao extends PagingAndSortingRepository<EcommerceGoods, Long> {

    /**
     * 根据查询条件查询结果，并限制返回结果  FIrst1代表 limit 1
     * select * from t_ecommerce_goods where goods_category = ? and brand_category = ?
     * and goods_name = ? limit 1;
     * */
    Optional<EcommerceGoods> findFirst1ByGoodsCategoryAndBrandCategoryAndGoodsName(
            GoodsCategory goodsCategory, BrandCategory brandCategory, String goodsName
    );
}
