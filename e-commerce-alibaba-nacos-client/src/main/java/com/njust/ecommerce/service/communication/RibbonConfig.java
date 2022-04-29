package com.njust.ecommerce.service.communication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * <h1>使用 Ribbon 之前的配置, 增强 RestTemplate</h1>
 * */
@Component
@Slf4j
public class RibbonConfig {

    @Bean
    @LoadBalanced
    private RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
