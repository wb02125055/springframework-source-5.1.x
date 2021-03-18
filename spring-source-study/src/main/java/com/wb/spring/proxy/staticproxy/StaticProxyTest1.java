package com.wb.spring.proxy.staticproxy;

/**
 * Created by wangbin33 on 2020/2/8.
 */
public class StaticProxyTest1 {
	public static void main(String[] args) {
		Father father = new Father(new Son());
		father.findLove();
	}
}
