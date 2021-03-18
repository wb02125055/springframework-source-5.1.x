package com.wb.spring.factorymethod;

import org.springframework.context.ApplicationContext;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/11/20 22:24
 */
public class TestMain {
	public static void main(String[] args) {
		ApplicationContext acx = new ClassPathXmlApplicationContext("classpath:factorymethod/factoryMethod.xml");
		OldDog dog1 = acx.getBean("dog1", OldDog.class);
		System.out.println(dog1);

		OldDog dog2 = acx.getBean("dog2", OldDog.class);
		System.out.println(dog2);
	}
}
