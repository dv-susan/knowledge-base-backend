package com.knowledge.base.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步任务线程池配置
 *
 * <p>所有 {@code CompletableFuture.runAsync/supplyAsync} 必须传入此线程池，
 * 禁止使用默认的 ForkJoinPool.commonPool()。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class AsyncTaskConfig {

    @Bean(name = "asyncTaskExecutor")
    public ThreadPoolTaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int cores = Runtime.getRuntime().availableProcessors();
        // 核心线程数 = CPU核数，最大线程数 = 核心线程数 × 2
        executor.setCorePoolSize(cores);
        executor.setMaxPoolSize(cores * 2);
        // 队列容量
        executor.setQueueCapacity(500);
        // 线程名前缀
        executor.setThreadNamePrefix("async-task-");
        // 等待任务完成后关闭
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 等待超时时间
        executor.setAwaitTerminationSeconds(60);
        // 拒绝策略：由调用线程执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        log.info("异步任务线程池初始化完成：corePoolSize={}, maxPoolSize={}, queueCapacity={}",
                cores, cores * 2, 500);
        return executor;
    }
}
