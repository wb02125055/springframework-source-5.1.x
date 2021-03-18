package com.wb.spring.propertyvalue;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by wangbin33 on 2020/2/28.
 */
public class TestMain1 {
	public static void main(String[] args) {
		ApplicationContext acx = new ClassPathXmlApplicationContext("module-all.xml");

		Object obj = acx.getBean("user");

		System.out.println(obj);
	}
}
