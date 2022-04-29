package com.njust.ecommerce.service.communication;

import com.alibaba.fastjson.JSON;
import com.njust.ecommerce.constant.CommonConstant;
import com.njust.ecommerce.vo.JwtToken;
import com.njust.ecommerce.vo.UsernameAndPassword;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class UseRestTemplateService {

    private final LoadBalancerClient loadBalanceClient;

    public UseRestTemplateService(LoadBalancerClient loadBalanceClient) {
        this.loadBalanceClient = loadBalanceClient;
    }

    /**
     * 从授权服务中心获取 JwtToken
     * */
    public JwtToken getTokenFromAuthorityService(UsernameAndPassword usernameAndPassword) {
        String requestUrl = "http://127.0.0.1:8000/ecommerce-authority-center/authority/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new RestTemplate().postForObject(
                requestUrl,
                new HttpEntity<>(JSON.toJSONString(usernameAndPassword), headers),
                JwtToken.class
        );
    }


    /**
     * 从授权服务中心获取 JwtToken 且带有负载均衡
     * */
    public JwtToken getTokenFromAuthorityServiceWithLoadBalancer(UsernameAndPassword usernameAndPassword) {
        //从 nacos client center获取授权中心微服务实例
        ServiceInstance serviceInstance = loadBalanceClient.choose(
                CommonConstant.AUTHORITY_CENTER_SERVICE_ID
        );

        log.info("Nacos client info: [{}], [{}], [{}]",
                serviceInstance.getServiceId(), serviceInstance.getInstanceId(),
                JSON.toJSONString(serviceInstance.getMetadata())
        );

        String requestUrl = String.format(
                "http://%s:%s/ecommerce-authority-center/authority/token",
                serviceInstance.getHost(),
                serviceInstance.getPort()
        );

        log.info("login request url and body: [{}], [{}]", requestUrl,
                JSON.toJSONString(usernameAndPassword));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new RestTemplate().postForObject(
                requestUrl,
                new HttpEntity<>(JSON.toJSONString(usernameAndPassword), headers),
                JwtToken.class
        );
    }

}
