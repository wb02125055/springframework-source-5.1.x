package com.wb.spring.designpattern.singleton;

import java.io.*;
import java.lang.reflect.Constructor;

/**
 * Created by wangbin33 on 2020/1/1.
 *
 * 测试序列化或者反射对枚举格式的单例类是否能够破坏
 *
 */
public class EnumSingletonTest {
	public static void main(String[] args) {

		testReflectEnumSingleton2();
	}
	/**
	 * 测试通过反射破坏类的单例
	 *
	 * 运行之后提示：java.lang.IllegalArgumentException: Cannot reflectively create enum objects
	 *
	 * 表示不允许通过反射去创建枚举类型的实例
	 *
	 * 查看Constructor类的newInstance方法，可以发现，在创建实例时，有个强制判断，如果修饰符是Modifier.ENUM枚举类型，
	 * 将会直接抛出异常.
	 *
	 * if ((clazz.getModifiers() & Modifier.ENUM) != 0) {
	 		throw new IllegalArgumentException("Cannot reflectively create enum objects");
	 * }
	 */
	public static void testReflectEnumSingleton2() {
		try {
			Class clazz = EnumSingleton.class;
			Constructor c = clazz.getDeclaredConstructor(String.class, int.class);
			c.setAccessible(true);
			EnumSingleton instance = (EnumSingleton) c.newInstance("王兵", 21);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	/**
	 * 测试通过反射破坏类的单例
	 *
	 * 运行之后提示：java.lang.NoSuchMethodException，没有找到无参构造方法
	 * 查看Enum类的源码，发现只有一个protected类型的带参构造方法.
	 *
	 */
	public static void testReflectEnumSingleton1() {
		try {
			Class clazz = EnumSingleton.class;
			Constructor c = clazz.getDeclaredConstructor();
			c.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 测试通过序列化破坏单例
	 * 结果：不能破坏
	 * 原因：枚举属于一种饿汉式的单例设计模式，通过反编译可以看到在类的静态代码块中会对实例进行初始化赋值
	 *       序列化时可以查看ObjectInputStream类的readObject方法看到，case TC_ENUM: 通过readEnum方法查找枚举实例
	 *       而查找过程是根据类名称和类对象找到一个唯一的枚举对象。所以枚举类对象不可能被类加载器加载多次.
	 */
	public static void testSerialEnumSingleton() {
		EnumSingleton s1;
		EnumSingleton s2 = EnumSingleton.getInstance();
		s2.setData(new Object());

		try {
			FileOutputStream fos = new FileOutputStream("D:/enumSerialData.obj");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(s2);
			oos.flush();
			oos.close();

			FileInputStream fis = new FileInputStream("D:/enumSerialData.obj");
			ObjectInputStream ois = new ObjectInputStream(fis);
			s1 = (EnumSingleton) ois.readObject();

			System.out.println(s1.getData());
			System.out.println(s2.getData());
			System.out.println(s1.getData() == s2.getData());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
