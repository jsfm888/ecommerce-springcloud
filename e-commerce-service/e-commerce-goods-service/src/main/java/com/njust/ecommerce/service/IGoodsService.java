package com.njust.ecommerce.service;

import com.njust.ecommerce.common.TableId;
import com.njust.ecommerce.goods.DeductGoodsInventory;
import com.njust.ecommerce.goods.GoodsInfo;
import com.njust.ecommerce.goods.SimpleGoodsInfo;
import com.njust.ecommerce.vo.PagedSimpleGoodsInfo;

import java.util.List;

/**
 * 商品微服务相关接口定义
 * */
public interface IGoodsService {

    /**
     * 根据 tableId 获取商品信息
     * */
    List<GoodsInfo> getGoodsInfoByTableId(TableId tableId);

    /**
     * 获取商品分页信息
     * */
    PagedSimpleGoodsInfo getSimpleGoodsInfoByPage(int page);

    /**
     * 根据 table Id 获取简单商品信息
     * */
    List<SimpleGoodsInfo> getSimpleGoodsInfoByTableId(TableId tableId);

    /**
     * 扣减商品库存
     * */
    boolean deductGoodsInventory(List<DeductGoodsInventory> deductGoodsInventories);
}
