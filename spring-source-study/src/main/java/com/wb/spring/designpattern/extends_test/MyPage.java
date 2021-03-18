package com.wb.spring.designpattern.extends_test;

/**
 * Created by wangbin33 on 2020/3/4.
 */
public class MyPage<T> extends Page<T> {
	private Integer age;

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return super.toString() + ", MyPage{" +
				"age=" + age +
				'}';
	}
}
