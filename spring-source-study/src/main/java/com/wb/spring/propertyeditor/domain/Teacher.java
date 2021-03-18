package com.wb.spring.propertyeditor.domain;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/10/20 16:49
 */
public class Teacher {
	private String name;
	private int age;
	private String email;

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Teacher{" +
				"name='" + name + '\'' +
				", age=" + age +
				", email='" + email + '\'' +
				'}';
	}
}
