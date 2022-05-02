package com.njust.ecommerce.stream.jsfm;

import com.alibaba.fastjson.JSON;
import com.njust.ecommerce.vo.JsfmMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;

/**
 * <h1>使用自定义的通信信道 JsfmSource 实现消息的发送</h1>
 * */
@Slf4j
@EnableBinding(JsfmSource.class)
public class JsfmSendService {

    private final JsfmSource jsfmSource;

    public JsfmSendService(JsfmSource jsfmSource) {
        this.jsfmSource = jsfmSource;
    }

    /**
     * 使用自定义的消息信道发送消息
     * */
    public void sendMessage(JsfmMessage message) {
        String _message = JSON.toJSONString(message);
        log.info("in JsfmSendService send message: [{}]", _message);

        jsfmSource.jsfmOutput().send(MessageBuilder.withPayload(_message).build());
    }
}
