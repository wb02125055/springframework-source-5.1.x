package com.wb.spring.designpattern.template.simpledemo;

/**
 * Created by wangbin33 on 2020/2/17.
 */
public class JavaCourse extends NetworkCourse {
	@Override
	void checkHomeWork() {
		System.out.println("检查Java的架构课件");
	}
}
