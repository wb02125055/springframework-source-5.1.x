package com.wb.spring.aop.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestMain {
	public static void main(String[] args) {
		ApplicationContext acx = new AnnotationConfigApplicationContext(AopConfig1.class);

		MyClass myClass = acx.getBean(MyClass.class);

		Pen pen = acx.getBean(Pen.class);

		myClass.write(pen);
	}
}
