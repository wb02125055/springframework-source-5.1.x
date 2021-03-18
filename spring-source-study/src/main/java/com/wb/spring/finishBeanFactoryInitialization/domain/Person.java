package com.wb.spring.finishBeanFactoryInitialization.domain;

import java.util.Date;

/**
 * Created by wangbin33 on 2020/1/27.
 */
public class Person {

	public Date birthday;
	public String name;

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Person{" +
				"birthday=" + birthday +
				", name='" + name + '\'' +
				'}';
	}
}

