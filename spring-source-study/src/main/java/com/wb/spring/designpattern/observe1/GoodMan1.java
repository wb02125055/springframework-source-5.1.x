package com.wb.spring.designpattern.observe1;

import java.util.Observable;
import java.util.Observer;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/11/7 16:22
 */
public class GoodMan1 implements Observer {
	@Override
	public void update(Observable o, Object arg) {
		System.out.println("goodMan1开始行动");
		System.out.println("===" + arg);
	}
}
