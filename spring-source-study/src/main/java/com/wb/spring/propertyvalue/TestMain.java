package com.wb.spring.propertyvalue;

import com.wb.spring.propertyvalue.config.ConfigOfPropertyValues;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by wangbin33 on 2020/2/24.
 */
public class TestMain {
	public static void main(String[] args) {
		ApplicationContext acx = new AnnotationConfigApplicationContext(ConfigOfPropertyValues.class);

//		ApplicationContext acx = new AnnotationConfigApplicationContext("com.wb.spring.propertyvalue");

		Object person = acx.getBean("person");

		// 从当前环境中获取properties中的值.
//		Environment environment = acx.getEnvironment();
//		String nickName = environment.getProperty("person.nick");
//		System.out.println("nick: " + nickName);

		System.out.println(person);
	}
}
