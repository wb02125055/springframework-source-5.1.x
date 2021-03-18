package com.wb.springmvc.domain;

/**
 * Created by wangbin33 on 2019/12/28.
 */
public class User {
	private String name;
	private Integer age;

	public User() {}

	public User(String name, Integer age) {
		this.name = name;
		this.age = age;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "User{" +
				"name='" + name + '\'' +
				", age=" + age +
				'}';
	}
}
