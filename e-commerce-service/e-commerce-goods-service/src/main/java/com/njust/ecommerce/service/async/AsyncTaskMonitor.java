package com.njust.ecommerce.service.async;

import com.njust.ecommerce.constant.AsyncTaskStatusEnum;
import com.njust.ecommerce.vo.AsyncTaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Aspect
@Component
public class AsyncTaskMonitor {

    private final AsyncTaskManager taskManager;

    public AsyncTaskMonitor(AsyncTaskManager taskManager) {
        this.taskManager = taskManager;
    }

    /**
     * <h2>异步任务执行的环绕切面</h2>
     * 环绕切面让我们可以在方法执行之前和执行之后做一些 "额外" 的操作
     * */
    @Around("execution(* com.njust.ecommerce.service.async.AsyncServiceImpl.*(..))")
    public Object taskHandler(ProceedingJoinPoint joinPoint) {
        //获取taskId
        String taskId = joinPoint.getArgs()[1].toString();

        //获取异步任务信息
        AsyncTaskInfo taskInfo = taskManager.getTaskInfo(taskId);
        log.info("AsyncTaskMonitor is monitoring async task: [{}]", taskId);

        taskInfo.setStatus(AsyncTaskStatusEnum.RUNNING);
        taskManager.setTaskInfo(taskInfo);


        Object result;
        AsyncTaskStatusEnum status;
        try {
            //执行异步任务
            result = joinPoint.proceed();
            status = AsyncTaskStatusEnum.SUCCESS;
        } catch (Throwable ex) {
            //异步任务执行失败 
            result = null;
            status = AsyncTaskStatusEnum.FAILED;
            log.error("AsyncTaskManage: async task [{}] is failed, Error info: [{}]",
                        taskId, ex.getMessage(), ex);
        }

        //设置异步任务其他的信息
        taskInfo.setStatus(status);
        taskInfo.setEndTime(new Date());
        taskInfo.setTotalTime(String.valueOf(
                taskInfo.getEndTime().getTime() - taskInfo.getStartTime().getTime()
        ));

        taskManager.setTaskInfo(taskInfo);
        return result;
    }
}
