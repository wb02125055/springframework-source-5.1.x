package com.wb.spring.designpattern.singleton;

/**
 * Created by wangbin33 on 2019/12/31.
 * 饿汉式的第一种方式
 */
public class HungrySingleton {
	private static final HungrySingleton singletonInstance = new HungrySingleton();

	private HungrySingleton() {}

	public static HungrySingleton getSingletonInstance() {
		return singletonInstance;
	}
}
