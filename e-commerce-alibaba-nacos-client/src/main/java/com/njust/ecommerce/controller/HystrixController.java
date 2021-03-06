package com.njust.ecommerce.controller;

import com.alibaba.fastjson.JSON;
import com.njust.ecommerce.service.NacosClientService;
import com.njust.ecommerce.service.hystrix.CacheHystrixCommand;
import com.njust.ecommerce.service.hystrix.CacheHystrixCommandAnnotation;
import com.njust.ecommerce.service.hystrix.NacosClientHystrixCommand;
import com.njust.ecommerce.service.hystrix.UseHystrixCommandAnnotation;
import com.njust.ecommerce.service.hystrix.request_merge.NacosClientCollapseCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rx.Observable;

import java.util.List;
import java.util.concurrent.Future;

/**
 * <h1>Hystrix Controller</h1>
 * */
@Slf4j
@RestController
@RequestMapping("/hystrix")
public class HystrixController {

   private final UseHystrixCommandAnnotation hystrixCommandAnnotation;

   private final CacheHystrixCommandAnnotation cacheHystrixCommandAnnotation;
   private final NacosClientService nacosClientService;

    public HystrixController(UseHystrixCommandAnnotation hystrixCommandAnnotation,
                             NacosClientService nacosClientService,
                             CacheHystrixCommandAnnotation cacheHystrixCommandAnnotation) {
        this.hystrixCommandAnnotation = hystrixCommandAnnotation;
        this.nacosClientService = nacosClientService;
        this.cacheHystrixCommandAnnotation = cacheHystrixCommandAnnotation;
    }

    @GetMapping("/hystrix-command-annotation")
    public List<ServiceInstance> getNacosClientInfoUseAnnotation(
            @RequestParam String serviceId) {
        log.info("request nacos client info use annotation: [{}], [{}]",
                serviceId, Thread.currentThread().getName());
        return hystrixCommandAnnotation.getNacosClientInfo(serviceId);
    }

    @GetMapping("/simple-hystrix-command")
    public List<ServiceInstance> getServiceInstanceByServiceId(
            @RequestParam String serviceId) throws Exception {

        // ???????????????
        List<ServiceInstance> serviceInstances01 = new NacosClientHystrixCommand(
                nacosClientService, serviceId
        ).execute();    // ????????????
        log.info("use execute to get service instances: [{}], [{}]",
                JSON.toJSONString(serviceInstances01), Thread.currentThread().getName());

        // ???????????????
        List<ServiceInstance> serviceInstances02;
        Future<List<ServiceInstance>> future = new NacosClientHystrixCommand(
                nacosClientService, serviceId
        ).queue();      // ???????????????
        // ??????????????????????????????, ??????????????????????????????
        serviceInstances02 = future.get();
        log.info("use queue to get service instances: [{}], [{}]",
                JSON.toJSONString(serviceInstances02), Thread.currentThread().getName());

        // ???????????????
        Observable<List<ServiceInstance>> observable = new NacosClientHystrixCommand(
                nacosClientService, serviceId
        ).observe();        // ???????????????
        List<ServiceInstance> serviceInstances03 = observable.toBlocking().single();
        log.info("use observe to get service instances: [{}], [{}]",
                JSON.toJSONString(serviceInstances03), Thread.currentThread().getName());

        // ???????????????
        Observable<List<ServiceInstance>> toObservable = new NacosClientHystrixCommand(
                nacosClientService, serviceId
        ).toObservable();        // ?????????????????????
        List<ServiceInstance> serviceInstances04 = toObservable.toBlocking().single();
        log.info("use toObservable to get service instances: [{}], [{}]",
                JSON.toJSONString(serviceInstances04), Thread.currentThread().getName());

        // execute = queue + get
        return serviceInstances01;
    }

    @GetMapping("/cache-hystrix-command")
    public void cacheHystrixCommand(@RequestParam String serviceId) {

        // ???????????? Command, ??????????????????
        CacheHystrixCommand command1 = new CacheHystrixCommand(
                nacosClientService, serviceId
        );
        CacheHystrixCommand command2 = new CacheHystrixCommand(
                nacosClientService, serviceId
        );

        List<ServiceInstance> result01 = command1.execute();
        List<ServiceInstance> result02 = command2.execute();
        log.info("result01, result02: [{}], [{}]",
                JSON.toJSONString(result01), JSON.toJSONString(result02));

        // ????????????
        CacheHystrixCommand.flushRequestCache(serviceId);

        // ???????????? Command, ??????????????????
        CacheHystrixCommand command3 = new CacheHystrixCommand(
                nacosClientService, serviceId
        );
        CacheHystrixCommand command4 = new CacheHystrixCommand(
                nacosClientService, serviceId
        );

        List<ServiceInstance> result03 = command3.execute();
        List<ServiceInstance> result04 = command4.execute();
        log.info("result03, result04: [{}], [{}]",
                JSON.toJSONString(result03), JSON.toJSONString(result04));
    }

    @GetMapping("/cache-annotation-01")
    public List<ServiceInstance> useCacheByAnnotation01(@RequestParam String serviceId) {

        log.info("use cache by annotation01(controller) to get nacos client info: [{}]",
                serviceId);

        List<ServiceInstance> result01 =
                cacheHystrixCommandAnnotation.useCacheByAnnotation01(serviceId);
        List<ServiceInstance> result02 =
                cacheHystrixCommandAnnotation.useCacheByAnnotation01(serviceId);

        // ???????????????
        cacheHystrixCommandAnnotation.flushCacheByAnnotation01(serviceId);

        List<ServiceInstance> result03 =
                cacheHystrixCommandAnnotation.useCacheByAnnotation01(serviceId);
        // ????????????????????????
        return cacheHystrixCommandAnnotation.useCacheByAnnotation01(serviceId);
    }

    @GetMapping("/cache-annotation-02")
    public List<ServiceInstance> useCacheByAnnotation02(@RequestParam String serviceId) {

        log.info("use cache by annotation02(controller) to get nacos client info: [{}]",
                serviceId);

        List<ServiceInstance> result01 =
                cacheHystrixCommandAnnotation.useCacheByAnnotation02(serviceId);
        List<ServiceInstance> result02 =
                cacheHystrixCommandAnnotation.useCacheByAnnotation02(serviceId);

        // ???????????????
        cacheHystrixCommandAnnotation.flushCacheByAnnotation02(serviceId);

        List<ServiceInstance> result03 =
                cacheHystrixCommandAnnotation.useCacheByAnnotation02(serviceId);
        // ????????????????????????
        return cacheHystrixCommandAnnotation.useCacheByAnnotation02(serviceId);
    }

    @GetMapping("/cache-annotation-03")
    public List<ServiceInstance> useCacheByAnnotation03(@RequestParam String serviceId) {

        log.info("use cache by annotation03(controller) to get nacos client info: [{}]",
                serviceId);

        List<ServiceInstance> result01 =
                cacheHystrixCommandAnnotation.useCacheByAnnotation03(serviceId);
        List<ServiceInstance> result02 =
                cacheHystrixCommandAnnotation.useCacheByAnnotation03(serviceId);

        // ???????????????
        cacheHystrixCommandAnnotation.flushCacheByAnnotation03(serviceId);

        List<ServiceInstance> result03 =
                cacheHystrixCommandAnnotation.useCacheByAnnotation03(serviceId);
        // ????????????????????????
        return cacheHystrixCommandAnnotation.useCacheByAnnotation03(serviceId);
    }

    /**
     * <h2>??????????????????????????????</h2>
     * */
    @GetMapping("/request-merge")
    public void requestMerge() throws Exception {

        // ???????????????????????????
        NacosClientCollapseCommand collapseCommand01 = new NacosClientCollapseCommand(
                nacosClientService, "e-commerce-nacos-client1");
        NacosClientCollapseCommand collapseCommand02 = new NacosClientCollapseCommand(
                nacosClientService, "e-commerce-nacos-client2");
        NacosClientCollapseCommand collapseCommand03 = new NacosClientCollapseCommand(
                nacosClientService, "e-commerce-nacos-client3");

        Future<List<ServiceInstance>> future01 = collapseCommand01.queue();
        Future<List<ServiceInstance>> future02 = collapseCommand02.queue();
        Future<List<ServiceInstance>> future03 = collapseCommand03.queue();

        future01.get();
        future02.get();
        future03.get();

        Thread.sleep(2000);

        // ???????????????????????????, ???????????????????????????
        NacosClientCollapseCommand collapseCommand04 = new NacosClientCollapseCommand(
                nacosClientService, "e-commerce-nacos-client4");
        Future<List<ServiceInstance>> future04 = collapseCommand04.queue();
        future04.get();
    }

    @GetMapping("/request-merge-annotation")
    public void requestMergeAnnotation() throws Exception {
        Future<List<ServiceInstance>> future01 = nacosClientService.findNacosClientInfo(
                "e-commerce-nacos-client01"
        );
        Future<List<ServiceInstance>> future02 = nacosClientService.findNacosClientInfo(
                "e-commerce-nacos-client02"
        );
        Future<List<ServiceInstance>> future03 = nacosClientService.findNacosClientInfo(
                "e-commerce-nacos-client03"
        );
        future01.get();
        future02.get();
        future03.get();
        Thread.sleep(2000);

        Future<List<ServiceInstance>> future04 = nacosClientService.findNacosClientInfo(
                "e-commerce-nacos-client04"
        );

        future04.get();
    }
}
