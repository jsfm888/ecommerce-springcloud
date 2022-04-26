package com.njust.ecommerce.service.impl;

import com.njust.ecommerce.account.BalanceInfo;
import com.njust.ecommerce.dao.EcommerceBalanceDao;
import com.njust.ecommerce.entity.EcommerceBalance;
import com.njust.ecommerce.filter.AccessContext;
import com.njust.ecommerce.service.IBalanceService;
import com.njust.ecommerce.vo.LoginUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class BalanceServiceImpl implements IBalanceService {

    private final EcommerceBalanceDao balanceDao;

    public BalanceServiceImpl(EcommerceBalanceDao balanceDao) {
        this.balanceDao = balanceDao;
    }

    @Override
    public BalanceInfo getCurrentBalanceInfo() {
        LoginUserInfo loginUserInfo = AccessContext.getLoginUserInfo();
        BalanceInfo balanceInfo = new BalanceInfo(
                loginUserInfo.getId(), 0L
        );

        EcommerceBalance ecommerceBalance = balanceDao.findByUserId(loginUserInfo.getId());
        if(null != ecommerceBalance) {
            balanceInfo.setBalance(ecommerceBalance.getBalance());
        } else {
            //如果没有余额记录，新建用户账户余额，账户余额默认为0
            EcommerceBalance newBalance = new EcommerceBalance();
            newBalance.setBalance(0L);
            newBalance.setUserId(loginUserInfo.getId());
            log.info("init user balance info: [{}]", balanceDao.save(newBalance).getId());
        }
        return balanceInfo;
    }

    @Override
    public BalanceInfo deductBalance(BalanceInfo balanceInfo) {
        LoginUserInfo loginUserInfo = AccessContext.getLoginUserInfo();
        EcommerceBalance ecommerceBalance = balanceDao.findByUserId(loginUserInfo.getId());

        //扣减余额的基本原则 当前余额大于等于要扣减的余额
        if(null == ecommerceBalance
                || ecommerceBalance.getBalance() - balanceInfo.getBalance() < 0) {
            //余额不足
            throw new RuntimeException("user balance is not enough!");
        }
        Long sourceBalance = ecommerceBalance.getBalance();
        ecommerceBalance.setBalance(sourceBalance - balanceInfo.getBalance());
        //打印扣件后余额记录id 扣减前余额 要扣减的余额
        log.info("deduct balance: [{}], [{}], [{}]",
                balanceDao.save(ecommerceBalance).getId(), sourceBalance, balanceInfo.getBalance());

        return new BalanceInfo(loginUserInfo.getId(), ecommerceBalance.getBalance());
    }
}
