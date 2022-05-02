package com.njust.ecommerce.service.communication.hystrix;

import com.alibaba.fastjson.JSON;
import com.njust.ecommerce.service.communication.AuthorityFeignClient;
import com.njust.ecommerce.vo.JwtToken;
import com.njust.ecommerce.vo.UsernameAndPassword;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthorityFeignClientFallback implements AuthorityFeignClient {

    @Override
    public JwtToken getTokenByFeign(UsernameAndPassword usernameAndPassword) {
        log.info("authority feign client get token by feign request error" +
                " (Hystrix Fallback): [{}]", JSON.toJSONString(usernameAndPassword));
        return new JwtToken("jsfm");
    }
}
