package com.wb.spring.ioc.domain;

/**
 * @author wangbin33
 * @date Created in 16:00 2019/11/24
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
