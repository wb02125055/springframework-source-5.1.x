package com.wb.spring.beans;

import org.springframework.beans.factory.FactoryBean;

/**
 * Created by wangbin33 on 2020/2/9.
 */
public class ConsumerBean implements FactoryBean<Student> {

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ConsumerBean() {
		System.out.println("构造函数被调用了！");
	}

	@Override
	public Student getObject() {
		return new Student();
	}

	@Override
	public Class<?> getObjectType() {
		return Student.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public String toString() {
		return "ConsumerBean{" +
				"id='" + id + '\'' +
				'}';
	}
}
