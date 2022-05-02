package com.njust.ecommerce.stream;

import com.alibaba.fastjson.JSON;
import com.njust.ecommerce.vo.JsfmMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

/**
 * 使用默认的消息通信信道接收消息
 * */
@Slf4j
@EnableBinding(Sink.class)
public class DefaultReceiveService {

    /**
     * 使用默认的输入信道接收消息
     * */
    @StreamListener(Sink.INPUT)
    public void receiveMessage(Object payload) {
        log.info("in defaultReceiveService consume message start");

        JsfmMessage message = JSON.parseObject(
                payload.toString(),
                JsfmMessage.class
        );

        //消费消息
        log.info("in defaultReceiveService consume message success: [{}]",
                JSON.toJSONString(message));
    }
}
