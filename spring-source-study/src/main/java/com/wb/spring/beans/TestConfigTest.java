package com.wb.spring.beans;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by wangbin33 on 2020/1/31.
 */
public class TestConfigTest {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext acx =
				new AnnotationConfigApplicationContext(TestConfig.class);


		Object student1 = acx.getBean("student");
		Object student2 = acx.getBean("student");


		System.out.println(student1);
		System.out.println(student2);


//		String displayName = acx.getDisplayName();
//		String id = acx.getId();
//		System.out.println("displayName:" + displayName);
//		System.out.println("id: " + id);
		acx.close();
	}
}
