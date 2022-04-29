package com.njust.ecommerce.service.async;

import com.njust.ecommerce.goods.GoodsInfo;

import java.util.List;

/**
 * 异步服务接口定义
 * */
public interface IAsyncService {

    /**
     * 异步保存商品信息
     * */
    void asyncImportGoods(List<GoodsInfo> goodsInfos, String taskId);
}
