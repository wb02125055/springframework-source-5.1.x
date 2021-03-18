package com.wb.mybatis;

import java.lang.reflect.Proxy;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/5/10 23:15
 */
public class MySqlSessionFactory {

	public static Object queryMapper(Class clazz) {
		Class[] cls = new Class[]{clazz};
		return Proxy.newProxyInstance(MySqlSessionFactory.class.getClassLoader(),
				cls,
				new MyInvocationHandler());
	}
}
