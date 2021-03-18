package com.wb.spring.designpattern.observe;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/11/7 16:03
 */
public class GoodMan implements Observer {
	@Override
	public void doThing(String str) {
		System.out.println("goodMan开始行动");
		System.out.println("---" + str);
	}
}
