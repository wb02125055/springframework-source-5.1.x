package com.wb.spring.preparebeanfactory;

import java.util.ArrayList;

/**
 * Created by wangbin33 on 2020/1/23.
 */
public class ListHolder {

//	@Autowired
	private ArrayList<String> list;

	public ArrayList<String> getList() {
		return list;
	}

	public void setList(ArrayList<String> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return "ListHolder{" +
				"list=" + list +
				'}';
	}
}
