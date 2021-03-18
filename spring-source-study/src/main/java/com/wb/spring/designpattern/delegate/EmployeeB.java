package com.wb.spring.designpattern.delegate;

/**
 * Created by wangbin33 on 2020/2/17.
 */
public class EmployeeB implements IEmployee {
	@Override
	public void doing(String command) {
		System.out.println("我是EmployeeB，开始干" + command + "工作.");
	}
}
