package com.wb.spring.beaninit.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wangbin33
 * @date 2020/4/26 20:33
 */
@Component("a")
public class A {
	public A() {}
//	public A(B b) {
//		this.b = b;
//	}
	@Autowired
	private B b;
//	public A() {
//		System.out.println("A init...");
//	}
}
