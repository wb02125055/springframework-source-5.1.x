package com.wb.spring.designpattern.singleton;

import java.io.Serializable;

/**
 * Created by wangbin33 on 2020/1/1.
 * 测试序列化破坏单例模式.
 *
 * 注意：该种方式在序列化时会破坏单例
 */
public class SeriableSingleton implements Serializable {

	public static final SeriableSingleton INSTANCE = new SeriableSingleton();
	private SeriableSingleton() {}

	public static SeriableSingleton getInstance() {
		return INSTANCE;
	}
}
