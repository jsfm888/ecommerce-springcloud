package com.njust.ecommerce.controller;

import com.njust.ecommerce.stream.DefaultSendService;
import com.njust.ecommerce.stream.jsfm.JsfmSendService;
import com.njust.ecommerce.vo.JsfmMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/message")
public class MessageController {

    private final DefaultSendService defaultSendService;
    private final JsfmSendService jsfmSendService;

    public MessageController(DefaultSendService defaultSendService, JsfmSendService jsfmSendService) {
        this.defaultSendService = defaultSendService;
        this.jsfmSendService = jsfmSendService;
    }

    /**
     * 默认信道
     * */
    @GetMapping("/default")
    public void defaultSend() {
        defaultSendService.sendMessage(JsfmMessage.defaultMessage());
    }

    /**
     * 自定义信道
     * */
    @GetMapping("/jsfm")
    public void jsfmSend() {
        jsfmSendService.sendMessage(JsfmMessage.defaultMessage());
    }
}
