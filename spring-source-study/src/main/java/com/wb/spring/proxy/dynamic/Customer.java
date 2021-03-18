package com.wb.spring.proxy.dynamic;

import com.wb.spring.proxy.common.Person;

/**
 * Created by wangbin33 on 2020/2/8.
 */
public class Customer implements Person {
	@Override
	public void findLove() {
		System.out.println("高富帅");
		System.out.println("身高180cm");
		System.out.println("有6块腹肌");
	}
}
