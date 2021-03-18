package com.wb.mybatis;

import org.apache.ibatis.annotations.Select;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/5/10 23:13
 */
public class MyInvocationHandler implements InvocationHandler {
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		System.out.println("query from db.");

		Select annotation = method.getAnnotation(Select.class);
		if (!StringUtils.isEmpty(annotation)) {
			System.out.println(annotation.value()[0]);
		}
		return null;
	}
}
