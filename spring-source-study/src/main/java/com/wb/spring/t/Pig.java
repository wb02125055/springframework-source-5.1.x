package com.wb.spring.t;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/11/14 10:11
 */
public class Pig {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Pig{" +
				"name='" + name + '\'' +
				'}';
	}
}
