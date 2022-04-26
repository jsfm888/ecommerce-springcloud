package com.njust.ecommerce.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户地址信息
 * */
@ApiModel(description = "用户地址信息")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAddress {

    @ApiModelProperty(value = "用户姓名")
    private String username;

    @ApiModelProperty(value = "电话")
    private String phone;

    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "详细地址")
    private String addressDetail;
}
