package com.wb.spring.proxy.dynamic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by wangbin33 on 2020/2/8.
 */
public class JDKMeipo implements InvocationHandler {
	// 被代理的对象
	private Object target;
	public Object getInstance(Object target) throws Exception {
		this.target = target;
		Class<?> clazz = target.getClass();
		return Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), this);
	}
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		before();
		// 执行被代理的对象的方法
		Object obj = method.invoke(this.target, args);
		after();
		return obj;
	}
	private void before() {
		System.out.println("--->我是媒婆：我要给你找对象，现在已经确认你的需求");
		System.out.println("--->开始物色");
	}
	private void after() {
		System.out.println("--->如果合适的话，就准备办事儿");
	}
}
