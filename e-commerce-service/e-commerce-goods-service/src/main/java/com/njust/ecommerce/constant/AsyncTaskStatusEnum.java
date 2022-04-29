package com.njust.ecommerce.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 异步任务状态枚举
 * */
@Getter
@AllArgsConstructor
public enum AsyncTaskStatusEnum {

    STARTED(0, "已经启动"),
    RUNNING(1, "正在运行"),
    SUCCESS(2, "执行成功"),
    FAILED(3, "执行失败"),
    ;

    /** 执行状态编码 */
    private final int status;

    /** 执行状态信息 */
    private final String stateInfo;
}
