package com.wb.spring.preparebeanfactory;

import java.util.List;
import java.util.Set;

/**
 * Created by wangbin33 on 2020/1/26.
 */
public class MyIgnoreInterfaceImpl implements MyIgnoreInterface {

	private List<String> list ;
	private Set<String> set ;

	@Override
	public void setList(List<String> list) {
		this.list = list;
	}

	@Override
	public void setSet(Set<String> set) {
		this.set = set;
	}

	@Override
	public String toString() {
		return "MyIgnoreInterfaceImpl{" +
				"list=" + list +
				", set=" + set +
				'}';
	}
}
