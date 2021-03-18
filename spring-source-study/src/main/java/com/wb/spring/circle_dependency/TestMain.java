package com.wb.spring.circle_dependency;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Description: 循环依赖
 *
 * @author wangbin33
 * @date 2020/12/25 9:22
 */
public class TestMain {

	public static void main(String[] args) {
		String path = "classpath:circle_dependency/spring.xml";

		ApplicationContext acx = new ClassPathXmlApplicationContext(path);

		ClassA classA = (ClassA) acx.getBean("classA");
		classA.print();
	}
}
