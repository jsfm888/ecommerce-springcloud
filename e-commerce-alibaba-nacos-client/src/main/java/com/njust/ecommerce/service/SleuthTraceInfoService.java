package com.njust.ecommerce.service;

import brave.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SleuthTraceInfoService {

    /** 跟踪对象 */
    private final Tracer tracer;

    public SleuthTraceInfoService(Tracer tracer) {
        this.tracer = tracer;
    }

    /** 打印当前的跟踪信息到日志中 */
    public void logCurrentTraceInfo() {
        log.info("Sleuth trace id: [{}]", tracer.currentSpan().context().traceId());
        log.info("Sleuth span id: [{}]", tracer.currentSpan().context().spanId());
    }


}
