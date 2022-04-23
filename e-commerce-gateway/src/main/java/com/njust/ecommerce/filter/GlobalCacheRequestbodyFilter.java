package com.njust.ecommerce.filter;

import com.njust.ecommerce.constant.GatewayConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 缓存请求body的全局过滤器
 * */
@Slf4j
@Component
@SuppressWarnings("all")
public class GlobalCacheRequestbodyFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        boolean isLoginOrRegister =
                exchange.getRequest().getURI().getPath().contains(GatewayConstant.LOGIN_URI)
                || exchange.getRequest().getURI().getPath().contains(GatewayConstant.REGISTER_URI);
        if(null == exchange.getRequest().getHeaders().getContentType()
                || !isLoginOrRegister) {
            return chain.filter(exchange);
        }
        //是正常的登录或注册请求
        //DataBufferUtils.join 拿到请求中的数据 --> databuffer
        return DataBufferUtils.join(exchange.getRequest().getBody())
                .flatMap(dataBuffer -> {
                    //确保缓冲区不被释放，必须要 DataBufferUtils.retain
                    DataBufferUtils.retain(dataBuffer);
                    //创建数据源 得到当前数据的副本
                    Flux<DataBuffer> cachedFlux = Flux.defer(() ->
                            Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount())));
                    //重新包装 ServerHttpRequest, 重写 getBody 方法，能够返回请求数据
                    ServerHttpRequest mutatedRequest =
                            new ServerHttpRequestDecorator(exchange.getRequest()) {
                                @Override
                                public Flux<DataBuffer> getBody() {
                                    return cachedFlux;
                                }
                            };
                    //将包装后的 ServerHttpRequest 向下继续传递
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                });

    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 1;
    }
}
