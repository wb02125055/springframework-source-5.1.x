package com.wb.spring.instantiationaware;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/11/20 8:54
 */
public class BeforeInstanceInterceptor implements MethodInterceptor {
	@Override
	public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
		System.out.println("调用之前，方法名称：" + method.getName());
		Object obj = methodProxy.invokeSuper(o, objects);
		System.out.println("调用之后，方法名称：" + method.getName());
		return obj;
	}
}
