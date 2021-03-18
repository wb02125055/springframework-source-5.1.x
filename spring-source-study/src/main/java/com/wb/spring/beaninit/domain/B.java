package com.wb.spring.beaninit.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author wangbin33
 * @date 2020/4/26 20:33
 */
@Component("b")
public class B {
	public B() {}
	public B(A a) {
		this.a = a;
	}
	@Autowired
	private A a;
//	public B() {
//		System.out.println("B init...");
//	}
}
