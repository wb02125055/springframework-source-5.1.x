package com.wb.spring.designpattern.adapter.dc220demo;

/**
 * Created by wangbin33 on 2020/2/17.
 *
 * 交流电220V
 */
public class AC220 {
	public int outputAC220V() {
		int output = 220;
		System.out.println("输出交流电" + output + "V.");
		return output;
	}
}
