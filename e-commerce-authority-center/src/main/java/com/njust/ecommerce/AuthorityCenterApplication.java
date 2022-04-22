package com.njust.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing //允许 Jpa 自动审计
@SpringBootApplication
@EnableDiscoveryClient
public class AuthorityCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthorityCenterApplication.class, args);
    }
}
