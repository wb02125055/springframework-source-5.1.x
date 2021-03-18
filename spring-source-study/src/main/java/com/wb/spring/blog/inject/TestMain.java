package com.wb.spring.blog.inject;

import com.wb.spring.blog.inject.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Description: Spring的注入方式测试
 *
 * @author wangbin33
 * @date 2021/2/14 19:54
 */
public class TestMain {

//	private static String configFileName = "inject/setter_inject.xml";
	private static String configFileName = "inject/inject.xml";

	private static ApplicationContext acx = new ClassPathXmlApplicationContext(configFileName);

	public static void main(String[] args) {
		testInject();
	}
	public static void testInject() {
		UserService userService = (UserService) acx.getBean("userService");
		userService.save();
	}
}
