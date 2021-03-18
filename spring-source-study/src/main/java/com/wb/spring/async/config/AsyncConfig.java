package com.wb.spring.async.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Description: 为目标bean生成增强代理.
 *	最后执行方法的时候是由：AsyncExecutionInterceptor的invoke方法执行.
 *
 * @author wangbin33
 * @date 2020/8/10 9:35
 */
@Configuration
@ComponentScan(basePackages = "com.wb.spring.async")
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		// 核心线程数
		executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
		// 最大线程数
		executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 5);
		// 队列容量。
		// 		队列容量如果大于0，初始化的时候使用LinkedBlockingQueue类型的队列
		//		队列容量如果小于等于0，初始化时使用SynchronousQueue类型的队列
		executor.setQueueCapacity(Runtime.getRuntime().availableProcessors() * 2);
		// 线程池名称
		executor.setThreadNamePrefix("test-executor-");
		// 初始化线程池
		executor.initialize();
		return executor;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		// 异步任务异常处理器.
		return new SimpleAsyncUncaughtExceptionHandler();
	}
}
