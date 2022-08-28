package com.wb.spring.replace_method;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * created by IDEA
 *
 * @author wangbing
 * @date 2022.08.2022/8/28.20:49
 */
public class TestMain {
	public static void main(String[] args) {
		ApplicationContext acx = new ClassPathXmlApplicationContext("replace_method/beans.xml");
		TestReplaceMethod replaceMethod = (TestReplaceMethod) acx.getBean("testReplaceMethod");
		replaceMethod.replaceMe();
	}
}
