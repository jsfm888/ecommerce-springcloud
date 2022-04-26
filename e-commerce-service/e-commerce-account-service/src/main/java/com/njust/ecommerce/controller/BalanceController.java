package com.njust.ecommerce.controller;


import com.njust.ecommerce.account.BalanceInfo;
import com.njust.ecommerce.service.IBalanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"用户余额服务"})
@Slf4j
@RestController
@RequestMapping("/balance")
public class BalanceController {

    private final IBalanceService balanceService;

    public BalanceController(IBalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @ApiOperation(value = "获取", notes = "获取当前用户余额信息", httpMethod = "GET")
    @GetMapping("current-balance")
    public BalanceInfo getCurrentBalanceInfo() {
        return balanceService.getCurrentBalanceInfo();
    }

    @ApiOperation(value = "扣减", notes = "扣减用户余额信息", httpMethod = "PUT")
    @PutMapping("/deduct-balance")
    public BalanceInfo deductBalance(@RequestBody BalanceInfo balanceInfo) {
        return balanceService.deductBalance(balanceInfo);
    }
}
