package com.wb.spring.beans;

/**
 * Created by wangbin33 on 2020/1/31.
 */
public class Student {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void initStudent() {
		System.out.println("Student init...");
	}
	public void destoryStudent() {
		System.out.println("Student destory...");
	}

	@Override
	public String toString() {
		return "Student{" +
				"name='" + name + '\'' +
				'}';
	}
}