package com.njust.ecommerce.service;

import com.alibaba.fastjson.JSON;
import com.njust.ecommerce.account.BalanceInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class BalanceServiceTest extends BaseTest {

    @Autowired
    private IBalanceService balanceService;

    @Test
    public void testGetCurrentBalanceInfo() {
        log.info("get current balance info: [{}]", JSON.toJSONString(
                balanceService.getCurrentBalanceInfo()
        ));
    }

    @Test
    public void testDeductInfo() {
        BalanceInfo balanceInfo = new BalanceInfo();
        balanceInfo.setBalance(100L);
        balanceInfo.setUserId(10L);

        log.info("test deduct balance: [{}]", balanceService.deductBalance(balanceInfo));
    }

}
