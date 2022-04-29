package com.njust.ecommerce.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 商品状态枚举类
 * */
@Getter
@AllArgsConstructor
public enum GoodsStatus {

    ONLINE(101, "上线"),
    OFFLINE(102, "下线"),
    STOCK_OUT(103, "缺货"),
    ;

    private final Integer status;
    private final String description;

    /**
     * 根据 code 找到 status
     * */
    public static GoodsStatus of(Integer status) {
        Objects.requireNonNull(status);

        return Stream.of(values())
                .filter(bean -> bean.status.equals(status))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(status + " is not exist"));
    }
}
