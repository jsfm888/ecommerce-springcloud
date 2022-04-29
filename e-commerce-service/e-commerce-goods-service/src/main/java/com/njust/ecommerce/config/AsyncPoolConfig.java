package com.njust.ecommerce.config;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自定义异步任务线程池、异步任务异常捕获处理器
 * */
@Slf4j
@EnableAsync //开启 spring 的异步任务支持
@Configuration
public class AsyncPoolConfig implements AsyncConfigurer {

    /**
     * 将自定义的线程池注入到Spring 容器中
     * */
    @Bean
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(20);
        taskExecutor.setQueueCapacity(20);
        taskExecutor.setThreadNamePrefix("Jsfm-Async-");
        taskExecutor.setKeepAliveSeconds(60);
        //所有任务结束后再关闭线程池
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(60);
        //设置线程池的拒绝策略
        taskExecutor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        //初始化线程池 初始化 core 线程
        taskExecutor.initialize();
        return taskExecutor;
    }

    /**
     * 指定系统中的异常任务在捕获到异常时的处理器
     * */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncExceptionHandler();
    }

    @SuppressWarnings("all")
    class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

        @Override
        public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
            throwable.printStackTrace();
            log.error("Async error: [{}], Method: [{}], Param: [{}]",
                    throwable.getMessage(), method.getName(), JSON.toJSONString(objects));

            //TODO: 发送短信或邮件做进一步的报警处理
        }
    }
}
