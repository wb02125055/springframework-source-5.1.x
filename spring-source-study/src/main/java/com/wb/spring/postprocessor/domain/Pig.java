package com.wb.spring.postprocessor.domain;

import java.util.UUID;

/**
 * Created by wangbin33 on 2020/1/8.
 */
public class Pig {
	private String id;
	private String name;

	public Pig() {
		System.out.println("开始创建Pig实例...");
	}

	public Pig(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Pig{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				'}';
	}
}
