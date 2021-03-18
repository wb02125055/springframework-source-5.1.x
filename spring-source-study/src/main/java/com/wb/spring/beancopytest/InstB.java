package com.wb.spring.beancopytest;

import java.util.List;

/**
 * Created by wangbin33 on 2020/3/9.
 */
public class InstB {
	private String name;
	private List<String> tools;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getTools() {
		return tools;
	}

	public void setTools(List<String> tools) {
		this.tools = tools;
	}

	@Override
	public String toString() {
		return "InstB{" +
				"name='" + name + '\'' +
				", tools=" + tools +
				'}';
	}
}
