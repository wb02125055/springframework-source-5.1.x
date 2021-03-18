package com.wb.spring.proxy.dynamic;

import com.wb.spring.proxy.common.DynamicDataSourceEntry;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wangbin33 on 2020/2/8.
 */
public class OrderServiceDynamicProxy implements InvocationHandler {
	private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
	private Object target;

	public Object getInstance(Object target) {
		this.target = target;
		Class<?> clazz = target.getClass();
		return Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), this);
	}

	/**
	 * @param proxy
	 * @param method
	 * @param args 表示的是被代理方法的入参.
	 * @return
	 * @throws Throwable
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		before(args[0]);
		Object obj = method.invoke(this.target, args);
		after();
		return obj;
	}
	public void before(Object target) {
		try {
			System.out.println("Proxy before method.");
			Long time = (Long) target.getClass().getMethod("getCreateTime").invoke(target);
			Integer dbRouter = Integer.valueOf(yearFormat.format(new Date(time)));
			System.out.println("静态代理类自动分配到【DB_" + dbRouter + "】数据源处理数据");
			DynamicDataSourceEntry.set(dbRouter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void after() {
		System.out.println("Proxy after method.");
	}
}
