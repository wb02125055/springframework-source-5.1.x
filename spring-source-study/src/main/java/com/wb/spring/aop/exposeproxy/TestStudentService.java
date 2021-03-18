package com.wb.spring.aop.exposeproxy;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

/**
 * Created by wangbin33 on 2020/2/15.
 */
public class TestStudentService {
	public static void main(String[] args) {
		GenericApplicationContext acx = new AnnotationConfigApplicationContext(StudentServiceConfig.class);
		StudentService studentService = (StudentService) acx.getBean("studentService");
		studentService.m1();
	}
}
