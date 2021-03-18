package com.wb.spring.postProcessBeanFactory;

/**
 * Created by wangbin33 on 2020/1/26.
 */
public class MyUser {
	private String name;
	private int age;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "MyUser{" +
				"name='" + name + '\'' +
				", age=" + age +
				'}';
	}
}
