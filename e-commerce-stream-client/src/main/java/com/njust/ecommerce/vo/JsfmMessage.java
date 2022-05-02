package com.njust.ecommerce.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息传递对象 springcloud stream + kafka/rocketmq
 * */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsfmMessage {

    private Integer id;
    private String projectName;
    private String org;
    private String author;
    private String version;

    /**
     * 返回一个默认的消息
     * */
    public static JsfmMessage defaultMessage() {
        return new JsfmMessage(
                1,
                "e-commerce-stream-client",
                "njust",
                "jsfm",
                "1.0"
        );
    }

}
