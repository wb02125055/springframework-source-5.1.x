package com.wb.spring.beaninit.domain;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/4/26 20:22
 */
public class User {
	private String name;

	public User() {}

	public User(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "User{" +
				"name='" + name + '\'' +
				'}';
	}
}
