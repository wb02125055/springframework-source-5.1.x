package com.wb.spring.beans;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/6/7 12:34
 */
public class MyApplicationContext extends AnnotationConfigApplicationContext {
	public MyApplicationContext() {
		super();
	}
//	@Override
//	protected void initPropertySources() {
//		getEnvironment().setRequiredProperties("key");
//	}
}
