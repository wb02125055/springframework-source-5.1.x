package com.wb.spring.lookup_method;

/**
 * created by IDEA
 *
 * @author wangbing
 * @date 2022.08.2022/8/28.20:10
 */
public class Teacher extends User {
	@Override
	public void printName() {
		System.out.println("This is Teacher.");
	}
}
