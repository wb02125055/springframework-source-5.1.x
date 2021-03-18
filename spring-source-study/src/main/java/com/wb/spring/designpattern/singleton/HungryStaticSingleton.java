package com.wb.spring.designpattern.singleton;

/**
 * Created by wangbin33 on 2019/12/31.
 * 使用静态代码块完成
 */
public class HungryStaticSingleton {
	private static final HungryStaticSingleton singletonInstance;

	static {
		singletonInstance = new HungryStaticSingleton();
	}

	private HungryStaticSingleton() {}

	public static HungryStaticSingleton getSingletonInstance() {
		return singletonInstance;
	}
}
