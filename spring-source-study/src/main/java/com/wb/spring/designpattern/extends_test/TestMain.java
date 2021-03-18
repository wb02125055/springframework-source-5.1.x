package com.wb.spring.designpattern.extends_test;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by wangbin33 on 2020/3/4.
 */
public class TestMain {
	public static void main(String[] args) {
		Executor pool = new ThreadPoolExecutor(4, 4,
				60L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

		for (int i = 0 ; i < 4; i++) {
			pool.execute(() -> executeTop());
		}
	}
	private static void executeTop() {
		System.out.println("Thread名称为：" + Thread.currentThread().getName());
	}
}
