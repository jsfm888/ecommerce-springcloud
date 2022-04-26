package com.njust.ecommerce.service;

import com.njust.ecommerce.account.AddressInfo;
import com.njust.ecommerce.common.TableId;

/**
 * 用户地址相关接口服务定义
 * */
public interface IAddressService {

    /** 创建用户地址 */
    TableId createAddressInfo(AddressInfo addressInfo);

    /** 获取当前登录用户的地址信息 */
    AddressInfo getCurrentAddressInfo();

    /** 通过 id 获取用户地址信息  id 是 EcommerceAddress 表的主键*/
    AddressInfo getAddressInfoById(Long id);

    /**
     * <h2>通过 TableId 获取用户地址信息</h2>
     * */
    AddressInfo getAddressInfoByTableId(TableId tableId);

}
