package com.wb.spring.designpattern.observe;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/11/7 16:04
 */
public class GoodMan1 implements Observer {
	@Override
	public void doThing(String str) {
		System.out.println("goodMan1开始行动");
		System.out.println("===" + str);
	}
}
