package com.wb.spring.circle_dependency;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/12/25 9:22
 */
public class ClassA {
	private ClassB classB;
	public ClassB getClassB() {
		return classB;
	}
	public void setClassB(ClassB classB) {
		this.classB = classB;
	}

	public void print() {
		System.out.println("ClassA.classB: " + this.classB);
	}
}
