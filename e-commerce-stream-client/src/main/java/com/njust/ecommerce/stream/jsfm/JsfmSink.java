package com.njust.ecommerce.stream.jsfm;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * 自定义输出信道
 * */
public interface JsfmSink {
    String INPUT = "jsfmInput";

    @Input(JsfmSink.INPUT)
    SubscribableChannel jsfmInput();
}
