package com.njust.ecommerce.service;

import com.njust.ecommerce.account.BalanceInfo;

public interface IBalanceService {

    /** 获取当前用户余额信息 */
    BalanceInfo getCurrentBalanceInfo();

    /**
     * 扣减用户余额
     * @param balanceInfo 要扣减的余额信息
     * @return
     */
    BalanceInfo deductBalance(BalanceInfo balanceInfo);

}
