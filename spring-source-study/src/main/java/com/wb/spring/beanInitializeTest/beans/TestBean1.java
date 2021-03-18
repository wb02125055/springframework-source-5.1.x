package com.wb.spring.beanInitializeTest.beans;

import org.springframework.beans.factory.InitializingBean;

/**
 * @author wangbin33
 * @date Created in 11:02 2019/11/30
 */
public class TestBean1 implements InitializingBean {
	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("设置属性.");
	}
}
