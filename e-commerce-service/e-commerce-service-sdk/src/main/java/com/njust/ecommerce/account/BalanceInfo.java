package com.njust.ecommerce.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户账户余额信息
 * */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "用户账户余额信息")
public class BalanceInfo {

    @ApiModelProperty(value = "用户主键 id")
    private Long userId;
    @ApiModelProperty(value = "用户账户余额")
    private Long balance;
}
