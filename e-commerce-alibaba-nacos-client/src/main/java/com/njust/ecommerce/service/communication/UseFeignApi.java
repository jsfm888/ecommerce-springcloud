package com.njust.ecommerce.service.communication;

import com.njust.ecommerce.controller.AuthorityFeignClient;
import com.njust.ecommerce.vo.JwtToken;
import com.njust.ecommerce.vo.UsernameAndPassword;
import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Random;

/**
 * 使用 feign 的原生api，openfeign = feign + ribbon
 * */
@Slf4j
@Service
public class UseFeignApi {
    private final DiscoveryClient discoveryClient;

    public UseFeignApi(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }


    /**
     * 使用 feign 的原生 api 调用远程服务
     * */
    public JwtToken thinkingInFeign(UsernameAndPassword usernameAndPassword) {
        //通过反射获取 AuthorityFeignClient feign接口中的服务名称
        String serviceId = null;
        Annotation[] annotations = AuthorityFeignClient.class.getAnnotations();
        for (Annotation annotation : annotations) {
            if(annotation.annotationType().equals(FeignClient.class)) {
                serviceId = ((FeignClient) annotation).value();
                log.info("get service id from AuthorityFeignClient: [{}]", serviceId);
                break;
            }
        }

        if(null == serviceId) {
            throw new RuntimeException("can not find service id");
        }

        //通过 serviceId 去注册中心找实例
        List<ServiceInstance> targetInstances = discoveryClient.getInstances(serviceId);
        if(CollectionUtils.isEmpty(targetInstances)) {
            throw new RuntimeException("can not get target instance from serviceId: " +
                    serviceId);
        }

        //随机找到一个实例  负载均衡
        ServiceInstance randomInstance = targetInstances.get(
                new Random().nextInt(targetInstances.size())
        );
        log.info("choose service instance: [{}], [{}], [{}]", serviceId,
                randomInstance.getHost(), randomInstance.getPort());

        //Feign 客户端初始化， 必须配置encoder decoder contract
        AuthorityFeignClient feignClient = Feign.builder()
                .encoder(new GsonEncoder())
                .decoder(new GsonDecoder())
                .contract(new SpringMvcContract())
                .target(
                        AuthorityFeignClient.class,
                        String.format("http://%s:%s",
                                randomInstance.getHost(), randomInstance.getPort())
                );
        return feignClient.getTokenByFeign(usernameAndPassword);
    }
}
