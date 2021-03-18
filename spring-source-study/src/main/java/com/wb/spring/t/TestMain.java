package com.wb.spring.t;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/11/1 10:52
 */
public class TestMain {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext acx = new AnnotationConfigApplicationContext(MyConfig.class);

		Object pigObj1 = acx.getBean("pigFactory");
		Object pigObj2 = acx.getBean("pigFactory");
		Object factory1 = acx.getBean("&&&pigFactory");
		Object factory2 = acx.getBean("&&pigFactory");
		Object factory3 = acx.getBean("&pigFactory");

		System.out.println(pigObj1);
		System.out.println(pigObj2);
		System.out.println("===================");
		System.out.println(factory1);
		System.out.println(factory2);
		System.out.println(factory3);
	}
}
