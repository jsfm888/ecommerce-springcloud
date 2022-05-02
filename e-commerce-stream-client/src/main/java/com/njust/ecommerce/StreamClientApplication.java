package com.njust.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 基于 springcloud stream 构建消息驱动微服务
 * */
@EnableDiscoveryClient
@SpringBootApplication
public class StreamClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(StreamClientApplication.class, args);
    }
}
