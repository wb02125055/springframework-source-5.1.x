package com.wb.spring.beans;

import javax.annotation.PostConstruct;

/**
 * @author wangbin33
 * @date Created in 12:56 2019/11/30
 */
public class Person {
	private String uuid;

	public void sayHello(String name) {
		System.out.println("这是第一个带入参的方法...");
	}

	private final String sayHello() {
		System.out.println("这是一个没有入参的方法...11");
		return "WangBin";
	}

	public Person() {
		System.out.println("Person create ...");
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@PostConstruct
	public void initPerson() {
		System.out.println("initPerson......");
	}

	@Override
	public String toString() {
		return "Person{" +
				"uuid='" + uuid + '\'' +
				'}';
	}
}
