package com.wb.spring.thread;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author wangbin33
 * @date Created in 18:48 2019/11/22
 */
public class ThreadPoolDemo1 {

	private static ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(12);

	public static void main(String[] args) throws Exception {
//		long start = System.currentTimeMillis();
//		System.out.println(start);
		testScheduleAtFixedRate();
//		System.out.println(System.currentTimeMillis() - start);

//		testScheduleWithFixedDelay();

//		int result = tableSizeFor(18);
//		System.out.println(result);
	}

	/**
	 * 以固定的速率执行任务。每次执行任务的开始时间都是固定的。与任务执行时长无关
	 */
	private static void testScheduleAtFixedRate() {
		pool.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				System.out.println("fixedRate task run..." + Thread.currentThread().getName());
				sleepWait(4);
			}
		}, 500l, 1000l, TimeUnit.MILLISECONDS);
	}
	/**
	 * 该方式每次执行任务的开始时间都是前一次任务的执行时长加上固定的时间间隔。如果任务执行时间比较长，下次任务将会延迟执行.
	 */
	private static void testScheduleWithFixedDelay() {
		pool.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				System.out.println("fixedDelay task run..." + Thread.currentThread().getName());
				sleepWait(5);
			}
		}, 0l, 1000l, TimeUnit.MILLISECONDS);
	}

	/**
	 * 等待指定的秒数
	 * @param seconds
	 */
	private static void sleepWait(long seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final int MAXIMUM_CAPACITY = 4000000;

	static final int tableSizeFor(int cap) {
		int n = cap - 1;
		n |= n >>> 1;
		n |= n >>> 2;
		n |= n >>> 4;
		n |= n >>> 8;
		n |= n >>> 16;
		return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
	}
}
