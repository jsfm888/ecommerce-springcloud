package com.njust.ecommerce.stream.jsfm;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * 自定义输出信道
 * */
public interface JsfmSource {

    String OUTPUT = "jsfmOutput";

    /** 输出信道的名称是 jsfmOutput, 需要使用 Stream 绑定器在 yml 文件中声明 */
    @Output(JsfmSource.OUTPUT)
    MessageChannel jsfmOutput();
}
