package com.wb.spring.aop.enhancer_test;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;

/**
 * @author wangbin33
 * @date Created in 11:03 2019/10/13
 * 运行一下的测试类将会出现死循环
 * 原因：cglib中锁调用的任何原代理的方法，都会被重新被代理到invoke方法中.
 */
public class AopEnhancerTest1 {

	public void test() {
		System.out.println("test run...");
	}


	public static void main(String[] args) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(AopEnhancerTest1.class);
		enhancer.setCallback(new InvocationHandler() {
			@Override
			public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
				System.out.println("before...");
				Object result = method.invoke(o, objects);
				System.out.println("after");
				return result;
			}
		});
		AopEnhancerTest1 test1 = (AopEnhancerTest1) enhancer.create();
		test1.test();
	}
}
