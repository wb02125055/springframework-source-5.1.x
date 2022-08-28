package com.wb.spring.lookup_method;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * created by IDEA
 *
 * @author wangbing
 * @date 2022.08.2022/8/28.20:11
 */
public class TestMain {
	public static void main(String[] args) {
		ApplicationContext acx = new ClassPathXmlApplicationContext("lookup_method/beans.xml");
		LookUpBeanTest lookUpBeanTest = (LookUpBeanTest) acx.getBean("lookUpBeanTest");
		lookUpBeanTest.printName();
	}
}
