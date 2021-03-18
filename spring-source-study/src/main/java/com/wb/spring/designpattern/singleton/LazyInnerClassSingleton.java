package com.wb.spring.designpattern.singleton;

/**
 * Created by wangbin33 on 2019/12/31.
 * 使用内部类
 */
public class LazyInnerClassSingleton {
	private LazyInnerClassSingleton() {
		if (null != LazyHolder.LAZY) {
			throw new RuntimeException("不允许创建多个实例!");
		}
	}

	public static final LazyInnerClassSingleton getInstance() {
		return LazyHolder.LAZY;
	}

	private static class LazyHolder {
		private static final LazyInnerClassSingleton LAZY = new LazyInnerClassSingleton();
	}
}
