package com.wb.spring.aop.domain;

/**
 * @author wangbin33
 * @date Created in 18:48 2019/10/6
 */
public class Color {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Color{" +
				"name='" + name + '\'' +
				'}';
	}
}
