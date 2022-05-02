package com.njust.ecommerce.service.communication;

import com.njust.ecommerce.service.communication.hystrix.AuthorityFeignClientFallbackFactory;
import com.njust.ecommerce.vo.JwtToken;
import com.njust.ecommerce.vo.UsernameAndPassword;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 与Authority 服务通信的Feign client接口定义
 *
 * openfeign 集成 hystrix 步骤
 * 1. feign.hytrix.enabled: true
 * 2. @FeignClient注解的fallback 和fallbackFactory属性
 *   两者不可以同时使用
 *   fallbackFactory 能够获取到OpenFeign调用抛出的异常
 * */
@FeignClient(
        contextId = "AuthorityFeignClient", value = "e-commerce-authority-center",
        //fallback = AuthorityFeignClientFallback.class
        fallbackFactory = AuthorityFeignClientFallbackFactory.class
)
public interface AuthorityFeignClient {

    @RequestMapping(value = "/ecommerce-authority-center/authority/token",
                    method = RequestMethod.POST,
                    consumes = "application/json", produces = "application/json")
    JwtToken getTokenByFeign(@RequestBody UsernameAndPassword usernameAndPassword);
}
