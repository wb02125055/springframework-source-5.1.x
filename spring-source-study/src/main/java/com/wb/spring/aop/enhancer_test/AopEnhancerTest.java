package com.wb.spring.aop.enhancer_test;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author wangbin33
 * @date Created in 10:41 2019/10/13
 * 测试cglib功能
 */
public class AopEnhancerTest {

	public void test() {
		System.out.println("test...");
	}

	public static void main(String[] args) {
		Enhancer enhancer = new Enhancer();
		// 设置父类的类型
		enhancer.setSuperclass(AopEnhancerTest.class);
		// 设置代理回调方法.
		enhancer.setCallback(new MethodInterceptor() {
			@Override
			public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
				System.out.println("before...");
				Object result = methodProxy.invokeSuper(obj, args);
				System.out.println("after...");
				return result;
			}
		});
		// 从enhancer中获取增强的代理类，并执行代理类的目标方法.
		AopEnhancerTest aopEnhancerTest = (AopEnhancerTest) enhancer.create();
		aopEnhancerTest.test();
	}
}