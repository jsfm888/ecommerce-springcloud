package com.njust.ecommerce.stream.jsfm;

import com.alibaba.fastjson.JSON;
import com.njust.ecommerce.vo.JsfmMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * 使用自定义的消息通道接收消息
 * */
@Slf4j
@EnableBinding(JsfmSink.class)
public class JsfmReceiveService {

    /**
     * 使用自定义的输入信道接收消息
     * */
    @StreamListener(JsfmSink.INPUT)
    public void receiveMessage(@Payload  Object payload) {
        log.info("in jsfmReceiveService consume message start");
        JsfmMessage message = JSON.parseObject(
                payload.toString(),
                JsfmMessage.class
        );
        //消费消息
        log.info("in jsfmReceiveService consume message success: [{}]",
                JSON.toJSONString(message));
    }


}
