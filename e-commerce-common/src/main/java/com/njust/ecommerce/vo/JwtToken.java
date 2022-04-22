package com.njust.ecommerce.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 授权中心鉴权之后返回给客户端的 token 对象
 * 单独将一个token字段做成一个对象，是为了日后方便扩展
 * */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtToken {

    private String token;

}
