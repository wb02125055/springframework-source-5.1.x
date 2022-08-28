package com.wb.spring.lookup_method;

/**
 * created by IDEA
 *
 * @author wangbing
 * @date 2022.08.2022/8/28.20:11
 */
public abstract class LookUpBeanTest {
	public void printName() {
		this.getBean().printName();
	}
	abstract User getBean();
}
