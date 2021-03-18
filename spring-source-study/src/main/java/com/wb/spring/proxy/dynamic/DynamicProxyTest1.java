package com.wb.spring.proxy.dynamic;

import com.wb.spring.proxy.common.Person;

/**
 * Created by wangbin33 on 2020/2/8.
 */
public class DynamicProxyTest1 {
	public static void main(String[] args) {
		try {
			Person person = (Person) new JDKMeipo().getInstance(new Customer());
			person.findLove();
		} catch (Exception e) {

		}
	}
}
