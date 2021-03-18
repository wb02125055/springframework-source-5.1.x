package com.wb.spring.share.class2.javaconfig;

import org.springframework.stereotype.Component;

/**
 * @author wangbin33
 */
@Component
public class MathCalc {
	/**
	 * 计算 i / j
	 * @param i i
	 * @param j j
	 * @return i/j
	 */
	public int div(int i, int j) {
		return i / j;
	}
}
