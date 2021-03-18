package com.wb.spring.finishBeanFactoryInitialization.domain;

/**
 * Created by wangbin33 on 2020/1/28.
 */
public class ClassA {
	private ClassB classB;

	public ClassB getClassB() {
		return classB;
	}

	public void setClassB(ClassB classB) {
		this.classB = classB;
	}

	@Override
	public String toString() {
		return "ClassA{" +
				"classB=" + classB +
				'}';
	}
}
