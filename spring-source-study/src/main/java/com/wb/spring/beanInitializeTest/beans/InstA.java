package com.wb.spring.beanInitializeTest.beans;

import org.springframework.stereotype.Component;

/**
 * @author wangbin33
 * @date Created in 22:24 2019/11/30
 */
@Component
public class InstA {
	public InstA() {
		System.out.println("InstA的构造函数被执行！注解注入...");
	}
}
