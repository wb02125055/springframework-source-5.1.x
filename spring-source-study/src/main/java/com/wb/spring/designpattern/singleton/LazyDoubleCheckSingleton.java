package com.wb.spring.designpattern.singleton;

/**
 * Created by wangbin33 on 2019/12/31.
 * 双次监测
 */
public class LazyDoubleCheckSingleton {

	/**
	 * 使用volatile是保证在创建对象时不会发生指令重排序
	 */
	private static volatile LazyDoubleCheckSingleton instance;

	private LazyDoubleCheckSingleton() {}

	public static LazyDoubleCheckSingleton getInstance() {
		if (null == instance) {
			synchronized (LazyDoubleCheckSingleton.class) {
				if (null == instance) {
					instance = new LazyDoubleCheckSingleton();
					/**
					 * 1.分配内存给这个对象
					 * 2.初始化对象
					 * 3.设置instance指向刚分配的内存地址
					 */
				}
			}
		}
		return instance;
	}
}
