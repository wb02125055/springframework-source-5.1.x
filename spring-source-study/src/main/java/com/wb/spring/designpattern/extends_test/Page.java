package com.wb.spring.designpattern.extends_test;

import java.util.List;

/**
 * Created by wangbin33 on 2020/3/4.
 */
public class Page<T> {
	private List<T> list;
	private String name;

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Page{" +
				"list=" + list +
				", name='" + name + '\'' +
				'}';
	}
}
