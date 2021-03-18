package com.wb.spring.designpattern.singleton;

/**
 * Created by wangbin33 on 2019/12/31.
 */
public class LazySimpleSingletonTest {
	public static void main(String[] args) {
		for (int i = 0 ; i < 200; i++) {
			new Thread(new ExecutorThread()).start();
		}
		System.out.println("End");
	}
}
