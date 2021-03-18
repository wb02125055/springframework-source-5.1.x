package com.wb.spring.designpattern.singleton;

/**
 * Created by wangbin33 on 2019/12/31.
 */
public class ExecutorThread implements Runnable {
	@Override
	public void run() {
//		LazySimpleSingleton singleton = LazySimpleSingleton.getInstance();
		LazyInnerClassSingleton singleton = LazyInnerClassSingleton.getInstance();
		System.out.println(Thread.currentThread().getName() + ":" + singleton);
	}
}
