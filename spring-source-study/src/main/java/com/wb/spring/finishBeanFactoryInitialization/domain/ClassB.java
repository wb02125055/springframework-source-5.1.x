package com.wb.spring.finishBeanFactoryInitialization.domain;

/**
 * Created by wangbin33 on 2020/1/28.
 */
public class ClassB {
	private ClassA classA;

	public ClassA getClassA() {
		return classA;
	}

	public void setClassA(ClassA classA) {
		this.classA = classA;
	}

	@Override
	public String toString() {
		return "ClassB{" +
				"classA=" + classA +
				'}';
	}
}
