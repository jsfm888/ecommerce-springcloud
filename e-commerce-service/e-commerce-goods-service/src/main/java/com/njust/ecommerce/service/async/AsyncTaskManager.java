package com.njust.ecommerce.service.async;

import com.njust.ecommerce.constant.AsyncTaskStatusEnum;
import com.njust.ecommerce.goods.GoodsInfo;
import com.njust.ecommerce.vo.AsyncTaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 异步任务管理器  相当于一个代理类
 * */
@Slf4j
@Component
public class AsyncTaskManager {

    /**
     * 异步任务执行信息容器
     * */
    private static Map<String, AsyncTaskInfo> taskContainer = new HashMap<>(16);


    private final IAsyncService asyncService;

    public AsyncTaskManager(IAsyncService asyncService) {
        this.asyncService = asyncService;
    }

    /**
     * 初始化异步任务
     * */
    public AsyncTaskInfo init() {
        AsyncTaskInfo taskInfo = new AsyncTaskInfo();

        taskInfo.setTaskId(UUID.randomUUID().toString());
        taskInfo.setStartTime(new Date());
        taskInfo.setStatus(AsyncTaskStatusEnum.STARTED);
        taskContainer.put(taskInfo.getTaskId(), taskInfo);

        return taskInfo;
    }

    /**
     * 提交异步任务
     * */
    public AsyncTaskInfo submit(List<GoodsInfo> goodsInfos) {
        AsyncTaskInfo taskInfo = init();
        asyncService.asyncImportGoods(goodsInfos, taskInfo.getTaskId());

        return taskInfo;
    }

    /**
     * 设置异步任务执行状态信息
     * */
    public void setTaskInfo(AsyncTaskInfo taskInfo) {
        taskContainer.put(taskInfo.getTaskId(), taskInfo);
    }

    /**
     * 获取异步任务执行状态信息
     * */
    public AsyncTaskInfo getTaskInfo(String taskId) {
        return taskContainer.get(taskId);
    }




}
