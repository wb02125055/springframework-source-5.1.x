package com.wb.spring.invoke.order;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/10/9 15:16
 */
public class TestMain {
	public static void main(String[] args) {
		ApplicationContext acx = new AnnotationConfigApplicationContext(MyConfig.class);

		Object sessionBean = acx.getBean("sessionBean");

		System.out.println(sessionBean);
	}
}
