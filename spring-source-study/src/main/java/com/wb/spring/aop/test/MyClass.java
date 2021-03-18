package com.wb.spring.aop.test;

import org.springframework.stereotype.Component;

@Component
public class MyClass {
	public String write(Pen pen) {
		return pen.writeText();
	}
}
