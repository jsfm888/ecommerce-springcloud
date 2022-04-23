package com.njust.ecommerce.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class HeaderTokenGatewayFilter implements GatewayFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //从HTTP Header 中找到 token:njust 键值对
        String name = exchange.getRequest().getHeaders().getFirst("token");
        if("njust".equals(name)) {
            return chain.filter(exchange);
        }
        //标记此次请求没有权限，并结束此次请求
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        //precedence: 优先
        return HIGHEST_PRECEDENCE + 2;
    }
}
