package com.wb.spring.designpattern.singleton;

/**
 * Created by wangbin33 on 2020/1/1.
 *
 * 使用枚举类的单例设计模式，该种设计模式是实际使用中的推荐
 * 使用序列化无法破坏该种设计模式中的类唯一性
 */
public enum EnumSingleton {
	INSTANCE;

	private Object data;
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}

	public static EnumSingleton getInstance() {
		return INSTANCE;
	}
}
