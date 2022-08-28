package com.wb.spring.multipleBeanName;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * created by IDEA
 *
 * @author wangbing
 * @date 2022.08.2022/8/28.17:20
 */
public class TestMain {
	public static void main(String[] args) {
		ApplicationContext acx = new ClassPathXmlApplicationContext("multipleBeanName/beans.xml");
		// 测试bean的name指定多个名称
		Object testBean1 = acx.getBean("testBean1");
		Object testBean2 = acx.getBean("testBean2");
		Object testBean3 = acx.getBean("testBean3");
		System.out.println(testBean1);
		System.out.println(testBean2);
		System.out.println(testBean3);

		// 测试别名，testBean5其实是testBean100的别名
		Object testBean5 = acx.getBean("testBean5");
		System.out.println(testBean5);
	}
}
