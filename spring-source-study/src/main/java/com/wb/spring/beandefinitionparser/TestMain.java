package com.wb.spring.beandefinitionparser;

import com.wb.spring.beandefinitionparser.domain.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by wangbin33 on 2020/1/9.
 */
public class TestMain {
	public static void main(String[] args) {

		 testContext();

//		List<String> ipList = getAddresses();
//		System.out.println(ipList);
	}

	private static void testContext() {
		String configPath = "mytag.xml";
		ApplicationContext acx = new ClassPathXmlApplicationContext(configPath);
		User user = (User) acx.getBean("wb");
		System.out.println(user);
	}
}
