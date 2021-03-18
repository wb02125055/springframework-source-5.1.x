package com.wb.spring.designpattern.singleton;

import java.io.Serializable;

/**
 * Created by wangbin33 on 2020/1/1.
 *
 * 通过增加readResolve方法来防止序列化时的单例模式被破坏
 *
 * 该单例类在进行序列化和反序列时也不会被破坏类的单例行为.
 */
public class SeriableSingleton1 implements Serializable {

	public static final SeriableSingleton1 INSTANCE = new SeriableSingleton1();
	/**
	 * 构造器私有化
	 */
	private SeriableSingleton1() {}

	/**
	 * 通过共有的静态方法获取单实例.
	 * @return 单例实例类
	 */
	public static SeriableSingleton1 getInstance() {
		return INSTANCE;
	}

	/**
	 * 增加该方法是为了在进行序列化时不破坏单例模式.
	 * @return 单例对象.
	 */
	private Object readResolve() {
		return INSTANCE;
	}
}
