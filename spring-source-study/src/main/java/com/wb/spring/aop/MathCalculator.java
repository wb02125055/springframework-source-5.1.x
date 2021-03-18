package com.wb.spring.aop;


import org.springframework.stereotype.Component;

/**
 * @author wangbin33
 * @date Created in 17:43 2019/10/6
 */
@Component
public class MathCalculator {

	public int div(int i, int j) {
		System.out.println("-----------------------------");
		return i / j;
	}

	public int add(int a, int b) {
		return a + b;
	}

	public long su() {
		return 11L;
	}

	public int sub() {
		return 101010101;
	}
}