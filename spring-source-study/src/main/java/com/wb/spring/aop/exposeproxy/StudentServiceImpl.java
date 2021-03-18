package com.wb.spring.aop.exposeproxy;

import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;

/**
 * Created by wangbin33 on 2020/2/15.
 *
 * 如果不加expose-proxy属性的话，在m1执行的时候会被切面拦截，但是m1中调用了m2方法，
 * 在m2方法执行的时候不会被切面拦截。因为m1调用m2时，是通过this调用的。this表示的是当前的StudentServiceImpl对象.
 * 而并不是代理对象.
 *
 * 如果要使其执行切面中的方法：需要在开启切面的地方设置expose-proxy属性为true
 * 并且在调用的地方使用((StudentService) AopContext.currentProxy()).m2()， 去调用。表示使用了当前aop上下文中的代理对象
 */
@Service("studentService")
public class StudentServiceImpl implements StudentService {
	@Override
	public void m1() {
		System.out.println("m1 invoke...");
//		 m2();  // 调用m2的时候，m2之前前不会被切面拦截.
		((StudentService) AopContext.currentProxy()).m2(); // 执行m2之前会被切面拦截.
	}
	@Override
	public void m2() {
		System.out.println("m2 invoke...");
	}
}
