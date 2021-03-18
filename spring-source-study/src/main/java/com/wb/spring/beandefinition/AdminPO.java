package com.wb.spring.beandefinition;

/**
 * Created by wangbin33 on 2019/12/28.
 */
public class AdminPO {
	private Integer age;
	private String name;

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
		return "AdminPO{" +
				"age=" + age +
				", name='" + name + '\'' +
				'}';
	}
}
