package com.wb.spring.aop.test;

import org.springframework.stereotype.Component;

@Component
@MyAnnotation
public class Pen {
	public String writeText() {
		return "Hello World";
	}
}
