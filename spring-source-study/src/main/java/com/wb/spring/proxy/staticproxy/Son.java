package com.wb.spring.proxy.staticproxy;

import com.wb.spring.proxy.common.Person;

/**
 * Created by wangbin33 on 2020/2/8.
 */
public class Son implements Person {
	@Override
	public void findLove() {
		System.out.println("儿子要求：肤白貌美大长腿");
	}
}
