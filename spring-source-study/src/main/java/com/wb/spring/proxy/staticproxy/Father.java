package com.wb.spring.proxy.staticproxy;

/**
 * Created by wangbin33 on 2020/2/8.
 */
public class Father {
	private Son son;
	public Father(Son son) {
		this.son = son;
	}
	public void findLove() {
		System.out.println("父亲无色对象");
		this.son.findLove();
		System.out.println("双方同意交往，确立关系");
	}
}
