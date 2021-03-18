package com.wb.spring.designpattern.singleton;

/**
 * Created by wangbin33 on 2019/12/31.
 * 懒汉式加载，线程不安全
 */
public class LazySimpleSingleton {
	private LazySimpleSingleton() {}
	private static LazySimpleSingleton simpleSingleton = null;
	public static synchronized LazySimpleSingleton getInstance() {
		if (null == simpleSingleton) {
			simpleSingleton = new LazySimpleSingleton();
		}
		return simpleSingleton;
	}
}
