package com.wb.spring.finishBeanFactoryInitialization;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by wangbin33 on 2020/1/28.
 *
 * 循环依赖测试.
 */
public class DependOnTest {

	public static void main(String[] args) {

		ApplicationContext acx = new ClassPathXmlApplicationContext("test5.xml");
		Object classA = acx.getBean("classA");
		System.out.println(classA);
	}
}
