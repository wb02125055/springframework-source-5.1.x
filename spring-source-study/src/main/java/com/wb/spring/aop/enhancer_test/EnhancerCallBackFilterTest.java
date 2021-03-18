package com.wb.spring.aop.enhancer_test;

import org.springframework.cglib.proxy.CallbackHelper;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.FixedValue;
import org.springframework.cglib.proxy.NoOp;

import java.lang.reflect.Method;

/**
 * @author wangbin33
 * @date Created in 11:13 2019/10/13
 * 使用callbackFilter来指定需要拦截哪些方法的执行，其他不需要拦截的方法可以直接放行
 */
public class EnhancerCallBackFilterTest {

	public void test() {
		System.out.println("test.");
	}

	public static void main(String[] args) {
		Enhancer enhancer = new Enhancer();
		CallbackHelper callbackHelper = new CallbackHelper(CallbackHelper.class, new Class[0]) {
			@Override
			protected Object getCallback(Method method) {
				if (method.getDeclaringClass() != Object.class &&
						method.getReturnType() != String.class) {
					return new FixedValue() {
						@Override
						public Object loadObject() throws Exception {
							return "Hello cglib.";
						}
					};
				} else {
					return NoOp.INSTANCE;
				}
			}
		};
		enhancer.setSuperclass(EnhancerCallBackFilterTest.class);
		enhancer.setCallbackFilter(callbackHelper);
		enhancer.setCallbacks(callbackHelper.getCallbacks());
		// 执行目标代理类的方法.
		EnhancerCallBackFilterTest test = (EnhancerCallBackFilterTest) enhancer.create();
		test.test();
	}
}
