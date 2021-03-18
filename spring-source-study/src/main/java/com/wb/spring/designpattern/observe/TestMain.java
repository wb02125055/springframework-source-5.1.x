package com.wb.spring.designpattern.observe;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/11/7 16:07
 */
public class TestMain {
	public static void main(String[] args) {
		BadMan bm = new BadMan();

		GoodMan goodMan = new GoodMan();
		GoodMan1 goodMan1 = new GoodMan1();
		bm.registerObserver(goodMan);
		bm.registerObserver(goodMan1);

		bm.run();
		bm.play();
	}
}
