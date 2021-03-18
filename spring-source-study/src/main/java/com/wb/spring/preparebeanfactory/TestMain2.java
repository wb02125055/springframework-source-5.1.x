package com.wb.spring.preparebeanfactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by wangbin33 on 2020/1/26.
 */
public class TestMain2 {
	public static void main(String[] args) {
		String configPath = "test2.xml";
		ApplicationContext acx = new ClassPathXmlApplicationContext(configPath);
		Object obj = acx.getBean("impl");
		System.out.println(obj);
	}
}
