package com.wb.spring.designpattern.singleton;

import java.lang.reflect.Constructor;

/**
 * Created by wangbin33 on 2019/12/31.
 */
public class LazyInnerClassSingletonTest {
	public static void main(String[] args) {
		try {
			Class<?> clazz = LazyInnerClassSingleton.class;

			Constructor c = clazz.getDeclaredConstructor(null);
			c.setAccessible(true);

			Object o1 = c.newInstance();
			Object o2 = c.newInstance();

			System.out.println("0");

			System.out.println(o1);
			System.out.println(o2);

			System.out.println(o1 == o2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
