package com.njust.ecommerce.controller;

import com.njust.ecommerce.service.SleuthTraceInfoService;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 打印跟踪信息
 * */
@Slf4j
@RestController
@RequestMapping("/sleuth")
public class SleuthTraceInfoController {

    private final SleuthTraceInfoService traceInfoService;

    public SleuthTraceInfoController(SleuthTraceInfoService traceInfoService) {
        this.traceInfoService = traceInfoService;
    }

    @GetMapping("/trace-info")
    public void logCurrentTraceInfo() {
        traceInfoService.logCurrentTraceInfo();
    }
}
