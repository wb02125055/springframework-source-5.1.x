package com.wb.spring.propertyvalue.domain;

/**
 * Created by wangbin33 on 2020/2/28.
 */
//@Component
public class User {

//	@Value("${user.name}")
	private String name;
//	@Value("${user.sex}")
	private String sex;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	@Override
	public String toString() {
		return "User{" +
				"name='" + name + '\'' +
				", sex='" + sex + '\'' +
				'}';
	}
}
