package com.njust.ecommerce.filter;

import com.alibaba.fastjson.JSON;
import com.njust.ecommerce.constant.CommonConstant;
import com.njust.ecommerce.constant.GatewayConstant;
import com.njust.ecommerce.util.TokenParseUtil;
import com.njust.ecommerce.vo.JwtToken;
import com.njust.ecommerce.vo.LoginUserInfo;
import com.njust.ecommerce.vo.UsernameAndPassword;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static com.njust.ecommerce.constant.CommonConstant.JWT_USER_INFO_KEY;

/** 全局登录鉴权过滤器 */
@Component
@Slf4j
public class GlobalLoginOrRegisterFilter implements GlobalFilter, Ordered {

    //从注册中心获取服务实例信息
    private final LoadBalancerClient loadBalancerClient;
    private final RestTemplate restTemplate;

    public GlobalLoginOrRegisterFilter(LoadBalancerClient loadBalancerClient, RestTemplate restTemplate) {
        this.loadBalancerClient = loadBalancerClient;
        this.restTemplate = restTemplate;
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //如果是登录
        if(request.getURI().getPath().contains(GatewayConstant.LOGIN_URI)) {
            String token = getTokenFromAuthorityCenter(exchange.getRequest(),
                                    GatewayConstant.AUTHORITY_CENTER_TOKEN_URL_FORMAT);
            response.getHeaders().add(JWT_USER_INFO_KEY,
                                    null == token ? "null" : token);
            response.setStatusCode(HttpStatus.OK);
            return response.setComplete();
        }
        if(request.getURI().getPath().contains(GatewayConstant.REGISTER_URI)) {
            String token = getTokenFromAuthorityCenter(exchange.getRequest(),
                                    GatewayConstant.AUTHORITY_CENTER_REGISTER_URL_FORMAT);
            response.getHeaders().add(JWT_USER_INFO_KEY,
                    null == token ? "null" : token);
            response.setStatusCode(HttpStatus.OK);
            return response.setComplete();
        }
        // 访问其他的服务, 则鉴权, 校验是否能够从 Token 中解析出用户信息
        HttpHeaders headers = request.getHeaders();
        String token = headers.getFirst(JWT_USER_INFO_KEY);
        LoginUserInfo loginUserInfo = null;

        try {
            loginUserInfo = TokenParseUtil.parseUserInfoFormToken(token);
        } catch (Exception ex) {
            log.error("parse user info from token error: [{}]", ex.getMessage(), ex);
        }

        //获取不到登录信息
        if(null == loginUserInfo) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //解析通过，放行
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 2;
    }

    /**
     * 从授权中心获取 token
     * */
    private String getTokenFromAuthorityCenter(ServerHttpRequest request, String uriFormat) {
        ServiceInstance instance = loadBalancerClient.choose(
                                    CommonConstant.AUTHORITY_CENTER_SERVICE_ID);
        log.info("Nacos client info: [{}], [{}], [{}]",
                    instance.getServiceId(), instance.getInstanceId(),
                        JSON.toJSONString(instance.getMetadata()));
        //获取token的请求路径
        String requestUrl = String.format(uriFormat, instance.getHost(), instance.getPort());
        //获取请求体参数
        UsernameAndPassword requestBody =
                    JSON.parseObject(parseBodyFromRequest(request), UsernameAndPassword.class);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        //使用restTemplate向授权中心发起获取token的post请求
        JwtToken jwtToken = restTemplate.postForObject(requestUrl,
                new HttpEntity<>(requestBody, httpHeaders), JwtToken.class);

        if(null != jwtToken) {
            return jwtToken.getToken();
        }

        return null;
    }

    /**
     * 从 post 请求中获取请求数据
     * */
    private String parseBodyFromRequest(ServerHttpRequest request) {
        //获取请求体
        Flux<DataBuffer> body = request.getBody();
        AtomicReference<String> bodyRef = new AtomicReference<>();

        //订阅缓冲区并去消费请求体中的数据
        body.subscribe(buffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            //一定要使用 DataBufferUtils.release 释放，否则会出现内存泄漏
            DataBufferUtils.release(buffer);
            bodyRef.set(charBuffer.toString());
        });

        //获取 request body
        return bodyRef.get();
    }
}
