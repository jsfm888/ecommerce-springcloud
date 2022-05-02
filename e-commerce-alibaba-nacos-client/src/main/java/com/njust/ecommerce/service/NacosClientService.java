package com.njust.ecommerce.service;

import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.HystrixObservableCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@Service
@Slf4j
public class NacosClientService {

    private final DiscoveryClient discoveryClient;

    public NacosClientService(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    public List<ServiceInstance> getNacosClientInfo(String serviceId) {

        // test UseHystrixCommandAnnotation 超时
        /*try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        log.info("request nacos client to get service instance info: [{}]", serviceId);
        return discoveryClient.getInstances(serviceId);
    }

    public List<List<ServiceInstance>> getNacosClientInfos(List<String> serviceIds) {
        log.info("get nacos client infos by serviceIds: [{}]", JSON.toJSONString(serviceIds));
        List<List<ServiceInstance>> result = new ArrayList<>(serviceIds.size());
        serviceIds.forEach(serviceId -> {
            result.add(getNacosClientInfo(serviceId));
        });
        return result;
    }

    //使用注解实现 Hystrix 请求合并
    @HystrixCollapser(
            batchMethod = "findNacosClientInfos",
            scope = com.netflix.hystrix.HystrixCollapser.Scope.GLOBAL,
            collapserProperties = {
                    @HystrixProperty(name = "timerDelayInMilliseconds", value = "300")
            }
    )
    public Future<List<ServiceInstance>> findNacosClientInfo(String serviceId) {
        throw new RuntimeException("this method body should not be executed");
    }

    @HystrixCommand
    public List<List<ServiceInstance>> findNacosClientInfos(List<String> serviceIds) {
        return getNacosClientInfos(serviceIds);
    }
}
