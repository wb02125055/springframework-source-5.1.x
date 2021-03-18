package com.wb.spring.circle_dependency;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/12/25 9:22
 */
public class ClassB {
	private ClassA classA;
	public ClassA getClassA() {
		return classA;
	}
	public void setClassA(ClassA classA) {
		this.classA = classA;
	}
	public void print() {
		System.out.println("ClassB.classA: " + this.classA);
	}
}
