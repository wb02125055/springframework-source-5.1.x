package com.wb.spring.designpattern.observe1;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/11/7 16:22
 */
public class TestMain {
	public static void main(String[] args) {
		BadMan badMan = new BadMan();

		GoodMan goodMan = new GoodMan();
		GoodMan1 goodMan1 = new GoodMan1();

		badMan.addObserver(goodMan);
		badMan.addObserver(goodMan1);

		badMan.run();
	}
}
