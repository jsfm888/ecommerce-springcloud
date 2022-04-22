package com.njust.ecommerce.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录用户信息
 * */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserInfo {

    private Long id;
    private String username;
}
