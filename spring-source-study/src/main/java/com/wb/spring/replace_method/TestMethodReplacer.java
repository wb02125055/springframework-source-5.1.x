package com.wb.spring.replace_method;

import org.springframework.beans.factory.support.MethodReplacer;

import java.lang.reflect.Method;

/**
 * created by IDEA
 *
 * @author wangbing
 * @date 2022.08.2022/8/28.20:53
 */
public class TestMethodReplacer implements MethodReplacer {
	@Override
	public Object reimplement(Object obj, Method method, Object[] args) throws Throwable {
		System.out.println("已经替换了原有的方法...");
		return null;
	}
}
