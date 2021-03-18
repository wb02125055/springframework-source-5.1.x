package com.wb.spring.tx.domain;

/**
 * @author wangbin33
 * @date Created in 23:16 2019/10/6
 */
public class User {
	private Integer id;
	private Integer age;
	private String name;

	public User() {
//		System.out.println("User init...");
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
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
				"id=" + id +
				", age=" + age +
				", name='" + name + '\'' +
				'}';
	}
}
