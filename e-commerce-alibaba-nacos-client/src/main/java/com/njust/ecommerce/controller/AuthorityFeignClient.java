package com.njust.ecommerce.controller;

import com.njust.ecommerce.vo.JwtToken;
import com.njust.ecommerce.vo.UsernameAndPassword;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 与Authority 服务通信的Feign client接口定义
 * */
@FeignClient(contextId = "AuthorityFeignClient", value = "e-commerce-authority-center")
public interface AuthorityFeignClient {

    @RequestMapping(value = "/ecommerce-authority-center/authority/token",
                    method = RequestMethod.POST,
                    consumes = "application/json", produces = "application/json")
    JwtToken getTokenByFeign(@RequestBody UsernameAndPassword usernameAndPassword);
}
