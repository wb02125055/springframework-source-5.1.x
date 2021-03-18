package com.wb.spring.beans;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * Created by wangbin33 on 2020/2/9.
 */
public class FactoryBeanTest {
	public static void main(String[] args) {
		AbstractApplicationContext acx = new AnnotationConfigApplicationContext(FactoryBeanConfig.class);

		Object object = acx.getBean("consumerBean");
		System.out.println(object);

		Object factoryBean = acx.getBean("&consumerBean");
		System.out.println(factoryBean);
	}
}
