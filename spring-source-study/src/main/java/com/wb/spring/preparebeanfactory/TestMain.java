package com.wb.spring.preparebeanfactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by wangbin33 on 2020/1/23.
 *
 * https://www.jianshu.com/p/3c7e0608ff1f
 */
public class TestMain {
	public static void main(String[] args) {

		ApplicationContext acx = new ClassPathXmlApplicationContext("test1.xml");
		Object list = acx.getBean("listHolder");
		System.out.println(list);
	}
}
