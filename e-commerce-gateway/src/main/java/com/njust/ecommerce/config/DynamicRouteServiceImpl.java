package com.njust.ecommerce.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 事件推送 Aware: 动态更新路由网关 Service
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class DynamicRouteServiceImpl implements ApplicationEventPublisherAware {

    /** 写路由定义 */
    private final RouteDefinitionWriter routeDefinitionWriter;
    /** 获取路由定义 */
    private final RouteDefinitionLocator routeDefinitionLocator;
    /** 时间发布 */
    public ApplicationEventPublisher publisher;

    public DynamicRouteServiceImpl(RouteDefinitionWriter routeDefinitionWriter,
                                   RouteDefinitionLocator routeDefinitionLocator) {
        this.routeDefinitionWriter = routeDefinitionWriter;
        this.routeDefinitionLocator= routeDefinitionLocator;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    /**
     * 增加路由定义
     * */
    public String addRouteDefinition(RouteDefinition definition) {
        log.info("gateway add route: [{}]", definition);
        //保存路由配置并发布
        routeDefinitionWriter.save(Mono.just(definition)).subscribe();
        //发布事件通知给 Gateway, 同步新增的路由定义
        this.publisher.publishEvent(new RefreshRoutesEvent(this));

        return "success";
    }

    /**
     * 根据路由id, 删除路由定义
     * */
    private String deleteById(String id) {
        try {
            log.info("gateway delete route by id: [{}]", id);
            routeDefinitionWriter.delete(Mono.just(id)).subscribe();
            this.publisher.publishEvent(new RefreshRoutesEvent(this));
            return "delete success";
        } catch (Exception ex) {
            log.error("gateway delete route fail: [{}]", ex.getMessage(), ex);
            return "delete fail";
        }
    }

    /**
     * 更新路由
     * 更新的实现策略比较简单: 删除 + 新增 = 更新
     * */
    private String updateByRouteDefinition(RouteDefinition definition) {

        try {
            log.info("gateway update route: [{}]", definition);
            this.routeDefinitionWriter.delete(Mono.just(definition.getId()));
        } catch (Exception ex) {
            return "update fail, not find route routeId: " + definition.getId();
        }

        try {
            this.routeDefinitionWriter.save(Mono.just(definition)).subscribe();
            this.publisher.publishEvent(new RefreshRoutesEvent(this));
            return "success";
        } catch (Exception ex) {
            return "update route fail";
        }
    }

    /**
     * 更新路由
     * */
    public String updateList(List<RouteDefinition> definitions) {
        log.info("gateway update route: [{}]", definitions);
        //先获取当前 gateway 存储的路由定义
        List<RouteDefinition> routeDefinitions
                = routeDefinitionLocator.getRouteDefinitions().buffer().blockFirst();
        if(!CollectionUtils.isEmpty(routeDefinitions)) {
            routeDefinitions.forEach(rd -> {
                log.info("delete route definition: [{}]", rd);
                deleteById(rd.getId());
            });
        }

        //更新的路由定义同步到 gateway 中
        definitions.forEach(definition -> updateByRouteDefinition(definition));

        return "update list success";
    }
}
