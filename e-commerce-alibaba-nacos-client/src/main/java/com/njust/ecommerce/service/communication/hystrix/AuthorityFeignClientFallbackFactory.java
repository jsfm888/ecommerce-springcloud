package com.njust.ecommerce.service.communication.hystrix;

import com.njust.ecommerce.service.communication.AuthorityFeignClient;
import com.njust.ecommerce.vo.JwtToken;
import com.njust.ecommerce.vo.UsernameAndPassword;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthorityFeignClientFallbackFactory
        implements FallbackFactory<AuthorityFeignClient> {

    @Override
    public AuthorityFeignClient create(Throwable throwable) {
        log.info("authority feign client get token by feign request error " +
                "(Hystrix Fallback Factory): [{}]", throwable.getMessage(), throwable);

        return new AuthorityFeignClient() {
            @Override
            public JwtToken getTokenByFeign(UsernameAndPassword usernameAndPassword) {
                return new JwtToken("jsfm factory fallback");
            }
        };
    }
}
