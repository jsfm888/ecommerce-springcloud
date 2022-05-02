package com.njust.ecommerce.stream;

import com.alibaba.fastjson.JSON;
import com.njust.ecommerce.vo.JsfmMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;

/**
 * 使用默认的消息通信信道发送消息
 * */
@Slf4j
@EnableBinding(Source.class)
public class DefaultSendService {
    private final Source source;

    public DefaultSendService(Source source) {
        this.source = source;
    }

    /**
     * 使用默认的输出信道发送消息
     * */
    public void sendMessage(JsfmMessage message) {
        String _message = JSON.toJSONString(message);
        log.info("in defaultSendMessage send message: [{}]", _message);

        //Spring Messaging, 统一消息的编程模型, 是 Stream 组件的重要组成部分之一
        source.output().send(MessageBuilder.withPayload(_message).build());
    }
}
