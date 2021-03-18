package com.wb.spring.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by wangbin33 on 2020/2/20.
 */
public class ThreadPoolExecutorDemo1 {

	private ExecutorService executorService;

	private int threadNumber = 5;

	public ThreadPoolExecutorDemo1() {
		System.out.println("threadNumber: " + threadNumber);
		executorService = new ThreadPoolExecutor(threadNumber, threadNumber, 0, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<>());
	}

	public static void main(String[] args) {
		ThreadPoolExecutorDemo1 demo = new ThreadPoolExecutorDemo1();
		for (int index = 0 ; index < 10; index ++) {
			demo.executorService.submit(() -> {
				for (int i = 0 ; i < 10; i++) {
					System.out.println("i=" + i);
				}
			});
		}
		demo.executorService.shutdown();
	}
}
