package com.wb.spring.designpattern.template.simpledemo;

/**
 * Created by wangbin33 on 2020/2/17.
 */
public class NetworkCourseTest {
	public static void main(String[] args) {
		System.out.println("---Java架构课程---");
		NetworkCourse javaCourse = new JavaCourse();
		javaCourse.createCourse();

		System.out.println("---大数据课程---");
		NetworkCourse bigDataCourse = new BigDataCourse(true);
		bigDataCourse.createCourse();
	}
}
