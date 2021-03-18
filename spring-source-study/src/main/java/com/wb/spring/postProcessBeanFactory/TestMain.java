package com.wb.spring.postProcessBeanFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by wangbin33 on 2020/1/26.
 */
public class TestMain {
	public static void main(String[] args) {
		ApplicationContext acx = new ClassPathXmlApplicationContext("test3.xml");
		Object obj = acx.getBean("myUser");
		System.out.println(obj);
	}
}
